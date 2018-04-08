# mr-data-factory  
ZT 数据抓取与结构化  

## 项目安装  
该项目是springboot工程，使用到了lombok插件， 需要安装该插件。  
如果eclipse，下载 lombok（https://projectlombok.org/downloads/lombok.jar)    
直接双击安装即可。  
如果是IDE，编译器会提示安装，非常简单。    
lombok安装完成后，导入mr-data-factory，编译通过即可。  
打开application.yml文件，修改 download-dir: {你本机的下载目录}

## 运行demo
任务demo类：com.mr.modules.api.site.instance.DemoSiteTask  
里面的任务逻辑： 
```
log.info("*******************call site task**************");  
Thread.sleep(30 * 1000);  
return null;  
```
运行com.mr.RootApplication，等待启动完成后开始测试。  
1、启动一个任务，发送http请求：http://localhost:8082/api/start/demo/1 ，  
如果返回为：  
{
    "code": "success"  
}
表示运行启动成功。  

2、查询任务执行状态：发送http请求：http://localhost:8082/api/is_finish/1  
如果返回为：  
{  
    "finish": true  
}  
表示运行完成   
如果返回为：  
{  
    "finish": false  
}  
表示运行未完成  

3、查询返回结果,发送http请求：http://localhost:8082/api/result_code/1  
如果返回为：  
{  
    "result_code": "success"  
}  
表示运行成功  
如果返回为：  
{  
    "result_code": "executing"  
}  
表示运行中  
如果返回为：  
{  
    "result_code": "failure"  
}  
表示运行失败  

4、查询错误信息,发送http请求：http://localhost:8082/api/throwable_info/1  
{  
    "throwable_info": "task not exists..."  
}  
表示执行失败，并且错误提示为：task not exists...， 如果运行成功, 无错误提示，即：throwable_info：""  

5、删除任务,发送http请求：http://localhost:8082/api/del/1  
如果返回为：  
{  
    "del_result": true  
}  
表示删除成功  

## 接口描述  

indexId：任务名：如任务demo类：com.mr.modules.api.site.instance.DemoSiteTask 对应名为demo， 在SiteTaskDict中定义  
callId：任务调用ID， 必须全局唯一，由客户端控制  

1、 开启任务：/api/start/{indexId}/{callId}   

2、任务完成状态查询：/api/is_finish/{callId}  

3、返回结果查询：/api/result_code/{callId}  

4、错误信息查询：/api/throwable_info/{callId} 

5、删除任务：/api/del/{callId}  

6、删除数据：/api/data/delete  
 ```
 params:  
 primary = {primary}  主键
 source={source}     来源
```

7、抓取单条数据：/api/data/{indexId}  
   格式样例：  curl -d "url=http://www.neeq.com.cn/uploads/1/file/public/201803/20180330182459_4nd3tuq1j5.pdf&punishTitle=关于对毛龙兵采取自律监管措施的决定&partyPerson=毛龙兵" "http://localhost:8082/api/data/site1"  
   换行符：使用\r\n替换  
 ```
 
 params:  
 根据具体填写参数, 例如：
 Site1：http://localhost:8082/api/data/site1
 个人当事人：
 url: http://www.neeq.com.cn/uploads/1/file/public/201803/20180330182459_4nd3tuq1j5.pdf  
 punishTitle: 关于对毛龙兵采取自律监管措施的决定  
 partyPerson: 毛龙兵  
 机构当事人：
 url: http://www.neeq.com.cn/uploads/1/file/public/201803/20180330182459_4nd3tuq1j5.pdf  
 punishTitle: 关于对安徽天智信息科技集团股份有限公司及相关当事人采取纪律处分的决定
 partyInstitution: 关于对安徽天智信息科技集团股份有限公司及相关当事人采取纪律处分的决定
 companyFullName: ST天智
  
 Site2：http://localhost:8082/api/data/site2
 url: http://www.csrc.gov.cn/pub/beijing/bjxzcf/201803/t20180314_335261.htm
 region: 北京  
 punishTitle: 中国证券监督管理委员会北京监管局行政处罚决定书（文细棠）  
 publishDate: 2018年3月7日  
  
 Site4:http://localhost:8082/api/data/site4  
 url:http://www.sse.com.cn/disclosure/credibility/supervision/measures/ident/c/af6f636d-4bf6-455e-b6b9-fdc25552eec3.pdf  
 stockCode:600423-1  
 stockShortName:柳化股份-1  
 supervisionType:通报批评  
 punishDate:2017-09-07  
 punishTitle:关于对柳州化工股份有限公司及其控股股东柳州化学工业集团有限公司和有关责任人予以纪律处分的决定-1   
 
 Site5:http://localhost:8082/api/data/site5  
  url:http://www.sse.com.cn/disclosure/credibility/bonds/disposition/criticism/c/c_20180205_4459490.shtml  
  stockCode:125884-1  
  stockShortName:15都堰债-1  
  supervisionType:公开谴责  
  punishDate:2018-02-02  
  punishTitle:关于对柳州化工股份有限公司及其控股股东柳州化学工业集团有限公司和有关责任人予以纪律处分的决定-1   
  
 Site6:http://localhost:8082/api/data/site6  
   url:http://www.sse.com.cn/disclosure/credibility/regulatory/punishment/c/4490049.docx    
   punishDate:2018-03-31  
   punishTitle:关于对卢荣妹名下证券账户实施限制交易纪律处分的决定-1   
 
 Site7:http://localhost:8082/api/data/site7  
    url:http://www.szse.cn/UpFiles/cfwj/2018-03-22_002207787.pdf    
    punishDate:2018-03-23  
    punishTitle:关于对新疆准东石油技术股份有限公司及相关当事人给予纪律处分的决定-1   
    partyInstitution:新疆准东石油技术股份有限公司、创越能源集团有限公司  
    companyShortName:*ST准油-1  
    companyCode:002207-1  
    punishCategory:通报批评-1  
 
 Site8:http://localhost:8082/api/data/site8  
     url:http://www.szse.cn/UpFiles/cfwj/2018-02-27_002667770.pdf    
     punishDate:2018-02-27  
     punishTitle:关于对利安达会计师事务所（特殊普通合伙）注册会计师蒋淑霞、李杰给予通报批评的决定-1   
     partyInstitution:蒋淑霞、李杰  
     companyShortName:鞍重股份-1  
     companyCode:002667-1  
     punishCategory:通报批评-1
     intermediaryCategory:中介机构人员   
 
 Site9:http://localhost:8082/api/data/site9  
      url:http://www.szse.cn/UpFiles/zqjghj/zqjghj_db319195-ec5b-4232-815d-bca06cc36663.pdf    
      publishDate:2018-01-29  
      punishTitle:关于对重庆西彭铝产业区开发投资有限公司及相关当事人给予通报批评处分的决定   
      partyInstitution:重庆西彭铝产业区开发投资有限公司及相关当事人  
      punishCategory:发行人及相关当事人  
      punishNo:深证上【2018】61号  
      relatedBond:16西彭债，118619
                  16西彭02，118690
      
      
      
       
 
```

## 任务开发：
1、继承com.mr.modules.api.site.SiteTask，实现execute()方法。  
任务执行返回值：""或者null为成功， 其它为失败。  
参考：com.mr.modules.api.site.instance.DemoSiteTask  
2、在开发好的SiteTask实现类上添加注解：  
@Component("{name}"),  
@Scope("prototype")  
说明：{name}为task任务名,等于前文开启接口/api/start/{indexId}/{callId} 中的{indexId}  
3、测试任务，参考前文描述《运行demo》。  

## 任务开发内容  
1、网页信息抓取逻辑  
2、解析网页逻辑  
3、持久化  

## 任务开发注意  
1、任务开发过程中用到功能尽量使用spring集成的工具。    
如：http访问类请使用 RestTemplate 来操作，可以通过SpringUtils.getBean("restTemplate")来获取。  
2、任务开发涉及到的三部分尽量独立，至少分开三个方法，便于后期维护。  
3、任务开发过程中只写抓取解析保存逻辑，不要考虑调度安全等问题，这些功能其它部分来考虑。  
4、错误没有特殊需要直接抛出来，由框架统一处理。   
