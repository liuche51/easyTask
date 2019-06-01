package com.github.liuche51.easyTask.core;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class ScheduleExt {
    private String id;
    private String taskClassPath;
    private volatile String oldId;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
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
}
