package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

 class ScheduleDao {
    private static Logger log = LoggerFactory.getLogger(AnnularQueue.class);

    public static boolean existTable() {
        SqliteHelper helper = new SqliteHelper();
        try {
            ResultSet resultSet = helper.executeQuery("SELECT COUNT(*) FROM sqlite_master where type='table' and name='schedule';");
            while (resultSet.next()) {
                int count = resultSet.getInt(1);
                if (count > 0)
                    return true;
            }
        } catch (Exception e) {
            log.error("ScheduleDao.existTable 异常:{}", e);
        } finally {
            helper.destroyed();
        }
        return false;
    }

    public static boolean save(Schedule schedule) {
        try {
            if (!DbInit.hasInit)
                DbInit.init();
            String sql = "insert into schedule(id,class_path,execute_time,task_type,period,unit,create_time) values('"
                    + schedule.getId() + "','" + schedule.getTaskClassPath() + "'," + schedule.getEndTimestamp()
                    +",'"+schedule.getTaskType().name()+"',"+schedule.getPeriod()+",'"+(schedule.getUnit()==null?"":schedule.getUnit().name())
                    +"','"+ LocalDateTime.now().toLocalTime()+ "');";
            int count = SqliteHelper.executeUpdateForSync(sql);
            if (count > 0) {
                log.debug("任务:{} 已经持久化", schedule.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("ScheduleDao.save 异常:{}", e);
        }
        return false;
    }

    public static List<Schedule> selectAll() {
        List<Schedule> list = new LinkedList<>();
        SqliteHelper helper = new SqliteHelper();
        try {
            ResultSet resultSet = helper.executeQuery("SELECT * FROM schedule;");
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String classPath = resultSet.getString("class_path");
                Long executeTime = resultSet.getLong("execute_time");
                Integer taskType = resultSet.getInt("task_type");
                Long period = resultSet.getLong("period");
                String unit = resultSet.getString("unit");
                Schedule schedule = new Schedule();
                schedule.setId(id);
                schedule.setTaskClassPath(classPath);
                schedule.setEndTimestamp(executeTime);
                if ("PERIOD".equals(taskType))
                    schedule.setTaskType(TaskType.PERIOD);
                else if ("ONECE".equals(taskType))
                    schedule.setTaskType(TaskType.ONECE);
                schedule.setPeriod(period.longValue());
                switch (unit) {
                    case "DAYS":
                        schedule.setUnit(TimeUnit.DAYS);
                        break;
                    case "HOURS":
                        schedule.setUnit(TimeUnit.HOURS);
                        break;
                    case "MINUTES":
                        schedule.setUnit(TimeUnit.MINUTES);
                        break;
                    case "SECONDS":
                        schedule.setUnit(TimeUnit.SECONDS);
                        break;
                    case "MILLISECONDS":
                        schedule.setUnit(TimeUnit.MILLISECONDS);
                        break;
                    case "MICROSECONDS":
                        schedule.setUnit(TimeUnit.MICROSECONDS);
                        break;
                    case "NANOSECONDS":
                        schedule.setUnit(TimeUnit.NANOSECONDS);
                        break;
                    default:
                        break;

                }

                list.add(schedule);
            }
        } catch (Exception e) {
            log.error("ScheduleDao.selectAll 异常:{}", e);
        } finally {
            helper.destroyed();
        }
        return list;
    }

    public static boolean delete(String id) {
        try {
            String sql = "delete FROM schedule where id='" + id + "';";
            int count = SqliteHelper.executeUpdateForSync(sql);
            if (count > 0)
                log.debug("任务:{} 已经删除", id);
        } catch (Exception e) {
            log.error("ScheduleDao.delete 异常:{}", e);
            return false;
        }
        return true;
    }
}
