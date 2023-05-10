package com.jeeplus.modules.homepage.web;

import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping(value = "/testsession")
public class TestSession {

	// 首页建筑集合
	@ResponseBody
	@RequestMapping(value = "/getsessionid")
	public String getsessionid(HttpServletRequest request,HttpServletResponse response) {
		System.out.println(request.getSession().getId());
		Cookie ckName = new Cookie("JSESSIONID", request.getSession().getId());
		response.addCookie(ckName);
		Random random = new Random();
		String messageCode = "";
		for (int i = 0; i < 6; i++) {
			messageCode += random.nextInt(10);
		}
		request.getSession().setAttribute("messageCode", messageCode);
		return messageCode+","+ckName.toString();
	}

	
	// 首页建筑集合
	@ResponseBody
	@RequestMapping(value = "/getcode")
	public String getcode(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String code = "";
		if(session != null) {
			Object obj = session.getAttribute("code");
			if(obj != null) {
				code = obj.toString();
			}
		} else {
			code = "";
		}
		
		return code;
	}
	
}
