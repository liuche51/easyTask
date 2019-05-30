import liuche.opensource.easyTask.core.AnnularQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    @org.junit.Test
    public void testAnnularQueue() {

    }

    @org.junit.Test
    public void test1(){
        System.out.println(ZonedDateTime.now().toInstant().toEpochMilli());
        System.out.println(ZonedDateTime .ofInstant(new Timestamp(ZonedDateTime.now().toInstant().toEpochMilli()).toInstant(), ZoneId.systemDefault()));
    }
    @org.junit.Test
    public void test2() {

    }
    @org.junit.Test
    public void test3() {

    }
    @org.junit.Test
    public void test4() {

    }
}
