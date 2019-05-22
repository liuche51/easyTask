package liuche.opensource.easyTask.core;

import java.time.LocalDateTime;
import java.util.Date;

class Schedule {
    /**
     * 任务截止运行时间
     */
    private String id;
    private long endTimestamp;
    private Runnable run;
    private String taskClassPath;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Runnable getRun() {
        return run;
    }

    public void setRun(Runnable run) {
        this.run = run;
    }

    public String getTaskClassPath() {
        return taskClassPath;
    }

    public void setTaskClassPath(String taskClassPath) {
        this.taskClassPath = taskClassPath;
    }

    public void save() {
        ScheduleDao.save(this);
    }
}
