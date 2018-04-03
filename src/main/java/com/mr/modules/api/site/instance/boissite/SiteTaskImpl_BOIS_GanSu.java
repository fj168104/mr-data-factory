package com.mr.modules.api.site.instance.boissite;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mr.modules.api.site.SiteTaskExtend;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component("gansu")
@Scope("prototype")
public class SiteTaskImpl_BOIS_GanSu extends SiteTaskExtend{
    /**
     * 甘肃保监局处罚 提取所需要的信息
     * 序号、处罚文号、机构当事人名称、机构当事人住所、机构负责人姓名、
     * 当事人集合（当事人姓名、当事人身份证号、当事人职务、当事人住址）、发布机构、发布日期、行政处罚详情、处罚机关、处罚日期
     */
    public Map extractContent(String fullTxt) {
		String pubTitle="";//发布主题		
		String publishOrg="";//发布机构		
		String publishDate="";//发布时间		
		String pubOffice="";//处罚机关		
		String punishDate="";//处罚时间		
		String punishNo="";//处罚文号		
		String punishToOrg="";//受处罚机构		
		String punishToOrgAddress="";//受处罚机构地址		
		String punishToOrgHolder="";//受处罚机构负责人		
		String priPerson="";//受处罚人		（自然人）
		String priPersonCert="";//受处罚人证件	（自然人）	
		String priJob="";//受处罚人职位		
		String priAddress="";//受处罚人地址		
		String punishTxtAll="";//处罚信息
		String priBusiType="";

        
		Document doc = Jsoup.parse(fullTxt);//.replace("(", "（").replace(")", "）"));
		Element elementsTxt = doc.getElementById("tab_content");
		// 全文提取
		//punishTxtAll=elementsTxt.text();
		//String txtAll = elementsTxt.text();
		//System.out.println("全文：" + txtAll);
		Elements elementsTD = elementsTxt.getElementsByTag("TD");
		String html="";
		String s="";
		if(elementsTD.size()>0){
			for(Element elmtTD:elementsTD){
				Elements elementsP = elmtTD.getElementsByTag("P");
				if(elementsP.size()>0){
					for(Element elmtP:elementsP){
						s=elmtP.text().replaceAll("　", "").replaceAll(" ", "").trim();
						if(s.length()>0 && s.indexOf("浏览次数：")<0 &&  s.indexOf("【关闭】")<0){
							html+=(s+"\n");
						}
						s="";
						//.indexOf("浏览次数：")<0)
						//	System.out.println(elmtP.text().replaceAll("　", "").replaceAll(" ", ""));
					}
				}else{
					s=elmtTD.text();
					if(s.length()>0 && s.indexOf("浏览次数：")<0 && s.indexOf("【关闭】")<0){
						html+=(s+"\n");
					}
					s="";
				}
			}
		}
		
		punishTxtAll=html.replaceAll("　", "").replaceAll(" ", "").replaceAll("分享到：【字体：大中小】", "").trim();
		html=html.replaceAll("　", "")
				.replaceAll(" ", "")
				.replaceAll("\\)", "）")
				.replaceAll("\\(", "（")
				.replaceAll("负责人姓名：", "主要负责人姓名:")
				.replaceAll("法定代表人：", "主要负责人姓名:")
				.replaceAll("姓名：", "负责人：")
				.replaceAll("住址", "地址")
				.replaceAll("住所", "地址")
				.replaceAll("当事人姓名:", "当事人:")
				.replaceAll("职 务", "职务")
				.replaceAll("、", "，")
				.replaceAll("：", ":")
				.replaceAll("处罚文号:", "")
				.replaceAll("分享到:【字体:大中小】", "")
				.trim();
		
		
//		System.out.println(html);
//		System.out.println(html.split("\n").length);

		
		String[] contents=html.split("\n");
//		System.out.println(contents.length);
		
	

		pubTitle=contents[0];
		publishDate=contents[1].substring(contents[1].indexOf("发布时间:")+5).trim();
		int index=-1;
		for(String content:contents){
			index++;
			//System.out.println(index+"--->"+content);

			if(content.indexOf("身份证")==0){
				if(index+1<contents.length && contents[index+1].indexOf("地址")>=0)
					priAddress=contents[index+1].substring(contents[index+1].indexOf(":")+1);
				
				
				
				if(priPersonCert.length()>0)
					priPersonCert+=(","+content.substring(content.indexOf(":")+1));
				else
					priPersonCert=content.substring(content.indexOf(":")+1);
					
				if(contents[index-2].indexOf("当事人")>=0){
					if(priPerson.length()>0){
						priPerson+=(","+contents[index-2].substring(contents[index-2].indexOf(":")+1).trim());
					}else{
						priPerson=contents[index-2].substring(contents[index-2].indexOf(":")+1).trim();
					}
					
					if(contents[index-1].length()>0){
						if(priJob.length()>0){
							priJob+=(","+contents[index-1]);
						}else{
							priJob=contents[index-1];
						}
					}
				}else if (contents[index-1].indexOf("当事人")>=0){
					if(priPerson.length()>0){
						priPerson+=(","+contents[index-1].substring(contents[index-1].indexOf(":")+1).trim());
					}else{
						priPerson=contents[index-1].substring(contents[index-1].indexOf(":")+1).trim();
					}
					if(index+1<contents.length && contents[index+1].indexOf("职")>=0){
						if(contents[index+1].length()>0){
							if(priJob.length()>0){
								priJob+=(","+contents[index+1].substring(contents[index+1].indexOf(":")+1).trim());
							}else{
								priJob=contents[index+1].substring(contents[index+1].indexOf(":")+1).trim();
							}
						}
					}
					if(index+2<contents.length && contents[index+2].indexOf("址")>=0){
						if(contents[index+2].length()>0){
							if(priAddress.length()>0){
								priAddress+=(","+contents[index+2].substring(contents[index+2].indexOf(":")+1).trim());
							}else{
								priAddress=contents[index+2].substring(contents[index+2].indexOf(":")+1).trim();
							}
						}
					}
				}
			}
			
			
			if(publishOrg.length()<1 && (content.indexOf("监管局")>0 || content.indexOf("保监局")>0)){
				if(content.indexOf("中国保监会")>=0){
					if(content.indexOf("支局")>0){
						publishOrg=content.substring(content.indexOf("中国保监会"));
						publishOrg=publishOrg.substring(0,publishOrg.indexOf("支局")+2);
					}else if(content.indexOf("分局")>0){
						publishOrg=content.substring(content.indexOf("中国保监会"));
						publishOrg=publishOrg.substring(0,publishOrg.indexOf("分局")+2);
					}else{
						publishOrg=content.substring(content.indexOf("中国保监会"));
						publishOrg=publishOrg.substring(0,publishOrg.indexOf("局")+1);
					}
				}else if(content.indexOf("中国保险")>=0){
					if(content.indexOf("支局")>0){
						publishOrg=content.substring(content.indexOf("中国保险"));
						publishOrg=publishOrg.substring(0,publishOrg.indexOf("支局")+2);
					}else if(content.indexOf("分局")>0){
						publishOrg=content.substring(content.indexOf("中国保险"));
						publishOrg=publishOrg.substring(0,publishOrg.indexOf("分局")+2);
					}else{
						publishOrg=content.substring(content.indexOf("中国保险"));
						publishOrg=publishOrg.substring(0,publishOrg.indexOf("局")+1);
					}
				}else if(content.indexOf("局行政处罚决定书")>0){
					publishOrg=content.substring(0,content.indexOf("行政处罚"));
				}
				/*
				else if(content.indexOf("监管局")>0){
					pubOrg=content.substring(content.indexOf("监管局")-2,content.indexOf("监管局")+3);
				}else if(content.indexOf("保监局")>0){
					pubOrg=content.substring(content.indexOf("保监局")-2,content.indexOf("保监局")+3);
				}
				*/
			}
			
			
			if(pubOffice.length()<1 && 
					(content.indexOf("作出")>0 && content.indexOf("处罚")>0)){
				pubOffice = content.indexOf("于")>0 ? content.substring(0,content.indexOf("于")):
					content.substring(0,content.indexOf("作出"));
				if(pubOffice.length()>20 || pubOffice.indexOf("我局")>=0)
					pubOffice="";
			}
			
			if(punishDate.length()<1 && 
					(content.indexOf("于")>0 && content.indexOf("作")>0 && content.indexOf("处罚")>0) && content.indexOf("年")>0 && content.indexOf("月")>0 && content.indexOf("日")>0){
				punishDate = content.indexOf("于")<0 ? 
						content.substring(content.indexOf("于")+1, content.indexOf("作")):publishDate;
			}
			
			if(punishNo.length()<1 && content.indexOf("号")>0 && content.indexOf("监罚")>0){
				punishNo=content.indexOf("作出")>0 ? 
						content.substring(content.indexOf("作出")+2,content.indexOf("号")+1):
							content.substring(content.indexOf("监罚")-2,content.indexOf("号")+1);
			}
			
			if(punishToOrg.length()<1 && 
					(content.indexOf("公司")>0 ||content.indexOf("以下简称")>0 ||content.indexOf("工作单位:")>0 ||content.indexOf("受处罚机构")>0 || content.indexOf("受处罚人名称")>0 || content.indexOf("受处罚人:")>0 || content.indexOf("当事人")>0 || content.indexOf("被处罚人:")>0 || content.indexOf("受处罚机构名称")>0 || content.indexOf("对")>0 && (content.indexOf("进行")>0 || content.indexOf("警告")>0 || content.indexOf("处罚")>0))){
				//System.out.println("-----------------------------");
				if(content.indexOf("公司")>0){
					if(content.indexOf("服务部")>0){
						punishToOrg=content.substring(0,content.indexOf("服务部")+3);
					}else if(content.indexOf("营业所")>0){
						punishToOrg=content.substring(0,content.indexOf("营业所")+3);
					}else if(content.indexOf("分公司")>0){
						punishToOrg=content.substring(0,content.indexOf("分公司")+3);
					}else if(content.indexOf("支公司")>0){
						punishToOrg=content.substring(0,content.indexOf("支公司")+3);
					}else{
						punishToOrg=content.substring(0,content.indexOf("公司")+2);
					}
					if(punishToOrg.indexOf("经查，")>=0){
						punishToOrg=punishToOrg.substring(punishToOrg.indexOf("经查，")+3);
					}
					if(punishToOrg.indexOf("名称")>=0){
						punishToOrg=punishToOrg.substring(punishToOrg.indexOf(":")+1);
					}
					if(punishToOrg.indexOf("期间，")>=0){
						punishToOrg=punishToOrg.substring(punishToOrg.indexOf("期间，")+3);
					}
					if(punishToOrg.indexOf("工作单位")>=0 ||punishToOrg.indexOf("受处罚机构名称:")>=0 || punishToOrg.indexOf("受处罚机构:")>=0 || punishToOrg.indexOf("受处罚人:")>=0 || punishToOrg.indexOf("受处罚人名称:")>=0 || punishToOrg.indexOf("当事人:")>=0 || punishToOrg.indexOf("被处罚人:")>=0){
						punishToOrg=punishToOrg.substring(punishToOrg.indexOf(":")+1);
						if(punishToOrg.indexOf(",")>0)
							punishToOrg=punishToOrg.substring(0,punishToOrg.indexOf(","));
					}

					if(punishToOrg.indexOf("（")>0){
						punishToOrg=punishToOrg.substring(punishToOrg.indexOf("（")+1);
					}
					if(punishToOrg.indexOf("(")>=0){
						punishToOrg=punishToOrg.substring(punishToOrg.indexOf("(")+1);
					}
					if(punishToOrg.indexOf(":")>0)
						punishToOrg="";
				}else if(content.indexOf("(以下简称")>0 && content.indexOf("公司")>0){
					//punishOrg=content.substring(content.indexOf("以下简称")+4,content.indexOf(")")).trim();
					punishToOrg=content.substring(0,content.indexOf("(以下简称")).trim();
					if(punishToOrg.indexOf("中国")>0){
						punishToOrg=punishToOrg.substring(punishToOrg.indexOf("中国"));
					}
				}else if(content.indexOf("受处罚人名称")>=0){
					punishToOrg=content.substring(content.indexOf("名称")+3).trim();
					punishToOrg = punishToOrg.indexOf("以下简称")>0 ? punishToOrg.substring(0,punishToOrg.indexOf("以下简称")-1):punishToOrg;
				}else if(content.indexOf("受处罚机构名称")>=0){
					punishToOrg=content.substring(content.indexOf("名称")+3).trim();
					punishToOrg = punishToOrg.indexOf("以下简称")>0 ? punishToOrg.substring(0,punishToOrg.indexOf("以下简称")-1):punishToOrg;
				}else if(content.indexOf("当事人")>=0){
					punishToOrg=content.substring(content.indexOf("当事人")+4).trim();
					punishToOrg = punishToOrg.indexOf("以下简称")>=0 ? punishToOrg.substring(0,punishToOrg.indexOf("以下简称")-1):punishToOrg;
				}else{
					if(content.indexOf("警告")>0 && content.indexOf("对")>0){
						punishToOrg = content.substring(content.indexOf("对")+1, content.indexOf("警告"));
					}else if(content.indexOf("处罚")>0 && content.indexOf("对")>0){
						punishToOrg = content.substring(content.indexOf("对")+1, content.indexOf("处罚"));
					}else if(content.indexOf("进行")>0 && content.indexOf("对")>0){
						punishToOrg =	content.substring(content.indexOf("对")+1, content.indexOf("进行"));
					}
				}
				while(punishToOrg.indexOf("对")>=0){
					punishToOrg=punishToOrg.substring(punishToOrg.indexOf("对")+1);
				}
				if(punishToOrg.indexOf("审核，")>=0){
					punishToOrg=punishToOrg.substring(punishToOrg.indexOf("审核，")+3);
				}
				if(punishToOrg.indexOf("任")>=0){
					punishToOrg=punishToOrg.substring(punishToOrg.indexOf("任")+1);
				}
				if(punishToOrg.indexOf("你公司")>=0 || punishToOrg.indexOf("你司")>=0 || punishToOrg.indexOf("给")>=0 || punishToOrg.indexOf("你")>=0  || punishToOrg.indexOf("进行")>=0){
					punishToOrg="";
				}
				if(punishToOrg.length()>25){
					punishToOrg="";
				}
			}
			
			
			if(punishToOrgAddress.length()<1 && content.indexOf("地址")>=0){
				punishToOrgAddress=content.substring(content.indexOf("地址")+3).trim();
//				if(punishToOrgAddress.indexOf("路")<1 && punishToOrgAddress.indexOf("号")<1 && punishToOrgAddress.indexOf("弄")<1)
//					punishToOrgAddress="";
			}
			
			if(punishToOrgHolder.length()<1){
				if(content.indexOf("负责人")>=0 || content.indexOf("法定代表人")>=0){
					if(content.indexOf("名")>0){
						punishToOrgHolder=content.substring(content.indexOf("名")+1).trim();
					}else if(content.indexOf("名")>content.length()-2){
						punishToOrgHolder=content.substring(content.indexOf("人")+2).trim();
					}
					if(punishToOrgHolder.indexOf(":")>=0)
						punishToOrgHolder=punishToOrgHolder.substring(punishToOrgHolder.indexOf(":")+1);
					if(punishToOrgHolder.length()>20)
						punishToOrgHolder="";
				}
			}
			
			
			if(priPerson.length()<1 &&(content.indexOf("(身份证")>0)){
				priPerson=content.substring(0,content.indexOf("(身份证"));
				if(priPerson.length()>0){
					if(priPerson.indexOf(")")>0){
						priPerson=priPerson.substring(priPerson.indexOf(")")+1);
					}else if(priPerson.indexOf("经理")>0){
						priPerson=priPerson.substring(priPerson.indexOf("经理")+2);
					}else if(priPerson.indexOf("主任")>0){
						priPerson=priPerson.substring(priPerson.indexOf("主任")+2);
					}else if(priPerson.indexOf("董事长")>0){
						priPerson=priPerson.substring(0,priPerson.indexOf("董事")+3);
					}else if(priPerson.indexOf("董事")>0){
						priPerson=priPerson.substring(priPerson.indexOf("董事")+2);
					}else if(priPerson.indexOf("助理")>0){
						priPerson=priPerson.substring(priPerson.indexOf("助理")+2);
					}
				}
			}
			
			if(priJob.length()<1 && (content.indexOf("职务:")>=0 || content.indexOf("任")>0 || content.indexOf("经理")>0|| content.indexOf("助理")>0 || content.indexOf("主任")>0 || content.indexOf("董事")>0)){
				//System.out.println("----------------");
				if(content.indexOf("任")>0 && content.indexOf("责任")<0 && content.indexOf("任用")<0 && content.indexOf("的")<0)
					priJob=content.substring(content.indexOf("任")+1);
				else if(content.indexOf("职务:")>=0)
					priJob=content.substring(content.indexOf("职务:")+3);
				else if(punishToOrg.length()>0 && content.indexOf(punishToOrg)>=0){
					priJob=content.substring(content.indexOf(punishToOrg)+punishToOrg.length());
				}
				
				
				if(priJob.indexOf("经理")>0){
					priJob=priJob.substring(0,priJob.indexOf("经理")+2);
				}else if(priJob.indexOf("主任")>0){
					priJob=priJob.substring(0,priJob.indexOf("主任")+2);
				}else if(priJob.indexOf("董事长")>0){
					priJob=priJob.substring(0,priJob.indexOf("董事")+3);
				}else if(priJob.indexOf("董事")>0){
					priJob=priJob.substring(0,priJob.indexOf("董事")+2);
				}else if(priJob.indexOf("助理")>0){
					priJob=priJob.substring(0,priJob.indexOf("助理")+2);
				}else if(priJob.indexOf("人")>0){
					priJob=priJob.substring(0,priJob.indexOf("人")+1);
				}
				
				if(priJob.length()>35){
					priJob="";
				}

			}
			
			if(priPersonCert.length()<1 && (content.indexOf("身份证")>0)){
				if(content.indexOf("(身份证号码")>0){
					priPersonCert=content.substring(content.indexOf("(身份证号码")+6);
					priPersonCert=priPersonCert.substring(0,priPersonCert.indexOf(")"));
				}
			}

		}
		if(publishOrg.length()<1 || publishOrg.length()>20){
			int i3Tmp=punishNo.indexOf("罚");
			if(i3Tmp>0)
				publishOrg=punishNo.substring(0,i3Tmp)+"局";
		}
		
		if(punishToOrgHolder.length()<1){
			int i2Tmp=html.indexOf("主要负责人姓名:");
			if(i2Tmp>=0){
				if(i2Tmp>=0){
					punishToOrgHolder=html.substring(i2Tmp+"主要负责人姓名:".length());
					punishToOrgHolder=punishToOrgHolder.substring(0,punishToOrgHolder.indexOf("\n"));
				}
			}
			i2Tmp=html.indexOf("主要负责人:");
			if(i2Tmp>=0){
				if(i2Tmp>=0){
					punishToOrgHolder=html.substring(i2Tmp+"主要负责人:".length());
					punishToOrgHolder=punishToOrgHolder.substring(0,punishToOrgHolder.indexOf("\n"));
				}
			}
			i2Tmp=html.indexOf("受处罚人:");
			if(punishToOrgHolder.length()<1 && i2Tmp>=0){
				punishToOrgHolder=html.substring(i2Tmp+"受处罚人:".length());
				punishToOrgHolder=punishToOrgHolder.substring(0,punishToOrgHolder.indexOf("\n"));
			}
			
			i2Tmp=html.indexOf("当事人:");
			if(punishToOrgHolder.length()<1 && i2Tmp>=0){
				punishToOrgHolder=html.substring(i2Tmp+"当事人:".length());
				punishToOrgHolder=punishToOrgHolder.substring(0,punishToOrgHolder.indexOf("\n"));
			}
		if(punishToOrgHolder.indexOf("，")>0){
			punishToOrgHolder=punishToOrgHolder.substring(0,punishToOrgHolder.indexOf("，"));
		}
		while(punishToOrgHolder.indexOf(":")>=0)
			punishToOrgHolder=punishToOrgHolder.substring(punishToOrgHolder.indexOf(":")+1);
		}
		
		
		if(priPerson.length()<1){
			String[] pp=html.split("受处罚人:");
			int iIndex=0;
			if(pp.length>=2){
				for(String p:pp){
					iIndex++;
					if(iIndex>1){
						priPerson+=(p.substring(0,p.indexOf("\n"))+",");
					}
				}
				if(priPerson.indexOf(",")>0)
					priPerson=priPerson.substring(0,priPerson.length()-1);
				if(priPerson.indexOf("，")>0)
					priPerson=priPerson.substring(0,priPerson.indexOf("，"));
				
			}
		}
		
		if(publishOrg.length()<1 || publishOrg.length()>20)
			publishOrg="中国保险监督管理委员会甘肃监管局";
		if(pubOffice.length()<1 || pubOffice.length()>20)
			pubOffice=publishOrg;
		if(punishDate.length()<1)
			punishDate=publishDate;

		if(contents.length>=1 && (contents[contents.length-1].indexOf("月")>0 && contents[contents.length-1].indexOf("年")>0 && contents[contents.length-1].indexOf("日")>0)
					|| contents[contents.length-1].indexOf("-")>0 
					|| contents[contents.length-1].indexOf("－")>0 
				){
				punishDate=contents[contents.length-1];
		}
		
		if(punishDate.indexOf("于")>=0){
			punishDate=punishDate.substring(punishDate.indexOf("于")+1);
			punishDate=punishDate.substring(0,punishDate.indexOf("日")+1);
		}
		if(punishDate.indexOf("年")>=4){
			punishDate=punishDate.substring(punishDate.indexOf("年")-4);
		}
		
		
	
	
	log.info("发布主题：" + pubTitle);
	log.info("发布机构：" + publishOrg);
	log.info("发布时间：" + publishDate);
	log.info("处罚机关：" + pubOffice);
	log.info("处罚时间：" + punishDate);
	log.info("处罚文号：" + punishNo);
	log.info("受处罚机构：" + punishToOrg);
	log.info("受处罚机构地址：" + punishToOrgAddress);
	log.info("受处罚机构负责人：" + punishToOrgHolder);
	log.info("受处罚人：" + priPerson);
	log.info("受处罚人证件：" + priPersonCert);
	log.info("受处罚人职位：" + priJob);
	log.info("受处罚人地址：" + priAddress);

	
	
	
	
	
	return null;
}


@Override
protected String execute() throws Throwable {
    String url = "http://www.circ.gov.cn/web/site14/tab3389/info4076022.htm";
    String fullTxt = getData(url);
    extractContent(fullTxt);
    log.info("----------------------甘肃保监局处罚信息提取完成------------------------");
    return null;
}
}	
