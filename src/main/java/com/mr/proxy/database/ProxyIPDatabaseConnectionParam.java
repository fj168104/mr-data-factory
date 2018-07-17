package com.mr.proxy.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther zjxu 18-05
 */

@Slf4j
//@ConfigurationProperties(prefix = "proxydatabase") //接收application.yml中的proxydatabase下面的属性
@Component
public class ProxyIPDatabaseConnectionParam{
    @Value("${proxydatabase.dbdriver}")
    private  String dbdriver;    //数据库驱动
    @Value("${proxydatabase.dburl}")
    private  String dburl;    //操作的数据库地址，端口及库名
    @Value("${proxydatabase.dbuser}")
    private  String dbuser;                       //数据库用户名
    @Value("${proxydatabase.dbpassword}")
    private  String dbpassword;

    public ProxyIPDatabaseConnectionParam(){
        this.dbdriver = dbdriver;
        this.dburl = dburl;
        this.dbuser = dbuser;
        this.dbpassword = dbpassword;
    }

    public String getDbdriver() {
        return dbdriver;
    }

    public void setDbdriver(String dbdriver) {
        this.dbdriver = dbdriver;
    }

    public String getDburl() {
        return dburl;
    }

    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

    public String getDbuser() {
        return dbuser;
    }

    public void setDbuser(String dbuser) {
        this.dbuser = dbuser;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public void setDbpassword(String dbpassword) {
        this.dbpassword = dbpassword;
    }

    public static Logger getLog() {
        return log;
    }



}
