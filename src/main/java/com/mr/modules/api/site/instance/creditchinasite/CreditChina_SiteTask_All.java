package com.mr.modules.api.site.instance.creditchinasite;

import com.mr.common.util.SpringUtils;
import com.mr.modules.api.TaskStatus;
import com.mr.modules.api.site.ResourceGroup;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 组装任务
 */
@Slf4j
@Component("creditchina_sitetask_all")
@Scope("prototype")
public class CreditChina_SiteTask_All extends SiteTaskExtend {

	@Override
	protected String execute() throws Throwable {
		for (String groupIndex : groupIndexs) {
			ResourceGroup task = (ResourceGroup) SpringUtils.getBean(groupIndex);

			log.info(groupIndex + " calling result：" + TaskStatus.getName(task.start()));
			while (!task.isFinish()) {
				Thread.sleep(3000);
			}
			log.info(groupIndex + " executing complete.");
		}

		return null;
	}

	String groupIndexs[] = {
			"creditchinamainsite0001",
			"creditchinamainsite0002",
			"creditchinamainsite0003",
			"creditchinamainsite0004",
			"creditchinamainsite0006",
			"creditchinamainsite0006",
			"creditchinashanxisiteentblacklist",
			"creditchinashanxisiteperblacklist",
			"shanghaisite_sxmdc",
			"shanghaisite_zdgzmd",
			"shanghaisite_ffjjyjmd",
			"shanghaisite_sxbzxr",
			"shanghaisite_zfcgyzwfsxmd",
			"shanghaisite_xzcf",
			"creditchinaanhui_aqsclylhcjdx",
			"creditchinaanhui_zdsswfaj_gs",
			"creditchina-anhui-l-tax",
			"creditchinaanhui_zdxmjcxx",
			"creditchinaanhui_sgkjsqqyhjxypjpjhbjs",
			"creditchina-shandong-black-sjrsxgl",
			"creditchina-shandong-black-dzswsx",
			"creditchina-shandong-black-sjrsxslqy",
			"creditchina-shandong-n-tax",
			"creditchina-shandong-l-tax",
			"creditchina-shandong-black-qsgg",
			"creditchina-shandong-black-yzsxzwr",
			"creditchina-shandong-black-zqscjr",
			"creditchina-shandong-black-crjjyjysx",
			"creditchina-shandong-black-aqsc",
			"creditchinashandong_xzcf",
			"creditchinahenansite0001",
			"creditchinahenansite0002",
			"creditchinahenansite0003",
			"creditchinahenansite0004",
			"creditchinahenansite0005",
			"creditchinahenansite0006",
			"creditchinahenansite_xzcf",
			"creditchinahunansite",
			"creditchinaguangdongitemlist",
			"creditchinaguangdong_xzcf",
			"creditchinahainan_xzcf",
			"creditchinasichuan_blacklist",
			"guizhou_11239",
			"guizhou_xzcf",
			"creditchina_ningxia_blacklist",
			"creditchina_ningxia_xzcf",
			"shanxi_xzcf",
			"shanxi_personblack",
			"shanxi_legalblack",
			"hubei_publicityPunishment",
			"hubei_blacklist",
			"hebei_xzcf",
			"hebei_xyheib",
			"xizang_jck",
			"xizang_fyqy",
			"xizang_fygr",
			"xizang_tq",
			"creditchina-gansu-xzcf-qy",
			"creditchina-gansu-xzcf-gr",
			"creditchina-gansu-sxbzxr-gr",
			"creditchina-gansu-sxbzxr-qy",
			"creditchina-gansu-shixin-340085",
			"creditchina-gansu-shixin-95838",
			"creditchina-gansu-shixin-90835",
			"creditchina-gansu-shixin-199795"
	};

}
