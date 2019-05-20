package liuche.opensource.easyTask.core;

public class Task implements Runnable{
    /**
     * 任务截止运行时间
     */
    private long endTimestamp;

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    @Override
    public void run() {

    }
}
