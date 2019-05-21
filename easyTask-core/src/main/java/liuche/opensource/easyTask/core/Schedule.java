package liuche.opensource.easyTask.core;

import java.time.LocalDateTime;
import java.util.Date;

public class Schedule {
    /**
     * 任务截止运行时间
     */
    private long endTimestamp;
    private LocalDateTime excuteTime;
    private Runnable task;

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public LocalDateTime getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(LocalDateTime excuteTime) {
        this.excuteTime = excuteTime;
    }

    public Runnable getTask() {
        return task;
    }

    public void setTask(Runnable task) {
        this.task = task;
    }
}
