package liuche.opensource.easyTask.test.task;

import liuche.opensource.easyTask.core.Task;
import liuche.opensource.easyTask.test.dto.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CusTask1 extends Task implements Runnable {
    private static Logger log = LoggerFactory.getLogger(CusTask1.class);

    @Override
    public void run() {
        Map<String, String> param = getParam();
        log.info("任务1已执行!姓名:{} 生日:{} 年龄:{}", param.get("name"), param.get("birthday"), param.get("age"));

    }
}
