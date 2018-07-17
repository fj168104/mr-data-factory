package com.mr.proxy.database;

import com.mr.proxy.IPModel.DatabaseMessage;
import com.mr.proxy.IPModel.IPMessage;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zjxu on 18-4-12.
 */
@Slf4j
@Component
public class DataBaseBusinessOperation {
    @Autowired
    ProxyIPDatabaseConnectionParam proxyIPDatabaseConnectionParam;

    @Value("${proxydatabase.dbdriver}")
    private  String dbdriver;    //数据库驱动
    @Value("${proxydatabase.dburl}")
    private  String dburl;    //操作的数据库地址，端口及库名
    @Value("${proxydatabase.dbuser}")
    private  String dbuser;                       //数据库用户名
    @Value("${proxydatabase.dbpassword}")
    private  String dbpassword;
  /* String dbdriver="com.mysql.jdbc.Driver";    //数据库驱动
   String dburl="jdbc:mysql://47.100.7.81:3306/data_factory_xu?useUnicode=true&characterEncoding=utf-8&useSSL=false&autoReconnect=true&allowMultiQueries=true";    //操作的数据库地址，端口及库名
    String dbuser="root";                      //数据库用户名
    String dbpassword="Root@1234";   */        //数据库密码*D03F98CADA08CB3823AFFF3EED680782213A28FE
    //数据库添加功能
    public  void add(List<IPMessage> list) throws ClassNotFoundException {
          /*String dbdriver =  proxyIPDatabaseConnectionParam.getDbdriver();    //数据库驱动
          String dburl  = proxyIPDatabaseConnectionParam.getDbuser();    //操作的数据库地址，端口及库名
          String dbuser  = proxyIPDatabaseConnectionParam.getDbpassword();                      //数据库用户名
          String dbpassword  = proxyIPDatabaseConnectionParam.getDbpassword();           //数据库密码*/
        Class.forName(dbdriver);                         //加载数据库驱动

        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
            PreparedStatement statement = conn.prepareStatement("INSERT INTO " +
                    "ProxyPool (IPAddress, IPPort, serverAddress, IPType, IPSpeed)" +
                    " VALUES (?, ?, ?, ?, ?)")) {

            for(IPMessage ipMessage : list) {
                statement.setString(1, ipMessage.getIPAddress());
                statement.setString(2, ipMessage.getIPPort());
                statement.setString(3, ipMessage.getServerAddress());
                statement.setString(4, ipMessage.getIPType());
                statement.setString(5, ipMessage.getIPSpeed());

                statement.executeUpdate();
            }

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //数据库添加功能
    public  void addOne(IPMessage ipMessage) throws ClassNotFoundException {

        Class.forName(dbdriver);                         //加载数据库驱动

        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
            PreparedStatement statement = conn.prepareStatement("INSERT INTO " +
                    "ProxyPool (IPAddress, IPPort, serverAddress, IPType, IPSpeed)" +
                    " VALUES (?, ?, ?, ?, ?)")) {


        statement.setString(1, ipMessage.getIPAddress());
        statement.setString(2, ipMessage.getIPPort());
        statement.setString(3, ipMessage.getServerAddress());
        statement.setString(4, ipMessage.getIPType());
        statement.setString(5, ipMessage.getIPSpeed());

        statement.executeUpdate();

        statement.close();
        conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //删除数据库指定IP
    public  void deleteIP(int IPid) {

        String sql = "DELETE FROM ProxyPool WHERE id = " + IPid;
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
            Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);

            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //数据库表清除功能(id也一并清除)
    public  void delete() {

        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
            Statement statement = conn.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE ProxyPool");

            statement.close();
            conn.close();
        }
        catch(SQLException e){
                e.printStackTrace();
        }
    }

    //数据库查找功能
    public  List<DatabaseMessage> query() throws ClassNotFoundException {

        Class.forName(dbdriver);                         //加载数据库驱动
        List<DatabaseMessage> list = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
            Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ProxyPool");

            while(resultSet.next()){
                DatabaseMessage databaseMessage = new DatabaseMessage();

                databaseMessage.setId(resultSet.getString(1));
                databaseMessage.setIPAddress(resultSet.getString(2));
                databaseMessage.setIPPort(resultSet.getString(3));
                databaseMessage.setServerAddress(resultSet.getString(4));
                databaseMessage.setIPType(resultSet.getString(5));
                databaseMessage.setIPSpeed(resultSet.getString(6));

                list.add(databaseMessage);
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    //数据库查找功能
    public  int queryIP(String ipAddress) throws ClassNotFoundException {

        Class.forName(dbdriver);                         //加载数据库驱动
        int count =0;
        try(Connection conn = DriverManager.getConnection(dburl, dbuser, dbpassword);
            Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM ProxyPool where ipAddress='"+ipAddress+"'");

            while(resultSet.next()){
                count = resultSet.getInt(1);
            }

            resultSet.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
}
