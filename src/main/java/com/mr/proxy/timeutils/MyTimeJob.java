package com.mr.proxy.timeutils;

import com.mr.proxy.IPModel.DatabaseMessage;
import com.mr.proxy.IPModel.IPMessage;
import com.mr.proxy.database.DataBaseBusinessOperation;
import com.mr.proxy.htmlparse.URLFecter;
import com.mr.proxy.ipfilter.IPFilter;
import com.mr.proxy.ipfilter.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paranoid on 17-4-13.
 */
@Slf4j
public class MyTimeJob implements Job {
    @Autowired
    IPUtils ipUtils;
    DataBaseBusinessOperation dataBaseBusinessOperation;
    public void execute(JobExecutionContext argv) throws JobExecutionException {
        List<String> Urls = new ArrayList<>();
        List<DatabaseMessage> databaseMessages = new ArrayList<>();
        List<IPMessage> list = new ArrayList<>();
        List<IPMessage> ipMessages = new ArrayList<>();
        String url = "http://www.xicidaili.com/nn/1";
        String IPAddress;
        String IPPort;
        int k, j;

        //首先使用本机ip进行爬取
        try {
            list = URLFecter.urlParse(url, list);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //对得到的IP进行筛选，选取链接速度前200名的
        list = IPFilter.Filter(list);
        log.info("-----------====+++++++"+list.toString());
        //构造种子Url
        for (int i = 1; i <= 20; i++) {
            Urls.add("http://www.xicidaili.com/nn/" + i);
        }

        //得到所需要的数据
        for (k = 0, j = 0; j < Urls.size(); k++) {
            url = Urls.get(j);

            IPAddress = list.get(k).getIPAddress();
            IPPort = list.get(k).getIPPort();
            //每次爬取前的大小
            int preIPMessSize = ipMessages.size();
            try {
                ipMessages = URLFecter.urlParse(url, IPAddress, IPPort, ipMessages);
                //每次爬取后的大小
                int lastIPMessSize = ipMessages.size();
                if(preIPMessSize != lastIPMessSize){
                    j++;
                }

                //对IP进行轮寻调用
                if (k >= list.size()) {
                    k = 0;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //对得到的IP进行筛选，选取链接速度前100名的
        ipMessages = IPFilter.Filter(ipMessages);

        //对ip进行测试，不可用的从数组中删除
        ipMessages = ipUtils.IPIsable(ipMessages);

        for(IPMessage ipMessage : ipMessages){
            log.info(ipMessage.getIPAddress());
            log.info(ipMessage.getIPPort());
            log.info(ipMessage.getServerAddress());
            log.info(ipMessage.getIPType());
            log.info(ipMessage.getIPSpeed());
        }

        //将得到的IP存储在数据库中(每次先清空数据库)
        try {
            dataBaseBusinessOperation.delete();
            dataBaseBusinessOperation.add(ipMessages);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //从数据库中将IP取到
        try {
            databaseMessages = dataBaseBusinessOperation.query();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (DatabaseMessage databaseMessage: databaseMessages) {
            log.info(databaseMessage.getId());
            log.info(databaseMessage.getIPAddress());
            log.info(databaseMessage.getIPPort());
            log.info(databaseMessage.getServerAddress());
            log.info(databaseMessage.getIPType());
            log.info(databaseMessage.getIPSpeed());
        }
    }
}
