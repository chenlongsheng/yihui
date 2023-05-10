package com.jeeplus.common.log.script;

import java.util.Map;
public interface Function extends Script {
	
	Object execute(Object object, Object[] arguments, Map<String, Object> context) throws ScriptException;

}
