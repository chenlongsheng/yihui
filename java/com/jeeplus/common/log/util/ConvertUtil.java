package com.jeeplus.common.log.util;

public class ConvertUtil {

	public static long asLong(Object obj, long defaultValue) {
		if(obj == null) {
			return defaultValue;
		}
		if(obj instanceof Number) {
			return ((Number)obj).longValue();
		}
		try {
			return Long.parseLong(String.valueOf(obj));
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public static int asInt(Object obj, int defaultValue) {
		if(obj == null) {
			return defaultValue;
		}
		if(obj instanceof Number) {
			return ((Number)obj).intValue();
		}
		try {
			return Integer.parseInt(String.valueOf(obj));
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public static double asDouble(Object obj, double defaultValue) {
		if(obj == null) {
			return defaultValue;
		}
		if(obj instanceof Number) {
			return ((Number)obj).doubleValue();
		}
		try {
			return Double.parseDouble(String.valueOf(obj));
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public static float asFloat(Object obj, float defaultValue) {
		if(obj == null) {
			return defaultValue;
		}
		if(obj instanceof Number) {
			return ((Number)obj).floatValue();
		}
		try {
			return Float.parseFloat(String.valueOf(obj));
		} catch(Exception e) {
			return defaultValue;
		}
	}
	
	public static String asString(Object obj, String defaultValue) {
		if(obj == null) {
			return defaultValue;
		}
		return obj.toString();
	}
	
	
}
