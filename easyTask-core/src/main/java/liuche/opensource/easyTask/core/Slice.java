package liuche.opensource.easyTask.core;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

class Slice {
    private ConcurrentSkipListSet<Schedule> list=new ConcurrentSkipListSet<>(new ScheduleComparator());;

    public ConcurrentSkipListSet<Schedule> getList() {
        return list;
    }

    public void setList(ConcurrentSkipListSet<Schedule> list) {
        this.list = list;
    }
}
