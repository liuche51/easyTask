package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.tree.Tree;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    @org.junit.Test
    public void testAnnularQueue(){
        Schedule task1 = new Schedule();
        task1.setTaskType(TaskType.PERIOD);
        task1.setEndTimestamp(11111111111111111l);
        task1.setTaskClassPath("sdfdsfdsfdsf");
        ScheduleDao.save(task1);
    }
    @org.junit.Test
    public void test1(){
        TreeSet<Schedule> list=new TreeSet<Schedule>();
        for(int i=0;i<10;i++){
            Schedule task1 = new Schedule();
            task1.setId(String.valueOf(i));
            task1.setEndTimestamp(LocalDateTime.now().minusSeconds(-i).toInstant(ZoneOffset.of("+8")).toEpochMilli());
            list.add(task1);
        }
        Schedule s=list.last();
        Schedule s2=s.clone();
        s2.setId("10");
        s2.setEndTimestamp(s2.getEndTimestamp()-50000);
        list.add(s2);
        System.console().readLine();
    }
}
