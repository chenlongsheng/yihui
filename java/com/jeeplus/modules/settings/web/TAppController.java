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
import com.jeeplus.modules.settings.entity.TApp;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.service.TAppService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "settings/tApp")
public class TAppController extends BaseController {
	@Autowired
	private TAppService tAppService;

	@ResponseBody
	@RequestMapping(value = { "list" })
	public String list(TApp tApp, HttpServletRequest request, HttpServletResponse response) {
        MapEntity entity = new MapEntity();

		Page<TApp> page = tAppService.findPage(new Page<TApp>(request, response), tApp);
		entity.put("page", page);
		entity.put("getSystemList",tAppService. getSystemList());   //设备型号
		entity.put("getModelTypeList", tAppService.getModelTypeList());	//设备类型

		return ServletUtils.buildRs(true, "成功", entity);
	}

//	@ResponseBody
//	@RequestMapping(value = { "getMessageList" })
//	public String getMessageList() {
//		return ServletUtils.buildRs(true, "查询前回调", tAppService.getMessageList());
//	}

	@ResponseBody
	@RequestMapping(value = "save")
	public String save(TApp tApp, MultipartFile file, HttpServletRequest request) throws Exception {
		String url = tApp.getUrl();
		try {
			if (file != null) {
				String newFileName = newFile(file, url, request);
				tApp.setUrl(newFileName);
			}
			if (StringUtils.isNotBlank(tApp.getId())) {
				tAppService.updateApp(tApp);
			} else {
				tAppService.insertApp(tApp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", "");
		}
		return ServletUtils.buildRs(true, "成功", "");
	}

	@ResponseBody
	@RequestMapping(value = "delete")
	public String delete(TApp tApp, RedirectAttributes redirectAttributes) {
		try {
			tAppService.deleteApp(tApp);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", "");
		}
		return ServletUtils.buildRs(true, "成功", "");
	}

	public String newFile(MultipartFile iconFile, String iconName, HttpServletRequest request)
			throws IllegalStateException, IOException {
		String newFileName = null;
		String originalFilename = iconFile.getOriginalFilename();
		// 上传图片
		if (iconFile != null && originalFilename != null && originalFilename.length() > 0) {
			// 存储图片的物理路径
			String pic_path = request.getSession().getServletContext().getRealPath("/");
			String path = "static_modules/app/";

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

			File oldFile = new File(pic_path + iconName);
			if (oldFile != null) {
				oldFile.delete();
			}
			newFileName = path + newFileName;
		}
		return newFileName;
	}
}