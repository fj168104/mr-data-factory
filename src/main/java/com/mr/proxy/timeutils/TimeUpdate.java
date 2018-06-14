package com.mr.proxy.timeutils;

import com.mr.modules.api.site.SiteTaskExtend;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by paranoid on 17-4-13.
 */
@Slf4j
@Scope("prototype")
@Component("crawler_proxy_ip_job")
public class TimeUpdate {
    /*@Override
    protected String execute() throws Throwable {
        TimeUpdate test = new TimeUpdate();
        test.go();
        return null;
    }*/

    public void go() throws Exception {
        // 首先，必需要取得一个Scheduler的引用(设置一个工厂)
        SchedulerFactory sf = new StdSchedulerFactory();

        //从工厂里面拿到一个scheduler实例
        Scheduler sched = sf.getScheduler();

        //真正执行的任务并不是Job接口的实例，而是用反射的方式实例化的一个JobDetail实例
        JobDetail job = newJob(MyTimeJob.class).withIdentity("job1", "group1").build();
        // 定义一个触发器，job 1将每隔执行一次
        CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1").
                withSchedule(cronSchedule("* * * * * ?")).build();

        //执行任务和触发器
        Date ft = sched.scheduleJob(job, trigger);

        //格式化日期显示格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        System.out.println(job.getKey() + " 已被安排执行于: " + sdf.format(ft) + "，" +
                "并且以如下重复规则重复执行: " + trigger.getCronExpression());

        sched.start();
    }

    public static void main(String[] args)  throws Exception {
        TimeUpdate test = new TimeUpdate();
        test.go();
    }
}
