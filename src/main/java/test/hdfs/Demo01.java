package test.hdfs;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;

public class Demo01 {


    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();

        //1.创建配置文件
        //封装了客户端或者服务器的配置
        Configuration conf= new Configuration();
        //在之前Linux系统中配置Hadoop时的core—site.xml文件中，有相关默认设置，用来指定我们要操作的文件系统hdfs
        conf.set("fs.defaultFS","hdfs://192.168.30.176:9000");
        //指定用户名root
//        System.setProperty("HaDOOP_USER_NAME","root");
        //2.获取文件系统对象
        FileSystem fs = FileSystem.get(conf);
        System.out.println(fs.toString());

        fs.mkdirs(new Path("/test1210_plain"));


    }
}
