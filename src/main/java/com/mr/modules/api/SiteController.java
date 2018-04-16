package com.mr.modules.api;

import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fengj
 * Created by MR on 2018/3/15.
 */
@RestController("APISiteController")
public class SiteController extends BaseController {

	@Resource
	private SiteService siteService;

	@Autowired
	private HttpServletRequest request;
	/**
	 * path /{indexId}/{callId}
	 *
	 * @return
	 */
	@RequestMapping(value = "/start/{indexId}/{callId}")
	public ModelMap start(@PathVariable("indexId") String indexId, @PathVariable("callId") String callId) throws Exception {
		ModelMap map = new ModelMap();
		String code = "";
		if(request.getQueryString()== null){//没有传参
			code = siteService.start(indexId, callId);
		}else{
			Map mapParams = new HashMap();
			if(request.getQueryString().contains("region=")){
				String region = request.getParameter("region");
				mapParams.put("region",region);
				code = siteService.startByParams(indexId,callId,mapParams);
			}else if(request.getQueryString().contains("publishDate=")){
				String publishDate = request.getParameter("publishDate");
				mapParams.put("publishDate",publishDate);
				code = siteService.startByParams(indexId,callId,mapParams);
			}else if(request.getQueryString().contains("url=")){
				String url = request.getParameter("url");
				log.info("---------url-----------"+url);
				mapParams.put("url",url);
				code = siteService.startByParams(indexId,callId,mapParams);
			}else{
				code = "params-error";
			}

		}
		map.addAttribute("code",code);
		return map;
	}

	@RequestMapping(value = "/result_code/{callId}")
	public ModelMap getResultCode(@PathVariable("callId") String callId) throws Exception {
		ModelMap map = new ModelMap();
		map.addAttribute("result_code", siteService.getResultCode(callId));
		return map;
	}

	@RequestMapping(value = "/is_finish/{callId}")
	public ModelMap isFinish(@PathVariable("callId") String callId) throws Exception {
		ModelMap map = new ModelMap();
		map.addAttribute("finish", siteService.isFinish(callId));
		return map;
	}

	@RequestMapping(value = "/throwable_info/{callId}")
	public ModelMap getThrowableInfo(@PathVariable("callId") String callId) throws Exception {
		ModelMap map = new ModelMap();
		map.addAttribute("throwable_info", siteService.getThrowableInfo(callId));
		return map;
	}

	@RequestMapping(value = "/del/{callId}")
	public ModelMap delSiteTaskInstance(@PathVariable("callId") String callId) throws Exception {
		ModelMap map = new ModelMap();
		map.addAttribute("del_result", siteService.delSiteTaskInstance(callId));
		return map;
	}

	/**
	 * 删除数据
	 *
	 * @param primaryKey
	 * @param source
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/data/delete")
	public ModelMap delSiteData(@RequestParam(value = "primaryKey", required = false) String primaryKey,
								@RequestParam(value = "source", required = false) String source) throws Exception {
		ModelMap map = new ModelMap();
		if (!StringUtils.isEmpty(primaryKey)) {
			map.addAttribute("delete_result", siteService.deleteByBizKey(primaryKey));
		} else if (!StringUtils.isEmpty(source)) {
			map.addAttribute("delete_result", siteService.deleteBySource(source));
		} else {
			map.addAttribute("delete_result", 0);
		}

		return map;
	}

	@RequestMapping(value = "/data/{indexId}")
	public ModelMap fetchOneRecord(@PathVariable("indexId") String indexId,
								   FinanceMonitorPunish financeMonitorPunish) throws Exception {
		ModelMap map = new ModelMap();
		if (StringUtils.isEmpty(siteService.fetchOneRecord(indexId, financeMonitorPunish))) {
			map.addAttribute("del_result", "fail");
		} else {
			map.addAttribute("del_result", financeMonitorPunish);
		}

		return map;
	}

	/**
	 * curl -F "file=@xxx.xlsx" http://localhost:8082/importData
	 *
	 * @param multiReq
	 * @return
	 */
	@RequestMapping(value = "/importData", method = RequestMethod.POST)
	public ModelMap importData(
			MultipartHttpServletRequest multiReq) throws Exception {
		ModelMap map = new ModelMap();
		// 获取上传文件的路径
		String uploadFilePath = multiReq.getFile("file").getOriginalFilename();

		FileInputStream fis = (FileInputStream) multiReq.getFile("file").getInputStream();
		String result = siteService.importData(fis, uploadFilePath);
		map.addAttribute("imort_result", result);
		return map;
	}

}
