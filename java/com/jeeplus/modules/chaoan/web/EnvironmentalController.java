/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.chaoan.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.chaoan.service.EnvironmentalService;

import com.jeeplus.modules.machine.service.TDeviceMachineService;
import com.jeeplus.modules.machine.service.TDeviceRportService;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "environmental/")
public class EnvironmentalController extends BaseController {

	@Autowired
	private EnvironmentalService environmentalService;

	public static Logger logger = LoggerFactory.getLogger(TDeviceMachineService.class);

	@RequestMapping(value = { "powerCodes" })
	@ResponseBody
	public String powerCodes(String status) {
		return ServletUtils.buildRs(true, "出线柜数据下拉框", environmentalService.powerCodes(status));
	}

	@RequestMapping(value = { "changeCymbols" })
	@ResponseBody
	public String changeCymbols(String chTypes) {
		return ServletUtils.buildRs(true, "出线柜表头", environmentalService.changeCymbols(chTypes));
	}

	@RequestMapping(value = { "tDeviceTypes" })
	@ResponseBody
	public String tDeviceTypes(String orgId) {
		return ServletUtils.buildRs(true, "环境数据报表", environmentalService.tDeviceTypes(orgId));
	}

	@RequestMapping(value = { "wenshiList" })
	@ResponseBody
	public String wenshiList(String beginDate, String endDate, String devId) {
		return ServletUtils.buildRs(true, "温湿度据报表", environmentalService.wenshiList(beginDate, endDate, devId));
	}

	@RequestMapping(value = { "getMessageLogs" })
	@ResponseBody
	public String getMessageLogs(HttpServletRequest request, HttpServletResponse response, String beginTime,
			String endTime, String devType, String phone, String addr, String level, String status, String alarmType) {

		MapEntity entity = new MapEntity();
		entity.put("beginTime", beginTime);
		entity.put("endTime", endTime);
		entity.put("devType", devType);
		entity.put("phone", phone);
		entity.put("addr", addr);
		entity.put("level", level);
		entity.put("status", status);
		entity.put("alarmType", alarmType);

		Page<MapEntity> page = environmentalService.findPageMessage(new Page<MapEntity>(request, response), entity);
		return ServletUtils.buildRs(true, "消息日志", page);

	}

	@RequestMapping(value = { "getPdfEntranceLogs" })
	@ResponseBody
	public String getPdfEntranceLogs(String userName, String beginTime, String endTime, String employeeNo,
			HttpServletRequest request, HttpServletResponse response) {

		System.out.println(userName);
		System.out.println(beginTime);
		System.out.println(endTime);
		System.out.println(employeeNo);

		MapEntity entity = new MapEntity();
		entity.put("userName", userName);
		entity.put("beginTime", beginTime);
		entity.put("endTime", endTime);
		entity.put("employeeNo", employeeNo);
		Page<MapEntity> page = environmentalService.findPage(new Page<MapEntity>(request, response), entity);
		return ServletUtils.buildRs(true, "人员管理", page);
	}

	@RequestMapping("/exportWenshiList") // 导出温湿度
	@ResponseBody
	public void exportWenshiList(String beginDate, String endDate, String devId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		environmentalService.exportWenshiList(httpServletRequest, httpServletResponse,
				environmentalService.wenshiList(beginDate, endDate, devId), "温湿度报表");
	}

	@RequestMapping(value = { "insertPdfEntranceLog" })
	@ResponseBody
	public String insertPdfEntranceLog(@RequestBody String pdfEntranceLog) {

		JSONObject jobj = JSON.parseObject(pdfEntranceLog);
		logger.debug(jobj.toString());
		System.out.println("添加门禁日志: " + jobj);
		try {
			environmentalService.insertPdfEntranceLog(jobj.getString("user_name").toString(),
					jobj.getString("unlock_type").toString(), jobj.getString("picture_url").toString(),
					jobj.getString("device_sn").toString(), jobj.getString("result").toString(),
					jobj.getString("employee_no").toString(), jobj.getString("event_time").toString(),
					jobj.getString("channel_no").toString());
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "添加门禁日志失败", null);
		}
		return ServletUtils.buildRs(true, "添加门禁日志成功", null);
	}

	/*
	 * 
	 * ph2.5
	 */

	@RequestMapping(value = { "ph2and10" })
	@ResponseBody
	public String ph2and10(String beginDate, String endDate, String devId) {
		return ServletUtils.buildRs(true, "ph2据报表", environmentalService.ph2and10(beginDate, endDate, devId));
	}

	@RequestMapping("/exportPH25_10") // 导出ph2.5
	@ResponseBody
	public void exportPH25_10(String beginDate, String endDate, String devId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		environmentalService.exportPH25_10(httpServletRequest, httpServletResponse,
				environmentalService.ph2and10(beginDate, endDate, devId));

	}

	/***
	 * 
	 * 氢气
	 */
	@RequestMapping(value = { "gasConcentration" })
	@ResponseBody
	public String gasConcentration(String beginDate, String endDate, String devId) {
		return ServletUtils.buildRs(true, "氢气据报表", environmentalService.gasConcentration(beginDate, endDate, devId));
	}

	@RequestMapping("/exportGasConcentration") // 导出氢气
	@ResponseBody
	public void exportGasConcentration(String beginDate, String endDate, String devId,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		environmentalService.exportGasConcentration(httpServletRequest, httpServletResponse,
				environmentalService.gasConcentration(beginDate, endDate, devId));

	}

	@RequestMapping(value = { "getConditioner" })
	@ResponseBody
	public String getConditioner(String beginDate, String endDate, String devId) {
		return ServletUtils.buildRs(true, "机密空调据报表", environmentalService.getConditioner(beginDate, endDate, devId));
	}

	@RequestMapping("/exportConditioner") // 导出机密空调
	@ResponseBody
	public void exportConditioner(String beginDate, String endDate, String devId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		environmentalService.exportWenshiList(httpServletRequest, httpServletResponse,
				environmentalService.getConditioner(beginDate, endDate, devId), "机密空调报表");
	}

	@RequestMapping(value = { "comingCabinet" })
	@ResponseBody
	public String comingCabinet(String beginDate, String endDate, String devId, String name, String chTypes) {
		return ServletUtils.buildRs(true, "线柜据报表",
				environmentalService.IncomingCabinet(beginDate, endDate, devId, name, chTypes));
	}

	@RequestMapping("/exportComingCabinet") // 导出进线柜
	@ResponseBody
	public void exportComingCabinet(String beginDate, String endDate, String devId, String chTypes, String name,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		environmentalService.exportComingCabinet(httpServletRequest, httpServletResponse,
				environmentalService.IncomingCabinet(beginDate, endDate, devId, name, chTypes), chTypes);
	}

	// 导出考勤表
	@RequestMapping(value = { "exportPdfEntranceLogs" })
	@ResponseBody
	public void exportPdfEntranceLogs(String userName, String beginTime, String endTime, String employeeNo,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		MapEntity entity = new MapEntity();
		entity.put("userName", userName);
		entity.put("beginTime", beginTime);
		entity.put("endTime", endTime);
		entity.put("employeeNo", employeeNo);
		environmentalService.exportPdfEntranceLogs(httpServletRequest, httpServletResponse,
				environmentalService.getPdfEntranceLogs(entity));
	}

	// 导出短信日志表
	@RequestMapping(value = { "exportMessageLogs" })
	@ResponseBody
	public void exportMessageLogs(String beginTime, String endTime, String devType, String phone, String addr,
			String level, String status, String alarmType, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws Exception {

		MapEntity entity = new MapEntity();
		entity.put("beginTime", beginTime);
		entity.put("endTime", endTime);
		entity.put("devType", devType);
		entity.put("phone", phone);
		entity.put("addr", addr);
		entity.put("level", level);
		entity.put("status", status);
		entity.put("alarmType", alarmType);

		try {
			environmentalService.exportMessageLogs(httpServletRequest, httpServletResponse,
					environmentalService.getMessageLogs(entity));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}