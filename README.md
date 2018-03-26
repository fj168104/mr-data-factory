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
