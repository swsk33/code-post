package io.github.swsk33.codepostcore.param;

/**
 * 验证码的保存方式常量
 */
public class CodeStorageMethod {

	/**
	 * 使用本地线程池存放验证码以及管理验证码过期
	 */
	public static final String LOCAL_THREAD_POOL = "thread_pool";

	/**
	 * 使用Redis存放验证码以及管理验证码过期
	 */
	public static final String REDIS = "redis";

}