package com.jeeplus.common.log.script.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jeeplus.common.log.script.Function;
import com.jeeplus.common.log.script.ScriptException;
import com.jeeplus.common.log.util.ConvertUtil;
import com.jeeplus.common.log.util.StringUtil;
import com.jeeplus.common.log.util.TrailConstants;



public class StringFunction implements Function {
	
	private String functionDesc;
	
	private NamedValue ret;
	
	private Method method;
	
	private List<ArgumentSign> argumentSigns = new ArrayList<ArgumentSign>();

	private boolean parsed = false;
	
	public StringFunction(String functionDesc) {
		this.functionDesc = functionDesc;
	}
	
	private void clear() {
		argumentSigns.clear();
		method = null;
		ret = null;
		parsed = false;
	}
	
	private static String parseVar(String var) throws ScriptException {
		var = StringUtil.trimToEmpty(var);
		if(StringUtil.isEmpty(var)) {
			throw new ScriptException();
		}
		if(!var.startsWith("$")) {
			throw new ScriptException();
		}
		String name = var.substring(1);
		if(StringUtil.isEmpty(name)) {
			throw new ScriptException();
		}
		return name;
	}
	
	private void parseArguments(List<String> argsList) throws ScriptException {
		
		List<ArgumentSign> resList = new ArrayList<ArgumentSign>();
		for(String argString : argsList) {
			String argSign = StringUtil.trimToEmpty(argString);
			if(StringUtil.isEmpty(argSign) || (!argSign.startsWith("$"))) {
				ArgumentSign as = ArgumentSign.createStringArgumentSign(argSign);
				resList.add(as);
				continue;
			}
			
			String varName = argSign.substring(1);
			varName = StringUtil.trimToEmpty(varName);
			if(StringUtil.isEmpty(varName)) {
				throw new ScriptException();
			}
			int argIndex = getArgIndex(varName);
			if(argIndex >= 0) {
				String property = parseArgProperty(varName, argIndex);
				ArgumentSign as = ArgumentSign.createFromArgArgumentSign(property, argIndex);
				resList.add(as);
				continue;
			} else {
				int dotPos = varName.indexOf(".");
				String realVarName = null;
				String property = null;
				if(dotPos < 0) {
					realVarName = varName;
				} else {
					realVarName = varName.substring(0, dotPos);
					property = varName.substring(dotPos + 1);
				}
				ArgumentSign as = ArgumentSign.createFromContextArgumentSign(property, realVarName);
				resList.add(as);
				continue;
			}
		}
		argumentSigns = resList;
	}
	
	
	private static String parseArgProperty(String str, int index) {
		String prefix = TrailConstants.ARG_STRING + "["+ index +"]";
		int len = prefix.length();
		if(len >= str.length()) {
			return null;
		}
		return str.substring(len + 1);
	}
	
	private static int getArgIndex(String varName) {
		if(StringUtil.isEmpty(varName)) {
			return -1;
		}
		final String LEFT_STRING = TrailConstants.ARG_STRING + "[";
		final int LEN = LEFT_STRING.length();
		int leftPos = varName.indexOf(LEFT_STRING);
		if(leftPos < 0) {
			return -1;
		}
		int rightPos = varName.indexOf("]");
		if(rightPos < 0) {
			return -1;
		}
		String value = varName.substring(LEN, rightPos);
		return ConvertUtil.asInt(value, -1);
	}
	
	
	private Method parseRealMethod(String functionName, Class<?> targetClass) throws ScriptException {
		Method[] methods = targetClass.getMethods();
		final int paramLen = argumentSigns.size();
		for(Method m : methods) {
			final String name = m.getName();
			final int len = m.getParameterTypes().length;
			if(name.equals(functionName) && len == paramLen) {
				return m;
			}
		}
		throw new ScriptException();
	}
	
	private void parseMethod(String methodSign, Class<?> targetClass) throws ScriptException {
		methodSign = StringUtil.trimToEmpty(methodSign);
		if(methodSign.endsWith(";")) {
			methodSign = StringUtil.left(methodSign, methodSign.length() - 1);
		}
		if(StringUtil.isEmpty(methodSign)) {
			throw new ScriptException();
		}
		int firstBracket = methodSign.indexOf("(");
		int lastBracket = methodSign.indexOf(")");
		if(firstBracket < 0 || lastBracket < 0) {
			throw new ScriptException();
		}
		if(!methodSign.endsWith(")")) {
			throw new ScriptException();
		}
		String functionName = methodSign.substring(0, firstBracket);
		functionName = StringUtil.trimToEmpty(functionName);
		if(StringUtil.isEmpty(functionName)) {
			throw new ScriptException();
		}
		String argListString = methodSign.substring(firstBracket + 1);
		argListString = StringUtil.left(argListString, argListString.length() - 1);
		List<String> argsList = StringUtil.split(argListString, ",");
		parseArguments(argsList);
		method = parseRealMethod(functionName, targetClass);
		
	}
	
	public void parse(Class<?> targetClass) throws ScriptException {
		clear();
		if(StringUtil.isEmpty(functionDesc)) {
			return;
		}
		int posAssgin = functionDesc.indexOf("=");
		if(posAssgin < 0) {
			parseMethod(functionDesc, targetClass);
		} else {
			String var = functionDesc.substring(0, posAssgin);
			String varName = parseVar(var);
			ret = new NamedValue(varName, null);
			
			String methodSign = functionDesc.substring(posAssgin + 1);
			parseMethod(methodSign, targetClass);
		}
		parsed = true;
	}
	
	public Object execute(Object object, Object[] arguments, Map<String, Object> context) throws ScriptException {
		if(method == null) {
			return null;
		}
		Object[] args = new Object[argumentSigns.size()];
		for(int i = 0; i < args.length; ++i) {
			args[i] = argumentSigns.get(i).getValue(arguments, context);
		}
		try {
			Object retValue = method.invoke(object, args);
			if(ret != null) {
				ret.setValue(retValue);
				context.put(ret.getName(), retValue);
			}
			return retValue;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isParsed() {
		return parsed;
	}

	public void setParsed(boolean parsed) {
		this.parsed = parsed;
	}
	
}
