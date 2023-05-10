/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.Collections3;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.dao.MenuDao;
import com.jeeplus.modules.sys.dao.RoleDao;
import com.jeeplus.modules.sys.entity.Menu;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.OfficeService;
import com.jeeplus.modules.sys.service.SystemService;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 角色Controller
 * 
 * @author jeeplus
 * @version 2013-12-05
 */
@Controller
@RequestMapping(value = "sys/role")
public class RoleController extends BaseController {

	@Autowired
	private SystemService systemService;

	@Autowired
	private OfficeService officeService;
	@Autowired
	RoleDao roleDao;
	@Autowired
	MenuDao menuDao;

	@ModelAttribute("role")
	public Role get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return systemService.getRole(id);
		} else {
			return new Role();
		}
	}
	
	@RequestMapping(value = { "list" })
	@ResponseBody
	public String list(Role role, Model model, HttpServletRequest request, HttpServletResponse response) {
		
		Page<Role> page = systemService.findPage(new Page<Role>(request, response), role);
		return ServletUtils.buildRs(true, "获取账号组名成功", page);
	}

	// 账号组下用户集合
	@RequestMapping(value = "roleUserList")
	@ResponseBody
	public String roleUserList(String id, HttpServletRequest request, HttpServletResponse response) {

		MapEntity entity = new MapEntity();
		entity.put("id", id);
		Page<MapEntity> page = systemService.findUser(new Page<MapEntity>(request, response), entity);
		return ServletUtils.buildRs(true, "账号组下用户集合", page);
	}

	@RequestMapping(value = "form")
	@ResponseBody
	public String form(Role role, Model model, HttpServletRequest request) {

		// model.addAttribute("role", role);
		// model.addAttribute("menuList", systemService.findAllMenu());
		// model.addAttribute("officeList", officeService.findAll());
		// return "modules/sys/roleForm";
		return ServletUtils.buildRs(true, "获取账号组名回调成功", role);
	}

	// 新写auth回调
	@RequestMapping(value = "auth")
	@ResponseBody
	public String auth(Role role, Model model) {

		MapEntity entity = new MapEntity();
		if (role != null && StringUtils.isNotBlank(role.getId())) {
			entity.put("roleMenu", roleDao.roleMenuList(role.getId()));
		}
  
		entity.put("menuList", menuDao.findAllMenuList());
	 
		return ServletUtils.buildRs(true, "获取成功", entity);
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public String save(Role role, String id, Model model, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		// 权限回调
		Office office = new Office();
		office.setId("1");
		role.setOffice(office);
		if (!"true".equals(checkName(role.getOldName(), role.getName(), role.getId()))) {
			return ServletUtils.buildRs(false, "保存角色'" + role.getName() + "'失败, 角色名已存在", null);
		}
		try {
			systemService.saveRole(role);
			UserUtils.saveLog("创建帐号组：" + role.getName(), "新增");// =====
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "保存角色'" + role.getName() + "'失败", null);
		}
		return ServletUtils.buildRs(true, "保存角色'" + role.getName() + "'成功", null);
	}

	@RequestMapping(value = "saveAuth")
	@ResponseBody
	public String saveAuth(Role role, Model model, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		// 保存权限
		List<String> list = role.getMenuIdList();
		for (String str : list) {
			System.out.println(str);
			if (str.equals("285421932680099999")) {
				roleDao.updateVisible("0",role.getId());//0是有添加新配电房权限
			}else {
				roleDao.updateVisible("1",role.getId());//1是没有添加新配电房权限
			}
		}
		try {
			systemService.saveAuth(role);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "保存权限'" + role.getName() + "'失败", null);
		}
		return ServletUtils.buildRs(true, "保存权限'" + role.getName() + "'成功", null);
	}

	// 删除账号组
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(Role role, RedirectAttributes redirectAttributes) {
		try {
			systemService.deleteRole(role);
		} catch (Exception e) {
			// TODO: handle exception
			return ServletUtils.buildRs(false, "删除角色失败", null);
		}
		return ServletUtils.buildRs(true, "删除角色成功", null);
	}

	
	/**
	 * 角色分配页面
	 * 
	 * @param role
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "assign")
	@ResponseBody
	public String assign(Role role, Model model) {
		
		List<User> userList = systemService.findUser(new User(new Role(role.getId())));
		model.addAttribute("userList", userList);
		return "modules/sys/roleAssign";
	}

	/**
	 * 角色分配 -- 打开角色分配对话框
	 * 
	 * @param role
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "usertorole")
	@ResponseBody
	public String selectUserToRole(Role role, Model model) {
		List<User> userList = systemService.findUser(new User(new Role(role.getId())));
		model.addAttribute("role", role);
		model.addAttribute("userList", userList);
		model.addAttribute("selectIds", Collections3.extractToString(userList, "name", ","));
		return "modules/sys/selectUserToRole";
	}

	/**
	 * 角色分配 -- 根据部门编号获取用户列表
	 * 
	 * @param officeId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "users")
	public List<Map<String, Object>> users(String officeId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		User user = new User();
		user.setOffice(new Office(officeId));
		Page<User> page = systemService.findUser(new Page<User>(1, -1), user);
		for (User e : page.getList()) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", 0);
			map.put("name", e.getName());
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 角色分配 -- 从角色中移除用户
	 * 
	 * @param userId
	 * @param roleId
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("sys:role:assign")
	@RequestMapping(value = "outrole")
	@ResponseBody
	public String outrole(String userId, String roleId, RedirectAttributes redirectAttributes) throws Exception {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/assign?id=" + roleId;
		}
		Role role = systemService.getRole(roleId);
		User user = systemService.getUser(userId);
		if (UserUtils.getUser().getId().equals(userId)) {
			addMessage(redirectAttributes, "无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
		} else {
			if (user.getRoleList().size() <= 1) {
				addMessage(redirectAttributes,
						"用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！这已经是该用户的唯一角色，不能移除。");
			} else {
				Boolean flag = systemService.outUserInRole(role, user);
				if (!flag) {
					addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
				} else {
					addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
				}
			}
		}
		return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
	}

	/**
	 * 角色分配
	 * 
	 * @param role
	 * @param idsArr
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequiresPermissions("sys:role:assign")
	@RequestMapping(value = "assignrole")
	@ResponseBody
	public String assignRole(Role role, String[] idsArr, RedirectAttributes redirectAttributes) throws Exception {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
		}
		StringBuilder msg = new StringBuilder();
		int newNum = 0;
		for (int i = 0; i < idsArr.length; i++) {
			User user = systemService.assignUserToRole(role, systemService.getUser(idsArr[i]));
			if (null != user) {
				msg.append("<br/>新增用户【" + user.getName() + "】到角色【" + role.getName() + "】！");
				newNum++;
			}
		}
		addMessage(redirectAttributes, "已成功分配 " + newNum + " 个用户" + msg);
		return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
	}

	
	/**
	 * 验证角色名是否有效
	 * @param oldName
	 * @param name
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "checkName")
	public String checkName(String oldName, String name, String officeId) {

		System.out.println(officeId);
		System.out.println(oldName);
		System.out.println(name);
		if (name != null && name.equals(oldName)) {
			return "true";
		} else if (name != null && systemService.getRoleByName(name, officeId) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 验证角色英文名是否有效
	 * 
	 * @param oldName
	 * @param name
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "checkEnname")
	public String checkEnname(String oldEnname, String enname, String officeId) {
		System.out.println(officeId);
		if (enname != null && enname.equals(oldEnname)) {
			return "true";
		} else if (enname != null && systemService.getRoleByEnname(enname, officeId) == null) {
			return "true";
		}
		return "false";
	}
	
	
	
	   @RequestMapping(value = "onlin/{id}/{name}/{status}")
	    @ResponseBody
	    // @PathVariable注解下的数据类型均可用
	    public String get(@PathVariable("id") Integer id ,@PathVariable("name") Integer name,@PathVariable("status") String status) {
	        // 返回一个User对象响应ajax的请求
	        
	        
	        return id+name+status ;
	    }


}
