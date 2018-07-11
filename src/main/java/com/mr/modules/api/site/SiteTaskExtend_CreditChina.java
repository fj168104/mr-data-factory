package com.mr.modules.api.site;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.common.OCRUtil;
import com.mr.common.util.CrawlerUtil;

import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.mapper.DiscreditBlacklistMapper;
import com.mr.modules.api.mapper.ProxypoolMapper;
import com.mr.modules.api.model.AdminPunish;
import com.mr.modules.api.model.DiscreditBlacklist;
import com.mr.modules.api.model.Proxypool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * @auterh 2018-06
 */
@Slf4j
public class SiteTaskExtend_CreditChina extends SiteTaskExtend{
    @Autowired
    protected ProxypoolMapper proxypoolMapper;
    @Autowired
    protected AdminPunishMapper adminPunishMapper;
    @Autowired
    protected DiscreditBlacklistMapper discreditBlacklistMapper;
    @Override
    protected String execute() throws Throwable {
        return null;
    }

    @Override
    protected String executeOne() throws Throwable {
        return super.executeOne();
    }
    
    @Override
	protected String getData(String url) {
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return CrawlerUtil.getHtmlPage(url);
	}
    /**
     * 创建一个htmlUnit webClient 客户端
     * @param ip
     * @param port
     * @return
     */
    public WebClient createWebClient(String ip, String port) throws Throwable{
        WebClient wc =  null;
        if ("".equals(ip) || "".equals(port)||ip==null||port==null) {
            wc = new WebClient(BrowserVersion.getDefault());
            log.info("通过本地ip进行处理···");
        } else {
            //获取代理对象
           wc = new WebClient(BrowserVersion.getDefault(), ip,Integer.valueOf(port));
           log.info("通过代理进行处理···");
        }

        //设置浏览器版本
        //是否使用不安全的SSL
        wc.getOptions().setUseInsecureSSL(true);
        //启用JS解释器，默认为true
        wc.getOptions().setJavaScriptEnabled(true);
        //禁用CSS
        wc.getOptions().setCssEnabled(false);
        //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnScriptError(true);
        //状态码错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //是否允许使用ActiveX
        wc.getOptions().setActiveXNative(false);
        //等待js时间
        //wc.waitForBackgroundJavaScript(10000);
        //设置Ajax异步处理控制器即启用Ajax支持
        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        //设置超时时间
        wc.getOptions().setTimeout(20000);
        //不跟踪抓取
        wc.getOptions().setDoNotTrackEnabled(false);
        //启动客户端重定向
        wc.getOptions().setRedirectEnabled(true);
        //
        wc.getCookieManager().clearCookies();
        //
        wc.setRefreshHandler(new ImmediateRefreshHandler());
        return wc;
    }


    /**
     * 通过本地IP去爬起
     * @param url
     * @param waitTime
     * @return
     */
	public static String getHtmlPage(String url, int waitTime) {
        if(waitTime<0){
            waitTime = 1000;
        }
        //设置浏览器版本
        WebClient wc = new WebClient(BrowserVersion.CHROME);
        //是否使用不安全的SSL
        wc.getOptions().setUseInsecureSSL(true);
        //启用JS解释器，默认为true
        wc.getOptions().setJavaScriptEnabled(false);
        //禁用CSS
        wc.getOptions().setCssEnabled(false);
        //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnScriptError(false);
        //状态码错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //是否允许使用ActiveX
        wc.getOptions().setActiveXNative(false);
        //等待js时间
        wc.waitForBackgroundJavaScript(600*1000);
        //设置Ajax异步处理控制器即启用Ajax支持
        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        //设置超时时间
        wc.getOptions().setTimeout(waitTime);
        //不跟踪抓取
        wc.getOptions().setDoNotTrackEnabled(false);
        //
        wc.getOptions().setRedirectEnabled(true);
        //
        wc.getCache().clear();
        //
        wc.getCookieManager().clearCookies();
        //
        wc.setRefreshHandler(new ImmediateRefreshHandler());
        try {
            //模拟浏览器打开一个目标网址
            HtmlPage htmlPage = wc.getPage(url);
            //为了获取js执行的数据 线程开始沉睡等待
            Thread.sleep(1000);//这个线程的等待 因为js加载需要时间的
            //以xml形式获取响应文本
            String xml = htmlPage.asXml();
            //并转为Document对象return
            return xml;
            //System.out.println(xml.contains("结果.xls"));//false
        }catch (InterruptedException e){
            e.getMessage();
        }catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *通过IP池去代理爬起数据
     * @param url
     * @param waitTime
     * @param ip
     * @param port
     * @return
     */
    public static String getHtmlPageProxy(String url, int waitTime,String ip,int port) {
        if(waitTime<0){
            waitTime = 1000;
        }
        //获取代理对象
        ProxyConfig proxyConfig = new ProxyConfig(ip,port);
        //设置浏览器版本
        WebClient wc = new WebClient(BrowserVersion.CHROME);
        //设置通过代理区爬起网页
        wc.getOptions().setProxyConfig(proxyConfig);
        //是否使用不安全的SSL
        wc.getOptions().setUseInsecureSSL(true);
        //启用JS解释器，默认为true
        wc.getOptions().setJavaScriptEnabled(false);
        //禁用CSS
        wc.getOptions().setCssEnabled(false);
        //js运行错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnScriptError(false);
        //状态码错误时，是否抛出异常
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //是否允许使用ActiveX
        wc.getOptions().setActiveXNative(false);
        //等待js时间
        wc.waitForBackgroundJavaScript(600*1000);
        //设置Ajax异步处理控制器即启用Ajax支持
        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        //设置超时时间
        wc.getOptions().setTimeout(waitTime);
        //不跟踪抓取
        wc.getOptions().setDoNotTrackEnabled(false);
        //
        wc.getOptions().setRedirectEnabled(true);
        //
        wc.getCache().clear();
        //
        wc.getCookieManager().clearCookies();
        //
        wc.setRefreshHandler(new ImmediateRefreshHandler());

        try {
            //模拟浏览器打开一个目标网址
            HtmlPage htmlPage = wc.getPage(url);
            //为了获取js执行的数据 线程开始沉睡等待
            Thread.sleep(1000);//这个线程的等待 因为js加载需要时间的
            //以xml形式获取响应文本
            String xml = htmlPage.asXml();
            //并转为Document对象return
            return xml;
            //System.out.println(xml.contains("结果.xls"));//false
        }catch (InterruptedException e){
            e.getMessage();
        }catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 下载页面
     */
    public String saveFile(Page page, String file) throws Exception {
        InputStream is = page.getWebResponse().getContentAsStream();
        FileOutputStream output = new FileOutputStream(OCRUtil.DOWNLOAD_DIR + File.separator + file);
        IOUtils.copy(is, output);
        output.close();
        return file;
    }

    /**
     * 获取IP代理池中的IP与Port
     * @return
     */
    public List<Proxypool> getProxyPool(){
        List<Proxypool> listProxypool = new ArrayList<>();
        listProxypool =proxypoolMapper.selectProxyPool();
        return  listProxypool;

    }

    /**
     *黑名单信息保存抓取结果
     * @param adminPunish
     * @param isForce true:强制保存插入;false:如果存在就不再保存（如系统中已存在该记录）
     */
    protected boolean saveAdminPunishOne(AdminPunish adminPunish, Boolean isForce) {
        //是否存在标识 true:存在 false：不存在
        boolean isFlag = false;
        List<AdminPunish> adminPunishList = adminPunishMapper.selectByUrl(adminPunish.getUrl(),adminPunish.getEnterpriseName(),adminPunish.getPersonName(),adminPunish.getJudgeNo(),adminPunish.getJudgeAuth());
        String strAdminPunish = "url地址："+adminPunish.getUrl()+"\n企业名称："+adminPunish.getEnterpriseName()+"\n+负责人名称："+adminPunish.getPersonName()+"\n处罚文号："+adminPunish.getJudgeNo();
        if (!isForce && adminPunishList.size()>0) {
            log.info(strAdminPunish+"此记录已经存在···不需要入库");
            isFlag = true;
        }else if(!isForce && adminPunishList.size()<=0){
            adminPunishMapper.insert(adminPunish);
            log.info(strAdminPunish+"此记录不存在···需要入库");
        } else if(isForce){
            if(adminPunishList.size()>0){
                adminPunishMapper.deleteByUrl(adminPunish.getUrl(),adminPunish.getEnterpriseName(),adminPunish.getPersonName(),adminPunish.getJudgeNo(),adminPunish.getJudgeAuth());
                adminPunishMapper.insert(adminPunish);
            }else{
                adminPunishMapper.insert(adminPunish);
            }
            log.info(strAdminPunish+"此记录入库完成···");
        }else{
            log.info(strAdminPunish+"此记录不满足入库条件···");
        }
        return isFlag;
    }

    /**
     *处罚信息保存抓取结果
     * @param discreditBlacklist
     * @param isForce true:强制保存插入;false:如果存在就不再保存（如系统中已存在该记录）
     */
    protected void saveDisneycreditBlackListOne(DiscreditBlacklist discreditBlacklist, Boolean isForce) {
        List<DiscreditBlacklist> adminDiscreditBlacklist = discreditBlacklistMapper.selectByUrl(discreditBlacklist.getUrl(),discreditBlacklist.getEnterpriseName(),discreditBlacklist.getPersonName(),discreditBlacklist.getJudgeNo(),discreditBlacklist.getJudgeAuth());
        String strDiscreditBlacklist = "url地址："+discreditBlacklist.getUrl()+"\n企业名称："+discreditBlacklist.getEnterpriseName()+"\n+负责人名称："+discreditBlacklist.getPersonName()+"\n处罚文号："+discreditBlacklist.getJudgeNo();
        if (!isForce && adminDiscreditBlacklist.size()>0) {
            log.info(strDiscreditBlacklist+"此记录已经存在···不需要入库");
        }else if(!isForce && adminDiscreditBlacklist.size()<=0){
            discreditBlacklistMapper.insert(discreditBlacklist);
            log.info(strDiscreditBlacklist+"此记录不存在···需要入库");
        } else if(isForce){
            if(adminDiscreditBlacklist.size()>0){
                discreditBlacklistMapper.deleteByUrl(discreditBlacklist.getUrl(),discreditBlacklist.getEnterpriseName(),discreditBlacklist.getPersonName(),discreditBlacklist.getJudgeNo(),discreditBlacklist.getJudgeAuth());
                discreditBlacklistMapper.insert(discreditBlacklist);
            }else{
                discreditBlacklistMapper.insert(discreditBlacklist);
            }
            log.info(strDiscreditBlacklist+"此记录入库完成···");
        }else{
            log.info(strDiscreditBlacklist+"此记录不满足入库条件···");
        }
    }

}
