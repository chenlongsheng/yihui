package com.jeeplus.common.log;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Using Spring AOP to do the log deal. Calling the LoggerService. Currently the
 * default intercept level is the name including "do" substring, like doSave(),
 * doUpdate() ... such method in the service level.
 */
@Component
@Aspect
public class LoggerAspect {

	@Autowired
	private LoggerService loggerservice;

	@Pointcut("execution(* com.jeeplus.modules.enterprise.service.*.*(..))")
	public void cutpoint(){}
	
	@Around("cutpoint()")
	public Object simpleAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] args = joinPoint.getArgs();
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		if (method.getDeclaringClass().isInterface()) {
			method = joinPoint.getTarget().getClass().getDeclaredMethod(
					joinPoint.getSignature().getName(),
					method.getParameterTypes());
		}
		
		LoggerAnnotation testnotation = method.getAnnotation(LoggerAnnotation.class);
		Object returnObj = joinPoint.proceed(args);
		try {
			if (null == testnotation) return returnObj;
			// determine whether use the dblog, annotation
			// 获取调用的代理方法
			if (null != testnotation) {
				Map<String, Object> trailContext = new HashMap<String, Object>();
				// 先执行begin脚本解析
				trailContext.put("args", args);
				String begin = testnotation.begin();
				loggerservice.begin(begin, joinPoint.getTarget(), args,trailContext);
				// doing oklog
				LoggerObject lo = getLoggerObject(testnotation);
				lo.setLogcontent(testnotation.okLog());
				lo.setClazz(joinPoint.getTarget().getClass().getName());
				lo.setJavaFun(method.getName());
				loggerservice.eval(lo, joinPoint.getTarget(), args,trailContext);
			}
		} catch (Exception exception) {
			if (null != testnotation) {
				Map<String, Object> trailContext = new HashMap<String, Object>();
				// 先执行begin脚本解析
				trailContext.put("args", args);
				String begin = testnotation.begin();
				loggerservice.begin(begin, joinPoint.getTarget(), args,
						trailContext);
				// doing oklog
				LoggerObject lo = getLoggerObject(testnotation);
				lo.setLogcontent(testnotation.errLog());
				lo.setClazz(joinPoint.getTarget().getClass().getName());
				lo.setJavaFun(method.getName());
				loggerservice.eval(lo, joinPoint.getTarget(), args,
						trailContext);
			}
			throw exception;
		}
		return returnObj;
	}

	private LoggerObject getLoggerObject(LoggerAnnotation testnotation) {
		LoggerObject lo = new LoggerObject();
		lo.setModelCode(testnotation.modelCode());
		lo.setIp(testnotation.ip());
		lo.setUserid(testnotation.user());
		lo.setTime(new Date());
		return lo;
	}
}
