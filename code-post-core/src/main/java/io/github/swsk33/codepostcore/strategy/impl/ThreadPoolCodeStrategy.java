package io.github.swsk33.codepostcore.strategy.impl;

import io.github.swsk33.codepostcore.strategy.EmailCodeStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 基于本地线程池的验证码管理策略
 */
@Slf4j
public class ThreadPoolCodeStrategy implements EmailCodeStrategy {

	/**
	 * 存放所有验证码键值对的哈希表
	 */
	private final Map<String, String> codeMap;

	/**
	 * 存放全部调度任务的哈希表
	 */
	private final Map<String, ScheduledFuture<?>> scheduleMap;

	/**
	 * 任务调度器
	 */
	private final ScheduledExecutorService executor;

	/**
	 * 构造器，用于初始化所有的对象
	 */
	public ThreadPoolCodeStrategy() {
		codeMap = new ConcurrentHashMap<>();
		scheduleMap = new ConcurrentHashMap<>();
		executor = Executors.newScheduledThreadPool(24);
		log.info("本地线程池已完成初始化！");
	}

	@Override
	public void saveCode(String key, String code, long period, TimeUnit timeUnit) {
		// 先把验证码存入列表中
		codeMap.put(key, code);
		// 建立延时任务
		ScheduledFuture<?> future = executor.schedule(() -> {
			// 时间到了后，移除验证码
			codeMap.remove(key);
			scheduleMap.remove(key);
			log.warn("验证码键：" + key + " 过期！");
		}, period, timeUnit);
		// 把任务放入任务列表
		scheduleMap.put(key, future);
	}

	@Override
	public boolean verifyCode(String key, String inputCode) {
		if (inputCode.equals(codeMap.get(key))) {
			// 校验成功，移除验证码，取消定时任务
			codeMap.remove(key);
			scheduleMap.remove(key).cancel(true);
			log.info("验证码键：" + key + " 校验成功！");
			return true;
		}
		// 校验失败则不做任何操作
		return false;
	}

}