package com.jeeplus.modules.maintenance.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.maintenance.entity.PdfMaintenance;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;
import com.jeeplus.modules.maintenance.entity.PdfUser;
import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.maintenance.service.PdfMaintenanceDetailService;
import com.jeeplus.modules.maintenance.service.PdfMaintenanceService;
import com.jeeplus.modules.maintenance.service.PdfUserService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.utils.UserUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Administrator on 2018-12-25.
 */
@Controller
@RequestMapping(value = "maintain/maintenance")
public class MaintenanceController {

	@Autowired
	private PdfMaintenanceService pdfMaintenanceService;
	@Autowired
	private PdfMaintenanceDetailService pdfMaintenanceDetailService;
	@Autowired
	private PdfUserService pdfUserService;

	// 分页查询前的查询条件
	@ResponseBody
	@RequestMapping(value = "/message")
	public String message(String orgName, HttpServletRequest request, HttpServletResponse response) {
		MapEntity entity = new MapEntity();

		String orgId = UserUtils.getUser().getArea().getId();
		System.out.println(orgId + "---用户区域id");
		Set<MapEntity> orgList = pdfUserService.orgList(orgName, orgId);
		List<MapEntity> tcodeList = pdfUserService.tcodeList(null);
		entity.put("orgList", orgList);
		entity.put("tcodeList", tcodeList);
		return ServletUtils.buildRs(true, "维保人员分页查询条件", entity);
	}

	// 维保分页显示
	@ResponseBody
	@RequestMapping(value = "/list")
	public String list(PdfMaintenance pdfMaintenance, HttpServletRequest request, HttpServletResponse response) {
		// System.out.println(pdfMaintenance.toString());
		Page<PdfMaintenance> page = null;
		try {
			page = pdfMaintenanceService.findPage(new Page<PdfMaintenance>(request, response), pdfMaintenance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ServletUtils.buildRs(true, "维保人员分页", page);
	}

	// 维保人员详情
	@ResponseBody
	@RequestMapping(value = "/maintenanceDetail")
	public String maintenanceDetail(String maintenanceId) {
		List<PdfMaintenanceDetail> list = pdfMaintenanceDetailService.selectMaintenanceDetail(maintenanceId);
		return ServletUtils.buildRs(true, "维保人员详情", list);
	}

	// 保存图片资质
	public String saveFile(HttpServletRequest request, MultipartFile file, PdfMaintenance maintenance) {
		String path = request.getSession().getServletContext().getRealPath("");
		// 修改图片的uuid
		String image = UUID.randomUUID()
				+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String filePath = "/upload/" + image;
		// 拼接图片的路径
		File saveDir = new File(path + filePath);
		if (!saveDir.getParentFile().exists()) {
			saveDir.getParentFile().mkdirs();
		}
		try {
			file.transferTo(saveDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	// 添加/修改回调
	@ResponseBody
	@RequestMapping(value = "/form")
	public String form(String id, String orgName) {
		String orgId = UserUtils.getUser().getArea().getId();
		MapEntity entity = new MapEntity();
		PdfMaintenance pdfMaintenance = new PdfMaintenance();
		if (StringUtils.isNotBlank(id)) {
			pdfMaintenance = pdfMaintenanceService.get(id);
			List<PdfMaintenanceDetail> mainDetailList = pdfMaintenanceDetailService.selectMaintenanceDetail(id);
			// List<MapEntity> mainDetailList =
			// pdfMaintenanceDetailService.getMainDetailList(id);
			entity.put("mainDetailList", mainDetailList);
		}
		List<MapEntity> tcodeList = pdfUserService.tcodeList(null);
		Set<MapEntity> orgList = pdfUserService.orgList(orgName, orgId);
		entity.put("tcodeList", tcodeList);
		entity.put("orgList", orgList);
		entity.put("PdfMaintenance", pdfMaintenance);
		return ServletUtils.buildRs(true, "添加/修改回调成功", entity);
	}

	// 添加维保单位
	@ResponseBody
	@RequestMapping(value = "/addMaintenance")
	public String addMaintenance(@RequestParam(value = "images", required = false) MultipartFile[] images,
			String mainUserList, PdfMaintenance maintenance, HttpServletRequest request) {
		System.out.println("添加维保单位进来的===========");
		System.out.println(maintenance.toString());
		JSONArray ja = null;
		try {
			if (mainUserList != null) {
				String list = mainUserList.replace("&quot;", "'");// 替换json的乱码
				System.out.println(mainUserList);
				ja = JSONArray.parseArray(list);
			}
			System.out.println(images.length);
			if (images != null && images.length > 0) {// 遍历文件
				for (int i = 0; i < images.length; i++) {
					MultipartFile file = images[i];
					// 保存文件
					String url = saveFile(request, file, maintenance);
					maintenance.getUrl().add(url);
				}
			}
			if (StringUtils.isBlank(maintenance.getId())) {
				pdfMaintenanceService.addMaintenance(maintenance, ja);// 添加

				UserUtils.saveLog("添加维保单位：" + maintenance.getName(), "新增");
			} else {

				String maintenanceName = pdfMaintenanceService.get(maintenance.getId()).getName();//获取名称
				pdfMaintenanceService.editMaintenance(maintenance, ja, request);// 修改
				UserUtils.saveLog("修改维保单位：" + maintenanceName+"信息被修改", "修改");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "添加/修改失败", null);
		}
		return ServletUtils.buildRs(true, "添加/修改成功", null);
	}

	// 下载公司图片资质地址
	@ResponseBody
	@RequestMapping(value = "/getPic")
	public String getPic(String id) {

		List<String> list = pdfMaintenanceService.findPicList(id);
		return ServletUtils.buildRs(true, "获取资质集合成功", list);
	}

	// 分页中的维保详情
	@ResponseBody
	@RequestMapping(value = "/maintenDetail")
	public String maintenDetail(String maintenanceId) {
		System.out.println("维保详情中的公司id===" + maintenanceId);
		List<PdfMaintenanceDetail> list = null;
		list = pdfMaintenanceDetailService.selectMaintenanceDetail(maintenanceId);
		for (PdfMaintenanceDetail pdfMaintenanceDetail : list) {
			System.out.println(pdfMaintenanceDetail.toString());
		}
		System.out.println("结束");
		return ServletUtils.buildRs(true, "维保公司详情", list);
	}


	// 详情中的维保人员管辖的类型集合
	@ResponseBody
	@RequestMapping(value = "/selectCodeList")
	public String selectCodeList(String contacts) {

		List<MapEntity> list = pdfMaintenanceDetailService.selectCodeList(contacts);
		return ServletUtils.buildRs(true, "维保设备类型", list);
	}

	// 删除维保单位
	@ResponseBody
	@RequestMapping(value = "/delMaintenanceById")
	public String delMaintenanceById(String id, HttpServletRequest request) {
		
		try {
			String pdfMaintenanceName  = pdfMaintenanceService.get(id).getName();
			pdfMaintenanceService.delMaintenanceById(id, request);
			UserUtils.saveLog("删除维保单位："+pdfMaintenanceName, "删除");
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "删除失败", null);
		}
		return ServletUtils.buildRs(true, "删除成功", null);
	}

}
