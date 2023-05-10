package com.jeeplus.common.log.script.impl;

import java.util.HashMap;
import java.util.Map;

import com.jeeplus.common.log.script.Function;
import com.jeeplus.common.log.script.FunctionUtil;
import com.jeeplus.common.log.script.ScriptException;



public class CachedFunction implements Function {

	private static final Map<String, StringFunction> cache = new HashMap<String, StringFunction>();
	
	private String functionString;
	
	public CachedFunction(String functionString) {
		this.functionString = functionString;
	}
	
	@SuppressWarnings("all")
	public Object execute(Object object, Object[] arguments, Map<String, Object> context) throws ScriptException {
		StringFunction ret = cache.get(object.getClass().getName()+"."+functionString);
		if(ret != null) {
			return ret.execute(object, arguments, context);
		}
		synchronized(FunctionUtil.class) {
			if(ret != null) {
				return ret.execute(object, arguments, context);
			}
			ret = new StringFunction(functionString);
			ret.parse(object.getClass());
			cache.put(object.getClass().getName()+"."+functionString, ret);
		}
		return ret.execute(object, arguments, context);
	}

}
