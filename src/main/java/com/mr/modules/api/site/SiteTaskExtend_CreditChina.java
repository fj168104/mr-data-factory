package com.mr.modules.api.site;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.common.OCRUtil;
import com.mr.common.util.CrawlerUtil;

import com.mr.modules.api.mapper.AdminPunishMapper;
import com.mr.modules.api.mapper.ProxypoolMapper;
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
    ProxypoolMapper proxypoolMapper;
    @Autowired
    AdminPunishMapper adminPunishMapper;
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
     * 保存抓取结果
     *
     * @return ture 保存成功		false 保存失败（如系统中已存在该记录）
     * 一主键为条件，筛选数据
     */
    /*@Override
    protected Boolean saveOne(AdminPunish adminPunish, Boolean isForce) {
        String primaryKey = buildFinanceMonitorPunishBizKey(adminPunish);
        log.debug("primaryKey:" + primaryKey);
        if (isForce || Objects.isNull(adminPunishMapper.selectByBizKey(adminPunish))) {
            insertOrUpdate(adminPunish);
            return true;
        } else {
            return false;
        }
    }
    *//**
     * 保存单条记录
     *
     * @param adminPunish
     * @return
     *//*
    private AdminPunish insertOrUpdate(AdminPunish adminPunish) {
        if (StringUtils.isEmpty(adminPunish.getPrimaryKey())) {
            buildFinanceMonitorPunishBizKey(adminPunish);
        }

        adminPunishMapper.deleteByBizKey(adminPunish.getPrimaryKey());
        //设置createTime
        if (StringUtils.isEmpty(adminPunish.getCreateTime())) {
            adminPunish.setCreatedAt(new Date());
            adminPunish.setUpdatedAt(new Date());
        }
        try {
            if (StrUtil.isEmpty(adminPunish.getCompanyFullName())) {
                adminPunish.setCompanyFullName(adminPunish.getPartyInstitution());
            }
            adminPunishMapper.insert(filterPlace(adminPunish));
        } catch (Exception e) {
            log.error(keyWords + ">>>" + e.getMessage());
        }
        return adminPunish;
    }

    *//**
     * 设置业务主键 格式：punish_no|punish_title|punish_institution|punish_date
     *
     * @return primaryKey
     *//*
    public static String buildAdminPunishBizKey(AdminPunish adminPunish) {
        String punishNo = StringUtils.isEmpty(adminPunish.getPunishNo())
                ? "NULL" : adminPunish.getPunishNo();
        String punishTitle = StringUtils.isEmpty(adminPunish.getPunishTitle())
                ? "NULL" : adminPunish.getPunishTitle();
        String punishDate = StringUtils.isEmpty(adminPunish.getPunishDate())
                ? "NULL" : adminPunish.getPunishDate();
        String punishInstitution = StringUtils.isEmpty(adminPunish.getPunishInstitution())
                ? "NULL" : adminPunish.getPunishInstitution();

        adminPunish.setPrimaryKey(String.format("%s|%s|%s|%s",
                punishNo, punishTitle, punishInstitution, punishDate));

        return adminPunish.getPrimaryKey();
    }

    protected AdminPunish filterPlace(AdminPunish adminPunish) throws Exception {
        if (Objects.isNull(adminPunish)) return adminPunish;
        Field[] fields = FinanceMonitorPunish.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.toLowerCase().equals("details")) continue;
            PropertyDescriptor prop = new PropertyDescriptor(fieldName, AdminPunish.class);

            // 获取getter方法，反射获取field值
            Object obj = prop.getReadMethod().invoke(adminPunish);

            if (Objects.isNull(obj) || obj instanceof java.util.Date) {
                continue;
            }
            String str = String.valueOf(obj);
            // 获取setter方法，反射赋值
            prop.getWriteMethod().invoke(adminPunish,
                    str.replaceAll("\\s*", "")
                            .replace("　", "")
                            .replace(" ", "")
                            .replace("　　", "")
                            .replace("\n", "").trim());

        }
        if (StrUtil.isNotEmpty(adminPunish.getDetails())) {
            adminPunish.setDetails(adminPunish.getDetails()
                    .replaceAll("\\s*", ""));
        }
        return adminPunish;
    }*/

}
