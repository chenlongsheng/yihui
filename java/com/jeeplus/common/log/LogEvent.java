package com.jeeplus.common.log;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 扩展log4j日志事件，记录相关的额外信息。
 */
public class LogEvent extends LoggingEvent{

	private static final long serialVersionUID = 1266190868001954553L;
	
	public LoggerObject msgObj;
	
	public LogEvent(String fqnOfCategoryClass, Category logger,
			long timeStamp, Priority level, Object message, Throwable throwable) {
		super(fqnOfCategoryClass, logger, timeStamp, level, message, throwable);
		// TODO Auto-generated constructor stub
	}
	
	public LogEvent(String fqnOfCategoryClass, Category logger,
		      Priority level, Object message, Throwable throwable){
		super(fqnOfCategoryClass, logger, level, message, throwable);
	}

	public LoggerObject getMsgObj() {
		return msgObj;
	}

	public void setMsgObj(LoggerObject msgObj) {
		this.msgObj = msgObj;
	}
	
}
