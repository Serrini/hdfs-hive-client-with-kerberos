package com.hive.client;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 简单的jdbc连接hive实例（已开启kerberos服务)
 */

public class HiveClient {
    /**
     * 用于连接Hive所需的一些参数设置 driverName:用于连接hive的JDBC驱动名 When connecting to
     * HiveServer2 with Kerberos authentication, the URL format is:
     * jdbc:hive2://<host>:<port>/<db>;principal=
     * <Server_Principal_of_HiveServer2>
     */
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    // 注意：这里的principal是固定不变的，其指的hive服务所对应的principal,而不是用户所对应的principal
    private static String url = "jdbc:hive2://192.168.30.176:10002/default;principal=root/hadoop00@EXAMPLE.COM";
    private static String sql = "";
    private static ResultSet res;
    private  Statement stmt;
    private  Connection conn;


    @Before
    public void before() throws URISyntaxException, IOException, InterruptedException, ClassNotFoundException, SQLException {
        BasicConfigurator.configure();

        String krb5Conf = "/Users/apple/IdeaProjects/test011/conf/krb5.conf";
//        String KEY_TAB_PATH = "/Users/apple/IdeaProjects/test011/keytab/root.keytab";
        String KEY_TAB_PATH = "/Users/apple/IdeaProjects/test011/keytab/root_new.keytab";

        /** 使用Hadoop安全登录 **/
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        System.setProperty("java.security.krb5.conf", krb5Conf);
        System.setProperty("sun.security.krb5.debug", String.valueOf(true));
        conf.addResource(new Path("/Users/apple/IdeaProjects/test011/conf/hdfs-site.xml"));
        conf.addResource(new Path("/Users/apple/IdeaProjects/test011/conf/core-site.xml"));
        conf.addResource(new Path("/Users/apple/IdeaProjects/test011/conf/yarn-site.xml"));
        conf.addResource(new Path("/Users/apple/IdeaProjects/test011/conf/hive-site.xml"));

        conf.setBoolean("hadoop.security.authorization", true);
        conf.set("hadoop.security.authentication", "kerberos");


        try {
            UserGroupInformation.setConfiguration(conf);
            UserGroupInformation.loginUserFromKeytab("root/hadoop00@EXAMPLE.COM", KEY_TAB_PATH);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Class.forName(driverName);
        conn = DriverManager.getConnection(url);

        stmt = conn.createStatement();

    }

    @After
    public void after() throws IOException, SQLException {
        conn.close();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!END!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    /**
     * 查看数据库
     *
     * @param
     * @return
     */
    @Test
    public void show_databases() throws IOException{
        sql = "SHOW DATABASES";
        System.out.println("Running:" + sql);
        try {
            ResultSet res = stmt.executeQuery(sql);
            System.out.println("执行“+sql+运行结果:");
            while (res.next()) {
                System.out.println(res.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看数据库下所有的表
     *
     * @param
     */
    @Test
    public void show_tables() throws IOException {
        sql = "SHOW TABLES";
        System.out.println("Running:" + sql);
        try {
            ResultSet res = stmt.executeQuery(sql);
            System.out.println("执行“+sql+运行结果:");
            while (res.next()) {
                System.out.println(res.getString(1));
            }
//            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        return false;
    }

    /**
     * 更新
     *
     * @param
     */
    @Test
    public void insert_into_tables() throws IOException {
        sql = "INSERT INTO `table_test_one` VALUES ('1', '201601', '张三', '0001', '数学', '98');" +
                "INSERT INTO `table_test_one` VALUES ('2', '201601', '张三', '0002', '语文', '66');" +
                "INSERT INTO `table_test_one` VALUES ('3', '201602', '李四', '0001', '数学', '60');" +
                "INSERT INTO `table_test_one` VALUES ('4', '201602', '李四', '0003', '英语', '78');" +
                "INSERT INTO `table_test_one` VALUES ('5', '201603', '王五', '0001', '数学', '99');" +
                "INSERT INTO `table_test_one` VALUES ('6', '201603', '王五', '0002', '语文', '99');" +
                "INSERT INTO `table_test_one` VALUES ('7', '201603', '王五', '0003', '英语', '98');";
        System.out.println("Running:" + sql);
        try {
            int res = stmt.executeUpdate(sql);
            System.out.println("执行“+sql+运行结果:");
//            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        return false;
    }

    /**
     * 获取表的描述信息
     *
     * @param
     * @param
     * @return
     */
    @Test
    public void describ_table() throws IOException{
        String tableName = "test_100m";
        sql = "DESCRIBE " + tableName;
        try {
            res = stmt.executeQuery(sql);
            System.out.print(tableName + "描述信息:");
            while (res.next()) {
                System.out.println(res.getString(1) + "\t" + res.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表
     *
     * @param
     * @param
     * @return
     */
    @Test
    public void drop_table() throws IOException{
        String tableName = "test_100m";
        sql = "DROP TABLE IF EXISTS " + tableName;
        System.out.println("Running:" + sql);
        try {
            stmt.execute(sql);
            System.out.println(tableName + "删除成功");
        } catch (SQLException e) {
            System.out.println(tableName + "删除失败");
            e.printStackTrace();
        }
    }

    /**
     * 查看表数据
     *
     * @param
     * @return
     */
    @Test
    public void queryData() throws IOException{
        String tableName = "table_test_one";
        sql = "SELECT * FROM " + tableName + " LIMIT 20";
        System.out.println("Running:" + sql);
        try {
            res = stmt.executeQuery(sql);
            System.out.println("执行“+sql+运行结果:");
            while (res.next()) {
                System.out.println(res.getString(1) + "," + res.getString(2) + "," + res.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建表
     *
     * @param
     * @return
     */
    @Test
    public void createTable() throws IOException{
//        sql = "CREATE TABLE test_1m_test2 AS SELECT * FROM test_1m_test"; //  为了方便直接复制另一张表数据来创建表
//        sql = "CREATE TABLE Persons" +
//                "(" +
//                "Id_P int," +
//                "LastName varchar(255)," +
//                "FirstName varchar(255)," +
//                "Address varchar(255)," +
//                "City varchar(255)" +
//                ")";

        sql = "CREATE TABLE `table_test_one` (" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT," +
                "  `student_no` varchar(10) NOT NULL," +
                "  `student_name` varchar(10) NOT NULL," +
                "  `subject_no` varchar(10) NOT NULL," +
                "  `subject_name` varchar(10) NOT NULL," +
                "  `score` int(11) NOT NULL," +
                "  PRIMARY KEY (`id`)" +
                ") ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;";
        System.out.println("Running:" + sql);
        try {
            boolean execute = stmt.execute(sql);
            System.out.println("执行结果 ：" + execute);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建库
     *
     * @param
     * @return
     */
    @Test
    public void createDatabase() throws IOException{
        sql = "CREATE DATABASE database_test_sl_"; //  为了方便直接复制另一张表数据来创建表
        System.out.println("Running:" + sql);
        try {
            boolean execute = stmt.execute(sql);
            System.out.println("执行结果 ：" + execute);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
/*
        BasicConfigurator.configure();

        try {
            Connection conn = get_conn();
//            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            // 创建的表名
            String tableName = "test_100m";
            show_databases(stmt);
            show_tables(stmt);
            // describ_table(stmt, tableName);
            */
/** 删除表 **//*

            // drop_table(stmt, tableName);
            // show_tables(stmt);
            // queryData(stmt, tableName);
            createTable(stmt, tableName);
            show_tables(stmt);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("!!!!!!END!!!!!!!!");
        }
*/
    }
}
