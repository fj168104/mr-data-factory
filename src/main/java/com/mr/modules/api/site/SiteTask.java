package com.mr.modules.api.site;

import com.mr.common.util.EhCacheUtils;
import com.mr.common.util.SpringUtils;
import com.mr.modules.api.TaskStatus;
import com.mr.modules.api.caller.SiteVisitor;
import com.mr.modules.api.model.FinanceMonitorPunish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by feng on 18-3-16
 */
@Slf4j
@Component
public abstract class SiteTask implements ResourceGroup, Callable<String> {

	private Integer returnCode;
	private String throwableInfo;
	private Future<String> future;
	private SiteVisitor<Integer> startVisitor;

	//单条处理时使用, 接受外部参数
	protected FinanceMonitorPunish oneFinanceMonitorPunish;

	@Override
	public void setFinanceMonitorPunish(FinanceMonitorPunish oneFinanceMonitorPunish) {
		this.oneFinanceMonitorPunish = oneFinanceMonitorPunish;
	}

	private static BlockingQueue<String> finishQueue = new LinkedBlockingQueue<>();

	static {
		Thread delThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (finishQueue.size() > 100) {
						for (int i = 0; i < 50; i++) {
							if (!Objects.isNull(EhCacheUtils.get(finishQueue.take()))) {
								EhCacheUtils.remove(finishQueue.take());
							}
						}
						Thread.sleep(5 * 1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		delThread.setDaemon(true);
		delThread.start();
	}

	public static void putFinishQueue(String callId) throws InterruptedException {
		finishQueue.put(callId);
	}

	public static void delSiteTaskInstance(String callId) throws InterruptedException {
		EhCacheUtils.remove(callId);
	}

	public SiteTask() {
		startVisitor = (SiteVisitor<Integer>) SpringUtils.getBean("startVisitor");
	}

	@Override
	public Integer start() {
		try {
			future = startVisitor.visit(this);
			//单条处理时使用闭锁同步
			if (!Objects.isNull(oneFinanceMonitorPunish)) {
				future.get();
			}
		} catch (Throwable e) {
			return TaskStatus.CALL_FAIL.index;
		}

		return TaskStatus.CALL_SUCCESS.index;
	}

	@Override
	public Integer getResultCode() {
		if (!future.isDone())
			return TaskStatus.CALL_FAIL.index;

		return returnCode;
	}

	@Override
	public String getThrowableInfo() {
		if (!future.isDone())
			return "executing...";
		return throwableInfo;
	}

	@Override
	public Boolean isFinish() {
		return future.isDone();
	}

	@Override
	public String call() throws Exception {
		try {
			String res = null;
			if (Objects.isNull(oneFinanceMonitorPunish)) {
				res = execute();
			} else {
				res = executeOne();
			}

			if (StringUtils.isEmpty(res)) {
				returnCode = TaskStatus.CALL_SUCCESS.index;
				throwableInfo = "";
			} else {
				returnCode = TaskStatus.CALL_FAIL.index;
				throwableInfo = "execute fail";
			}

		} catch (Throwable e) {
			returnCode = TaskStatus.CALL_FAIL.index;
			throwableInfo = e.getMessage();
			e.printStackTrace();
			log.error(throwableInfo);
		}
		return "";
	}

	protected abstract String execute() throws Throwable;

	protected abstract String executeOne() throws Throwable;
}
