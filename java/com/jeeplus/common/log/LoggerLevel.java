package com.jeeplus.common.log;

import org.apache.log4j.Level;

public class LoggerLevel extends Level{

	private static final long serialVersionUID = 1259189928839844421L;
	
	public static final int DBLOG_INT = 60000;
	
	public static final Level DBLOG_LEVEL = new LoggerLevel(DBLOG_INT, "DBLOG", 7);
	 
	private LoggerLevel(int level, String name, int sysLogLevel) {
		super(level, name, sysLogLevel);
	}
}
