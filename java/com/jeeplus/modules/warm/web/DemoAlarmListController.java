package com.jeeplus.modules.warm.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.warm.service.PdfAlarmService;

@Controller
@RequestMapping("/warm")
public class DemoAlarmListController {
	
	@Autowired
	PdfAlarmService pdfAlarmService;
	
	@Autowired
	 AreaService areaService;
	
//	@Value("${rabbitmq.virtualHost}")
//	private String virtualHost;
	

	// 模板
	@RequestMapping("/getAlarmList")
	@ResponseBody
	public String getAlarmList(String pdfId) {		
				
		Area area = areaService.get(pdfId);
		List<MapEntity> alarmList = pdfAlarmService.getAlarmListByPdfId(area.getCode());
		return ServletUtils.buildRs(true, "查詢成功", alarmList);
	}
	
}
