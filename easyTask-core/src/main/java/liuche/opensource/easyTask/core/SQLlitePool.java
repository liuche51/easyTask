package liuche.opensource.easyTask.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedDeque;

class SQLlitePool {
    final static Logger logger = LoggerFactory.getLogger(SqliteHelper.class);
    private static String driver = "org.sqlite.JDBC";
    /**
     * 任务持久化保存路径。可以自定义
     */
    public static String dbFilePath = null;
    private static ConcurrentLinkedDeque<Connection> pool;//支持并发
    public static int poolSize = Runtime.getRuntime().availableProcessors() * 2;
    private static SQLlitePool singleton = null;

    public static SQLlitePool getInstance() {
        if (singleton == null) {
            synchronized (SQLlitePool.class) {
                if (singleton == null) {
                    singleton = new SQLlitePool();
                }
            }
        }
        return singleton;
    }

    private SQLlitePool() {
    }

    public void init() {
        try {
            if (dbFilePath == null || dbFilePath.equals("")) {
                try {
                    //非jar包时，得到classes目录。jar包时会报异常
                    String path1 = this.getClass().getClassLoader().getResource("").getPath();
                    dbFilePath = path1 + "easyTask.db";
                } catch (Exception e) {

                }
            }
            if (dbFilePath == null || dbFilePath.equals("")) {
                try {
                    //非jar包时，得到当前类所属jar包的classes目录。jar包时会得到所属运行jar包的物理路径（含.jar部分）
                    String path2 = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
                    dbFilePath = path2 + "-easyTask.db";

                } catch (Exception e) {
                }
            }
            logger.debug("db-path:{}", dbFilePath);
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.error("sqlite ClassNotFoundException fail", e);
        }
        //避免重复创建
        if (pool != null && pool.size() > 0)
            return;
        pool = new ConcurrentLinkedDeque<Connection>();
        for (int i = 0; i < poolSize; i++) {
            try {
                Connection con = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
                pool.addLast(con);
            } catch (SQLException e) {
                logger.error("sqlite ClassNotFoundException fail", e);
            }
        }
    }

    public Connection getConnection() {
        if (pool.size() > 0 && !(pool.getLast() == null)) {
            Connection conn = pool.poll();
            return conn;
        }
        try {
            Class.forName(driver);
            Connection con = DriverManager.getConnection(dbFilePath);
            return con;
        } catch (Exception e) {
            logger.error("sqlite connection create fail", e);
        }
        return null;
    }

    public boolean freeConnection(Connection conn) {
        if (pool.size() < SQLlitePool.poolSize && pool.size() > 0) {
            pool.addLast(conn);
            return true;
        } else {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Sqlite connection close exception", e);
            }
        }
        return false;
    }
}

