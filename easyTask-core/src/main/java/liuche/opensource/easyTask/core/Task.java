package liuche.opensource.easyTask.core;

import java.time.LocalDateTime;

public class Task {
    private LocalDateTime excuteTime;
    private Runnable run;

    public LocalDateTime getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(LocalDateTime excuteTime) {
        this.excuteTime = excuteTime;
    }

    public Runnable getRun() {
        return run;
    }

    public void setRun(Runnable run) {
        this.run = run;
    }
}
