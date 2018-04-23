package com.mr.modules.api.site.instance.circsite;

import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTaskExtend;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by feng on 18-3-16
 * 保监会
 * 行政处罚决定列表清单
 */

@Slf4j
@Component("circ")
@Scope("prototype")
public class SiteTaskImpl_CIRC_List extends SiteTaskExtend {

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		extract();
        return null;
	}

	@Override
	protected String executeOne() throws Throwable {

		if(!oneFinanceMonitorPunish.getUrl().equalsIgnoreCase("")){
			extractPageByUrl(oneFinanceMonitorPunish.getUrl());
		}else{
			extract();
		}
		return null;
	}

	private void extract(){

		log.info("*******************call circ task**************");
		//到出Excel文件
		List listsExcel = new ArrayList();

		//3.输出到xlsx
		//0.获取保监会处罚列表页码数量
		int pageAll = 1;
		//获取清单列表页数pageAll
		String targetUri1 = "http://bxjg.circ.gov.cn/web/site0/tab5240/";
		String fullTxt1 = getData(targetUri1);
		pageAll = extractPage(fullTxt1);
		//1.保监会处罚列表清单
		List<List<?>> listList = new ArrayList<>();
		for (int i = 1;i<=pageAll;i++){
			String targetUri2 = "http://bxjg.circ.gov.cn/web/site0/tab5240/module14430/page"+i+".htm";
			String fullTxt2 = getData(targetUri2);
			if(oneFinanceMonitorPunish!=null){
				if(!oneFinanceMonitorPunish.getPublishDate().equalsIgnoreCase("")){
					listList.add(extractListByDate(fullTxt2,oneFinanceMonitorPunish.getPublishDate()));
				}
			}else{
				listList.add(extractList(fullTxt2));
			}

		}

		//2.获取处罚详情信息
		for(List<?> list : listList) {//其内部实质上还是调用了迭代器遍历方式，这种循环方式还有其他限制，不建议使用。
			for (int i=0;i<list.size();i++){
				String urlStr = list.get(i).toString();
//                   log.info(urlStr);
				String[] urlArr = urlStr.split("\\|\\|");
				String id = urlArr[0];
				String url = urlArr[1];
				String fileName = urlArr[2];

				//提取正文结构化数据
				Map record = extractContent(getData(url),id,fileName);

				getObj(record,url);

//				   listsExcel.add(record);
				//下载文件
//                   downLoadFile(url,fileName+".html");
//                   log.info("序号："+i+"----->>>------"+url);
			}
		}
//		exportToXls("circ.xlsx", listsExcel);
		log.info("保监会处罚信息抓起完成···");
	}
	/**
	 * 获取保监会处罚列表所有页数
	 * @param fullTxt
	 * @return
	 */
	public int extractPage(String fullTxt){
		int pageAll = 1;
		Document doc = Jsoup.parse(fullTxt);
		Elements td = doc.getElementsByClass("Normal");
		//记录元素的数量
		int serialNo = td.size();
		pageAll = Integer.valueOf(td.get(serialNo-1).text().split("/")[1]);
        log.info("-------------********---------------");
        log.info("处罚列表清单总页数为："+pageAll);
        log.info("-------------********---------------");
		return  pageAll;
	}
	/**
	 * 获取保监会处罚里列表清单
	 * @param fullTxt
	 * @return
	 */
	private List<?> extractList(String fullTxt){
		List<String> list = new ArrayList<>();
        Document doc = Jsoup.parse(fullTxt);
        Elements span = doc.getElementsByAttributeValue("id","lan1");

        for (Element elementSpan : span){
            Elements elements = elementSpan.getElementsByTag("a");
            Element elementA = elements.get(0);
            //抽取编号Id
            String id = elementA.attr("id");
            //抽取连接
            String href = "http://bxjg.circ.gov.cn"+elementA.attr("href");
            //抽取标题
            String title = elementA.attr("title").replace("(","（").replace(")","）");
            //抽取发布的时间
     //       String extract_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Element element_td = elementSpan.parent().nextElementSibling();
			String extract_Date = "20" + element_td.text().replace("(","").replace(")","");

            String urlStr = id+"||"+href+"||"+title+"||"+extract_Date;
            list.add(urlStr);
        }
		return list;
	}
	/**
	 * 根据发布日期获取总页数下的所有连接url，获取指定日期的数据时格式为yyyy-mm-dd,获取某年某一个月内的数据时格式为yyyy-mm
	 * */
	private List<?> extractListByDate(String fullTxt,String date){
		List<String> list = new ArrayList<>();
		Document doc = Jsoup.parse(fullTxt);
		Elements span = doc.getElementsByAttributeValue("id","lan1");

		for (Element elementSpan : span){
			//发布时间
			Element element_td = elementSpan.parent().nextElementSibling();
			String extract_Date = "20" + element_td.text().replace("(","").replace(")","");

			if(date.equalsIgnoreCase(extract_Date) || extract_Date.contains(date)){
				Elements elements = elementSpan.getElementsByTag("a");
				Element elementA = elements.get(0);
				//抽取编号Id
				String id = elementA.attr("id");
				//抽取连接
				String href = "http://bxjg.circ.gov.cn"+elementA.attr("href");
				//抽取标题
				String title = elementA.attr("title").replace("(","（").replace(")","）");
				//抽取发布的时间
			//	String extract_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

				String urlStr = id+"||"+href+"||"+title+"||"+extract_Date;
				list.add(urlStr);
			}

		}
		return list;
	}

	/**
	 * 对传入的url作处理
	 * */
	private void extractPageByUrl(String url){
		log.info("url:"+url);
		Map record = extractContent(getData(url),"","");
		getObj(record,url);
	}
	/**
	 * 保监会处罚 提取所需要的信息
	 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
	 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
	 */
	private Map extractContent(String fullTxt, String id, String title) {
		Map map = new LinkedHashMap();
		//文件类型
        String fileType = ""; //TODO 对公处罚，个人处罚，处罚情况
		//序号***** TODO 需确认
		String seqNo = "";  //可以提取链接中的ID
		seqNo = id;

		//处罚文号******
		String punishNo = "";//丛链接中提取 TODO 部分在链接中不存在，需要在正文中提取

		//机构当事人名称
		List  orgPerson = new ArrayList<>();

		//机构当事人住所
		List  orgAddress = new ArrayList<>();

		//机构负责人姓名
		List  orgHolderName = new ArrayList<>();


		//当事人集合
		List allPerson = new ArrayList<>();
		//当事人
		List priPerson = new ArrayList();
		//身份证号
		List priCert = new ArrayList();
		//职务
		List priJob = new ArrayList();
		//住址
		List priAddress = new ArrayList();

		//发布机构******
		String releaseOrg = "中国保监会";   //链接中提取

		//发布日期******
		String releaseDate = "";//链接中提取 TODO 链接中的时间格式不全，需要在正文中提取

		//行政处罚详情
		String punishDetail = "";

		//处罚机关******	TODO 需确认
		String punishOrg = "";
		punishOrg = "中国保监会";  //TODO 可以从正文中提取

		//处罚日期***** TODO 可以中正文中提取，但是格式非阿拉伯数字类型
		String punishDate = "";
		//数据来源  TODO 来源（全国中小企业股转系统、地方证监局、保监会、上交所、深交所、证监会）
		String source = "保监会";
		//主题 TODO 主题（全国中小企业股转系统-监管公告、行政处罚决定、公司监管、债券监管、交易监管、上市公司处罚与处分记录、中介机构处罚与处分记录
		String object = "行政处罚决定";
		//获取正文内容
		Document doc = Jsoup.parse(fullTxt.replace("&nbsp;","")
				.replace("\"","")
				.replace("<span>住 </span>","")
				.replace("<span>所：","<span>地址：")
				.replace("<span>址：","<span>地址：")
				.replace("<span>职 </span>","")
				.replace("<span>务：","<span>职务：")
				.replace("<span>当</span>","")
				.replace("<span>事</span>","")
				.replace("<span>人：","<span>当事人：")
				.replace("<br>","</p><p>")
				.replace("　","")
				.replace(":","：")
				.replace("香港永久性居民身份证号：","证件号码：")
				.replace("身份证号：","证件号码：")
				.replace("台胞证号：","证件号码：")
				.replace("台胞证号：","证件号码：")
				.replace("台湾身份证号码：","证件号码：")
				.replace("港澳证件号码：","证件号码：")
				.replace("身份证号码：","证件号码：")
				.replace("住 址：","地址：")
				.replace("注册地址：","地址：")
				.replace("营业地址：","地址：")
				.replace("营业场所：","地址：")
				.replace("住所","地址")
				.replace("住所：","地址：")
				.replace("\"所：","地址：")
				.replace("住 所：","地址：")
				.replace("住址：","地址：")
				.replace("负责人姓名：","负责人：")
				.replace("法定代表人：","负责人：")
				.replace("主要负责人姓名：","负责人：")
				.replace("受处罚机构名称：","当事人：")
				.replace("　　当事人：","当事人：")
				.replace("当 事 人：","当事人：")
				.replace("受处罚人：","当事人：")
				.replace("受处罚机构名称：","当事人：")
				.replace("受处罚人姓名：","当事人：")
				.replace("受处罚人名称：","当事人：")
				.replace("受处罚机构：","当事人：")
				.replace("（以下简称长城人寿）总","（以下简称长城人寿）总精算师")
				.replace("夏博恩（Bernd Scharrer）","夏博恩")
		);

		//获取正文主节点
	//	Elements textElements = doc.getElementsByAttributeValue("id","tab_content");
		Element elementsTxt = doc.getElementById("tab_content");
		Elements elementsTD = elementsTxt.getElementsByTag("TD");
		Element elementsTitle = elementsTD.first();

		title = elementsTitle.text();
	//	log.info("----------title-----------"+title);
		//获得主节点下的所有文本内容
		String text = elementsTxt.text();
	//	log.info("--------text-----"+text);
		/*1.提取发布时间*/
		releaseDate = text.substring(text.indexOf("发布时间：")+5,text.indexOf("分享到：")).trim();

//		log.info("-----文号："+punishNo+"-------文件名称："+title +"-----------提取发布时间:"+releaseDate);
		/*3.提取处罚机关*/
		Elements elementsSpan = doc.getElementsByClass("xilanwb");
		Element elementSpanXilanwb = elementsSpan.first();
		Elements elementsSpanChild = elementSpanXilanwb.children();

		//4.处罚机关
		int elementsSpanChildCount = elementsSpanChild.size();
		punishOrg = elementsSpanChild.get(elementsSpanChildCount-2).text().trim();
		/*5.提取处罚时间*/
		String strLastOne = elementsSpanChild.get(elementsSpanChildCount-1).text();
		if(strLastOne.contains("年")&&strLastOne.contains("月")&&strLastOne.endsWith("日")){
			punishDate  = elementsSpanChild.get(elementsSpanChildCount-1).text().trim();
		}
		//输出详情内容
		StringBuffer stringBufferDetail = new StringBuffer();
		/*TODO 保监会处罚站点分三大类*/
		//一、主题中包括： TODO 中国保监会对 指自然人
		if(text.indexOf("当事人：")>-1){
			//当事人标识 TODO false 为对公，true为自然人
			Boolean currentFlag = false;

		    List<String >  strList  = new ArrayList<>();

            for(Element elementP : elementsSpanChild){

            	//处理“：”的特殊情况，其在HTML中没有与前后内容正常在统一元素中，而是在不同元素中
				String strSelect = elementP.text().replace("营业地址：","地址：")
						.replace("住址：","地址：")
						.replace("主要负责人：","负责人：")
						.replace("法定代表人：","负责人：")
						.replace("身份证号：","证件号码：")
						.replace("住所","地址");
				if(elementP.text().contains("担任")&&elementP.text().contains("期间")&&elementsSpanChild.contains("赵冰")){
					strList.add("职务："+elementP.text().substring(elementP.text().indexOf("担任"),elementP.text().indexOf("期间")));
				}
				//只包含“：”的当事人记录
				if(strSelect.contains("：")&&!strSelect.contains("；")){
					//判断冒号切分，数组长度为2
					String[] strMH = strSelect.split("：");
            		if(strMH.length==2){
            			if(strMH[0].equals("当事人")&&strMH[1].contains("，")&&strMH[1].split("，")[0].length()<6){
							strList.add("当事人："+strMH[1].split("，")[0]);
							strList.add("职务："+strMH[1].split("，")[1]);
						}else{
							strList.add(strSelect);
						}
					}
					if(strMH.length>2){
						for(String strAll :strSelect.split("，")){
							strList.add(strAll);
						}
					}
				}
				//既包含"："，又包含“；”的当事人记录
				if(strSelect.contains("：")&&strSelect.contains("；")){
					//判断冒号切分，数组长度为2
					String[] strMH = strSelect.split("；");
					for(String strSpecially : strMH){
						String[] strMHArr = strSelect.split("：");
						if(strMHArr.length==2){
							if(strMHArr[0].equals("当事人")&&strMHArr[1].contains("，")&&strMHArr[1].split("，")[0].length()<6){
								strList.add("当事人："+strMHArr[1].split("，")[0]);
								strList.add("职务："+strMHArr[1].split("，")[1]);
							}else{
								strList.add(strSelect);
							}
						}
						if(strMHArr.length>2){
							for(String strAll :strSelect.split("，")){
								strList.add(strAll);
							}
						}
					}

				}
				if(strSelect.contains("年")&&strSelect.contains("月")&&strSelect.endsWith("日")){
					if(strSelect.indexOf("年")<4){
						punishDate = strSelect.trim();
					}else{
						punishDate = strSelect.trim().substring(strSelect.lastIndexOf("年")-4);
					}
				}
            }
            for(String strObject : strList ){
            	if(strObject.contains("当事人")&&strObject.contains("（以下简称")){
					strObject = strObject.split("（以下简称")[0];
				}
            	if(strObject.contains("：")){
					String[] strObjectArr = strObject.split("：");

					if(strObjectArr[0].equals("当事人")&&strObjectArr[1].length()>5){

						orgPerson.add(strObjectArr[1].toString());
						currentFlag = false;

					}
					if(currentFlag==false&&strObjectArr[0].equals("地址")){
						orgAddress.add(strObjectArr[1].toString());
					}
					if(currentFlag==false&&strObjectArr[0].equals("负责人")){
						orgHolderName.add(strObjectArr[1].toString());
					}
					if(strObjectArr[0].equals("当事人")&&strObjectArr[1].length()<6){
						priPerson.add(strObjectArr[1].toString());
						currentFlag = true;
					}
					if(strObjectArr[0].equals("证件号码")&&currentFlag == true){
						priCert.add(strObjectArr[1].toString());
					}
					if(strObjectArr[0].equals("职务")&&currentFlag == true){
						priJob.add(strObjectArr[1].toString());
					}
					if(strObjectArr[0].equals("地址")&&currentFlag == true){
						priAddress.add(strObjectArr[1].toString());
					}
				}

			}
			punishOrg="中国保险监督管理委员会";
			punishNo =elementsSpanChild.first().text();
			if(!punishNo.contains("保监罚")){
				punishNo =title.split("（")[1].replace("）","");
			}
			stringBufferDetail.append(elementsSpan.text().replace("总精算师精算师","总精算师"));
		}else{//三、主题为包括： TODO 处罚实施情况内容
			object ="处罚实施情况";
			punishNo = title;
			stringBufferDetail.append(elementsSpan.text());
			punishOrg="";
			punishDate="";
		}
		//获取处罚时间不在P标签中的处罚时间
		String[] punishDateStr = stringBufferDetail.toString().split("。");
		if(punishDateStr.length>=2){
			if(punishDateStr[punishDateStr.length-1].contains("会")&&punishDateStr[punishDateStr.length-1].endsWith("日")){
				punishDate = punishDateStr[punishDateStr.length-1].split("会")[1];
			}
		}
		map.put("seqNo",seqNo);
		map.put("punishNo",punishNo);

		if(orgPerson.size()==0){
			map.put("orgPerson","");
		}else{
			map.put("orgPerson",orgPerson.toString().replace("[","").replace("]",""));
		}
		if(orgAddress.size()==0){
			map.put("orgAddress","");
		}else{
			map.put("orgAddress",orgAddress.toString().replace("[","").replace("]",""));
		}
		if(orgHolderName.size()==0){
			map.put("orgHolderName","");
		}else{
			map.put("orgHolderName",orgHolderName.toString().replace("[","").replace("]",""));
		}
		if(priPerson.size()==0){
			map.put("priPerson","");
		}else{
			map.put("priPerson",priPerson.toString().replace("[","").replace("]",""));
		}
		if(priCert.size()==0){
			map.put("priCert","");
		}else{
			map.put("priCert",priCert.toString().replace("[","").replace("]",""));
		}
		if(priJob.size()==0){
			map.put("priJob","");
		}else{
			map.put("priJob",priJob.toString().replace("[","").replace("]",""));
		}
		if(priAddress.size()==0){
			map.put("priAddress","");
		}else{
			map.put("priAddress",priAddress.toString().replace("[","").replace("]",""));
		}
		map.put("stringBufferDetail",stringBufferDetail.toString());
		map.put("releaseOrg",releaseOrg);
		map.put("releaseDate",releaseDate);
		map.put("punishOrg",punishOrg);
		map.put("punishDate",punishDate);
		map.put("source",source);
		map.put("object",object);
		map.put("title",title);

		return  map;
	}

	/**
	 * 获取Obj,并入库
	 * */
	public FinanceMonitorPunish getObj(Map<String,String> mapInfo, String href){

		FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
		financeMonitorPunish.setPunishNo(mapInfo.get("punishNo"));//处罚文号
		financeMonitorPunish.setPunishTitle(mapInfo.get("title"));//标题
		financeMonitorPunish.setPublisher(mapInfo.get("releaseOrg"));//发布机构
		financeMonitorPunish.setPublishDate(mapInfo.get("releaseDate"));//发布时间
		financeMonitorPunish.setPunishInstitution(mapInfo.get("punishOrg"));//处罚机关
		financeMonitorPunish.setPunishDate(mapInfo.get("punishDate"));//处罚时间
		financeMonitorPunish.setPartyInstitution(mapInfo.get("orgPerson"));//当事人（公司）=处罚对象
		financeMonitorPunish.setCompanyFullName(mapInfo.get("orgPerson"));//当时人（公司）全称
		financeMonitorPunish.setDomicile(mapInfo.get("orgAddress"));//机构住址
		financeMonitorPunish.setLegalRepresentative(mapInfo.get("orgHolderName"));//机构负责人
		financeMonitorPunish.setPartyPerson(mapInfo.get("priPerson"));//受处罚人
		financeMonitorPunish.setPartyPersonId(mapInfo.get("priCert"));//受处罚人证件号码
		financeMonitorPunish.setPartyPersonTitle(mapInfo.get("priJob"));//职务
		financeMonitorPunish.setPartyPersonDomi(mapInfo.get("priAddress"));//自然人住址
		financeMonitorPunish.setDetails(mapInfo.get("stringBufferDetail"));//详情
		financeMonitorPunish.setUrl(href);
		financeMonitorPunish.setSource(mapInfo.get("source"));
		financeMonitorPunish.setObject(mapInfo.get("object"));
		log.info("url:"+href);
		//保存入库
		saveOne(financeMonitorPunish,false);

		return financeMonitorPunish;
	}
}
