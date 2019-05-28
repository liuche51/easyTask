package liuche.opensource.easyTask.test;

import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.TaskType;
import liuche.opensource.easyTask.test.task.CusTask1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    public static void main(String[] args){
        annularQueue.start();
        Thread th1=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10000;i++) {
                    CusTask1 task1 = new CusTask1();
                    task1.setExecuteTime(LocalDateTime.now().minusSeconds(-10));
                    try {
                        AnnularQueue.getInstance().submit(task1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread th2=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10000;i++) {
                    CusTask1 task1 = new CusTask1();
                    task1.setPeriod(1);
                    task1.setTaskType(TaskType.PERIOD);
                    task1.setUnit(TimeUnit.MINUTES);
                    task1.setExecuteTime(LocalDateTime.now().minusSeconds(-10));
                    try {
                        AnnularQueue.getInstance().submit(task1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th1.start();
        th2.start();
        System.console().readLine();
    }
}
