package com.mr.config;

import com.mr.common.OCRUtil;
import com.mr.framework.core.date.DateUtil;
import com.mr.framework.core.io.FileUtil;
import com.mr.framework.core.util.CharsetUtil;
import com.mr.framework.cron.CronUtil;
import com.mr.framework.cron.task.Task;
import com.mr.framework.log.Log;
import com.mr.framework.log.LogFactory;
import com.mr.modules.api.model.FinanceMonitorPunish;
import com.mr.modules.api.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

/**
 * Created by feng on 18-5-14
 */
@Configuration
public class CronJob {
	private static Log log = LogFactory.get();

	private static String pattern = "45 4 * * *";

	private static String testPattern = "*/1 * * * *";

	@Autowired
	private SiteService siteService;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String from;

	@Bean
	public Void startJob() {
		//每天4:45 run task
		CronUtil.schedule(pattern, new Task() {

			@Override
			public void execute() {
				log.info("Task excuted.");
				try {
					List<FinanceMonitorPunish> list = siteService.selectYesterday();
					StringBuffer sb = new StringBuffer("昨日新增记录：\n");
					for (FinanceMonitorPunish financeMonitorPunish : list) {
						sb.append("primaryKey").append(":").append(financeMonitorPunish.getPrimaryKey()).append("\t");
						sb.append("punishTitle").append(":").append(financeMonitorPunish.getPunishTitle()).append("\t");
						sb.append("source").append(":").append(financeMonitorPunish.getSource()).append("\t");
						sb.append("url").append(":").append(financeMonitorPunish.getUrl()).append("\t");
						sb.append("\n\n");
					}

					sb.append("解析错误日志：\n");
					String logPath = OCRUtil.DOWNLOAD_DIR + "/log.txt";
					if (FileUtil.exist(logPath)) {
						sb.append(FileUtil.readString(logPath, CharsetUtil.UTF_8));
						sb.append("\n\n");
					}

					String mailPath = OCRUtil.DOWNLOAD_DIR + "/mail.config";
					if (FileUtil.exist(mailPath)) {
						String mailConfigPath = FileUtil.readString(mailPath, CharsetUtil.UTF_8);
						String[] to = mailConfigPath.split("\\n");
						//发送邮件
						sendSimpleMail(to,DateUtil.formatDate(DateUtil.yesterday()) + "-抓取日志", sb.toString());
					}
					//移除错误记录
					FileUtil.del(logPath);


				} catch (Exception e) {
					log.warn("定时日报邮件异常");
				}
			}
		});
		CronUtil.start();
		log.info("Cron Task start.");

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
			log.error("发送简单邮件时发生异常！", e);
		}
	}

}
