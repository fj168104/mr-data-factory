package com.mr.proxy.ipfilter;

import com.mr.proxy.IPModel.IPMessage;
import com.mr.proxy.database.DataBaseBusinessOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @Auther 18-4-10.
 * 测试此Ip是否有效
 */
@Slf4j
public class IPUtils {

    DataBaseBusinessOperation dataBaseBusinessOperation = new DataBaseBusinessOperation();

    public  List<IPMessage> IPIsable(List<IPMessage> ipMessages) {
        String ip;
        String port;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        for(int i = 0; i < ipMessages.size(); i++) {
            ip = ipMessages.get(i).getIPAddress();
            port = ipMessages.get(i).getIPPort();

            HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
            RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(3000).
                    setSocketTimeout(3000).build();
            HttpGet httpGet = new HttpGet("https://www.baidu.com");
            httpGet.setConfig(config);

            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                    "q=0.9,image/webp,*/*;q=0.8");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
            httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit" +
                    "/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

            try {
                response = httpClient.execute(httpGet);
                try {
                    if(dataBaseBusinessOperation.queryIP(ip)==0){
                        dataBaseBusinessOperation.addOne(ipMessages.get(i));
                    }
                }catch (Exception e){
                    log.warn("数据入口异常···"+e.getMessage());
                }

                log.info("这是个可用的代理" + ipMessages.get(i).getIPAddress() + ": " + ipMessages.get(i).getIPPort());
            } catch (IOException e) {
                log.warn("不可用代理已删除" + ipMessages.get(i).getIPAddress() + ": " + ipMessages.get(i).getIPPort());
                ipMessages.remove(ipMessages.get(i));
                i--;
            }
        }

        try {
            httpClient.close();
            response.close();
        } catch (IOException e) {
            log.warn("验证IP的有效性异常："+e.getMessage());
        }

        return ipMessages;
    }
}
