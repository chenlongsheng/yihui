package com.jeeplus.common.log.script;

import com.jeeplus.common.log.script.impl.CachedFunction;

public class FunctionUtil {
	
	public static Function createCachedFunction(String functionString) {
		return new CachedFunction(functionString);
	}
	

}
