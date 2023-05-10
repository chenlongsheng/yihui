/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ctc.wstx.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 区域Controller
 * 
 * @author jeeplus
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "sys/area")
public class AreaController extends BaseController {

	@Autowired
	private AreaService areaService;

	@Autowired
	private TDeviceDao tDeviceDao;

	@ModelAttribute("area")
	public Area get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return areaService.get(id);
		} else {
			return new Area();
		}
	}

	// 区域集合查询
	@RequestMapping(value = { "homePageList" })
	@ResponseBody
	public String homePageList(String orgId) {
		if (StringUtils.isBlank(orgId)) {
			orgId = UserUtils.getUser().getArea().getId();
		}
		return ServletUtils.buildRs(true, "所有集合查询", areaService.orgEditList(orgId));
	}

	// 获取配电房集合根据orgId,只有区域下配电房
	@RequestMapping(value = { "getPdfListByOrgId" })
	@ResponseBody
	public String getPdfListByOrgId(String orgId) {

		if (StringUtils.isBlank(orgId)) {
			orgId = UserUtils.getUser().getArea().getId();
		}
		return ServletUtils.buildRs(true, "配电房集合查询", areaService.getOrgListById(orgId));
	}

	// 获取配电房集合根据orgId,区域下所有配电房
	@RequestMapping(value = { "pdfList" })
	@ResponseBody
	public String pdfList(String orgId) {

		if (StringUtils.isBlank(orgId)) {
			orgId = UserUtils.getUser().getArea().getId();
		}
		return ServletUtils.buildRs(true, "配电房集合查询", areaService.pdfList(orgId));
	}

	// 首页树形所有集合 和配电房树形查询
	@RequestMapping(value = { "list" })
	@ResponseBody
	public String list(Area area, Model model) {

		if (area.getBeginCount() == null) {
			area.setBeginCount(0);
		}
		if (area.getEndCount() == null) {
			area.setEndCount(99999);
		}
		Set<MapEntity> list = null;
		Set<MapEntity> pdfList = new HashSet<MapEntity>();
		try {
			list = areaService.elecOrgList(area);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "所有集合查报错", null);
		}
		return ServletUtils.buildRs(true, "所有集合查询", list);
	}

	// 配电房添加前回调
	@RequestMapping(value = "form")
	@ResponseBody
	public String form(String id, Model model) {

		MapEntity entity = new MapEntity();
		String orgId = UserUtils.getUser().getArea().getId();
		List<MapEntity> orgList = areaService.orgList(orgId);
		List<MapEntity> userList = areaService.userList();
		if (StringUtils.isNotBlank(id)) {
			MapEntity org = areaService.getOrg(id);
			entity.put("org", org);
		}
		entity.put("orgList", orgList);
		entity.put("userList", userList);
		return ServletUtils.buildRs(true, "添加/修改回調", entity);
	}

	// 配电房管理添加
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(Area area, Model model, RedirectAttributes redirectAttributes) {

		if (StringUtils.isNotBlank(area.getParentId())) {
			Area a = new Area();
			a.setId(area.getParentId());
			area.setParent(a);
		}
		System.out.println(area.getParent().getId() + "------区域父id-----------");
		// 获取区域底下最大code
		String addCode = "";
		String str = "";
		String code = "10";
		String maxCode = "10";
		if (!"0".equals(area.getParentId())) {
			code = areaService.selectCode(area.getParentId());
			maxCode = areaService.maxCode(area.getParentId());
		}
		if (maxCode != null) {
			str = maxCode.substring(maxCode.length() - 2);
			System.out.println(str);
		}
		if (maxCode == null) {
			addCode = code + "01";
		} else {
			addCode = code + String.format("%02d", (Long.parseLong(str) + 1));
		}
		// 修改区域时候变更父id改变code
		if (area.getId() != null) {
			Area a = areaService.get(area.getId());
			System.out.println("加入area---" + area.getParentId());
			System.out.println("加入area---" + a.getParentId());
			if (!a.getParentId().equals(area.getParentId())) {
				area.setCode(addCode);
			     area.setParentIds(a.getParentIds());
			}
		} else {
			Area a = areaService.get(area.getParentId());
			area.setType("5");
			area.setCode(addCode);
		}
		try {
			String id = area.getId();
			areaService.save(area);
			if (StringUtils.isBlank(id)) {
				UserUtils.saveLog("新建配电房：" + area.getName(), "新增");
			} else {
				UserUtils.saveLog("修改配电房信息：" + area.getName(), "修改");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "保存区域'" + area.getName() + "'失败", null);
		}
		return ServletUtils.buildRs(true, "保存区域'" + area.getName() + "'成功", null);
	}

	// 删除配电房前
	@RequestMapping(value = "deleteDevBefore")
	@ResponseBody
	public String deleteDevBefore(String id) {

		int count = areaService.deviceByOrgCount(id);
		if (count > 0) {
			return ServletUtils.buildRs(false, "无法删除", null);
		}
		return ServletUtils.buildRs(true, "允许删除", null);
	}

	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(Area area, RedirectAttributes redirectAttributes) {

		try {
			areaService.delete(area);
			UserUtils.saveLog("删除配电房：" + area.getName(), "删除");
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "删除失败！", null);
		}
		return ServletUtils.buildRs(true, "删除成功", null);
	}

	// 行政区域---------==============================
	@RequestMapping(value = "getorgList")
	@ResponseBody
	public String getorgList() {
		return ServletUtils.buildRs(true, "行政区域所有", areaService.getorgList());
	}

	@RequestMapping(value = "deleteBefore")
	@ResponseBody
	public String deleteBefore(String id) {
		int key = areaService.count(id);
		if (key > 0) {
			return ServletUtils.buildRs(false, "无法删除", null);
		}
		return ServletUtils.buildRs(true, "允许删除", null);
	}

	@RequestMapping(value = "deleteOrg")
	@ResponseBody
	public String deleteOrg(Area area) {

		try {
			areaService.delete(area);
			UserUtils.saveLog("删除行政区划：" + area.getName(), "删除");
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "删除失败！", null);
		}
		return ServletUtils.buildRs(true, "删除成功", null);
	}

	// 添加行政区域
	@RequestMapping(value = "saveOrg")
	@ResponseBody
	public String saveOrg(Area area) {

		if (StringUtils.isNotBlank(area.getParentId())) {///// 你好
			Area a = new Area();
			a.setId(area.getParentId());
			area.setParent(a);
		}

		System.out.println(area.toString());
		// 获取区域底下最大code

		// 修改区域时候变更父id改变code
		if (StringUtils.isNotBlank(area.getId())) {
			Area a = areaService.get(area.getId());
			if (!a.getParentId().equals(area.getParentId())) {
			}
		} else {
//			String addCode = "10" + area.getCode();
//			area.setCode(addCode);
			Area a = areaService.get(area.getParentId());
			String type = (Integer.parseInt(a.getType()) + 1) + "";
			area.setType(type);
		}
		try {
			String id = area.getId();

			if (StringUtils.isBlank(id)) {
				areaService.saveOrg(area);
				UserUtils.saveLog("新建新政区划：" + area.getName(), "新增");
			} else {
				String oldName = areaService.get(id).getName();
				areaService.saveOrg(area);

				UserUtils.saveLog("编辑新政区划：" + area.getName(), "修改");// -------------------------------------
				UserUtils.saveLog("行政区划" + oldName + "被改为" + area.getName(), "修改");
			}
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "保存区域'" + area.getName() + "'失败", null);
		}
		return ServletUtils.buildRs(true, "保存区域'" + area.getName() + "'成功", null);
	}

	// ---------------------------------
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
			HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Area> list = areaService.findAll();
		for (int i = 0; i < list.size(); i++) {
			Area e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId())
					&& e.getParentIds().indexOf("," + extId + ",") == -1)) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeDataUser")
	public List<Map<String, Object>> treeDataUser(@RequestParam(required = false) String extId,
			HttpServletResponse response) {
		String orgId = UserUtils.getUser().getArea().getId();
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Area> list = areaService.userOrgList(orgId);
		for (int i = 0; i < list.size(); i++) {
			Area e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId())
					&& e.getParentIds().indexOf("," + extId + ",") == -1)) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	/**
	 * 
	 * 
	 * 
	 *   excel表格导入改写
	 */
//	@ResponseBody
//	@RequestMapping(value = "excel")
//	public String excel(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
//		Area area = new Area();
//		Area parentArea = new Area();
//		CommonsMultipartFile cf = (CommonsMultipartFile) file;
//		// 这个myfile是MultipartFile的
//		DiskFileItem fi = (DiskFileItem) cf.getFileItem();
//		File f = fi.getStoreLocation();
//		try {
//			Workbook workbook = Workbook.getWorkbook(f);
//			Sheet sheet = workbook.getSheet(0);
//			// j为行数，getCell("列号","行号")
//			for (int i = 1, j = sheet.getRows(); i < j; i++) {
//				Cell c1 = sheet.getCell(0, i);
//				String parentName = c1.getContents();
//				Cell c2 = sheet.getCell(1, i);
//				String name = c2.getContents();
//				Cell c3 = sheet.getCell(2, i);
//				String remarks = c3.getContents();
//
////				String id = areaService.parentName(parentName);
//				String id = "";
//				if (id == null) {
//					return ServletUtils.buildRs(false, "添加区域" + name + "失败!", null);
//				}
//				System.out.println(id + "====parentId");
//				parentArea.setId(id);
//				area.setParent(parentArea);
//				area.setName(name);
//				area.setRemarks(remarks);
//				this.save(area, model, redirectAttributes);
//				System.out.println(" 上级名:" + parentName + ",名称:" + name + ",备注:" + remarks);
//			}
//		} catch (BiffException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return ServletUtils.buildRs(true, "添加区域成功", null);
//	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "excelPoi")
	public String excelPoi(@RequestParam("file") MultipartFile file, Model model,
			RedirectAttributes redirectAttributes) {
		CommonsMultipartFile cf = (CommonsMultipartFile) file;
		// 这个myfile是MultipartFile的
		DiskFileItem fi = (DiskFileItem) cf.getFileItem();
		File f = fi.getStoreLocation();
		try {
			InputStream is = cf.getInputStream();
//			FileInputStream is = new FileInputStream(f);
			XSSFWorkbook workbook = new XSSFWorkbook(is);
			XSSFSheet sheet = workbook.getSheetAt(0);
			for (int i = 1, j = sheet.getLastRowNum(); i <= j; i++) {
				XSSFCell c_name = sheet.getRow(i).getCell((short) 0);
				String parentName = c_name.toString();
				XSSFCell c_score = sheet.getRow(i).getCell((short) 1);
				String name = c_score.toString();
				
				System.out.println(" 上级名:" + parentName + ",名称:" + name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "添加区域失败", null);
		}
		return ServletUtils.buildRs(true, "添加区域成功", null);
	}

	
	@RequestMapping(value = "test123")//新写配电房管理树形
	@ResponseBody
	public String test123(Area area, Model model, RedirectAttributes redirectAttributes) {
		
		return ServletUtils.buildRs(true, "", areaService.test123(area));
		
	}
}
