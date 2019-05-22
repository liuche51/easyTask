package liuche.opensource.easyTask.core.test;

import liuche.opensource.easyTask.core.AnnularQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    @org.junit.Test
    public void testAnnularQueue(){
        AnnularQueue.start();
    }
    @org.junit.Test
    public void testMain() throws InterruptedException {

    }
}
