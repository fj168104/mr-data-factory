package com.mr.modules.api.site.instance.circsite;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.groovy.util.ListHashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
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
		log.info("*******************call circ task**************");
		//到出Excel文件
		List listsExcel = new ArrayList();

		//3.输出到xlsx
		//0.获取保监会处罚列表页码数量
		int pageAll = 1;
		//获取清单列表页数pageAll
		String targetUri1 = "http://www.circ.gov.cn/web/site0/tab5240/";
		String fullTxt1 = getData(targetUri1);
//        pageAll = extractPage(fullTxt1);
        //1.保监会处罚列表清单
        List<List<?>> listList = new ArrayList<>();
        for (int i = 1;i<=pageAll;i++){
            String targetUri2 = "http://www.circ.gov.cn/web/site0/tab5240/module14430/page"+i+".htm";
            String fullTxt2 = getData(targetUri2);
            listList.add(extractList(fullTxt2));
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
				   listsExcel.add(record);
                   //下载文件
//                   downLoadFile(url,fileName+".html");
//                   log.info("序号："+i+"----->>>------"+url);
               }
        }
		exportToXls("circ.xlsx", listsExcel);
        return null;
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
            String href = "http://www.circ.gov.cn"+elementA.attr("href");
            //抽取标题
            String title = elementA.attr("title");
            //抽取发布的时间
            String extract_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String urlStr = id+"||"+href+"||"+title+"||"+extract_Date;
            list.add(urlStr);
        }
		return list;
	}

	/**
	 * 保监会处罚 提取所需要的信息
	 * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
	 * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
	 */
	private Map extractContent(String fullTxt, String id, String title) {
		Map map = new LinkedHashMap();
		//序号***** TODO 需确认
		String seqNo = "";  //可以提取链接中的ID
		seqNo = id;

		//处罚文号******
		String punishNo = "";
		punishNo = "保监罚〔2018〕10号";    //丛链接中提取 TODO 部分在链接中不存在，需要在正文中提取


		//机构当事人名称
		String orgPerson = "";

		//机构当事人住所
		String orgAddress = "";

		//机构负责人姓名
		String orgHolderName = "";


		//当事人集合
		List<List> allPerson = new ArrayList<>();
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
		//获取正文内容
		Document doc = Jsoup.parse(fullTxt.replace("&nbsp;",""));
		//获取正文主节点
		Elements textElements = doc.getElementsByAttributeValue("id","tab_content");
		//获得主节点下的所有文本内容
		String text = textElements.text();
		/*1.提取发布时间*/
		releaseDate = text.substring(text.indexOf("发布时间：")+6,text.indexOf("分享到：")).trim();
		/*2.提取文号*/
		punishNo = title.substring(title.indexOf("（保"),title.indexOf("号）")+2);
//		log.info("-----文号："+punishNo+"-------文件名称："+title +"-----------提取发布时间:"+releaseDate);
		/*3.提取处罚机关*/
		Elements elementsSpan = doc.getElementsByClass("xilanwb");
		Elements elementsSpanChild =elementsSpan.select("P");
//		log.info("----"+elementsSpanChild.toString());
		//4.处罚机关
		int elementsSpanChildCount = elementsSpanChild.size();
		punishOrg = elementsSpanChild.get(elementsSpanChildCount-2).text().trim();
		/*5.提取处罚时间*/
		punishDate  = elementsSpanChild.get(elementsSpanChildCount-1).text().trim();
//		log.info("处罚机关:"+punishOrg+"---处罚时间:"+punishDate+"---文件名称："+title);
		/*解析机构与当事人*/
		int countPerson =1;
		StringBuffer stringBufferDetail = new StringBuffer();
		for(Element elementP : elementsSpanChild){
			if(elementP.text().indexOf("：")>-1){
				String[] arrString = elementP.text().split("：");

				if(arrString.length==2){
					//进行分析了【法定代表人，当事人，住所，身份证号，职务，住址】等字符标记长度不超过6，所以考虑用长度来过滤
					if(arrString[0].length()<=6){
						//提取机构相关信息（对公）
						if(countPerson<=3){
							//6.机构当事人名称
							if(countPerson==1){orgPerson = arrString[1];}
							//7.机构当事人住所
							if(countPerson==2){orgAddress = arrString[1];}
							//8.机构负责人姓名
							if(countPerson==3){orgHolderName = arrString[1];}
						}
						//提取相关当事人信息（个人）
						if(countPerson>3){

							if((countPerson-3)%4==1){
								priPerson.add(arrString[1]);
//								System.out.println("0:"+arrString[1]);
							}
							if((countPerson-3)%4==2){
								priCert.add(arrString[1]);
//								System.out.println("1:"+arrString[1]);
							}
							if((countPerson-3)%4==3){
								priJob.add(arrString[1]);
//								System.out.println("2:"+arrString[1]);
							}
							if((countPerson-3)%4==0){
								priAddress.add(arrString[1]);
//								System.out.println("3"+arrString[1]);
							}

						}
						countPerson++;
//						log.info(arrString[0]+"-----------------------------------"+arrString[1]);
					}
				}
			}else{
				stringBufferDetail.append(elementP.text()+"\n");
			}

		}
		map.put("seqNo",seqNo);
		map.put("punishNo",punishNo);
		map.put("orgPerson",orgPerson);
		map.put("orgAddress",orgAddress);
		map.put("orgHolderName",orgHolderName);
		map.put("priPerson",priPerson.toString());
		map.put("priCert",priCert.toString());
		map.put("priJob",priJob.toString());
		map.put("priAddress",priAddress.toString());
		map.put("stringBufferDetail",stringBufferDetail.toString());
		map.put("releaseOrg",releaseOrg);
		map.put("releaseDate",releaseDate);
		map.put("punishOrg",punishOrg);
		map.put("punishDate",punishDate);

		log.info(		"\nseqNo:"+seqNo+"\n"+
						"punishNo:"+punishNo+"\n"+
						"orgPerson:"+orgPerson+"\n"+
						"orgAddress:"+orgAddress+"\n"+
						"orgHolderName:"+orgHolderName+"\n"+
						"releaseOrg:"+releaseOrg+"\n"+
						"releaseDate:"+releaseDate+"\n"+
						"punishOrg:"+punishOrg+"\n"+
						"punishDate:"+punishDate+"\n"
				);

		log.info(priPerson.toString());
		log.info(priCert.toString());
		log.info(priJob.toString());
		log.info(priAddress.toString());
		log.info(stringBufferDetail.toString());
		//		log.info("parse demo...\n" + punishDetail);
		return  map;
	}
}
