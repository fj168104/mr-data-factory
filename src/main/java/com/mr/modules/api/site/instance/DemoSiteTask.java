package com.mr.modules.api.site.instance;

import com.mr.common.OCRUtil;
import com.mr.framework.core.date.DateUtil;
import com.mr.framework.core.io.FileUtil;
import com.mr.framework.core.util.CharsetUtil;
import com.mr.modules.api.mapper.FinanceMonitorPunishMapper;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.site.SiteTask;
import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
public class DemoSiteTask extends SiteTaskExtend {

	@PostConstruct
	public void initAfter() {
		log.info("demo instance created..............");
	}

	@Autowired
	private FinanceMonitorPunishMapper financeMonitorPunishMapper;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String from;

	/**
	 * @return ""或者null为成功， 其它为失败
	 * @throws Throwable
	 */
	protected String execute() throws Throwable {
		log.info("*******************call demo task**************");
		Thread.sleep(1 * 1000);
//		String s = getData("http://www.cbrc.gov.cn/chinese/home/docViewPage/110002&current=1");
//		log.debug(s);


		String mailPath = OCRUtil.DOWNLOAD_DIR + "/mail.config";
		if (FileUtil.exist(mailPath)) {
			String mailConfigPath = FileUtil.readString(mailPath, CharsetUtil.UTF_8);
			String[] to = mailConfigPath.split("\\n");
			//发送邮件
			log.info("*******");
			sendSimpleMail(to, "测试", "今天天气不错呀");
			System.out.println("0000000000000000");
		}
		return null;
	}

	@Override
	protected String executeOne() throws Throwable {
		return null;
	}


	private void sendSimpleMail(String[] to, String subject, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);

		try {
			mailSender.send(message);
			log.info("简单邮件已经发送。");
		} catch (Exception e) {
			log.warn("发送简单邮件时发生异常！ " + e.getMessage());
		}

	}


}
