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
			"creditchina-shandong-black-dzswsx",
			"creditchina-shandong-black-sjrsxgl",
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
			"hubei_blacklist",
			"hubei_blacklist",
			"hubei_blacklist",
			"hubei_blacklist",
			"hubei_blacklist",
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
			"creditchina-gansu-shixin-199795",
			"creditchina-gansu-black-372740",
			"creditchina_gansu_blacklist_sjryzsxxwmd",
			"creditchina-gansu-black-327147",
			"creditchina-gansu-black-137827",
			"creditchina-gansu-black-126208",
			"creditchina_gansu_zyypbhgmd",
			"creditchina-gansu-black-97898",
			"creditchina_gansu_blacklist_dzswlyyzsxhmd",
			"creditchina-gansu-black-91101",
			"creditchina_gansu_blacklist_aqscbljlhmd",
			"creditchina-gansu-black-91744",
			"creditchina-gansu-black-91754",
			"creditchina-gansu-black-91750",
			"creditchina-gansu-black-91752",
			"creditchina-gansu-black-91758",
			"creditchina-gansu-black-91760",
			"creditchina-gansu-black-91762",
			"creditchina-gansu-black-91764",
			"creditchina-gansu-black-94089",
			"creditchina-gansu-black-91732",
			"creditchina-gansu-black-94110",
			"creditchina-gansu-black-94116",
			"gansu_94084",
			"gansu_94105",
			"gansu_94101",
			"gansu_94099",
			"gansu_94096",
			"gansu_94094",
			"gansu_94092",
			"gansu_94130",
			"gansu_94126",
			"gansu_94128",
			"gansu_94124",
			"gansu_94122",
			"gansu_94120",
			"gansu_94112",
			"gansu_94107"
	};

}
