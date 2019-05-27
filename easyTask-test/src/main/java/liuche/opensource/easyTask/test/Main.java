package liuche.opensource.easyTask.test;

import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.DbInit;
import liuche.opensource.easyTask.test.task.CusTask1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    public static void main(String[] args){

        annularQueue.start();
        CusTask1 task1 = new CusTask1();
        task1.setExcuteTime(LocalDateTime.now().minusSeconds(-10));
        AnnularQueue.submit(task1);
    }
}
