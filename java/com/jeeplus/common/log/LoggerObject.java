package com.jeeplus.common.log;

import java.io.Serializable;
import java.util.Date;

public class LoggerObject implements Serializable{
	private static final long serialVersionUID = 1L;

	public String modelCode;
	
	public String clazz;
	
	public String javaFun;
	
	public String ip;
	
	public String logcontent;
	
	public Date time;
	
	public String userid;
	

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLogcontent() {
		return logcontent;
	}

	public void setLogcontent(String logcontent) {
		this.logcontent = logcontent;
	}


	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getJavaFun() {
		return javaFun;
	}

	public void setJavaFun(String javaFun) {
		this.javaFun = javaFun;
	}
}
