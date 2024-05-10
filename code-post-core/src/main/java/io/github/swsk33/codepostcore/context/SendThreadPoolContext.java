package io.github.swsk33.codepostcore.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 维护异步发送邮件的线程池的全局类
 */
public class SendThreadPoolContext {

	/**
	 * 发生邮件的异步任务线程池
	 */
	private static final ExecutorService SEND_EMAIL_THREAD_POOL = Executors.newCachedThreadPool();

	/**
	 * 提交发送邮件的异步任务
	 *
	 * @param sendTask 发送邮件的任务，可以使用lambda表达式传入匿名内部类实例
	 */
	public static void submitTask(Runnable sendTask) {
		SEND_EMAIL_THREAD_POOL.execute(sendTask);
	}

}