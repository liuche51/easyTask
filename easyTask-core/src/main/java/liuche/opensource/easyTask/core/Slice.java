package liuche.opensource.easyTask.core;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

class Slice {
    private ConcurrentSkipListMap<String,Schedule> list=new ConcurrentSkipListMap<String,Schedule>();;

    public ConcurrentSkipListMap<String,Schedule> getList() {
        return list;
    }

    public void setList(ConcurrentSkipListMap<String,Schedule> list) {
        this.list = list;
    }
}
