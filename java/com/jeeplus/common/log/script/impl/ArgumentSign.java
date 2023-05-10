package com.jeeplus.common.log.script.impl;

import java.util.Map;

import com.jeeplus.common.log.util.BeanUtil;
import com.jeeplus.common.log.util.StringUtil;



public class ArgumentSign {
	
	private int index;
	
	private String property;
	
	private ArgType argType = ArgType.NULL;
	
	private String varName;
	
	public static ArgumentSign createStringArgumentSign(String value) {
		ArgumentSign ret = new ArgumentSign();
		ret.setArgType(ArgType.STRING);
		ret.setProperty(value);
		return ret;
	}
	
	public static ArgumentSign createFromArgArgumentSign(String property, int index) {
		ArgumentSign ret = new ArgumentSign();
		ret.setArgType(ArgType.FROM_ARG);
		ret.setProperty(property);
		ret.setIndex(index);
		return ret;
	}
	
	public static ArgumentSign createFromContextArgumentSign(String property, String varName) {
		ArgumentSign ret = new ArgumentSign();
		ret.setArgType(ArgType.FROM_CONTEXT);
		ret.setProperty(property);
		ret.setVarName(varName);
		return ret;
	}

	public ArgumentSign() {
	}

	public Object getValue(Object[] arguments, Map<String, Object> context) {
		if(arguments == null) {
			return null;
		}
		if(index >= arguments.length) {
			return null;
		}
		Object obj = null;
		if(argType.equals(ArgType.FROM_ARG)) {
			obj = arguments[index];
		} else if(argType.equals(ArgType.FROM_CONTEXT)) {
			obj = context.get(varName);
		} else if(argType.equals(ArgType.STRING)) {
			return property;
		} else if(argType.equals(ArgType.NULL)) {
			return null;
		}
		if(StringUtil.isEmpty(property)) {
			return obj;
		}
		return BeanUtil.getProperty(obj, property);
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
	
	public ArgType getArgType() {
		return argType;
	}

	public void setArgType(ArgType argType) {
		this.argType = argType;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}
}
