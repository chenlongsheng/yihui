/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TCodeType;
import com.jeeplus.modules.settings.service.TCodeService;
import com.jeeplus.modules.settings.service.TCodeTypeService;
import com.jeeplus.modules.settings.service.TDeviceService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * code管理Controller
 * 
 * @author long
 * @version 2018-08-09
 */
@Controller
@RequestMapping(value = "settings/tCode")
public class TCodeController extends BaseController {
	@Autowired
	private TCodeService tCodeService;
	@Autowired
	private TCodeTypeService tCodeTypeService;

	/**
	 * code管理列表页面
	 */
//	@RequiresPermissions("settings:tCode:list")
	@RequestMapping(value = { "list" })
	@ResponseBody
	public String list(TCode tCode, HttpServletRequest request, HttpServletResponse response, Model model) {

		
		Page<TCode> page = tCodeService.findPage(new Page<TCode>(request, response), tCode);
		return ServletUtils.buildRs(true, "字典集合成功", page);
	}
	
	
	@RequestMapping(value = { "devTypeList" })
	@ResponseBody
	public String devTypeList(TCode tCode, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		return ServletUtils.buildRs(true, "字典集合成功", tCodeService.devTypeList());
	}


	/**
	 * 查看，增加，编辑code管理表单页面
	 */
//	@RequiresPermissions(value = { "settings:tCode:view", "settings:tCode:edit" }, logical = Logical.OR)
	@RequestMapping(value = "form")
	@ResponseBody
	public String form(String id, String typeId, Model model) {
		MapEntity entity = new MapEntity();
		TCode tCode = null;
		if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(typeId)) {
			tCode = tCodeService.get(id, typeId);
		}
		if (tCode == null) {
			tCode = new TCode();
		}
//		model.addAttribute("tCode", tCode);
		List<TCodeType> tCodeTypelist = tCodeTypeService.findList(new TCodeType());
		entity.put("tCodeTypelist", tCodeTypelist);
		entity.put("tCode", tCode);
		return ServletUtils.buildRs(true, "字典主类型集合成功", entity);
		// model.addAttribute("tCodeTypelist", tCodeTypelist);
		// return "modules/settings/tCodeForm";
	}

//	@RequiresPermissions(value = { "settings:tCode:add" }, logical = Logical.OR)
	@RequestMapping(value = "formAdd")
	@ResponseBody
	public String formAdd(Model model) {
		MapEntity entity = new MapEntity();
		TCode tCode = new TCode();
		// model.addAttribute("tCode", tCode);
		List<TCodeType> tCodeTypelist = tCodeTypeService.findList(new TCodeType());
		entity.put("tCodeTypelist", tCodeTypelist);
		entity.put("tCode", tCode);
		return ServletUtils.buildRs(true, "字典主类型集合成功", entity);
		// model.addAttribute("tCodeTypelist", tCodeTypelist);
		// return "modules/settings/tCodeFormAdd";
	}
    
	/**
	 * 保存code管理
	 */
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(TCode tCode, Model model, RedirectAttributes redirectAttributes) throws Exception {
		try {
			if (!tCode.getIsNewRecord()) {// 编辑表单保存
				TCode t = tCodeService.get(tCode.getId(), tCode.getTypeId() + "");// 从数据库取出记录的值
				MyBeanUtils.copyBeanNotNull2Bean(tCode, t);// 将编辑表单中的非NULL值覆盖数据库记录中的值
				tCodeService.save(t);// 保存
			}
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "保存字典管理失败", null);
		}
		return ServletUtils.buildRs(true, "保存字典管理成功", null);
	}

	// 添加
	@RequestMapping(value = "saveAdd")
	@ResponseBody
	public String saveAdd(TCode tCode, String id, Model model, RedirectAttributes redirectAttributes) throws Exception {

		TCode t = tCodeService.get(tCode.getId(), tCode.getTypeId() + "");
		System.out.println(t == null);
		if (t == null) {
			try {
				tCodeService.saveAdd(tCode);
			} catch (Exception e) {
				return ServletUtils.buildRs(false, "保存字典管理失败", null);
			}
			return ServletUtils.buildRs(true, "保存字典管理成功", null);
		} else {
			return ServletUtils.buildRs(false, "已存在ID,保存字典管理失败", null);
		}
	}

	/**
	 * 删除code管理
	 */
//	@RequiresPermissions("settings:tCode:del")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(TCode tCode, RedirectAttributes redirectAttributes) {

		try {
			tCodeService.delete(tCode);
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "删除字典管理失败", null);
		}
		return ServletUtils.buildRs(true, "删除字典管理成功", null);
	}

	public String newFile(MultipartFile iconFile, Long typeId,String iconName, HttpServletRequest request)
			throws IllegalStateException, IOException {
		String newFileName = null;
		String originalFilename = iconFile.getOriginalFilename();
		// 上传图片
		if (iconFile != null && originalFilename != null && originalFilename.length() > 0) {
			// 存储图片的物理路径
			String pic_path = request.getSession().getServletContext().getRealPath("/");
			String path = null;
			if (typeId == 1) {
				path = "static_modules/device/";
			} else {
				path = "static_modules/channel/";
			}
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
			
			File oldFile = new File(pic_path + path + iconName);
			if(oldFile!=null) {
				oldFile.delete();
			}
		}
		return newFileName;
	}

	// 上传图片
	@RequestMapping(value = { "upLoadPic" })
	@ResponseBody
	public JSONObject upLoadPic(String id, Long typeId, HttpServletRequest request,
			@RequestParam(value = "iconFile", required = false) MultipartFile iconFile,
			@RequestParam(value = "warnFile", required = false) MultipartFile warnFile,
			@RequestParam(value = "offlineFile", required = false) MultipartFile offlineFile,
			@RequestParam(value = "defenceFile", required = false) MultipartFile defenceFile,
			@RequestParam(value = "withdrawingFile", required = false) MultipartFile withdrawingFile,
			@RequestParam(value = "sidewayFile", required = false) MultipartFile sidewayFile,
			HttpServletResponse response) throws Exception {

		JSONObject json = new JSONObject();
		TCode tCode = new TCode();
		tCode.setId(id);
		tCode.setTypeId(typeId);

		tCode = tCodeService.get(id, typeId + "");
        
		if (iconFile != null) {
			
			
			String newFileName = newFile(iconFile, typeId, tCode.getIconSkin(),request);
			tCode.setIconSkin(newFileName);
			UserUtils.saveLog("设备类型：" + tCode.getName() + " 的[正常]图例被修改", "修改");
		}
		if (warnFile != null) {
			String newFileName = newFile(warnFile, typeId, tCode.getWarnIconSkin(),request);
			tCode.setWarnIconSkin(newFileName);
			UserUtils.saveLog("设备类型：" + tCode.getName() + " 的[报警]图例被修改", "修改");
		}
		if (offlineFile != null) {
			String newFileName = newFile(offlineFile, typeId,tCode.getOfflineIconSkin(), request);
			tCode.setOfflineIconSkin(newFileName);
			UserUtils.saveLog("设备类型：" + tCode.getName() + " 的[离线]图例被修改", "修改");
		}
		if (defenceFile != null) {
			String newFileName = newFile(defenceFile, typeId,tCode.getDefenceIconSkin(), request);
			tCode.setDefenceIconSkin(newFileName);
			UserUtils.saveLog("设备类型：" + tCode.getName() + " 的[布防]图例被修改", "修改");
		}
		if (withdrawingFile != null) {
			String newFileName = newFile(withdrawingFile, typeId,tCode.getWithdrawingIconSkin(), request);
			tCode.setWithdrawingIconSkin(newFileName);
			UserUtils.saveLog("设备类型：" + tCode.getName() + " 的[撤防]图例被修改", "修改");

		}
		if (sidewayFile != null) {
			String newFileName = newFile(sidewayFile, typeId,tCode.getSidewayIconSkin() ,request);
			tCode.setSidewayIconSkin(newFileName);
			UserUtils.saveLog("设备类型：" + tCode.getName() + " 的[旁路]图例被修改", "修改");
		}
		TCode t = tCodeService.get(tCode.getId(), tCode.getTypeId() + "");// 从数据库取出记录的值
		MyBeanUtils.copyBeanNotNull2Bean(tCode, t);// 将编辑表单中的非NULL值覆盖数据库记录中的值
		try {
			tCodeService.save(t);// 保存
		} catch (Exception e) {
			json.put("suc", "上传失败!");
		}
		json.put("suc", "成功上传!");
		return json;
	}

	// 下载tcode小图标
	@RequestMapping(value = { "getPic" }, method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getpic(String id, String typeId, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		JSONObject json = new JSONObject();

		TCode tCode = tCodeService.get(id, typeId);
		String imageIcon = tCode.getIconSkin();

		String imageWarn = tCode.getWarnIconSkin();
		String imageOffline = tCode.getOfflineIconSkin();

		String defenceIcon = tCode.getDefenceIconSkin();
		String withdrawingIcon = tCode.getWithdrawingIconSkin();
		String sidewayIcon = tCode.getSidewayIconSkin();

		String path = null;
		if (tCode.getTypeId() == 1) {
			path = "static_modules/device/";
		} else {
			path = "static_modules/channel/";
		}
		String imageIconPath = request.getSession().getServletContext().getRealPath("/") + path + imageIcon;
		String imageWarnPath = request.getSession().getServletContext().getRealPath("/") + path + imageWarn;
		String imageOfflinePath = request.getSession().getServletContext().getRealPath("/") + path + imageOffline;
		String imageDefenceIcon = request.getSession().getServletContext().getRealPath("/") + path + defenceIcon;
		String imageWithdrawingIcon = request.getSession().getServletContext().getRealPath("/") + path
				+ withdrawingIcon;
		String imageSidewayIcon = request.getSession().getServletContext().getRealPath("/") + path + sidewayIcon;
		System.out.println(imageIconPath);

		File file1 = new File(imageIconPath);
		File file2 = new File(imageWarnPath);
		File file3 = new File(imageOfflinePath);

		File file4 = new File(imageDefenceIcon);
		File file5 = new File(imageWithdrawingIcon);
		File file6 = new File(imageSidewayIcon);

		if (StringUtils.isNotBlank(tCode.getIconSkin()) && StringUtils.isNotBlank(file1.getAbsolutePath())) {
			json.put("icon", path + imageIcon);
		}
		if (StringUtils.isNotBlank(tCode.getWarnIconSkin()) && StringUtils.isNotBlank(file2.getAbsolutePath())) {
			json.put("warn", path + imageWarn);
		}
		if (StringUtils.isNotBlank(tCode.getOfflineIconSkin()) && StringUtils.isNotBlank(file3.getAbsolutePath())) {
			json.put("offline", path + imageOffline);
		}
		if (StringUtils.isNotBlank(tCode.getDefenceIconSkin()) && StringUtils.isNotBlank(file4.getAbsolutePath())) {
			json.put("defence", path + defenceIcon);
		}
		if (StringUtils.isNotBlank(tCode.getWithdrawingIconSkin()) && StringUtils.isNotBlank(file5.getAbsolutePath())) {
			json.put("withdrawing", path + withdrawingIcon);
		}
		if (StringUtils.isNotBlank(tCode.getSidewayIconSkin()) && StringUtils.isNotBlank(file6.getAbsolutePath())) {
			json.put("sideway", path + sidewayIcon);
		}

		return json;
	}

	/**
	 * 批量删除code管理
	 */
	@RequiresPermissions("settings:tCode:del")
	@RequestMapping(value = "deleteAll")
	@ResponseBody
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] = ids.split(",");
		for (String is : idArray) {
			String id[] = is.split("_");
			System.out.println(id[0] + "idcode=====");
			System.out.println(id[1] + "typecode=====");
			tCodeService.delete(tCodeService.get(id[0], id[1]));
		}
		return ServletUtils.buildRs(true, "删除字典管理成功", null);
//		addMessage(redirectAttributes, "删除字典管理成功");
//		return "redirect:" + Global.getAdminPath() + "/settings/tCode/?repage&signLogo=0";
	}

	// 小图片回调
	@RequestMapping(value = "codeRedirect")
	@ResponseBody
	public String codeRedirect() throws Exception {
		return ServletUtils.buildRs(true, "小图标回调", null);
	}
	//设备类型
	
	

}