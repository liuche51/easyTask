package liuche.opensource.easyTask.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class ScheduleExt {
    private String taskClassPath;
    private String oldId;
    private AtomicInteger flag=new AtomicInteger(0);
    public String getTaskClassPath() {
        return taskClassPath;
    }

    public void setTaskClassPath(String taskClassPath) {
        this.taskClassPath = taskClassPath;
    }  public String getOldId() {
        return oldId;
    }

    public void setOldId(String oldId) {
        this.oldId = oldId;
    }

    public AtomicInteger getFlag() {
        return flag;
    }

    public void setFlag(AtomicInteger flag) {
        this.flag = flag;
    }
}
