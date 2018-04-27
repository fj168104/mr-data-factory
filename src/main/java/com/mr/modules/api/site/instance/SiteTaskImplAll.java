package com.mr.modules.api.site.instance;

import com.mr.common.util.SpringUtils;
import com.mr.modules.api.TaskStatus;
import com.mr.modules.api.site.ResourceGroup;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component("site_all")
@Scope("prototype")
public class SiteTaskImplAll extends SiteTaskExtend {

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

	String groupIndexs[] = {"site1", "site2", "site3", "site4", "site5",
							"site6", "site7", "site8", "site9", "site10"};

}
