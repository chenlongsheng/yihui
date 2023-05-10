/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctc.wstx.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.beanvalidator.BeanValidators;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.sms.SMSUtils;
import com.jeeplus.common.utils.*;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.dao.RoleDao;
import com.jeeplus.modules.sys.dao.UserDao;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.SystemConfig;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.service.BureauService;
import com.jeeplus.modules.sys.service.OfficeService;
import com.jeeplus.modules.sys.service.SystemConfigService;
import com.jeeplus.modules.sys.service.SystemService;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 帐号Controller
 * 
 * @author jeeplus
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemConfigService systemConfigService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private BureauService bureauService;
	
	  /**
     *登录验证
     * @param
     */
    @RequestMapping("/checkLogin")
    public String checkLogin(HttpServletRequest request,HttpSession session,HttpServletResponse response) throws UnsupportedEncodingException {
        ModelMap map = new ModelMap();
        String username =  URLDecoder.decode(request.getParameter("username"), "utf-8");
        String password = request.getParameter("password");
        System.out.println(username);
     
        try {
            UsernamePasswordToken usernamePasswordToken=new UsernamePasswordToken(username,password);
            Subject subject = SecurityUtils.getSubject();
            subject.login(usernamePasswordToken);//完成登录
            
            User user=(User) subject.getPrincipal();
            session.setAttribute("user", user);
            map.put("status",200);
            map.put("message","登录成功");
            map.put("User",user);
        }catch (Exception e){
            map.put("status",500);
            map.put("message",e.getMessage());
        }
         return map.toString();
    }
    
   
	
	@RequestMapping(value = { "get" })
	@ResponseBody
	public String get(String id) {
		if (StringUtils.isNotBlank(id)) {
			return ServletUtils.buildRs(true, "获取帐号成功", systemService.getUser(id));
		} else {
			return ServletUtils.buildRs(true, "获取帐号成功", new User());
		}
	}

	@RequestMapping(value = { "index" })
	public String index(User user, Model model) {
		return "modules/sys/userIndex";
	}

	@RequestMapping(value = { "list" })
	@ResponseBody
	public String list(String loginName, String name, String roleId, String loginFlag, String areaId, User user,
			HttpServletRequest request, HttpServletResponse response) {

		MapEntity en = new MapEntity();
		Area area = new Area(areaId);
		user.setArea(area);
		user.setName(name);
		user.setLoginName(loginName);
		user.setLoginFlag(loginFlag);
		Role role = new Role();
		role.setId(roleId);
		user.setRole(role);
		user.setId(UserUtils.getUser().getId());
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);// 分頁
		List<MapEntity> elecOrgList = bureauService.getBureauListByUserId();//
		
		List<MapEntity> rolelist = systemService.roleNameList();
		en.put("page", page);
		en.put("elecOrgList", elecOrgList);
		en.put("rolelist", rolelist);
		return ServletUtils.buildRs(true, "获取帐号成功", en);
	}
	
	/*
	 * 用户归属所有供电所和配电房
	 */
	@RequestMapping(value = { "getBureauList" })
	@ResponseBody
	public String getBureauList() {
		return ServletUtils.buildRs(true, "用户归属供电所集合", systemService.getBureauList());
	}
	
	
	@RequestMapping(value = "form")
	@ResponseBody
	public String form(User user, String orgId, Model model, HttpServletRequest request) {
		Area area = new Area();
		area.setId(orgId);
		MapEntity entity = new MapEntity();
		if (user != null && StringUtils.isNotBlank(user.getId())) {
			user = systemService.getUser(user.getId());
			Role role = new Role();
			String roleId = userDao.userRole(user.getId());
			System.out.println(roleId + "----roleid添加時所有用戶-----");
			role.setId(roleId);
			user.setRole(role);
			entity.put("user", user);
			entity.put("sysUserOrgList", userDao.sysUserOrgList(user.getId()));
		}
//		List<MapEntity> orgElecList = systemService.userAreaList(user.getArea().getId());     
		Set<MapEntity> elecOrgList = areaService.elecOrgList(area);
		entity.put("orgElecList", elecOrgList);// 添加时所有配电房
		entity.put("allRoles", systemService.roleNameList());
		return ServletUtils.buildRs(true, "获取数据成功", entity);
	}

	
	@RequestMapping(value = "officeRole")
	@ResponseBody
	public JSONObject officeRole(String officeId) {
		JSONObject json = new JSONObject();
		Role role = new Role();
		Office office = new Office();
		office.setId(officeId);
		role.setOffice(office);
		List<Role> list = systemService.findRole(role);
		json.put("data", list);
		return json;
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public String save(User user, String areaId, String roleId, String orgElecList, HttpServletRequest request,
			Model model, RedirectAttributes redirectAttributes) {

		Role role = new Role();
		role.setId(roleId);
		user.setRole(role);
		Area area = new Area();
		area.setId(areaId);
		user.setArea(area);
		System.out.println(orgElecList + "==json==orgElecList");
		if (StringUtils.isBlank(orgElecList)) {
			orgElecList = "";
		}
		String list = orgElecList.replace("&quot;", "'");
		System.out.println(orgElecList + "==进入添加帐号===");
		JSONArray ja = JSONArray.parseArray(list);
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(user.getPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getPassword()));
		}
//		if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
//			return ServletUtils.buildRs(false, "保存帐号'" + user.getLoginName() + "'失败，登录名已存在", null);
//		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		roleList.add(roleDao.get(roleId));
//		List<String> roleIdList = user.getRoleIdList();
//		roleIdList.add(user.getRole().getId());
//		systemService.findAllRole();
//		for (Role r : systemService.findAllRole()) {
//			System.out.println(r.getId());
//			if (roleIdList.contains(r.getId())) {
//				roleList.add(r);
//			}
//		}
		user.setRoleList(roleList);
		// 生成帐号二维码，使用登录名
		String realPath = Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL + user.getId() + "/qrcode/";
		FileUtils.createDirectory(realPath);
		String name = user.getId() + ".png"; // encoderImgId此处二维码的图片名
		String filePath = realPath + name; // 存放路径
		user.setQrCode(request.getContextPath() + Global.USERFILES_BASE_URL + user.getId() + "/qrcode/" + name);
		// 保存帐号信息
		try {
			systemService.saveUser(user, ja);
			// 清除当前帐号缓存
			if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
				UserUtils.clearCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "保存帐号'" + user.getLoginName() + "'失败", null);
		}
		return ServletUtils.buildRs(true, "保存帐号'" + user.getLoginName() + "'成功", null);
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(User user) {
		try {
			System.out.println(user.getId() + "===============");
			User u = userDao.get(user.getId());
			systemService.deleteUser(user);
			UserUtils.saveLog("删除帐号：" + u.getLoginName(), "删除");
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "删除帐号失败", null);
		}
		return ServletUtils.buildRs(true, "删除帐号成功", null);
	}

	@RequestMapping(value = "reset")
	@ResponseBody
	public String reset(User user) {
		try {
			systemService.resetPassword(user.getId(), SystemService.entryptPassword("888888"));
			user = userDao.get(user.getId());
			UserUtils.clearCache(user);
			UserUtils.saveLog("帐号：" + user.getLoginName() + "密码被重置", "修改");// ---------------------
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "修改帐号失败", null);
		}
		return ServletUtils.buildRs(true, "修改帐号成功", null);
	}

	/**
	 * 导出帐号数据
	 * 
	 * @param user
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:export")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(User user, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = "帐号数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
			new ExportExcel("帐号数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出帐号失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 导入帐号数据
	 * 
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:import")
	@RequestMapping(value = "import", method = RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<User> list = ei.getDataList(User.class);
			for (User user : list) {
				try {
					if ("true".equals(checkLoginName("", user.getLoginName()))) {
						user.setPassword(SystemService.entryptPassword("123456"));
						BeanValidators.validateWithException(validator, user);
						systemService.saveUser(user, null);
						successNum++;
					} else {
						failureMsg.append("<br/>登录名 " + user.getLoginName() + " 已存在; ");
						failureNum++;
					}
				} catch (ConstraintViolationException ex) {
					failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList) {
						failureMsg.append(message + "; ");
						failureNum++;
					}
				} catch (Exception ex) {
					failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：" + ex.getMessage());
				}
			}
			if (failureNum > 0) {
				failureMsg.insert(0, "，失败 " + failureNum + " 条帐号，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 " + successNum + " 条帐号" + failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入帐号失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 下载导入帐号数据模板
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:import")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "帐号数据导入模板.xlsx";
			List<User> list = Lists.newArrayList();
			list.add(UserUtils.getUser());
			new ExportExcel("帐号数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage&signLogo=0";
	}

	/**
	 * 验证登录名是否有效
	 * 
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName != null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	@ResponseBody
	@RequestMapping(value = "checkUserLoginName")
	public String checkUserLoginName(String oldLoginName, String loginName) {
		if (loginName != null && loginName.equals(oldLoginName)) {
			return ServletUtils.buildRs(true, "成功", "");
		} else if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
			return ServletUtils.buildRs(true, "成功", "");
		}
		return ServletUtils.buildRs(false, "失败", "");
	}

	/**
	 * 帐号信息显示
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "info")
	public String info(HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfo";
	}

	/**
	 * 帐号信息显示编辑保存
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "infoEdit")
	public String infoEdit(User user, boolean __ajax, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())) {
			if (Global.isDemoMode()) {
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			if (user.getName() != null)
				currentUser.setName(user.getName());
			if (user.getEmail() != null)
				currentUser.setEmail(user.getEmail());
			if (user.getPhone() != null)
				currentUser.setPhone(user.getPhone());
			if (user.getMobile() != null)
				currentUser.setMobile(user.getMobile());
			if (user.getRemarks() != null)
				currentUser.setRemarks(user.getRemarks());
			// if(user.getPhoto() !=null )
			// currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			if (__ajax) {// 手机访问
				AjaxJson j = new AjaxJson();
				j.setSuccess(true);
				j.setMsg("修改个人资料成功!");
				return renderString(response, j);
			}
			model.addAttribute("user", currentUser);
			model.addAttribute("Global", new Global());
			model.addAttribute("message", "保存帐号信息成功");
			return "modules/sys/userInfo";
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfoEdit";
	}

	/**
	 * 帐号头像显示编辑保存
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "imageEdit")
	public String imageEdit(User user, boolean __ajax, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())) {
			if (Global.isDemoMode()) {
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			if (user.getPhoto() != null)
				currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			if (__ajax) {// 手机访问
				AjaxJson j = new AjaxJson();
				j.setSuccess(true);
				j.setMsg("修改个人头像成功!");
				return renderString(response, j);
			}
			model.addAttribute("message", "保存帐号信息成功");
			return "modules/sys/userInfo";
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userImageEdit";
	}

	/**
	 * 帐号头像显示编辑保存
	 * 
	 * @param user
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "imageUpload")
	public String imageUpload(HttpServletRequest request, HttpServletResponse response, MultipartFile file)
			throws IllegalStateException, IOException {
		User currentUser = UserUtils.getUser();
		// 判断文件是否为空
		if (!file.isEmpty()) {
			// 文件保存路径
			String realPath = Global.USERFILES_BASE_URL + UserUtils.getPrincipal() + "/images/";
			// 转存文件
			FileUtils.createDirectory(Global.getUserfilesBaseDir() + realPath);
			file.transferTo(new File(Global.getUserfilesBaseDir() + realPath + file.getOriginalFilename()));
			currentUser.setPhoto(request.getContextPath() + realPath + file.getOriginalFilename());
			systemService.updateUserInfo(currentUser);
		}
		return "modules/sys/userImageEdit";
	}

	/**
	 * 返回帐号信息
	 * 
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoData")
	public AjaxJson infoData() {
		AjaxJson j = new AjaxJson();
		j.setSuccess(true);
		j.setErrorCode("-1");
		j.setMsg("获取个人信息成功!");
		j.put("data", UserUtils.getUser());
		return j;
	}

	/**
	 * 修改个人帐号密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
			if (Global.isDemoMode()) {
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())) {
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			} else {
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
			return "modules/sys/userInfo";
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}

	/**
	 * 保存签名
	 */
	@ResponseBody
	@RequestMapping(value = "saveSign")
	public AjaxJson saveSign(User user, boolean __ajax, HttpServletResponse response, Model model) throws Exception {
		AjaxJson j = new AjaxJson();
		User currentUser = UserUtils.getUser();
		currentUser.setSign(user.getSign());
		systemService.updateUserInfo(currentUser);
		j.setMsg("设置签名成功");
		return j;
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId,
			HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = systemService.findUserByOfficeId(officeId);
		for (int i = 0; i < list.size(); i++) {
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_" + e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtils.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * web端ajax验证帐号名是否可用
	 * 
	 * @param loginName
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "validateLoginName")
	public boolean validateLoginName(String loginName, HttpServletResponse response) {

		User user = userDao.findUniqueByProperty("login_name", loginName);
		if (user == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * web端ajax验证手机号是否可以注册（数据库中不存在）
	 */
	@ResponseBody
	@RequestMapping(value = "validateMobile")
	public boolean validateMobile(String mobile, HttpServletResponse response, Model model) {

		User user = userDao.findUniqueByProperty("mobile", mobile);
		if (user == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * web端ajax验证手机号是否已经注册（数据库中已存在）
	 */
	@ResponseBody
	@RequestMapping(value = "validateMobileExist")
	public boolean validateMobileExist(String mobile, HttpServletResponse response, Model model) {
		User user = userDao.findUniqueByProperty("mobile", mobile);
		if (user != null) {
			return true;
		} else {
			return false;
		}
	}

	@ResponseBody
	@RequestMapping(value = "resetPassword")
	public AjaxJson resetPassword(String mobile, HttpServletResponse response, Model model) {
		SystemConfig config = systemConfigService.get("1");// 获取短信配置的帐号名和密码
		AjaxJson j = new AjaxJson();
		if (userDao.findUniqueByProperty("mobile", mobile) == null) {
			j.setSuccess(false);
			j.setMsg("手机号不存在!");
			j.setErrorCode("1");
			return j;
		}
		User user = userDao.findUniqueByProperty("mobile", mobile);
		String newPassword = String.valueOf((int) (Math.random() * 900000 + 100000));
		try {
			String result = UserUtils.sendPass(config.getSmsName(), config.getSmsPassword(), mobile, newPassword);
			if (!result.equals("100")) {
				j.setSuccess(false);
				j.setErrorCode("2");
				j.setMsg("短信发送失败，密码重置失败，错误代码：" + result + "，请联系管理员。");
			} else {
				j.setSuccess(true);
				j.setErrorCode("-1");
				j.setMsg("短信发送成功，密码重置成功!");
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
			}
		} catch (IOException e) {
			j.setSuccess(false);
			j.setErrorCode("3");
			j.setMsg("因未知原因导致短信发送失败，请联系管理员。");
		}
		return j;
	}
	// @InitBinder
	// public void initBinder(WebDataBinder b) {
	// b.registerCustomEditor(List.class, "roleList", new PropertyEditorSupport(){
	// @Autowired
	// private SystemService systemService;
	// @Override
	// public void setAsText(String text) throws IllegalArgumentException {
	// String[] ids = StringUtils.split(text, ",");
	// List<Role> roles = new ArrayList<Role>();
	// for (String id : ids) {
	// Role role = systemService.getRole(Long.valueOf(id));
	// roles.add(role);
	// }
	// setValue(roles);
	// }
	// @Override
	// public String getAsText() {
	// return Collections3.extractToString((List) getValue(), "id", ",");
	// }
	// });
	// }

	/**
	 * 查询帐号可用的菜单列表 header带token
	 * 
	 * @return
	 */
	@RequestMapping(value = "findMenuListByUser")
	@ResponseBody
	public JSONObject findMenuListByUser() {
		String userId = UserUtils.getUser().getId();
		System.out.println(userId + "-----userId");
		if (StringUtils.isBlank(userId)) {
			return null;
		}
		List<JSONObject> menuList = userDao.findMenuListByUser(userId);
		return ServletUtils.buildJsonRs(true, "", menuList);
	}

	@RequestMapping(value = "rolesByUserId") // 新写的接口帐号获取所有菜单999
	@ResponseBody
	public String rolesByUserId() {

		return ServletUtils.buildRs(true, "登入后新写菜单权限", systemService.rolesByUserId());
	}
    
//	@RequestMapping(value = "roleListByUserId") // 新写的接口帐号获取所有菜单999
//	@ResponseBody
//	public String roleListByUserId() {
//		return ServletUtils.buildRs(true, "", systemService.roleListByUserId());
//	}
	
	
}
