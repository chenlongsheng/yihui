package com.jeeplus.common.log.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.jeeplus.common.log.LoggerAnnotation;

import com.jeeplus.modules.sys.entity.User;
@SuppressWarnings("all")
public class TestLoggerService {
	
	/**Lo
	 * Using the Annotation to do the operator log
	 * @return
	 */
	@LoggerAnnotation(begin="$user=getUser()", okLog = "$user succ", errLog="$user failed", modelCode="1")
	public int doUpdateOperation() {
		return 0;
	}
	
	/**
	 * Test the Annotation for the exception recording
	 * @return
	 */
	@LoggerAnnotation(begin="$user=getUser()", okLog = "$user succ", errLog="$user failed",  modelCode="1")
	public int doUpdateOperationFailed() {
		int failint = 8/0;
		System.out.println(failint);
		return 0;
	}
	
	public User getUser(){
		User user =new User();
		user.setName("userName");
		return user;
	}
	
	
	public static void main(String[] args) {
		ApplicationContext act = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		TestLoggerService service = (TestLoggerService) act.getBean("testService");
		service.doUpdateOperation();
	}
}
