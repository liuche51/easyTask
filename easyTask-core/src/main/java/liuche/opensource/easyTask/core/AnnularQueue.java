package liuche.opensource.easyTask.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 环形任务队列
 */
public class AnnularQueue {
    private static Logger log = LoggerFactory.getLogger(AnnularQueue.class);
    private static AnnularQueue singleton = null;
    private static boolean isRunning = false;//防止所线程运行环形队列
    /**
     * 任务调度线程池
     */
    private static ExecutorService dispatchs = null;
    /**
     * 工作任务线程池
     */
    private static ExecutorService workers = null;
    static Slice[] slices = new Slice[60];

    static {
        for (int i = 0; i < slices.length; i++) {
            slices[i] = new Slice();
        }
    }

    public static AnnularQueue getInstance() {
        if (singleton == null) {
            synchronized (AnnularQueue.class) {
                if (singleton == null) {
                    singleton = new AnnularQueue();
                }
            }
        }
        return singleton;
    }

    private AnnularQueue() {
    }

    ;

    /**
     * set the Dispatch ThreadPool
     *
     * @param dispatchs
     */
    public void setDispatchThreadPool(ThreadPoolExecutor dispatchs) throws Exception {
        if (isRunning)
            throw new Exception("please before AnnularQueue started set");
        this.dispatchs = dispatchs;
    }

    /**
     * set the Worker ThreadPool
     *
     * @param workers
     */
    public void setWorkerThreadPool(ThreadPoolExecutor workers) throws Exception {
        if (isRunning)
            throw new Exception("please before AnnularQueue started set");
        this.workers = workers;
    }

    /**
     * set Task Store Path.example  C:\\db
     * @param path
     * @throws Exception
     */
    public void setTaskStorePath(String path) throws Exception {
        if (isRunning)
            throw new Exception("please before AnnularQueue started set");
        SqliteHelper.dbFilePath = path + "\\easyTask.db";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void setDefaultThreadPool() {
        if (this.dispatchs == null)
            this.dispatchs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        if (this.workers == null)
            this.workers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    public void start() {
        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                runQueue();
            }
        });
        th1.start();
    }

    /**
     * start the AnnularQueue
     */
    private synchronized void runQueue() {
        //避免重复执行
        if (isRunning)
            return;
        try {
            DbInit.init();
            recover();
            isRunning = true;
            setDefaultThreadPool();
            int lastSecond = 0;
            while (true) {
                int second = LocalDateTime.now().getSecond();
                if (second == lastSecond) {
                    try {
                        Thread.sleep(500l);
                        continue;
                    } catch (Exception e) {

                    }
                }
                Slice slice = slices[second];
                log.debug("已执行时间分片:{}，任务数量:{}", second, slice.getList() == null ? 0 : slice.getList().size());
                lastSecond = second;
                dispatchs.submit(new Runnable() {
                    public void run() {
                        TreeSet<Schedule> schedules = slice.getList();
                        List<Schedule> willremove = new LinkedList<>();
                        for (Schedule x : schedules) {
                            if (System.currentTimeMillis() >= x.getEndTimestamp()) {
                                workers.submit(x.getRun());
                                willremove.add(x);
                                log.debug("已提交分片:{} 一个任务:{}", second, x.getId());
                            }
                            //因为列表是已经按截止执行时间排好序的，可以节省后面元素的过期判断
                            else break;
                        }
                        schedules.removeAll(willremove);
                        submitNewPeriodSchedule(willremove);
                    }
                });
            }

        } catch (
                Exception e) {
            isRunning = false;
            log.error("AnnularQueue start fail.", e);
            throw e;
        }

    }

    public String submit(Task task) throws Exception {
        Schedule schedule = new Schedule();
        schedule.setTaskType(task.getTaskType());
        schedule.setPeriod(task.getPeriod());
        schedule.setUnit(task.getUnit());
        schedule.setParam(task.getParam());
        if (task.getTaskType().equals(TaskType.ONECE))
            schedule.setEndTimestamp(task.getExecuteTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        if (task.getTaskType().equals(TaskType.PERIOD)) {
            if (task.isImmediateExecute())
                schedule.setEndTimestamp(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            else
                schedule.setEndTimestamp(Schedule.getTimeStampByTimeUnit(schedule.getPeriod(), schedule.getUnit()));
        }
        task.setId(schedule.getId());
        Runnable proxy = (Runnable) new ProxyFactory(task).getProxyInstance();
        schedule.setRun(proxy);
        AddSchedule(schedule);
        String path = task.getClass().getName();
        schedule.setTaskClassPath(path);
        schedule.save();
        log.debug("已添加任务:{}，所属分片:{} 预计执行时间:{}", schedule.getId(), task.getExecuteTime().getSecond(), task.getExecuteTime().toLocalTime());
        return schedule.getId();
    }

    /**
     * 批量创建新周期任务
     *
     * @param list
     */
    public void submitNewPeriodSchedule(List<Schedule> list) {
        for (Schedule schedule : list) {
            if (!TaskType.PERIOD.equals(schedule.getTaskType()))//周期任务需要重新提交新任务
                continue;
            try {
                Schedule schedule1 = schedule.clone();
                schedule1.setEndTimestamp(Schedule.getTimeStampByTimeUnit(schedule1.getPeriod(), schedule1.getUnit()));
                AddSchedule(schedule1);
                schedule1.save();
                log.debug("已添加新周期任务:{}，旧任务:{}", schedule1.getId(), schedule.getId());
            } catch (Exception e) {
                log.error("submitNewPeriodSchedule exception！", e);
            }
        }
    }

    /**
     * 恢复中断后的系统任务
     */
    private void recover() {
        List<Schedule> list = ScheduleDao.selectAll();
        try {
            for (Schedule schedule : list) {
                try {
                    Class c = Class.forName(schedule.getTaskClassPath());
                    Object o = c.newInstance();
                    Task task = (Task) o;//强转后设置id，o对象值也会变，所以强转后的task也是对象的引用而已
                    task.setId(schedule.getId());
                    task.setParam(schedule.getParam());
                    Runnable proxy = (Runnable) new ProxyFactory(task).getProxyInstance();
                    schedule.setRun(proxy);
                    AddSchedule(schedule);
                } catch (Exception e) {
                    log.error("task:{} recover fail.", schedule.getId());
                }
            }
            log.debug("easyTask recover success! count:{}", list.size());
        } catch (Exception e) {
            log.error("easyTask recover fail.");
        }
    }

    private void AddSchedule(Schedule schedule1) {
        LocalDateTime time = LocalDateTime.ofEpochSecond(schedule1.getEndTimestamp(), 0, ZoneOffset.ofHours(8));
        int second = time.getSecond();
        Slice slice = slices[second];
        TreeSet<Schedule> list2 = slice.getList();
        if (list2 == null) {
            list2 = new TreeSet<>();
            slice.setList(list2);
        }
        list2.add(schedule1);
    }
}
