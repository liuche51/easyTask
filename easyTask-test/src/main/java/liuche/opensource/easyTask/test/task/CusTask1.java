package liuche.opensource.easyTask.test.task;

import liuche.opensource.easyTask.core.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CusTask1 extends Task implements Runnable{
    private static Logger log = LoggerFactory.getLogger(CusTask1.class);
    @Override
    public void run() {
        log.info("任务1已执行");
    }
}
