import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    @org.junit.Test
    public void testAnnularQueue() {

    }

    @org.junit.Test
    public void testMain() throws InterruptedException {

    }
    @org.junit.Test
    public void test2() {

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
    @org.junit.Test
    public void test4() {

    }
}
