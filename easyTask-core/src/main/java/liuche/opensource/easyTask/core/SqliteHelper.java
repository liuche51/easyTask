package liuche.opensource.easyTask.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sqlite帮助类，直接创建该类示例，并调用相应的借口即可对sqlite数据库进行操作
 *
 * 本类基于 sqlite jdbc v56
 *
 * @author haoqipeng
 */
public class SqliteHelper {
    final static Logger logger = LoggerFactory.getLogger(SqliteHelper.class);

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private String dbFilePath=this.getClass().getClassLoader().getResource("").getPath()+ "easyTask.db";

    public SqliteHelper() {
        try {
            this.dbFilePath = dbFilePath;
            connection = getConnection(dbFilePath);
        }catch (Exception e){
            logger.error("SqliteHelper get connection exception");
            destroyed();
        }

    }
    /**
     * 构造函数
     * @param dbFilePath sqlite db 文件路径
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public SqliteHelper(String dbFilePath) throws ClassNotFoundException, SQLException {
        this.dbFilePath = dbFilePath;
        connection = getConnection(dbFilePath);
    }

    /**
     * 获取数据库连接
     * @param dbFilePath db文件路径
     * @return 数据库连接
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection(String dbFilePath) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        return conn;
    }
    /**
     * 执行数据库更新sql语句
     * @param sql
     * @return 更新行数
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        try {
            int c = getStatement().executeUpdate(sql);
            return c;
        } finally {
            destroyed();
        }

    }

    /**
     * 执行多个sql更新语句
     * @param sqls
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeUpdate(String...sqls) throws SQLException, ClassNotFoundException {
        try {
            for (String sql : sqls) {
                getStatement().executeUpdate(sql);
            }
        } finally {
            destroyed();
        }
    }

    /**
     * 执行数据库更新 sql List
     * @param sqls sql列表
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void executeUpdate(List<String> sqls) throws SQLException, ClassNotFoundException {
        try {
            for (String sql : sqls) {
                getStatement().executeUpdate(sql);
            }
        } finally {
            destroyed();
        }
    }
    /**
     * 执行sql查询
     * @param sql sql select 语句
     * @return 查询结果
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ResultSet executeQuery(String sql) throws SQLException, ClassNotFoundException {
            resultSet = getStatement().executeQuery(sql);
            return resultSet;
    }
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (null == connection) connection = getConnection(dbFilePath);
        return connection;
    }

    private Statement getStatement() throws SQLException, ClassNotFoundException {
        if (null == statement) statement = getConnection().createStatement();
        return statement;
    }

    /**
     * 数据库资源关闭和释放
     */
    public void destroyed() {
        try {
            if (null != connection) {
                connection.close();
                connection = null;
            }

            if (null != statement) {
                statement.close();
                statement = null;
            }

            if (null != resultSet) {
                resultSet.close();
                resultSet = null;
            }
        } catch (SQLException e) {
            logger.error("Sqlite数据库关闭时异常", e);
        }
    }
}