package com.huadong.spoon.utils;

/**
 * String操作的工具类
 * @author jinjinhui
 * @date 2019/5/9
 * @version V1.0
 */
public class StringUtils {
	
	/**
	 * String转换为Integer
	 * @author nihaifeng 2013-10-29 下午02:25:53
	 * @param value String
	 * @return Integer
	 */
	public static Integer toInteger(String value) {
		return toInteger(value, 0);
	}
	
	/**
	 * String转换为Integer
	 * @author nihaifeng 2013-10-29 下午02:25:53
	 * @param value String
	 * @param defaults Integer  转换失败后的默认值
	 * @return Integer
	 */
	public static Integer toInteger(String value, Integer defaults) {
		if (StringUtils.isNumeric(value)) {
			return Integer.parseInt(value);
		}
		return defaults;
	}
	
	/**
	 * String转换为Long
	 * @author nihaifeng 2013-10-29 下午02:25:53
	 * @param value String
	 * @return Long
	 */
	public static Long toLong(String value) {
		return toLong(value, 0L);
	}
	
	/**
	 * String转换为Long
	 * @author nihaifeng 2013-10-29 下午02:25:53
	 * @param value String
	 * @param defaults Long  转换失败后的默认值
	 * @return Long
	 */
	public static Long toLong(String value, Long defaults) {
		if (StringUtils.isNumeric(value)) {
			return Long.parseLong(value);
		}
		return defaults;
	}
	
	/**
	 * String转换为Boolean
	 * @author nihaifeng 2013-10-29 下午02:25:53
	 * @param value String
	 * @return Boolean
	 */
	public static Boolean toBoolean(String value) {
		return toBoolean(value, false);
	}
	
	/**
	 * String转换为Boolean
	 * @author nihaifeng 2013-10-29 下午02:25:53
	 * @param value String
	 * @param defaults Boolean  转换失败后的默认值
	 * @return Boolean
	 */
	public static Boolean toBoolean(String value, Boolean defaults) {
		if ("true".equals(value)) {
			return true;
		}
		if ("false".equals(value)) {
			return false;
		}
		return defaults;
	}
	
	public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
	
	public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
	
	public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }
}
