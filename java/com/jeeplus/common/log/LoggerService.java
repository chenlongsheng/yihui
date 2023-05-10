package com.jeeplus.common.log;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jeeplus.common.log.script.ScriptUtil;
import com.jeeplus.common.log.text.TextRender;
import com.jeeplus.common.log.text.VelocityTextRender;
import com.jeeplus.common.log.util.StringUtil;
import com.jeeplus.common.log.util.TrailConstants;
import com.jeeplus.modules.enterprise.entity.TOperLog;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.SystemService;
import com.jeeplus.modules.sys.utils.UserUtils;

import org.springframework.transaction.annotation.Propagation;
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class LoggerService {
	
	@Autowired
	SystemService systemService;
	
	@Autowired
	TOperLogService tOperLogService;

	private  TextRender textRender = new VelocityTextRender();

	public  void begin(String beginScript, Object target,
			Object[] arguments, Map<String, Object> context) {
		try {
			context.put(TrailConstants.ARG_STRING, arguments);
			ScriptUtil.execute(beginScript, target, arguments, context);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Transactional(readOnly = false)
	public  void eval(LoggerObject obj, Object target,
			Object[] arguments, Map<String, Object> context) {
		try {
			context.put(TrailConstants.ARG_STRING, arguments);
			String evaledText = StringUtil.EMPTY;
			String toEvalText = obj.getLogcontent();
			if (!StringUtil.isEmpty(toEvalText)) {
				evaledText = textRender.render(toEvalText, context);
			}
			obj.setLogcontent(evaledText);
			//if default is null, using the default method
			if (StringUtil.isEmpty(obj.getIp())){
				obj.setIp(this.getRemoteIp());
			}
			if (obj.getUserid().equals("")){
				obj.setUserid(this.getUser());
			}
			// calling logger factory
			Logger logger = Logger.getLogger(obj.getClazz());
			if (LoggerLevel.DBLOG_LEVEL.isGreaterOrEqual(logger.getEffectiveLevel())) {
			LogEvent event = new LogEvent(logger.getClass().getName(),logger, LoggerLevel.OFF,
						obj, null);
			event.setMsgObj(obj);
			//new OperAppender(event).flushBuffer();

			
			System.out.println(tOperLogService);
			
			
			TOperLog ol = new TOperLog();
			ol.setAddTime(new Date());
			if (null == event.getMsgObj().getIp()) {
				ol.setIp(getRemoteIp());
			}
			ol.setOprContent(event.getMsgObj().getLogcontent());
			if (event.getMsgObj().getModelCode()==null) {
				ol.setLogCode(Long.parseLong(event.getMsgObj().getModelCode()));
			}
			ol.setIp(getRemoteIp());
			ol.setUser(UserUtils.getUser());
			ol.setDevId(0L);
			ol.setChId(0L);
			ol.setCreator(UserUtils.getUser().getId());
			ol.setCrDate(new Date());
			ol.setCrUnit("");
			tOperLogService.save(ol);
			
//			logger.callAppenders(event);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	protected String getRemoteIp() {
		//2012-10-10 
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (null != attr){
			HttpServletRequest request = attr.getRequest();
			return request.getRemoteAddr();
		}else{
			return "127.0.0.0";
		}
	}
	
	protected String getUser() {
		User currentUser = null;
		try{
			currentUser = UserUtils.getUser();
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(currentUser != null){
			return currentUser.getId();
		}
		return "";
	}
}
