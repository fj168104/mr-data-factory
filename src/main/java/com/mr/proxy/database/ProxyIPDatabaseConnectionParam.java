package com.mr.proxy.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Auther zjxu 18-05
 */
@Slf4j
@Data
@Component
@Configuration
//@ConfigurationProperties(prefix = "proxydatabase") //接收application.yml中的proxydatabase下面的属性
public class ProxyIPDatabaseConnectionParam{
    @Value("${proxydatabase.dbdriver}")
    private  String dbdriver;    //数据库驱动
    @Value("${proxydatabase.dburl}")
    private  String dburl;    //操作的数据库地址，端口及库名
    @Value("${proxydatabase.dbuser}")
    private  String dbuser;                       //数据库用户名
    @Value("${proxydatabase.dbpassword}")
    private  String dbpassword;

}
