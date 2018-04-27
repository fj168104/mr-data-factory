package com.mr.modules.api.site.instance.boissite;

import com.mr.common.util.SpringUtils;
import com.mr.modules.api.TaskStatus;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.ResourceGroup;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("bois")
@Scope("prototype")
public class SiteTaskImpl_BOIS_Facade extends SiteTaskExtend {

	@Override
	protected String execute() throws Throwable {
		for (String groupIndex : groupIndexs) {
			ResourceGroup task = (ResourceGroup) SpringUtils.getBean(groupIndex);

			log.info(groupIndex + " calling result：" + TaskStatus.getName(task.start()));
			while (!task.isFinish()){
				Thread.sleep(3000);
			}
			log.info(groupIndex + " executing complete.");
		}

		return null;
	}

	String groupIndexs[] = {"bois_anhui",
			"bois_beijing",
			"bois_chongqing", "bois_dalian",
			"bois_fujian", "bois_gansu",
			"bois_guangdong", "bois_guangxi",
			"bois_guizhou", "bois_hainan",
			"bois_heilongjiang", "bois_henan",
			"bois_hubei", "bois_hunan",
			"bois_jiangsu", "bois_jiangxi",
			"bois_jilin", "bois_liaoning",
			"bois_neimenggu", "bois_ningbo",
			"bois_ningxia", "bois_qingdao",
			"bois_qinghai", "bois_shaanxi",
			"bois_shandong", "bois_shanghai",
			"bois_shantou", "bois_shanxi",
			"bois_shenzhen", "bois_sichuan",
			"bois_suzhou", "bois_tangshan",
			"bois_wenzhou", "bois_xiamen",
			"bois_xinjiang", "bois_xizang",
			"bois_yantai", "bois_yunnan",
			"bois_zhejiang"};

}
