package com.jeeplus.common.log.script;

import java.util.Map;

import com.jeeplus.common.log.script.impl.MutilFunctionScript;

public class ScriptUtil {
	
	public static Object execute(String scripts, Object object, Object[] arguments, Map<String, Object> context) {
		MutilFunctionScript script = new MutilFunctionScript(scripts);
		return script.execute(object, arguments, context);
	}

}
