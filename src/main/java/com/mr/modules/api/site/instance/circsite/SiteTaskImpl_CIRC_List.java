package com.mr.modules.api.site.instance.circsite;

import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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
		//获取清单列表页数pageAll http://bxjg.circ.gov.cn/web/site0/tab5240/
		String targetUri1 = "http://bxjg.circ.gov.cn/web/site0/tab5240/";
		String fullTxt1 = getData(targetUri1);
        pageAll = extractPage(fullTxt1);
        //1.保监会处罚列表清单
        List<List<?>> listList = new ArrayList<>();
        for (int i = 1;i<=pageAll;i++){
            String targetUri2 = "http://bxjg.circ.gov.cn/web/site0/tab5240/module14430/page"+i+".htm";
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
		exportToXls("20180408circ.xlsx", listsExcel);
        log.info("保监会处罚信息抓起完成···");
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
            String href = "http://bxjg.circ.gov.cn"+elementA.attr("href");
            //抽取标题
            String title = elementA.attr("title").replace("(","（").replace(")","）");
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
		//文件类型
        String fileType = ""; //TODO 对公处罚，个人处罚，处罚情况
		//序号***** TODO 需确认
		String seqNo = "";  //可以提取链接中的ID
		seqNo = id;

		//处罚文号******
		String punishNo = "";//丛链接中提取 TODO 部分在链接中不存在，需要在正文中提取

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
		Document doc = Jsoup.parse(fullTxt.replace("&nbsp;","").replace("　","").replace(":","："));
		//获取正文主节点
		Elements textElements = doc.getElementsByAttributeValue("id","tab_content");
		//获得主节点下的所有文本内容
		String text = textElements.text();
		/*1.提取发布时间*/
		releaseDate = text.substring(text.indexOf("发布时间：")+5,text.indexOf("分享到：")).trim();

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
		//输出详情内容
		StringBuffer stringBufferDetail = new StringBuffer();
		/*TODO 保监会处罚站点分三大类*/
		//一、主题中包括： TODO 中国保监会对 指自然人
		if(title.indexOf("中国保监会对")>-1){
            fileType = "个人处罚";
		    List<String >  strList  = new ArrayList<>();
		    int count = 1;
            for(Element elementP : elementsSpanChild){
                if(count==1){punishNo = elementP.text();}
                if(count==3){
                    //TODO 模型：受处罚人：李涛，瑞福德健康保险股份有限公司副总裁
                    String[] strName =  elementP.text().split("：");
                    String[] strNameJob =  strName[1].split("，");
                    priPerson.add(strNameJob[0]);
                    priCert.add("");
                    if(strNameJob.length==2){
                        priJob.add(strNameJob[1]);
                    }else{
                        priJob.add("");
                    }

                    priAddress.add("");
                }

                if(!elementP.text().equals("")){
                    strList.add(elementP.text());
                }

                count++;
            }
            log.info("strList:"+strList.size());
            punishOrg=strList.get(strList.size()-2);
            if(punishOrg.length()>11){
				punishOrg="中国保险监督管理委员会";
			}
            punishDate = strList.get(strList.size()-1);
			stringBufferDetail.append(elementsSpanChild.text());
		}else if (title.indexOf("中国保险监督管理委员会行政处罚决定书")>-1){ //二、主题为包括： TODO 中国保险监督管理委员会行政处罚决定书 指法人
            fileType = "对公处罚";
			/*2.提取文号*/
			punishNo = title.substring(title.indexOf("（保"),title.indexOf("号）")+2);
			/*解析机构与当事人*/
			int countPerson =1;//统计当事人
			int countPMaker = 0;//统计P标签
            boolean busiflag = false;//判断是否缺失法人代表标识
            boolean priFlag = false;// TODO 判断是否为自然人
            String peopleFeature = "";// TODO 特殊情况下标识自然人
			for(Element elementP : elementsSpanChild){
				if(elementP.text().indexOf("：")>-1){
					String[] arrString = elementP.text().split("：");
					if(arrString.length==2 ){
						//进行分析了【法定代表人，当事人，住所，身份证号，职务，住址】等字符标记长度不超过6，所以考虑用长度来过滤
						if(arrString[0].length()<=6){
						    //通过身份证号判断为自然人
						    if(countPerson==2&&(arrString[0].equals("身份证号")||arrString[0].equals("护照号"))){
                                priFlag=true;
                            }
                            if(countPerson==1){peopleFeature =arrString[1];}


							//提取机构相关信息（对公）
							if(countPerson<=3 && priFlag == false){
								//6.机构当事人名称
								if(countPerson==1){orgPerson = arrString[1];}
								//7.机构当事人住所
								if(countPerson==2){orgAddress = arrString[1];}
								//8.机构负责人姓名
								if(countPerson==3) {
									if (arrString[0].equals("当事人")) {
										orgHolderName = "缺失法定代表人";
									}else{
										orgHolderName = arrString[1];
									}
								}
							}
							//提取相关当事人信息（个人）
                            if(countPerson>=3 && priFlag == false ) {
                                if (countPerson == 3 && arrString[0].equals("当事人")) {
                                    busiflag = true;
                                }
                                if(busiflag==true){
                                    if ((countPerson) % 4 == 3) {
                                        priPerson.add(arrString[1]);
                                    }
                                    if ((countPerson) % 4 == 0) {
                                        priCert.add(arrString[1]);
                                    }
                                    if ((countPerson) % 4 == 1) {
                                        priJob.add(arrString[1]);
                                    }
                                    if ((countPerson) % 4 == 2) {
                                        priAddress.add(arrString[1]);
                                    }
                                }
                                if(busiflag==false){
                                    if(countPerson>3){
                                        if((countPerson)%4==0){
                                            priPerson.add(arrString[1]);
                                        }
                                        if((countPerson)%4==1){
                                            priCert.add(arrString[1]);
                                        }
                                        if((countPerson)%4==2){
                                            priJob.add(arrString[1]);
                                        }
                                        if((countPerson)%4==3){
                                            priAddress.add(arrString[1]);
                                        }
                                    }
                                }

							}

                            if(priFlag==true){
                                orgPerson = "";
                                orgAddress = "";
                                orgHolderName = "";
                                priPerson.add(peopleFeature);
                                fileType = "个人处罚";
                                if((countPerson)%4==1){
                                    priPerson.add(arrString[1]);
                                }
                                if((countPerson)%4==2){
                                    priCert.add(arrString[1]);
                                }
                                if((countPerson)%4==3){
                                    priJob.add(arrString[1]);
                                }
                                if((countPerson)%4==0){
                                    priAddress.add(arrString[1]);
                                }
                            }
							countPerson++;
						}
					}
				}
				if(countPMaker<=elementsSpanChild.size()-1){
					stringBufferDetail.append(elementP.text()+"\n");
				}
				countPMaker++;
			}
			log.info("提取："+title+"······完成·····");
		}else{//三、主题为包括： TODO 处罚实施情况内容
			fileType ="处罚实施情况";
			punishNo = title;
			stringBufferDetail.append(elementsSpanChild.text());
			punishOrg="";
			punishDate="";
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
		map.put("fileType",fileType);

		/*log.info(		"\nseqNo:"+seqNo+"\n"+
						"punishNo:"+punishNo+"\n"+
						"orgPerson:"+orgPerson+"\n"+
						"orgAddress:"+orgAddress+"\n"+
						"orgHolderName:"+orgHolderName+"\n"+
						"releaseOrg:"+releaseOrg+"\n"+
						"releaseDate:"+releaseDate+"\n"+
						"punishOrg:"+punishOrg+"\n"+
						"punishDate:"+punishDate+"\n"
				);*/

//		log.info(priPerson.toString());
//		log.info(priCert.toString());
//		log.info(priJob.toString());
//		log.info(priAddress.toString());
//		log.info(stringBufferDetail.toString());
		//		log.info("parse demo...\n" + punishDetail);
		return  map;
	}
}
