package liuche.opensource.easyTask.test;

import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.DbInit;
import liuche.opensource.easyTask.test.task.CusTask1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args){
         DbInit db=new DbInit();
        db.init();
        Thread th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                AnnularQueue.start();
            }
        });
        th1.start();
        CusTask1 task1 = new CusTask1();
        task1.setExcuteTime(LocalDateTime.now().minusSeconds(-10));
        AnnularQueue.submit(task1);
        try {
            th1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
