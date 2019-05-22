package liuche.opensource.easyTask.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalField;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class AnnularQueue {
    private static Logger log = LoggerFactory.getLogger(AnnularQueue.class);
    /**
     * 任务调度线程池
     */
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(5));
    /**
     * 工作任务线程池
     */
    static ThreadPoolExecutor workers = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(5));
    static Slice[] slices = new Slice[60];

    static {
        for (int i = 0; i < slices.length; i++) {
            slices[i] = new Slice();
        }
    }

    public static void start() {
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
            executor.submit(new Runnable() {
                public void run() {
                    List<Schedule> schedules = slice.getList();
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
    }

    public static String submit(Task task) {
        Schedule schedule = new Schedule();
        schedule.setEndTimestamp(task.getExcuteTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        Runnable proxy= (Runnable) new ProxyFactory(task).getProxyInstance();
        schedule.setRun(proxy);
        String id = UUID.randomUUID().toString().replace("-", "");
        schedule.setId(id);
        task.setId(id);
        int second = task.getExcuteTime().getSecond();
        Slice slice = slices[second];
        List<Schedule> list = slice.getList();
        if (list == null) {
            list = new LinkedList<>();
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
                    Runnable run = (Runnable) c.newInstance();
                    schedule.setRun(run);
                    LocalDateTime time = LocalDateTime.ofEpochSecond(schedule.getEndTimestamp(), 0, ZoneOffset.ofHours(8));
                    int second = time.getSecond();
                    Slice slice = slices[second];
                    List<Schedule> list2 = slice.getList();
                    if (list2 == null) {
                        list2 = new LinkedList<>();
                        slice.setList(list2);
                    }
                    list2.add(schedule);
                } catch (Exception e) {
                    log.error("task:{} recover fail.", schedule.getId());
                }
            }
            log.debug("easyTask recover success.");
        } catch (Exception e) {
            log.error("easyTask recover fail.");
        }

    }
}
