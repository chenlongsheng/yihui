package com.jeeplus.common.log.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {
	
	public static final String EMPTY = "";

	public static boolean isEmpty(String str) {
		if(str == null || str.length() == 0) {
			return true;
		}
		return false;
	}
	
	public static String trimToNull(String str) {
		if(str == null || str.length() == 0) {
			return null;
		}
		return str.trim();
	}
	
	public static String trimToEmpty(String str) {
		if(str == null || str.length() == 0) {
			return EMPTY;
		}
		return str.trim();
	}
	
	public static String left(String str, int count) {
		if(isEmpty(str)) {
			return str;
		}
		if(count > str.length()) {
			return str;
		}
		return str.substring(0, count);
	}
	
	public static List<String> split(String target, String token) {
		List<String> ret = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(target, token);
	     while (st.hasMoreTokens()) {
	    	 ret.add(st.nextToken());
	     }
		return ret;
	}
	
	
	
}
