package com.mr.modules.api;

import com.google.common.base.Strings;
import com.mr.modules.api.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author fengj
 * Created by MR on 2018/3/15.
 */
@RestController("APISiteController")
public class SiteController extends BaseController {

	@Resource
	private SiteService siteService;


	/**
	 * path /{indexId}/{callId}
	 *
	 * @return
	 */
	@RequestMapping(value = "/start/{indexId}/{callId}")
	public ModelMap start(@PathVariable("indexId") String indexId, @PathVariable("callId") String callId) throws Exception {
		ModelMap map = new ModelMap();
		map.addAttribute("code", siteService.start(indexId, callId));
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

	@RequestMapping(value = "/data/delete")
	public ModelMap delSiteData(@RequestParam(value = "primaryKey", required = false) String primaryKey,
								@RequestParam(value = "object",required = false) String object) throws Exception {
		ModelMap map = new ModelMap();
		if (!StringUtils.isEmpty(primaryKey)) {
			map.addAttribute("delete_result", siteService.deleteByBizKey(primaryKey));
		} else if (!StringUtils.isEmpty(object)) {
			map.addAttribute("delete_result", siteService.deleteByObject(object));
		} else {
			map.addAttribute("delete_result", 0);
		}

		return map;
	}

}
