package liuche.opensource.easyTask.core;

import java.time.LocalDateTime;

public class Task {
    private String id;
    private LocalDateTime excuteTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(LocalDateTime excuteTime) {
        this.excuteTime = excuteTime;
    }
}
