package com.mr.modules.api.site;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mr.common.OCRUtil;
import com.mr.common.util.CrawlerUtil;
import com.mr.modules.api.mapper.*;
import com.mr.modules.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @auterh zjxu
 * @DateTIme 2018-08
 */
@Slf4j
public class SiteTaskExtend_CollgationSite extends SiteTaskExtend{
    @Autowired
    DiscreditBlacklistMapper discreditBlacklistMapper;
    @Autowired
    protected ProxypoolMapper proxypoolMapper;
    @Autowired
    protected AdminPunishMapper adminPunishMapper;
    @Autowired
    protected CreditLevelMapper creditLevelMapper;
    @Autowired
    protected ProductionQualityMapper productionQualityMapper;
    @Autowired
    protected ScrapyDataMapper scrapyDataMapper;
    @Autowired
    protected CustomEntMapper customEntMapper;
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
	 * 获取页面数据
	 * @param wc
	 * @param url
	 * @return
	 */
	protected String getData(WebClient wc, String url) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return CrawlerUtil.getHtmlPage(wc, url);
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
            wc = new WebClient(BrowserVersion.CHROME);
            log.info("通过本地ip进行处理···");
        } else {
            //获取代理对象
            wc = new WebClient(BrowserVersion.CHROME, ip,Integer.valueOf(port));
            log.info("通过代理进行处理···");
        }

        //设置浏览器版本
        //是否使用不安全的SSL
        wc.getOptions().setUseInsecureSSL(true);
        //启用JS解释器，默认为true
        wc.getOptions().setJavaScriptEnabled(false);
        //禁用CSS TODO HTMLUNIT 本来就没有界面所以静止 false为不启用
        wc.getOptions().setCssEnabled(false);
        //js运行错误时，是否抛出异常 false:为不启用
        wc.getOptions().setThrowExceptionOnScriptError(false);
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
        }finally {
        	wc.close();
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
        }finally {
        	wc.close();
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
     * 下载页面
     * @param page
     * @param file
     * @param filePath
     * @return
     */
    public String saveFile(Page page, String file,String filePath)  {
        try {
            InputStream is = page.getWebResponse().getContentAsStream();
            File fileName = new File(filePath);
            if(!fileName.exists()){
                fileName.mkdirs();
            }
            FileOutputStream output = new FileOutputStream(filePath + File.separator + file);
            IOUtils.copy(is, output);
            output.close();
        }catch (FileNotFoundException fe){
            log.warn("保存文件出现文件不能找到异常······请排查！"+fe.getMessage());
        }catch (IOException e) {
            log.warn("保存文件出现IO异常······请排查！"+e.getMessage());
        }
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
    protected boolean saveDisneycreditBlackListOne(DiscreditBlacklist discreditBlacklist, Boolean isForce) {
        boolean isFlag = false;
        List<DiscreditBlacklist> adminDiscreditBlacklist = discreditBlacklistMapper.selectByUrl(discreditBlacklist.getUrl(),discreditBlacklist.getEnterpriseName(),discreditBlacklist.getPersonName(),discreditBlacklist.getJudgeNo(),discreditBlacklist.getJudgeAuth(),discreditBlacklist.getDiscreditAction());
        String strDiscreditBlacklist = "url地址："+discreditBlacklist.getUrl()+"\n企业名称："+discreditBlacklist.getEnterpriseName()+"\n+负责人名称："+discreditBlacklist.getPersonName()+"\n处罚文号："+discreditBlacklist.getJudgeNo();
        if (!isForce && adminDiscreditBlacklist.size()>0) {
            log.info(strDiscreditBlacklist+"此记录已经存在···不需要入库");
            isFlag = true;
        }else if(!isForce && adminDiscreditBlacklist.size()<=0){
            discreditBlacklistMapper.insert(discreditBlacklist);
            log.info(strDiscreditBlacklist+"此记录不存在···需要入库");
        } else if(isForce){
            if(adminDiscreditBlacklist.size()>0){
                discreditBlacklistMapper.deleteByUrl(discreditBlacklist.getUrl(),discreditBlacklist.getEnterpriseName(),discreditBlacklist.getPersonName(),discreditBlacklist.getJudgeNo(),discreditBlacklist.getJudgeAuth(),discreditBlacklist.getDiscreditAction());
                discreditBlacklistMapper.insert(discreditBlacklist);
            }else{
                discreditBlacklistMapper.insert(discreditBlacklist);
            }
            log.info(strDiscreditBlacklist+"此记录入库完成···");
        }else{
            log.info(strDiscreditBlacklist+"此记录不满足入库条件···");
        }
        return isFlag;
    }

    /**
     * 爬网原始数据及中间数据入库
     * @param scrapyData
     * @param isForce
     * @return
     */
    protected boolean saveScrapyDataOne(ScrapyData scrapyData, Boolean isForce) {
        boolean isFlag = false;
        int scrapyDataCount = scrapyDataMapper.selectCountByUrl(scrapyData.getUrl());

        if (!isForce && scrapyDataCount>0) {
            log.info("Url为："+scrapyData.getUrl()+"此记录已经存在···不需要入库");
            isFlag = true;
        }else if(!isForce && scrapyDataCount<=0){
            scrapyDataMapper.insert(scrapyData);
            log.info("Url为："+scrapyData.getUrl()+"此记录不存在···需要入库");
        } else if(isForce){
            if(scrapyDataCount>0){
                scrapyDataMapper.deleteAllByUrl(scrapyData.getUrl());
                scrapyDataMapper.insert(scrapyData);
            }else{
                scrapyDataMapper.insert(scrapyData);
            }
            log.info("Url为："+scrapyData.getUrl()+"此记录入库完成···");
        }else{
            log.info("Url为："+scrapyData.getUrl()+"此记录不满足入库条件···");
        }
        return isFlag;
    }

    /**
     * 质检曝光数据(工信部，商务部)
     * @param productionQuality
     * @param isForce
     * @return
     */
    protected boolean saveProductionQualityOne(ProductionQuality productionQuality, Boolean isForce) {
        boolean isFlag = false;
        List<ProductionQuality> productionQualityList = productionQualityMapper.selectByUrl(productionQuality.getUrl(),productionQuality.getEnterpriseName(),productionQuality.getPublishDate());

        if (!isForce && productionQualityList.size()>0) {
            log.info("Url为："+productionQuality.getUrl()+"此记录已经存在···不需要入库");
            isFlag = true;
        }else if(!isForce && productionQualityList.size()<=0){
            productionQualityMapper.insert(productionQuality);
            log.info("Url为："+productionQuality.getUrl()+"此记录不存在···需要入库");
        } else if(isForce){
            if(productionQualityList.size()>0){
                productionQualityMapper.deleteByUrl(productionQuality.getUrl(),productionQuality.getEnterpriseName(),productionQuality.getPublishDate());
                productionQualityMapper.insert(productionQuality);
            }else{
                productionQualityMapper.insert(productionQuality);
            }
            log.info("Url为："+productionQuality.getUrl()+"此记录入库完成···");
        }else{
            log.info("Url为："+productionQuality.getUrl()+"此记录不满足入库条件···");
        }
        return isFlag;
    }

    /**
     * 海关企业名录（失信，异常）
     * @param customEnt
     * @param isForce
     * @return
     */
    protected boolean saveCustomEntOne(CustomEnt customEnt, Boolean isForce) {
        boolean isFlag = false;
        List<CustomEnt> customEntList = customEntMapper.selectByUrl(customEnt.getUrl(),customEnt.getEnterpriseName(),customEnt.getStartDate());

        if (!isForce && customEntList.size()>0) {
            log.info("Url为："+customEnt.getUrl()+"此记录已经存在···不需要入库");
            isFlag = true;
        }else if(!isForce && customEntList.size()<=0){
            customEntMapper.insert(customEnt);
            log.info("Url为："+customEnt.getUrl()+"此记录不存在···需要入库");
        } else if(isForce){
            if(customEntList.size()>0){
                customEntMapper.deleteByUrl(customEnt.getUrl(),customEnt.getEnterpriseName(),customEnt.getStartDate());
                customEntMapper.insert(customEnt);
            }else{
                customEntMapper.insert(customEnt);
            }
            log.info("Url为："+customEnt.getUrl()+"此记录入库完成···");
        }else{
            log.info("Url为："+customEnt.getUrl()+"此记录不满足入库条件···");
        }
        return isFlag;
    }
    /**
     * 入库黑名单
     * @param map
     * @return
     */
    public boolean insertDiscreditBlacklist(Map map){
        boolean isFlag = true;
        DiscreditBlacklist discreditBlacklist = new DiscreditBlacklist();
        /**
         * 本条记录创建时间
         */
        //@Column(name = "created_at")
        discreditBlacklist.setCreatedAt(new Date());

        /**
         * 本条记录最后更新时间
         */
        //@Column(name = "updated_at")
        discreditBlacklist.setUpdatedAt(new Date());

        /**
         * 数据来源
         */
        discreditBlacklist.setSource(map.get("source").toString());
        /**
         * 主题
         */
        discreditBlacklist.setSubject(map.get("subject").toString());
        /**
         * url
         */
        discreditBlacklist.setUrl(map.get("sourceUrl")==null?"":map.get("sourceUrl").toString());
        /**
         * 主体类型: 01-企业 02-个人
         */
        //@Column(name = "object_type")
        discreditBlacklist.setObjectType(map.get("objectType")==null?"":map.get("objectType").toString());

        /**
         * 企业名称
         */
        //@Column(name = "enterprise_name")
        discreditBlacklist.setEnterpriseName(map.get("enterpriseName")==null?"":map.get("enterpriseName").toString());

        /**
         * 统一社会信用代码
         */
        //@Column(name = "enterprise_code1")
        discreditBlacklist.setEnterpriseCode1(map.get("enterpriseCode1")==null?"":map.get("enterpriseCode1").toString());
        /**
         * 营业执照注册号
         */
        //@Column(name = "enterprise_code2")
        discreditBlacklist.setEnterpriseCode2(map.get("enterpriseCode2")==null?"":map.get("enterpriseCode2").toString());

        /**
         * 组织机构代码
         */
        //@Column(name = "enterprise_code3")
        discreditBlacklist.setEnterpriseCode3(map.get("enterpriseCode3")==null?"":map.get("enterpriseCode3").toString());

        //enterprise_code4	纳税人识别号
        discreditBlacklist.setEnterpriseCode4(map.get("enterpriseCode4")==null?"":map.get("enterpriseCode4").toString());
        /**
         * 法定代表人/负责人姓名|负责人姓名
         */
        //@Column(name = "person_name")
        discreditBlacklist.setPersonName(map.get("personName")==null?"":map.get("personName").toString());
        /**
         * 法定代表人身份证号|负责人身份证号
         */
        //@Column(name = "person_id")
        discreditBlacklist.setPersonId(map.get("personId")==null?"":map.get("personId").toString());
        /**
         * 失信类型
         */
        //@Column(name = "discredit_type")
        discreditBlacklist.setDiscreditType(map.get("discreditType")==null?"":map.get("discreditType").toString());
        /**
         * 失信行为
         */
        //@Column(name = "discredit_action")
        discreditBlacklist.setDiscreditAction(map.get("discreditAction")==null?"":map.get("discreditAction").toString());
        /**
         * 列入原因
         */
        //@Column(name = "punish_reason")
        discreditBlacklist.setPunishReason(map.get("punishReason")==null?"":map.get("punishReason").toString());
        /**
         * 处罚结果
         */
        //@Column(name = "punish_result")
        discreditBlacklist.setPunishResult(map.get("punishResult")==null?"":map.get("punishResult").toString());
        /**
         * 执行文号
         */
        //@Column(name = "judge_no")
        discreditBlacklist.setJudgeNo(map.get("judgeNo")==null?"":map.get("judgeNo").toString());
        /**
         * 执行时间
         */
        //@Column(name = "judge_date")
        discreditBlacklist.setJudgeDate(map.get("judgeDate")==null?"":map.get("judgeDate").toString());
        /**
         * 判决机关
         */
        //@Column(name = "judge_auth")
        discreditBlacklist.setJudgeAuth(map.get("judgeAuth")==null?"":map.get("judgeAuth").toString());
        /**
         * 发布日期
         */
        //@Column(name = "publish_date")
        discreditBlacklist.setPublishDate(map.get("publishDate")==null?"":map.get("publishDate").toString());
        /**
         * 当前状态
         */
        discreditBlacklist.setStatus(map.get("status")==null?"":map.get("status").toString());
        /**
         * 唯一性标识(同一数据来源的同一主题内唯一)
         */
        discreditBlacklist.setUniqueKey(discreditBlacklist.getUrl()+"@"+discreditBlacklist.getEnterpriseName()+"@"+discreditBlacklist.getPersonName()+"@"+discreditBlacklist.getJudgeNo()+"@"+discreditBlacklist.getJudgeAuth());
        isFlag = saveDisneycreditBlackListOne(discreditBlacklist,false);

        return isFlag;
    }

    /**
     * 入库行政处罚
     * @param map
     * @return
     */
    public boolean adminPunishInsert(Map<String,String> map){
        boolean isFlag = true;
        AdminPunish adminPunish = new AdminPunish();
        //created_at	本条记录创建时间
        adminPunish.setCreatedAt(new Date());
        //updated_at	本条记录最后更新时间
        adminPunish.setUpdatedAt(new Date());
        //source	数据来源
        adminPunish.setSource(map.get("source"));
        //subject	主题
        adminPunish.setSubject(map.get("subject"));
        //url	url
        adminPunish.setUrl(map.get("sourceUrl")==null?"":map.get("sourceUrl"));
        //object_type	主体类型: 01-企业 02-个人
        adminPunish.setObjectType("01");
        //enterprise_name	企业名称
        adminPunish.setEnterpriseName(map.get("enterpriseName")==null?"": map.get("enterpriseName"));
        //enterprise_code1	统一社会信用代码--cfXdrShxym
        adminPunish.setEnterpriseCode1(map.get("enterpriseCode1")==null?"":map.get("enterpriseCode1"));
        //enterprise_code2	营业执照注册号
        adminPunish.setEnterpriseCode2(map.get("enterpriseCode2")==null?"":map.get("enterpriseCode2"));
        //enterprise_code3	组织机构代码
        adminPunish.setEnterpriseCode3(map.get("enterpriseCode3")==null?"":map.get("enterpriseCode3"));
        //enterprise_code4	纳税人识别号
        adminPunish.setEnterpriseCode3(map.get("enterpriseCode4")==null?"":map.get("enterpriseCode4"));
        //person_name	法定代表人/负责人姓名|负责人姓名
        adminPunish.setPersonName(map.get("personName")==null?"":map.get("personName"));
        //person_id	法定代表人身份证号|负责人身份证号
        adminPunish.setPersonId(map.get("personId")==null?"":map.get("personId"));
        //punish_type	处罚类型
        adminPunish.setPunishType(map.get("punishType")==null?"":map.get("punishType"));
        //punish_reason	处罚事由
        adminPunish.setPunishReason(map.get("punishReason")==null?"":map.get("punishReason"));
        //punish_according	处罚依据
        adminPunish.setPunishAccording(map.get("punishAccording")==null?"":map.get("punishAccording"));
        //punish_result	处罚结果
        adminPunish.setPunishResult(map.get("punishResult")==null?"":map.get("punishResult"));
        //judge_no	执行文号
        adminPunish.setJudgeNo(map.get("judgeNo")==null?"":map.get("judgeNo"));
        //judge_date	执行时间
        adminPunish.setJudgeDate(map.get("judgeDate")==null?"":map.get("judgeDate"));
        //judge_auth	判决机关
        adminPunish.setJudgeAuth(map.get("judgeAuth")==null?"":map.get("judgeAuth"));
        //publish_date	发布日期
        adminPunish.setPublishDate(map.get("publishDate")==null?"":map.get("publishDate"));
        /**
         * 唯一性标识(同一数据来源的同一主题内唯一)
         */
        adminPunish.setUniqueKey(adminPunish.getUrl()+"@"+adminPunish.getEnterpriseName()+"@"+adminPunish.getPersonName()+"@"+adminPunish.getJudgeNo()+"@"+adminPunish.getJudgeAuth());
        isFlag = saveAdminPunishOne(adminPunish,false);
        return isFlag;
    }

    public boolean scrapyDataInsert(Map<String,String> map){
        boolean isFlag = true;
        ScrapyData scrapyData = new ScrapyData();
        //created_at	本条记录创建时间
        scrapyData.setCreatedAt(new Date());
        //source	数据来源
        scrapyData.setSource(map.get("source"));
        //url	地址
        scrapyData.setUrl(map.get("sourceUrl"));
        //hash_key	url的md5结果（如有附件，则保存在此目录中）
        scrapyData.setHashKey(map.get("hashKey"));
        //attachment_type	附件类型（pdf,doc,xls,jpg,tiff...）
        scrapyData.setAttachmentType(map.get("attachmentType"));
        //html	正文html
        scrapyData.setHtml(map.get("html"));
        //text	正文text，提取到的正文
        scrapyData.setText(map.get("text"));
        //fields	提取到的关键数据
        scrapyData.setFields(map.get("fields"));

        isFlag = saveScrapyDataOne(scrapyData,false);
        return isFlag;
    }

}
