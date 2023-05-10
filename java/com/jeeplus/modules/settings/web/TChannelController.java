/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TDeviceService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 通道管理Controller
 * 
 * @author ywk
 * @version 2018-06-22
 */
@Controller
@RequestMapping(value = "settings/tChannel")
public class TChannelController extends BaseController {

	@Autowired
	private TChannelService tChannelService;

	@Autowired
	private TDeviceService tDeviceService;

	@Autowired
	private AreaService areaService;

	@ModelAttribute("tChannel")
	public TChannel get(@RequestParam(required = false) String id) {
		TChannel entity = null;
		if (id != null) {
			entity = tChannelService.get(id);
		}
		if (entity == null) {
			entity = new TChannel();
		}
		return entity;
	}
	/**
	 * 通道列表页面
	 */
//	@RequiresPermissions("settings:tChannel:list")
	@RequestMapping(value = { "list",""})
	public String list(String tDeviceId, String loginOrg, String chName, String chCodeName, String chNotUse,
			String signLogo, HttpServletRequest request, HttpServletResponse response, Model model) {
		String orgId = UserUtils.getUser().getArea().getId();
		System.out.println(tDeviceId + "---tDdevice");
		System.out.println(loginOrg+"----新加的区域");
		
		if (signLogo == null) {
			request.getSession().removeAttribute("chtDeviceId");
			request.getSession().removeAttribute("chLoginOrg");
			request.getSession().removeAttribute("chtCodeName");
			request.getSession().removeAttribute("chNotUse");
			request.getSession().removeAttribute("chName");
		}
		if (tDeviceId != null || chName != null || loginOrg != null || chCodeName != null || chNotUse != null) {
			System.out.println("有的");
			request.getSession().setAttribute("chtDeviceId", tDeviceId);
			request.getSession().setAttribute("chLoginOrg", loginOrg);
			request.getSession().setAttribute("chtCodeName", chCodeName);
			request.getSession().setAttribute("chNotUse", chNotUse);
			request.getSession().setAttribute("chName", chName);
		}
		if (signLogo != null) {
			System.out.println("空的");
			tDeviceId = (String) request.getSession().getAttribute("chtDeviceId");
			loginOrg = (String) request.getSession().getAttribute("chLoginOrg");
			chCodeName = (String) request.getSession().getAttribute("chtCodeName");
			chNotUse = (String) request.getSession().getAttribute("chNotUse");
			chName = (String) request.getSession().getAttribute("chName");
		}

		MapEntity entity = new MapEntity();
		entity.put("tDeviceId", tDeviceId);
		entity.put("codeName", chCodeName);
		entity.put("notUse", chNotUse);
		entity.put("chName", chName);
		System.out.println(orgId);
		List<MapEntity> orgList = tChannelService.orgList(orgId);
		model.addAttribute("orgList", orgList);
		// 添加时需要t_org参数
		String id = null;
		TChannel tChannel = tChannelService.get(id);
		model.addAttribute("tChannel", tChannel);
		// 获取t_code表集合
		List<MapEntity> typeList = tChannelService.typeList();
		model.addAttribute("typeList", typeList);

		Page<MapEntity> page = tChannelService.findPage(new Page<MapEntity>(request, response), entity);
		model.addAttribute("page", page);
		model.addAttribute("tDeviceId", tDeviceId);
		model.addAttribute("chCodeName", chCodeName);
		model.addAttribute("chNotUse", chNotUse);
		model.addAttribute("chName", chName);
		if(loginOrg!=null) {
			orgId = loginOrg;
		}
		model.addAttribute("loginOrg", orgId);
		return "modules/settings/tChannelList";
	}

	
	/**
	 * 查看，增加，编辑通道表单页面
	 */
	@RequiresPermissions(value = { "settings:tChannel:view", "settings:tChannel:add",
			"settings:tChannel:edit" }, logical = Logical.OR)
	@RequestMapping(value = "form")
	public String form(@RequestParam(required = false) String id, Model model,HttpServletRequest request) {
		System.out.println(id + "---id");
		String orgId = UserUtils.getUser().getArea().getId();
		String tDeviceId = (String) request.getSession().getAttribute("chtDeviceId");
		TDevice tDevice = tDeviceService.get(tDeviceId);
		TChannel tChannel = tChannelService.get(id);
		if(tChannel==null) {
			tChannel = new TChannel();
			tChannel.setDevId(Long.parseLong(tDeviceId));
			tChannel.setDevName(tDevice.getName());
			String orgTreeId=tDevice.getOrgId();			
//			String orgTreeId = (String) request.getSession().getAttribute("chLoginOrg");
			if(orgTreeId!=null) {
				orgId=orgTreeId;
			}
			tChannel.setLogicOrgId(orgId);
			tChannel.setArea(areaService.get(orgId));
		}
		model.addAttribute("tChannel", tChannel);

		List<TChannel> tChannelList = tChannelService.findAllList();
		model.addAttribute("tChannelList", tChannelList);
		// 区域地区集合
		List<MapEntity> orgList = tChannelService.orgList(null);
		model.addAttribute("orgList", orgList);

		// 获取t_code表集合
		List<MapEntity> typeList = tChannelService.typeList();
		model.addAttribute("typeList", typeList);
		// 获取t_device表全部集合
		List<TDevice> findAllList = tDeviceService.findAllList();
		model.addAttribute("findAllList", findAllList);
		return "modules/settings/tChannelForm";
	}

	/**
	 * 保存通道
	 */
	@RequiresPermissions(value = { "settings:tChannel:add", "settings:tChannel:edit" }, logical = Logical.OR)
	@RequestMapping(value = "save")
	public String save(TChannel tChannel, Model model, HttpServletRequest request ,RedirectAttributes redirectAttributes) throws Exception {

		System.out.println(tChannel.toString() + "----");
		if (!beanValidator(model, tChannel)) {
			return form(tChannel.getId(), model,request);
		}
		if (!tChannel.getIsNewRecord()) {// 编辑表单保存
			TChannel t = tChannelService.get(tChannel.getId());// 从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(tChannel, t);// 将编辑表单中的非NULL值覆盖数据库记录中的值
			tChannelService.save(t);// 保存
		} else {// 新增表单保存
			tChannelService.save(tChannel);// 保存
		}
		addMessage(redirectAttributes, "保存通道成功");
		return "redirect:" + Global.getAdminPath() + "/settings/tChannel/?repage&signLogo=0";
	}

	/**
	 * 删除通道
	 */
	@RequiresPermissions("settings:tChannel:del")
	@RequestMapping(value = "delete")
	public String delete(TChannel tChannel, RedirectAttributes redirectAttributes) {
		tChannelService.delete(tChannel);
		addMessage(redirectAttributes, "删除通道成功");
		return "redirect:" + Global.getAdminPath() + "/settings/tChannel/?repage&signLogo=0";
	}

	/**
	 * 批量删除通道
	 */
	@RequiresPermissions("settings:tChannel:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] = ids.split(",");
		for (String id : idArray) {
			tChannelService.delete(tChannelService.get(id));
		}
		addMessage(redirectAttributes, "删除通道成功");
		return "redirect:" + Global.getAdminPath() + "/settings/tChannel/?repage&signLogo=0";
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("settings:tChannel:export")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(TChannel tChannel, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = "通道" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<TChannel> page = tChannelService.findPage(new Page<TChannel>(request, response, -1), tChannel);
			new ExportExcel("通道", TChannel.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出通道记录失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/settings/tChannel/?repage";
	}

	/**
	 * 导入Excel数据
	 */
	@RequiresPermissions("settings:tChannel:import")
	@RequestMapping(value = "import", method = RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TChannel> list = ei.getDataList(TChannel.class);
			for (TChannel tChannel : list) {
				try {
					tChannelService.save(tChannel);
					successNum++;
				} catch (ConstraintViolationException ex) {
					failureNum++;
				} catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum > 0) {
				failureMsg.insert(0, "，失败 " + failureNum + " 条通道记录。");
			}
			addMessage(redirectAttributes, "已成功导入 " + successNum + " 条通道记录" + failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入通道失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/settings/tChannel/?repage";
	}

	/**
	 * 下载导入通道数据模板
	 */
	@RequiresPermissions("settings:tChannel:import")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "通道数据导入模板.xlsx";
			List<TChannel> list = Lists.newArrayList();
			new ExportExcel("通道数据", TChannel.class, 1).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + Global.getAdminPath() + "/settings/tChannel/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
			@RequestParam(required = false) String deviceName, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		System.out.println(deviceName + "====deviceName");
		System.out.println(extId + "---extId");
		TDevice tDevice = new TDevice();
		tDevice.setName(deviceName);
		List<TDevice> list = tDeviceService.findList(tDevice);
		for (int i = 0; i < list.size(); i++) {
			TDevice e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("name",  e.getId()+":"+e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	
	
	@RequestMapping(value = "eMapForm")
	public String form(String orgId,String coldId,String typeId,String coordX,String coordY, HttpServletRequest request,Model model) {
		System.out.println(orgId+"----哈哈哈");
		System.out.println(coldId);
		
		String[] str = coldId.split(",");
		
		List<MapEntity> channelList = tChannelService.channelPic(orgId,str[1],str[2]);
		
		model.addAttribute("length",channelList.size());
		model.addAttribute("channelList",channelList);
		request.getSession().setAttribute("coordX", coordX);
		request.getSession().setAttribute("coordY", coordY);
		System.out.println(coordX);
		System.out.println(coordY);
		
		return "modules/settings/eMapChannelForm";
	}
	
	@RequestMapping(value = "eMapSave")
	@ResponseBody
	public String eMapSave(String id,Model model, HttpServletRequest request) {
		System.out.println(id+"----哈哈哈");
		String coordX = (String) request.getSession().getAttribute("coordX");
		String coordY = (String) request.getSession().getAttribute("coordY");
	System.out.println(coordX);
	System.out.println(coordY);
	int key = tChannelService.updateCoords(id,coordX,coordY);
	System.out.println(key);
	return "1";
	}
	

}