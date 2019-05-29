package liuche.opensource.easyTask.test.task;

import liuche.opensource.easyTask.core.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CusTask1 extends Schedule implements Runnable {
    private static Logger log = LoggerFactory.getLogger(CusTask1.class);

    @Override
    public void run() {
        Map<String, String> param = getParam();
        log.info("任务1已执行!姓名:{} 生日:{} 年龄:{}", param.get("name"), param.get("birthday"), param.get("age"));

    }
}
