import liuche.opensource.easyTask.core.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task1 extends Task implements Runnable{
    private static Logger log = LoggerFactory.getLogger(Task1.class);
    @Override
    public void run() {
        log.info("任务1已执行");
    }
}
