package com.github.liuche51.easyTask.core;

import java.util.Comparator;

public class ScheduleComparator implements Comparator<com.github.liuche51.easyTask.core.Schedule>
{
    public int compare(com.github.liuche51.easyTask.core.Schedule s1, com.github.liuche51.easyTask.core.Schedule s2)
    {

        if (s1.getEndTimestamp() >= s2.getEndTimestamp())
            return 1;
       /* else  if (s1.getEndTimestamp()== s2.getEndTimestamp())
            return 0;*/
        else
            return -1;
    }
}