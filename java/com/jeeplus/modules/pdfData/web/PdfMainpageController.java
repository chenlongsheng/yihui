package com.jeeplus.modules.pdfData.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.pdfData.service.PdfMainpageService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;

@Controller
@RequestMapping("/pdf/mainpage")
public class PdfMainpageController {

	@Autowired
	PdfMainpageService pdfMainpageService;
	
	@Autowired
	TOrgService orgService;
	
    @RequestMapping("/data")
    @ResponseBody
    public String pdfData(String pdfId) {
    	Map<String,Object> result = new HashMap<String,Object>();
		TOrg org = orgService.get(pdfId);
		String code = org.getCode();
    	//配电房基本信息
    	Map<String,Object> baseInfo = pdfMainpageService.getPdfBaseInfo(pdfId);
    	result.put("baseInfo", baseInfo);
    	
    	//监控设备数据
    	Map<String,Object> monitorDeviceData = new HashMap<String,Object>();
	    	//灯控
	    	List<Map<String,Object>> light = pdfMainpageService.getLightData(code);
	    	monitorDeviceData.put("light", light);
	    	//温湿度
	    	List<Map<String,Object>> humTemp = pdfMainpageService.getHumTempData(code);
	    	monitorDeviceData.put("humTemp", humTemp);
	    	//水浸
	    	List<Map<String,Object>> waterData = pdfMainpageService.getWaterData(code);
	    	monitorDeviceData.put("waterData", waterData);
	    	//门磁
	    	List<Map<String,Object>> doorData = pdfMainpageService.getDoorData(code);
	    	monitorDeviceData.put("doorData", doorData);
	    	//烟感
	    	List<Map<String,Object>> smokeData = pdfMainpageService.getSmokeData(code);
	    	monitorDeviceData.put("smokeData", smokeData);
    	result.put("monitorDeviceData", monitorDeviceData);
    	
    	//电力监控数据
    	Map<String,Object> eleMonitorData = new HashMap<String,Object>();
			//高压数据
			List<Map<String,Object>> highVoltageData = pdfMainpageService.getHighVoltageData(pdfId);
			eleMonitorData.put("highVoltageData ", highVoltageData);
			//电力柜数据
			Map<String,Object> eleBoxData = pdfMainpageService.getEleBoxData(pdfId);
			eleMonitorData.put("eleBoxData ", eleBoxData);
    	result.put("eleMonitorData", eleMonitorData);
    	
    	//维保信息
    	Map<String,Object> maintainanceData = pdfMainpageService.getMaintainanceData();
    	result.put("maintainanceData", maintainanceData);
    	
    	//排班信息
    	Map<String,Object> arrangementData = pdfMainpageService.getArrangementData();
    	result.put("arrangementData", arrangementData);
    	
    	
    	return ServletUtils.buildRs(true, "", result);
    }
	
}
