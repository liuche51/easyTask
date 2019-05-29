package liuche.opensource.easyTask.test;

import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.TaskType;
import liuche.opensource.easyTask.test.task.CusTask1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    public static void main(String[] args){
        allcustomSimpleSetTest();
    }
    static void allcustomSimpleSetTest(){
        AnnularQueue annularQueue=AnnularQueue.getInstance();
        try {
            annularQueue.setTaskStorePath("C:\\db\\");
            annularQueue.setDispatchThreadPool( new ThreadPoolExecutor(4, 4, 1000, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>()));
            annularQueue.setWorkerThreadPool( new ThreadPoolExecutor(4, 8, 1000, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>()));
            annularQueue.start();
            CusTask1 task1 = new CusTask1();
            task1.setEndTimestamp(LocalDateTime.now().minusSeconds(-10).toInstant(ZoneOffset.of("+8")).toEpochMilli());
            Map<String,String> param=new HashMap<String,String>(){
                {
                    put("name","刘彻");
                    put("birthday","1988-1-1");
                    put("age","25");
                }
            };
            task1.setParam(param);
            CusTask1 task2 = new CusTask1();
            task2.setPeriod(30);
            task2.setImmediateExecute(true);
            task2.setTaskType(TaskType.PERIOD);
            task2.setUnit(TimeUnit.SECONDS);
            Map<String,String> param2=new HashMap<String,String>(){
                {
                    put("name","王林");
                    put("birthday","1986-1-1");
                    put("age","32");
                }
            };
            task2.setParam(param2);
            //annularQueue.submit(task1);
            annularQueue.submit(task2);
            try {
            System.console().readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void highlyConcurrentTest(){
        annularQueue.start();
        Thread th1=new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10000;i++) {
                    CusTask1 task1 = new CusTask1();
                    task1.setEndTimestamp(LocalDateTime.now().minusSeconds(-10).toInstant(ZoneOffset.of("+8")).toEpochMilli());
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
                    task1.setImmediateExecute(true);
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
