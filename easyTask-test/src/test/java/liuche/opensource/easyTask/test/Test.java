package liuche.opensource.easyTask.test;

import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.DbInit;
import liuche.opensource.easyTask.core.SqliteHelper;
import liuche.opensource.easyTask.core.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);

    @org.junit.Test
    public void testAnnularQueue() {
        AnnularQueue.start();
    }

    @org.junit.Test
    public void testMain() throws InterruptedException {
        DbInit db=new DbInit();
        db.init();
        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                AnnularQueue.start();
            }
        });
        //th1.start();
        Task task = new Task();
        task.setExcuteTime(LocalDateTime.now().minusSeconds(-30));
        Task1 task1 = new Task1();
        task.setRun(task1);
      /*  task.setRun(new Runnable() {
            @Override
            public void run() {
                log.info("任务1已执行");
            }
        });*/
        AnnularQueue.submit(task);
        th1.join();
    }
    @org.junit.Test
    public void test2() {
        DbInit db=new DbInit();
        db.init();
    }
    @org.junit.Test
    public void test3() {
        Task task = new Task();
        String path=task.getClass().getName();
        try {
            Class c= Class.forName(path);
            Task t=(Task) c.newInstance();
            t.setExcuteTime(LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
