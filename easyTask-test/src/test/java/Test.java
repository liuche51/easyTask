import liuche.opensource.easyTask.core.AnnularQueue;
import liuche.opensource.easyTask.core.Task;
import liuche.opensource.easyTask.test.task.CusTask1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.tree.Tree;

import java.time.LocalDateTime;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);
    private static AnnularQueue annularQueue=AnnularQueue.getInstance();
    @org.junit.Test
    public void testAnnularQueue() {

    }

    @org.junit.Test
    public void test1(){

    }
    @org.junit.Test
    public void test2() {
        AnnularQueue.getInstance().start();
        for(int i=0;i<10;i++){
            CusTask1 task1 = new CusTask1();
            task1.setExcuteTime(LocalDateTime.now().minusSeconds(-10));
            AnnularQueue.submit(task1);
            try {
                Thread.sleep(500l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AnnularQueue a=AnnularQueue.getInstance();
        System.console().readLine();
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
