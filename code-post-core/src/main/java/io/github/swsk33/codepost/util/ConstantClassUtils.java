package io.github.swsk33.codepost.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 关于常量存放类的实用类
 */
public class ConstantClassUtils {

	/**
	 * 传入一个字符串，利用反射的方式检查这个字符串是否属于某个常量类中常量中的一个
	 *
	 * @param constantClass 存放常量的类
	 * @param value         传入字符串
	 * @return 是否为指定常量类中常量中的某一个
	 */
	public static boolean contains(Class<?> constantClass, String value) {
		Field[] fields = constantClass.getDeclaredFields();
		// 遍历所有的字段
		for (Field field : fields) {
			// 如果说不是public static final的字段，直接跳过
			int modifiers = field.getModifiers();
			if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
				continue;
			}
			// 比对字段值
			try {
				if (value.equals(field.get(null))) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}