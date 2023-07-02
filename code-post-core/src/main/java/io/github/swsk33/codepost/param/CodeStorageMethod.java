package io.github.swsk33.codepost.param;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

	/**
	 * 传入一个验证码储存方式名，利用反射的方式检查这个储存方式是否属于上述所有的常量中的某一个
	 *
	 * @param codeStorageMethod 文件储存方式名
	 * @return 是否为上述常量中的某一个
	 */
	public static boolean contains(String codeStorageMethod) {
		Field[] fields = CodeStorageMethod.class.getDeclaredFields();
		// 遍历所有的字段
		for (Field field : fields) {
			// 如果说不是public static final的字段，直接跳过
			int modifiers = field.getModifiers();
			if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
				continue;
			}
			// 比对字段值
			try {
				if (codeStorageMethod.equals(field.get(null))) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}