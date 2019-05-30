import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.Schedule;
import liuche.opensource.easyTask.core.ScheduleComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    @org.junit.Test
    public void testAnnularQueue() {
        try {
            ConcurrentSkipListSet<Schedule> list=new ConcurrentSkipListSet<>(new ScheduleComparator());
            for(int i=0;i<10;i++){
                Schedule schedule=new Schedule();
                schedule.setId(String.valueOf(i));
                schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(1).toInstant().toEpochMilli());
                list.add(schedule);
            }
        Schedule schedule=new Schedule();
        schedule.setId(String.valueOf(11));
        schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(-10).toInstant().toEpochMilli());
        list.add(schedule);
            System.out.print(list.size());
        }catch (Exception e){
            log.error("隐藏",e);
        }

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
