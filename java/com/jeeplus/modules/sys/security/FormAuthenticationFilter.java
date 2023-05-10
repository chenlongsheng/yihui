/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.jeeplus.modules.sys.utils.UserUtils;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 表单验证（包含验证码）过滤类
 * 
 * @author jeeplus
 * @version 2014-5-19
 */
@Service
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	public static final String DEFAULT_CAPTCHA_PARAM = "validateCode";
	public static final String DEFAULT_MOBILE_PARAM = "mobileLogin";
	public static final String DEFAULT_MESSAGE_PARAM = "message";

	private String captchaParam = DEFAULT_CAPTCHA_PARAM;
	private String mobileLoginParam = DEFAULT_MOBILE_PARAM;
	private String messageParam = DEFAULT_MESSAGE_PARAM;

	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
		String username = getUsername(request);
		String password = getPassword(request);
		if (password == null) {
			password = "";
		}
		boolean rememberMe = isRememberMe(request);
		String host = StringUtils.getRemoteAddr((HttpServletRequest) request);
		String captcha = getCaptcha(request);
		String cartchaId = getCaptchaId(request);
		boolean mobile = isMobileLogin(request);
		return new UsernamePasswordToken(username, password.toCharArray(), rememberMe, host, captcha, cartchaId,
				mobile);
	}
    
	public String getCaptchaParam() {
		return captchaParam;
	}

	protected String getCaptcha(ServletRequest request) {
		return WebUtils.getCleanParam(request, "captcha");
	}

	protected String getCaptchaId(ServletRequest request) {
		return WebUtils.getCleanParam(request, "captchaId");
	}

	public String getMobileLoginParam() {
		return mobileLoginParam;
	}

	protected boolean isMobileLogin(ServletRequest request) {
		return WebUtils.isTrue(request, getMobileLoginParam());
	}

	public String getMessageParam() {
		return messageParam;
	}

	/**
	 * 登录成功之后跳转URL
	 */
	public String getSuccessUrl() {
		return super.getSuccessUrl();
	}

	@Override
	protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
		
		logger.debug("issueSuccessRedirect");
		
		response.setContentType("text/html;charset=UTF-8");
		Principal p = UserUtils.getPrincipal();
		JSONObject obj = new JSONObject();
		obj.put("success", 0);
		obj.put("message", "login success");
		JSONObject data = new JSONObject();
		data.put("username", p.getLoginName());
		data.put("name", p.getName());
		data.put("token", p.getToken());
		data.put("userId", p.getId());
		data.put("mobile", p.getMobile());
		obj.put("data", data);
		
		UserUtils.saveLog("用户登入", "登入/登出");
		
		PrintWriter out = null;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		out = httpServletResponse.getWriter();
		out.println(obj);
		out.flush();
		out.close();
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		return super.isAccessAllowed(request, response, mappedValue);
	}

	/**
	 * 登录失败调用事件
	 */
	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		
		logger.debug("onLoginFailure");
		
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String message = "帐号或密码错误";
		if (e != null) {
			String errorName = e.getClass().getSimpleName();
			if (errorName.equals("CaptchaException")) {
				message = "验证码错误";
			} else if (errorName.equals("NoAccountException")) {
				message = "帐号或密码错误";
			} else if (errorName.equals("PassportException")) {
				message = "帐号中心连接失败";
			} else if (errorName.equals("RSAException")) {
				message = "RSA错误";
			} else if (errorName.equals("OutLoginTimeException")) {
				message = "帐号禁用";
			} else if (errorName.equals("CaptchaException")) {
				message = "验证码错误";
			}
		}
		try {
			httpServletResponse.setCharacterEncoding("UTF-8");
			httpServletResponse.setContentType("application/json");
			PrintWriter out = null;
			out = httpServletResponse.getWriter();
			JSONObject obj = new JSONObject();
			obj.put("success", 1);
			obj.put("message", message);
			out.println(obj);
			out.flush();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return false;
	}

}