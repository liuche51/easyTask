package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
 class DbInit {
    private static Logger log = LoggerFactory.getLogger(AnnularQueue.class);
    public static boolean hasInit=false;//数据库是否已经初始化

     /**
      * 数据库初始化。需要避免多线程
      * @return
      */
    public static synchronized boolean init() {
        if(hasInit)
            return true;
        try {
            SQLlitePool.getInstance().init();
            boolean exist=ScheduleDao.existTable();
            if(exist)
                return true;
            String sql = "CREATE TABLE \"schedule\" (\n" +
                    "\"id\"  TEXT NOT NULL,\n" +
                    "\"class_path\"  TEXT,\n" +
                    "\"execute_time\"  INTEGER,\n" +
                    "\"task_type\"  TEXT,\n" +
                    "\"period\"  INTEGER,\n" +
                    "\"unit\"  TEXT,\n" +
                    "\"param\"  TEXT,\n" +
                    "\"create_time\"  TEXT,\n" +
                    "PRIMARY KEY (\"id\" ASC)\n" +
                    ");";
            SqliteHelper.executeUpdateForSync(sql);
            hasInit=true;
            log.debug("Sqlite 初始化完成。线程:{}",Thread.currentThread().getId());
            return true;
        } catch (Exception e) {
            log.error("easyTask.db init fail.",e);
            return false;
        }
    }
}
