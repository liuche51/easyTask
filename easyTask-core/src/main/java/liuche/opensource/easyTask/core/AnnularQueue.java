package liuche.opensource.easyTask.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.*;

public class AnnularQueue {
    private static Logger log = LoggerFactory.getLogger(AnnularQueue.class);
    private static AnnularQueue singleton = null;
    private static boolean isRunning = false;//防止所线程运行环形队列
    /**
     * 任务调度线程池
     */
    private static ExecutorService dispatchs =null;
    /**
     * 工作任务线程池
     */
    private static ExecutorService workers =null;
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
    private AnnularQueue(){};
    /**
     * set the Dispatch ThreadPool
     *
     * @param dispatchs
     */
    public void setDispatchThreadPool(ThreadPoolExecutor dispatchs) {
        this.dispatchs = dispatchs;
    }

    /**
     * set the Worker ThreadPool
     *
     * @param workers
     */
    public void setWorkerThreadPool(ThreadPoolExecutor workers) {
        this.workers = workers;
    }

    private void setDefaultThreadPool() {
        if (this.dispatchs == null)
            this.dispatchs = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        if (this.workers == null)
            this.workers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }
    public void start(){
        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                this.run();
            }
        });
        th1.start();
    }
    /**
     * start the AnnularQueue
     */
    private synchronized void run() {
        //避免重复执行
        if (isRunning)
            return;
        try {
            DbInit.init();
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
                        schedules.forEach(x -> {
                            if (System.currentTimeMillis() >= x.getEndTimestamp()) {
                                workers.submit(x.getRun());
                                willremove.add(x);
                                log.debug("已提交分片:{} 一个任务:{}", second, x.getId());
                            }
                        });
                        schedules.removeAll(willremove);
                    }
                });
            }

        } catch (Exception e) {
            isRunning = false;
            log.error("AnnularQueue start fail.", e);
            throw e;
        }
    }

    public static String submit(Task task) {
        Schedule schedule = new Schedule();
        schedule.setEndTimestamp(task.getExcuteTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        Runnable proxy = (Runnable) new ProxyFactory(task).getProxyInstance();
        schedule.setRun(proxy);
        String id = UUID.randomUUID().toString().replace("-", "");
        schedule.setId(id);
        task.setId(id);
        int second = task.getExcuteTime().getSecond();
        Slice slice = slices[second];
        TreeSet<Schedule> list = slice.getList();
        if (list == null) {
            list = new TreeSet<>();
            slice.setList(list);
        }
        list.add(schedule);
        String path = task.getClass().getName();
        schedule.setTaskClassPath(path);
        schedule.save();
        log.debug("已添加任务:{}，所属分片:{} 预计执行时间:{}", schedule.getId(), task.getExcuteTime().getSecond(), task.getExcuteTime().toLocalTime());
        return schedule.getId();
    }

    public static void recover() {
        List<Schedule> list = ScheduleDao.selectAll();
        try {
            for (Schedule schedule : list) {
                try {
                    Class c = Class.forName(schedule.getTaskClassPath());
                    Object o = c.newInstance();
                    Task task = (Task) o;//强转后设置id，o对象值也会变，所以强转后的task也是对象的引用而已
                    task.setId(schedule.getId());
                    Runnable proxy = (Runnable) new ProxyFactory(o).getProxyInstance();
                    schedule.setRun(proxy);
                    LocalDateTime time = LocalDateTime.ofEpochSecond(schedule.getEndTimestamp(), 0, ZoneOffset.ofHours(8));
                    int second = time.getSecond();
                    Slice slice = slices[second];
                    TreeSet<Schedule> list2 = slice.getList();
                    if (list2 == null) {
                        list2 = new TreeSet<>();
                        slice.setList(list2);
                    }
                    list2.add(schedule);
                } catch (Exception e) {
                    log.error("task:{} recover fail.", schedule.getId());
                }
            }
            log.debug("easyTask recover success! count:{}", list.size());
        } catch (Exception e) {
            log.error("easyTask recover fail.");
        }

    }
}
