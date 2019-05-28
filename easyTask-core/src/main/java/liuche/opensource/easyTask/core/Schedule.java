package liuche.opensource.easyTask.core;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class Schedule implements Comparable {
    /**
     * 任务截止运行时间
     */
    private String id;
    private long endTimestamp;
    private TaskType taskType;
    private long period;
    private TimeUnit unit;
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

    public void save() {
        ScheduleDao.save(this);
    }
    public Schedule(){
        this.id=UUID.randomUUID().toString().replace("-", "");
    }
    public Schedule clone(){
        Schedule schedule=new Schedule();
        schedule.setId( UUID.randomUUID().toString().replace("-", ""));
        schedule.setEndTimestamp(this.endTimestamp);
        schedule.setPeriod(this.period);
        schedule.setTaskType(this.taskType);
        schedule.setUnit(this.unit);
        schedule.setRun(this.run);
        schedule.setTaskClassPath(this.taskClassPath);
        return schedule;
    }

    /**
     * 获取周期性任务下次执行时间。已当前时间为基准计算下次而不是上次截止执行时间
     * @param period
     * @param unit
     * @return
     * @throws Exception
     */
    public static long getTimeStampByTimeUnit(long period,TimeUnit unit) throws Exception {
        if(period==0)
            throw new Exception("period can not zero");
        switch (unit)
        {
            case DAYS:
                return LocalDateTime.now().plusDays(period).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            case HOURS:
                return LocalDateTime.now().plusHours(period).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            case MINUTES:
                return LocalDateTime.now().plusMinutes(period).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            case SECONDS:
                return LocalDateTime.now().plusSeconds(period).toInstant(ZoneOffset.of("+8")).toEpochMilli();
            case MILLISECONDS:
                return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()+period;
            case MICROSECONDS:
                return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()+period/1000;
            case NANOSECONDS:
                return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli()+period/1000000;
                default:throw new Exception("unSupport TimeUnit type");
        }
    }
    /**
     * 按任务截止触发时间顺序排序
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        Schedule schedule = (Schedule) o;
        if (this.endTimestamp >= schedule.endTimestamp)
            return 1;
        else
            return -1;

    }
}
