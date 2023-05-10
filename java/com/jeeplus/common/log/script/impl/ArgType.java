package com.jeeplus.common.log.script.impl;


public enum ArgType {

	FROM_ARG(1, 		"来自参数"),
	FROM_CONTEXT(2, 	"来自上下文"),
	NULL(3, 			"空"),
	STRING(4,			"来自字符串");
	
	private ArgType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	final int id;
	
	final String name;

}
