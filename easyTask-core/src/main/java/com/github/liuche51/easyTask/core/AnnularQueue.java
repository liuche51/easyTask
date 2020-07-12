package com.github.liuche51.easyTask.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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

    Slice[] getSlices() {
        return slices;
    }

    ExecutorService getDispatchs() {
        return dispatchs;
    }

    ExecutorService getWorkers() {
        return workers;
    }

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
     *
     * @param path
     * @throws Exception
     */
    public void setTaskStorePath(String path) throws Exception {
        if (isRunning)
            throw new Exception("please before AnnularQueue started set");
        SQLlitePool.dbFilePath = path + "\\easyTask.db";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * set SQLlitePool Size，default qty 15
     *
     * @param count
     * @throws Exception
     */
    public void setSQLlitePoolSize(int count) throws Exception {
        if (isRunning)
            throw new Exception("please before AnnularQueue started set");
        if (count < 1)
            throw new Exception("poolSize must >1");
        SQLlitePool.poolSize = count;
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
                int second = ZonedDateTime.now().getSecond();
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
                        ConcurrentSkipListMap<String, Schedule> schedules = slice.getList();
                        List<Schedule> periodSchedules = new LinkedList<>();
                        for (Map.Entry<String, Schedule> entry : schedules.entrySet()) {
                            Schedule s = entry.getValue();
                            //因为计算时有一秒钟内的精度问题，所以判断时当前时间需多补上一秒。这样才不会导致某些任务无法得到及时的执行
                            if (System.currentTimeMillis() + 1000l >= s.getEndTimestamp()) {
                                Runnable proxy = (Runnable) new ProxyFactory(s).getProxyInstance();
                                workers.submit(proxy);
                                if (TaskType.PERIOD.equals(s.getTaskType()))//周期任务需要重新提交新任务
                                    periodSchedules.add(s);
                                schedules.remove(entry.getKey());
                                log.debug("工作任务:{}已提交代理执行，所属时间分片:{}", s.getScheduleExt().getId(), second);
                            }
                            //因为列表是已经按截止执行时间排好序的，可以节省后面元素的过期判断
                            else break;
                        }
                        submitNewPeriodSchedule(periodSchedules);
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

    public String submit(Schedule schedule) throws Exception {
        schedule.getScheduleExt().setId(Util.generateUniqueId());
        String path = schedule.getClass().getName();
        schedule.getScheduleExt().setTaskClassPath(path);
        //以下两行代码不要调换，否则可能发生任务已经执行完成，而任务尚未持久化，导致无法执行删除持久化的任务风险
        schedule.save();
        beforeAddSlice(schedule);
        AddSlice(schedule);
        ZonedDateTime time = ZonedDateTime.ofInstant(new Timestamp(schedule.getEndTimestamp()).toInstant(), ZoneId.systemDefault());
        log.debug("已添加类型:{}任务:{}，所属分片:{} 预计执行时间:{} 线程ID:{}", schedule.getTaskType().name(), schedule.getScheduleExt().getId(), time.getSecond(), time.toLocalTime(), Thread.currentThread().getId());
        return schedule.getScheduleExt().getId();
    }

    /**
     * 批量创建新周期任务
     *
     * @param list
     */
    public void submitNewPeriodSchedule(List<Schedule> list) {
        for (Schedule schedule : list) {
            try {
                schedule.setEndTimestamp(Schedule.getNextExcuteTimeStamp(schedule.getPeriod(), schedule.getUnit()));
                int slice = AddSlice(schedule);
                log.debug("已重新提交周期任务:{}，所属分片:{}，线程ID:{}", schedule.getScheduleExt().getId(), slice, Thread.currentThread().getId());
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
                    Class c = Class.forName(schedule.getScheduleExt().getTaskClassPath());
                    Object o = c.newInstance();
                    Schedule schedule1 = (Schedule) o;//强转后设置id，o对象值也会变，所以强转后的task也是对象的引用而已
                    schedule1.getScheduleExt().setId(schedule.getScheduleExt().getId());
                    schedule1.setEndTimestamp(schedule.getEndTimestamp());
                    schedule1.setPeriod(schedule.getPeriod());
                    schedule1.setTaskType(schedule.getTaskType());
                    schedule1.setUnit(schedule.getUnit());
                    schedule1.getScheduleExt().setTaskClassPath(schedule.getScheduleExt().getTaskClassPath());
                    schedule1.setParam(schedule.getParam());
                    recoverBeforeAddSlice(schedule1);
                    AddSlice(schedule1);
                } catch (Exception e) {
                    log.error("schedule:{} recover fail.", schedule.getScheduleExt().getId());
                }
            }
            log.debug("easyTask recover success! count:{}", list.size());
        } catch (Exception e) {
            log.error("easyTask recover fail.");
        }
    }

    /**
     * 将任务添加到时间分片中去。
     *
     * @param schedule
     * @return
     */
    private int AddSlice(Schedule schedule) throws Exception {
        ZonedDateTime time = ZonedDateTime.ofInstant(new Timestamp(schedule.getEndTimestamp()).toInstant(), ZoneId.systemDefault());
        int second = time.getSecond();
        Slice slice = slices[second];
        ConcurrentSkipListMap<String, Schedule> list2 = slice.getList();
        list2.put(schedule.getEndTimestamp() + "-" + Util.GREACE.getAndIncrement(), schedule);
        return second;
    }

    /**
     * 提交任务，在添加到时间轮分片前需要做的一些逻辑判断
     *
     * @param schedule
     * @throws Exception
     */
    private void submitAddSlice(Schedule schedule) throws Exception {
        //立即执行的任务，第一次不走时间分片，直接提交执行
        if (System.currentTimeMillis()+1000l>=schedule.getEndTimestamp()) {
            log.debug("立即执行类工作任务:{}已提交代理执行", schedule.getScheduleExt().getId());
            Runnable proxy = (Runnable) new ProxyFactory(schedule).getProxyInstance();
            workers.submit(proxy);
            //如果是一次性任务，则不用继续提交到时间分片中了
            if (schedule.getTaskType().equals(TaskType.ONECE)) {
                return;
            }
        }
        //周期任务，在这里计算下一次执行时间
        if (schedule.getTaskType().equals(TaskType.PERIOD)) {
            schedule.setEndTimestamp(Schedule.getNextExcuteTimeStamp(schedule.getPeriod(), schedule.getUnit()));
        }
        AddSlice(schedule);
    }
    /**
     * 提交或恢复任务，在添加到时间轮分片前需要做的一些逻辑判断
     *
     * @param schedule
     * @throws Exception
     */
    private void recoverAddSlice(Schedule schedule) throws Exception {
        //立即执行的任务，第一次不走时间分片，直接提交执行
        if (schedule.getTaskType().equals(TaskType.ONECE)&&System.currentTimeMillis()>=schedule.getEndTimestamp()) {
            log.debug("恢复一次性工作任务:{}，因为执行时间已过期，需立即提交代理执行", schedule.getScheduleExt().getId());
            Runnable proxy = (Runnable) new ProxyFactory(schedule).getProxyInstance();
            workers.submit(proxy);
            return;
        }
        //周期任务，在这里计算下一次执行时间
        else if (schedule.getTaskType().equals(TaskType.PERIOD)) {
            schedule.setEndTimestamp(Schedule.getNextExcuteTimeStamp(schedule.getPeriod(), schedule.getUnit()));
        }
        AddSlice(schedule);
    }
}
