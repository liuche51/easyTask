package liuche.opensource.easyTask.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
    static Slice[] slices=new Slice[60];
    public static void start(){
        int lastSecond=0;
        while (true){
            int second= LocalDateTime.now().getSecond();
            if(second==lastSecond){
                try {
                    Thread.sleep(500l);
                    continue;
                }catch (Exception e){

                }
            }
            final Slice slice=slices[second];
            log.debug("已执行时间分片:{}",second);
            executor.submit(new Runnable() {
                public void run() {
                    List<Task> tasks=slice.getList();
                    tasks.forEach(x->{
                        if(System.currentTimeMillis()>=x.getEndTimestamp()){
                            workers.submit(x);
                        }
                    });

                }
            });
        }


    }
}
