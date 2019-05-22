package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

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
}
