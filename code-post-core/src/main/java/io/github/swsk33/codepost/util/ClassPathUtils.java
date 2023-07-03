package io.github.swsk33.codepost.util;

/**
 * classpath路径的实用类
 */
public class ClassPathUtils {

	/**
	 * 将一个相对路径形式的classpath处理成绝对路径<br>
	 * 例如传入xxx/file.txt，则转换为/xxx/file.txt
	 *
	 * @param classpath classpath相对路径
	 * @return 转换后的绝对路径，若传入的是绝对路径，则不作任何操作
	 */
	public static String toAbsolutePath(String classpath) {
		String result = classpath;
		if (result.startsWith("./")) {
			result = result.substring(1);
		} else if (!result.startsWith("/")) {
			result = "/" + result;
		}
		return result;
	}

	/**
	 * 判断一个classpath路径的文件是否存在
	 *
	 * @param classpathFile classpath路径（绝对路径，传入相对路径会被处理为绝对路径）
	 * @return 是否存在
	 */
	public static boolean classpathFileExists(String classpathFile) {
		return ClassPathUtils.class.getResource(toAbsolutePath(classpathFile)) != null;
	}

}