package liuche.opensource.easyTask.core;

import java.util.Comparator;

public class ScheduleComparator implements Comparator<Schedule>
{
    public int compare(Schedule s1, Schedule s2)
    {
        if (s1.getEndTimestamp() >= s2.getEndTimestamp())
            return 1;
      /*  else  if (s1.getEndTimestamp()== s2.getEndTimestamp())
            return 0;*/
        else
            return -1;
    }
}