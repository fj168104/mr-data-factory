package com.mr.modules.api.site.instance;

import com.mr.modules.api.mapper.FinanceMonitorPunishMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by feng on 18-3-16
 * demo
 */

@Slf4j
@Component("demo")
@Primary
@Scope("prototype")
public class DemoSiteTask extends SiteTask {

	@PostConstruct
	public void initAfter(){
		log.info("demo instance created..............");
	}

	@Autowired
	private FinanceMonitorPunishMapper financeMonitorPunishMapper;
	/**
	 *
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	@Override
	protected String execute() throws Throwable {
		log.info("*******************call demo task**************");
		Thread.sleep(3 * 1000);

		FinanceMonitorPunish financeMonitorPunish = new FinanceMonitorPunish();
		financeMonitorPunish.setPrimaryKey("aaa");
		financeMonitorPunish.setPunishNo("111");
		financeMonitorPunish.setPunishTitle("证监会");
		financeMonitorPunishMapper.insert(financeMonitorPunish);
		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		return null;
	}

}
