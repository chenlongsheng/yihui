package com.jeeplus.modules.enterprise.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.http.HttpResponse;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.restlet.engine.adapter.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.beanvalidator.BeanValidators;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.FileUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.sys.dao.UserDao;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.SystemConfig;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.jeeplus.modules.sys.service.SystemConfigService;
import com.jeeplus.modules.sys.service.SystemService;
import com.jeeplus.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "enterprise/user")
public class EntpUserController extends BaseController {

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private SystemService systemService;

	@Autowired
	private TOperLogService operLogService;

	@RequestMapping(value = "login1")
	@ResponseBody
	public String login1(HttpServletResponse response,String username, String password, Model model) {
				
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			return ServletUtils.buildRs(false, "账号密码错误不能空", null);
		}
		User user= systemService.getUserByLoginName(username);	
		if (SystemService.validatePassword(password, user.getPassword())) {
			System.out.println("====加载");
			Principal principal = new Principal(user);
			AjaxJson j = new AjaxJson();
			j.setSuccess(true);
			j.setErrorCode("0");
			j.setMsg("登录成功!");
			j.put("username", principal.getLoginName());
			j.put("name", principal.getName());
			j.put("mobileLogin", "true");
			j.put("JSESSIONID", principal.getSessionid());
			System.out.println("手机登入:登入成功!");
			return renderString(response, j);
//			return ServletUtils.buildRs(true, "登入成功", j);
		}
		return ServletUtils.buildRs(false, "账号密码错误", null);
	}

	/**
	 * 修改个人用户密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "modifyPwd")
	@ResponseBody
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
			if (SystemService.validatePassword(oldPassword, user.getPassword())) {
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				return ServletUtils.buildRs(true, "修改密码成功", null);
			} else {
				return ServletUtils.buildRs(false, "修改密码失败", null);
			}
		}
		return ServletUtils.buildRs(true, "修改密码失败", null);
	}
}