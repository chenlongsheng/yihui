package com.jeeplus.common.log.script.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jeeplus.common.log.script.Function;
import com.jeeplus.common.log.script.FunctionUtil;
import com.jeeplus.common.log.script.Script;
import com.jeeplus.common.log.script.ScriptException;
import com.jeeplus.common.log.util.StringUtil;

public class MutilFunctionScript implements Script {
	
	private String scriptString;
	
	private List<Function> functions = new ArrayList<Function>();
	
	public MutilFunctionScript(String scriptString) {
		this.scriptString = scriptString;
		parse();
	}
	
	private void parse() {
		if(StringUtil.isEmpty(scriptString)) {
			return;
		}
		ArrayList<Function> parsedFunctions = new ArrayList<Function>();
		List<String> strFuns = StringUtil.split(scriptString, ";");
		for(String strFun : strFuns) {
			strFun = StringUtil.trimToEmpty(strFun);
			if(StringUtil.isEmpty(strFun)) {
				continue;
			}
			parsedFunctions.add(FunctionUtil.createCachedFunction(strFun));
		}
		functions = parsedFunctions;
	}
	
	public Object execute(Object object, Object[] arguments,
			Map<String, Object> context) throws ScriptException {
		Object ret = null;
		for(Function fun : functions) {
			ret = fun.execute(object, arguments, context);
		}
		return ret;
	}

}
