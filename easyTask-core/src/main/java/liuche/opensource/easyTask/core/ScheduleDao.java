package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class ScheduleDao {
    private static Logger log = LoggerFactory.getLogger(AnnularQueue.class);
    public static boolean existTable(){
            SqliteHelper helper=new SqliteHelper();
            try {
                ResultSet resultSet = helper.executeQuery("SELECT COUNT(*) FROM sqlite_master where type='table' and name='schedule';");
                while (resultSet.next()) {
                    int count=resultSet.getInt(1);
                    if(count>0)
                        return true;
                }
            }catch (Exception e){
                log.error("ScheduleDao.existTable 异常:{}",e);
            }finally {
                helper.destroyed();
            }
            return false;
    }
    public static boolean save(Schedule schedule){
        SqliteHelper helper=new SqliteHelper();
        try {
            String sql="insert into schedule(id,class_path,execute_time) values('"+schedule.getId()+"','"+schedule.getTaskClassPath()+"',"+schedule.getEndTimestamp()+");";
            int count = helper.executeUpdate(sql);
           if(count>0)
           {
               log.debug("任务:{} 已经持久化",schedule.getId());
               return true;
           }
        }catch (Exception e){
            log.error("ScheduleDao.save 异常:{}",e);
        }finally {
            helper.destroyed();
        }
        return false;
    }
    public static List<Schedule> selectAll(){
        List<Schedule> list=new LinkedList<>();
        SqliteHelper helper=new SqliteHelper();
        try {
            ResultSet resultSet = helper.executeQuery("SELECT * FROM schedule;");
            while (resultSet.next()) {
                String id=resultSet.getString("id");
                String classPath=resultSet.getString("class_path");
                Long executeTime=resultSet.getLong("execute_time");
                Schedule schedule=new Schedule();
                schedule.setId(id);
                schedule.setTaskClassPath(classPath);
                schedule.setEndTimestamp(executeTime);
                list.add(schedule);
            }
        }catch (Exception e){
            log.error("ScheduleDao.selectAll 异常:{}",e);
        }finally {
            helper.destroyed();
        }
        return list;
    }
    public static boolean delete(String id){
        SqliteHelper helper=new SqliteHelper();
        try {
            String sql="delete FROM schedule where id='"+id+"';";
            int count = helper.executeUpdate(sql);
            if(count>0)
                log.debug("任务:{} 已经删除",id);
        }catch (Exception e){
            log.error("ScheduleDao.delete 异常:{}",e);
            return false;
        }finally {
            helper.destroyed();
        }
        return true;
    }
}
