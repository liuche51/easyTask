import com.github.liuche51.easyTask.core.AnnularQueue;
import com.github.liuche51.easyTask.core.Schedule;
import com.github.liuche51.easyTask.core.ScheduleComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
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
                schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(1).toInstant().toEpochMilli());
                list.add(schedule);
            }
        Schedule schedule=new Schedule();
        schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(-10).toInstant().toEpochMilli());
        list.add(schedule);
        list.forEach(x->{
        });
            //System.out.print(list.size());
        }catch (Exception e){
            log.error("隐藏",e);
        }

    }

    @org.junit.Test
    public void test1(){
        TreeSet<Schedule> list=new TreeSet<>(new ScheduleComparator());
        for(int i=0;i<10;i++){
            Schedule schedule=new Schedule();
            schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(1).toInstant().toEpochMilli());
            list.add(schedule);
        }
        Schedule schedule=new Schedule();
        schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(-10).toInstant().toEpochMilli());
        list.add(schedule);
        list.forEach(x->{
        });
    }
    @org.junit.Test
    public void test2() {
        ConcurrentSkipListMap<String,Schedule> list=new ConcurrentSkipListMap<>();
        for(int i=0;i<10;i++){
            Schedule schedule=new Schedule();
            schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(1).toInstant().toEpochMilli());
        }
        Schedule schedule=new Schedule();
        schedule.setEndTimestamp(ZonedDateTime.now().plusSeconds(-10).toInstant().toEpochMilli());
        for (Map.Entry<String,Schedule> entry:list.entrySet()){
            System.out.println(entry.getKey());
        }
    }
    @org.junit.Test
    public void test3() {

    }
    @org.junit.Test
    public void test4() {

    }
}
