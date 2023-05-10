package com.jeeplus.common.log.util;

public class ArrayUtil {
	
	public static String toReadableString(Object[] array) {
		if(array == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for(Object obj : array) {
			builder.append(obj);
			builder.append(",");
		}
		return builder.toString();
	}

}
