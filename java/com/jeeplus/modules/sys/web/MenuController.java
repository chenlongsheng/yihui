/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.entity.Menu;
import com.jeeplus.modules.sys.service.SystemService;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 菜单Controller
 * 
 * @author jeeplus
 * @version 2013-3-23
 */
@Controller
@RequestMapping(value = "sys/menu")
public class MenuController extends BaseController {

	@Autowired
	private SystemService systemService;

	@ModelAttribute("menu")
	public Menu get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return systemService.getMenu(id);
		} else {
			return new Menu();
		}
	}

	// 查询回调
	@RequestMapping(value = { "message" })
	@ResponseBody
	public String parentMenuList() {
		return ServletUtils.buildRs(true, "获取菜单回调查询", systemService.parentMenuList());
	}

	@RequestMapping(value = { "list" })
	@ResponseBody
	public String list(String menuId, HttpServletRequest request, Model model) {
		String bathPath = request.getSession().getServletContext().getRealPath("");
		List<Menu> list = Lists.newArrayList();
		List<Menu> sourcelist = systemService.findMenuList(menuId);

		Menu.sortList(list, sourcelist, Menu.getRootId(), true);
//		for (int i = 0; i < list.size(); i++) {
//			if (list.get(i).getIcon() != null) {
//				list.get(i).setIcon(bathPath + list.get(i).getIcon());
//			}
//			List<Menu> listChild = list.get(i).getChildren();
//			if (listChild != null) {
//				for (int j = 0; j < listChild.size(); j++) {
//					Menu m = listChild.get(j);
////					if (m.getIcon() != null) {
////						m.setIcon(bathPath + m.getIcon());
////					}
//					List<Menu> listChildren = listChild.get(j).getChildren();
//					for (int k = 0; k < listChildren.size(); k++) {
//						Menu me = listChildren.get(k);
////						if (me.getIcon() != null) {
////							me.setIcon(bathPath + me.getIcon());
////					}
//					}					
//				}
//			}
//		}
		return ServletUtils.buildRs(true, "获取菜单成功", list);
	}

	@ResponseBody
	public String form(Menu menu, Model model) {
		if (menu.getParent() == null || menu.getParent().getId() == null) {
			menu.setParent(new Menu(Menu.getRootId()));
		}
		menu.setParent(systemService.getMenu(menu.getParent().getId()));
		// 获取排序号，最末节点排序号+30
		if (StringUtils.isBlank(menu.getId())) {
			List<Menu> list = Lists.newArrayList();
			List<Menu> sourcelist = systemService.findAllMenu();
			Menu.sortList(list, sourcelist, menu.getParentId(), false);
			if (list.size() > 0) {
				menu.setSort(list.get(list.size() - 1).getSort() + 30);
			}
		}
		return ServletUtils.buildRs(true, "菜单回调成功", menu);
	}

	@RequestMapping(value = "save")
	@ResponseBody
	public String save(Menu menu, MultipartFile file, HttpServletRequest request) throws Exception {

		System.out.println("菜单保存刚进来===");
		if (file != null) {
			String iconFileName = newFile(file, request);
			menu.setIcon(iconFileName);
		}
		try {
			systemService.saveMenu(menu);
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "保存菜单'" + menu.getName() + "'失败", null);
		}
		return ServletUtils.buildRs(true, "保存菜单'" + menu.getName() + "'成功", null);
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(Menu menu, RedirectAttributes redirectAttributes) {

		try {
			systemService.deleteMenu(menu);
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "删除菜单失败", null);
		}
		return ServletUtils.buildRs(true, "删除菜单成功", null);
	}

	public String newFile(MultipartFile iconFile, HttpServletRequest request)
			throws IllegalStateException, IOException {
		String newFileName = null;
		String originalFilename = iconFile.getOriginalFilename();
		// 上传图片
		if (iconFile != null && originalFilename != null && originalFilename.length() > 0) {
			// 存储图片的物理路径
			String pic_path = request.getSession().getServletContext().getRealPath("");
			String path = "/static_modules/icon/";

			System.out.println(pic_path + path);
			File file = new File(pic_path + path);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				System.out.println("//不存在");
				file.mkdir();
			} else {
				System.out.println("//目录存在");
			}
			newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
			// 新图片
			System.out.println(pic_path + path + newFileName);
			File newFile = new File(pic_path + path + newFileName);
			// 将内存中的数据写入磁盘
			iconFile.transferTo(newFile);
			newFileName = path + newFileName;
		}
		return newFileName;
	}

	@RequiresPermissions("sys:menu:del")
	@RequestMapping(value = "deleteAll")
	@ResponseBody
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		// 无用

		String idArray[] = ids.split(",");
		for (String id : idArray) {
			Menu menu = systemService.getMenu(id);
			if (menu != null) {
				systemService.deleteMenu(systemService.getMenu(id));
			}
		}
		addMessage(redirectAttributes, "删除菜单成功");

		return "redirect:" + adminPath + "/sys/menu/";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "tree")
	@ResponseBody
	public String tree() {
		return "modules/sys/menuTree";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "treeselect")
	@ResponseBody
	public String treeselect(String parentId, Model model) {
		model.addAttribute("parentId", parentId);
		return "modules/sys/menuTreeselect";
	}

	/**
	 * 批量修改菜单排序
	 */
	@RequiresPermissions("sys:menu:updateSort")
	@RequestMapping(value = "updateSort")
	@ResponseBody
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
		for (int i = 0; i < ids.length; i++) {
			Menu menu = new Menu(ids[i]);
			menu.setSort(sorts[i]);
			systemService.updateMenuSort(menu);
		}
		addMessage(redirectAttributes, "保存菜单排序成功!");
		return "redirect:" + adminPath + "/sys/menu/";
	}

	/**
	 * isShowHide是否显示隐藏菜单
	 * 
	 * @param extId
	 * @param isShowHidden
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
			@RequestParam(required = false) String isShowHide, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Menu> list = systemService.findAllMenu();
		for (int i = 0; i < list.size(); i++) {
			Menu e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId())
					&& e.getParentIds().indexOf("," + extId + ",") == -1)) {
				if (isShowHide != null && isShowHide.equals("0") && e.getIsShow().equals("0")) {
					continue;
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}

	@ResponseBody
	@RequestMapping(value = "findAllList")
	public String findAllList() {

		return ServletUtils.buildRs(true, "9999", systemService.findAllList());

	}

}
