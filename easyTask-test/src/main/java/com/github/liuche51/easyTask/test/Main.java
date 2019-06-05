package com.github.liuche51.easyTask.test;

import com.github.liuche51.easyTask.core.AnnularQueue;
import com.github.liuche51.easyTask.core.TaskType;
import com.github.liuche51.easyTask.core.TimeUnit;
import com.github.liuche51.easyTask.test.task.CusTask1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    private static Object obj=new Object();
    public static void main(String[] args){
        allcustomSimpleSetTest();
    }
    static void allcustomSimpleSetTest(){
        try {
            //annularQueue.setTaskStorePath("C:\\db\\");
            annularQueue.setDispatchThreadPool( new ThreadPoolExecutor(4, 4, 1000, java.util.concurrent.TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>()));
            annularQueue.setWorkerThreadPool( new ThreadPoolExecutor(4, 8, 1000, java.util.concurrent.TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>()));
            annularQueue.setSQLlitePoolSize(10);
            annularQueue.start();
            CusTask1 task1 = new CusTask1();
            task1.setEndTimestamp(ZonedDateTime.now().plusSeconds(10).toInstant().toEpochMilli());
            Map<String,String> param=new HashMap<String,String>(){
                {
                    put("name","刘彻");
                    put("birthday","1988-1-1");
                    put("age","25");
                    put("threadid",String.valueOf(Thread.currentThread().getId()));
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
                    put("name","Jack");
                    put("birthday","1986-1-1");
                    put("age","32");
                    put("threadid",String.valueOf(Thread.currentThread().getId()));
                }
            };
            task2.setParam(param2);
            annularQueue.submit(task1);
            annularQueue.submit(task2);
            obj.wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void highlyConcurrentTest(){
        annularQueue.start();
        for(int i=0;i<20;i++){
            Thread th1=new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<10;i++) {
                        CusTask1 task1 = new CusTask1();
                        task1.setEndTimestamp(ZonedDateTime.now().plusSeconds(10).toInstant().toEpochMilli());
                        Map<String,String> param=new HashMap<String,String>(){
                            {
                                put("name","刘彻");
                                put("birthday","1988-1-1");
                                put("age","25");
                                put("threadid",String.valueOf(Thread.currentThread().getId()));
                            }
                        };
                        task1.setParam(param);
                        try {
                            AnnularQueue.getInstance().submit(task1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            th1.start();
        }
        for(int i=0;i<20;i++){
            Thread th2=new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<10;i++) {
                        CusTask1 task1 = new CusTask1();
                        task1.setPeriod(30);
                        task1.setTaskType(TaskType.PERIOD);
                        task1.setUnit(TimeUnit.SECONDS);
                        task1.setImmediateExecute(true);
                        Map<String,String> param=new HashMap<String,String>(){
                            {
                                put("name","王林");
                                put("birthday","1986-1-1");
                                put("age","35");
                                put("threadid",String.valueOf(Thread.currentThread().getId()));
                            }
                        };
                        task1.setParam(param);
                        try {
                            AnnularQueue.getInstance().submit(task1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            th2.start();
        }
        try {
            obj.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
