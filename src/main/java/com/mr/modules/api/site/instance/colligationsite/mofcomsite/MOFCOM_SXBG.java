package com.mr.modules.api.site.instance.colligationsite.mofcomsite;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.mr.common.OCRUtil;
import com.mr.modules.api.model.ScrapyData;
import com.mr.modules.api.site.SiteTaskExtend_CollgationSite;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.mr.modules.api.site.instance.colligationsite.util.MD5Util;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 站点：国家商务部网站
 * url：http://www.ipraction.gov.cn/article/xxgk/shxy/sxbg/
 * 主题：失信曝光
 * 属性：企业名称 检查产品  检查结果 检查机关  发布日期
 * 提取：TODO 唯一标识（unique_key）、来源、地址、标题、公司名称 发布日期  检查机关,文号
 * 注：目前没有存储标题的属性
 */
@Slf4j
@Component("mofcom_sxbg")
@Scope("prototype")
public class MOFCOM_SXBG extends SiteTaskExtend_CollgationSite{
    @Override
    protected String execute() throws Throwable {
        webContext();
       return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }

    //源文件存储的跟路径
    String fileBasePath = OCRUtil.DOWNLOAD_DIR+ File.separator+"mofcomsite"+File.separator;
    //数据来源
    String source = "国家商务部网站";
    //要提取的字段
    String fields = "source,subject,url,enterprise_name,publish_date/punishDate,judge_no,title";
    //唯一标识 注：一般为，title/JubgeNo+enterpriseName+publishdate/punishdate
    String unique_key = "";
    public void webContext(){
        String baseUrl = "http://www.ipraction.gov.cn";
        String urlNext = "http://www.ipraction.gov.cn/article/xxgk/shxy/sxbg/?2";
        //String urln = "http://www.ipraction.gov.cn/article/xxgk/shxy/sxbg/?"+n;
        //第一页
        try {
            htmlParse(baseUrl,urlNext);
        } catch (Throwable throwable) {
            log.error("请查阅错误信息···"+throwable.getMessage());
        }
        //第2页  到 第351页
        for(int n=2;n<352;n++){
            String urln = "http://www.ipraction.gov.cn/article/xxgk/shxy/sxbg/?"+n;
            try {
                htmlParse(baseUrl,urln);
            } catch (Throwable throwable) {
                log.error("请查阅错误信息···"+throwable.getMessage());
            }
        }



    }
    @Async(value="asyncServiceExecutor")
    public void htmlParse(String baseUrl ,String resultUrl)throws Throwable{
        log.info("******************************************************当前线程为："+Thread.currentThread().getName());
        WebClient webClient = createWebClient("","");
        try {
            HtmlPage htmlPage = webClient.getPage(resultUrl);
            List<HtmlElement> htmlElementList = htmlPage.getByXPath("//body//section[@class='blank']//div[@class='column_01']//section[@class='clearfix mt20p messageCon']//article[@class='mainL fl']//div[@class='newsList']//ul[@class='newsList01']//li");
            for(HtmlElement htmlElement :htmlElementList){

                //创建对象
                ScrapyData scrapyData = new ScrapyData();

                if(!htmlElement.getAttribute("class").equals("listline")){
                    HtmlElement htmlElementLi = htmlElement;
                    HtmlElement htmlElementA = htmlElementLi.getElementsByTagName("a").get(0);
                    //目标地址Url
                    String urlDetail = baseUrl+htmlElementA.getAttribute("href");

                    //目标地址 拼上Url MD5加密作为文件存储路径
                    String filePath = fileBasePath+MD5Util.encode(urlDetail);
                    scrapyData.setUrl(urlDetail);
                    scrapyData.setSource(source);
                    scrapyData.setHashKey(filePath);
                    scrapyData.setCreatedAt(new Date());
                    //目标地址主题
                    String urlTitle =  htmlElementA.getAttribute("title");
                    //文章发布时间
                    String publishDate = htmlElementLi.getElementsByTagName("span").get(0).asText();

                    //创建新的浏览器，访问详情界面信息
                    WebClient webClientDetail = createWebClient("","");
                    HtmlPage htmlPageDetail = webClientDetail.getPage(urlDetail);
                    try {
                        saveFile(htmlPageDetail,urlTitle+".html",filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //获取目标HTML 的对应标签模块
                    DomElement imageSrc =  htmlPageDetail.getElementById("zoom");
                    String textHtml = imageSrc.asXml();
                    scrapyData.setHtml(textHtml);
                    scrapyData.setText("发布主题："+urlTitle+"\n发布时间："+publishDate+"\n"+imageSrc.asText());
                    if(imageSrc!=null){

                        DomNodeList<HtmlElement> imageSrcImg =  imageSrc.getElementsByTagName("img");
                        DomNodeList<HtmlElement> imageSrcA =  imageSrc.getElementsByTagName("a");
                        if(imageSrcImg.size()>0&&imageSrcA.size()<1){//图片标签
                            for(int i=0;i<imageSrcA.size();i++){
                                HtmlElement imageSrcUrlDD =  imageSrcImg.get(i);
                                String imageSrcUrl = imageSrcUrlDD.getAttribute("src");
                                String file = imageSrcUrlDD.getAttribute("title");
                                Page page = webClientDetail.getPage(imageSrcUrl);
                                try {
                                    String[] strFile = file.split("\\.");
                                    String flieName = urlTitle+"."+strFile[1];
                                    scrapyData.setAttachmentType(strFile[1]);
                                    saveFile(page,flieName,filePath);
                                } catch (Exception e) {
                                    log.error("图片附件下载有异常·····"+e.getMessage());
                                }finally {
                                    webClientDetail.close();
                                }
                            }

                        }else if (imageSrcA.size()>0){//非图片标签
                            for(int i=0;i<imageSrcA.size();i++){
                                HtmlAnchor imageSrcUrlAA =  (HtmlAnchor) imageSrcA.get(i);
                                String file = imageSrcUrlAA.asText();
                                Page page = imageSrcUrlAA.click();
                                try {
                                    String[] strFile = file.split(".");
                                    String flieName = urlTitle+"."+strFile[1];
                                    scrapyData.setAttachmentType(strFile[1]);
                                    saveFile(page,flieName,filePath);
                                } catch (Exception e) {
                                    log.error("非图片附件下载有异常·····"+e.getMessage());
                                }finally {
                                    webClientDetail.close();
                                }
                            }

                        }else {//TODO 其他情况
                            continue;
                        }

                    }

                }
                //入库
                saveScrapyDataOne(scrapyData,false);
            }
        } catch (IOException e) {
            log.error("发生IO处理异常，请检查···"+e.getMessage());
        }finally {
            webClient.close();
        }
    }
}
