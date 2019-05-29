package liuche.opensource.easyTask.core;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Task{
    private String id;
    private LocalDateTime executeTime;
    private TaskType taskType=TaskType.ONECE;
    private boolean immediateExecute=false;
    private long period;
    private TimeUnit unit;
    /**
     * customer myself param
     * notice:key or value can not contain char "#;" and "&;" because it is a special char
     */
    private Map<String,String> param;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(LocalDateTime executeTime) {
        this.executeTime = executeTime;
    }

    public boolean isImmediateExecute() {
        return immediateExecute;
    }

    public void setImmediateExecute(boolean immediateExecute) {
        this.immediateExecute = immediateExecute;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public Map<String,String> getParam() {
        return param;
    }

    public void setParam(Map<String,String> param) {
        this.param = param;
    }

}
