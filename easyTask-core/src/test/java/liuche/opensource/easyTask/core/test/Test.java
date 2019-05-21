package liuche.opensource.easyTask.core.test;

import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    @org.junit.Test
    public void testAnnularQueue(){
        AnnularQueue.start();
    }
    @org.junit.Test
    public void testMain() throws InterruptedException {
        Thread th1=new Thread(new Runnable() {
            @Override
            public void run() {
                AnnularQueue.start();
            }
        });
        th1.start();
        Schedule schedule=new Schedule();
        schedule.setExcuteTime(LocalDateTime.now().minusSeconds(-30));
        schedule.setTask(new Runnable() {
            @Override
            public void run() {
                log.info("任务1已执行");
            }
        });
        AnnularQueue.submit(schedule);
    }
}
