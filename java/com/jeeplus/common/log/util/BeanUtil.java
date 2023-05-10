package com.jeeplus.common.log.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class BeanUtil {
	
	private static final Class<?>[] EMPTY_CLS_ARRAY = new Class<?>[] {};
	
	private static final Object[] EMPTY_OBJ_ARRAY = new Object[] {};
	
	/**
	 * ֧�� user.addr.zip ��ʽ��ȡ
	 * @param obj
	 * @param propertyName
	 * @return
	 */
	public static Object getProperty(Object obj, String propertyName) {
		if(propertyName == null || propertyName.length() < 1) {
			return obj;
		}
		int pos = propertyName.indexOf(".");
		if(pos < 0) {
			return BeanUtil.getPropertyNormal(obj, propertyName);
		}
		String baseProperty = propertyName.substring(0, pos);
		String childProperty =  propertyName.substring(pos + 1);
		Object target = BeanUtil.getPropertyNormal(obj, baseProperty);
		return getProperty(target, childProperty);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getPropertyNormal(Object obj, String propertyName) {
		if(obj == null) {
			return null;
		}
		if(obj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)obj;
			return map.get(propertyName);
		}
		Class<?> clazz = obj.getClass();
		try {
			Method method = clazz.getMethod(asGetterString(propertyName), EMPTY_CLS_ARRAY);
			if(method == null) {
				return null;
			}
			return method.invoke(obj, EMPTY_OBJ_ARRAY);
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}
	
	public static String asGetterString(String name) {
		return "get" + upperCaseFirstLetter(name);
	}
	
	private static String upperCaseFirstLetter(String name) {
		if(name == null || name.length() < 1) {
			return name;
		}
		String first = name.charAt(0) + "";
		return first.toUpperCase() + name.substring(1);
	}
}
