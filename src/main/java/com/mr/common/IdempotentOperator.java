package com.mr.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 幂等操作类
 *
 * @param <T>
 */
@Slf4j
public class IdempotentOperator<T> {

	private Callable<T> task;

	public IdempotentOperator(Callable<T> task) {
		this.task = task;
	}

	public T execute() {
		return execute(10);
	}

	/**
	 * 重试操作
	 *
	 * @param maxRetryTimes 重试次数
	 * @return
	 */
	public T execute(int maxRetryTimes) {
		Throwable ex = null;
		boolean executeSuccess = false;
		T result = null;
		int retryTimes = 0;
		while (!executeSuccess && retryTimes++ < maxRetryTimes) {
			try {
				result = (T) task.call();
				executeSuccess = true;
			} catch (Throwable e) {
				log.warn(e.getMessage());
				ex = e;
				try {
					Thread.sleep(retryTimes * 1000);
					log.info("retry...");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		if (!executeSuccess)
			if (ex instanceof RuntimeException) {
				throw (RuntimeException) ex;
			} else {
//				log.error(ex.getMessage());
				throw new RuntimeException("超过重试次数,执行失败");
			}
		return result;
	}
}