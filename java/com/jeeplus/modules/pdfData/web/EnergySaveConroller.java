package com.jeeplus.modules.pdfData.web;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.pdfData.dao.EnergySaveDao;
import com.jeeplus.modules.pdfData.service.EnergySaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2019/1/4.
 */
@Controller
@RequestMapping("/pdf/energySave")
public class EnergySaveConroller extends BaseController {
	@Autowired
	EnergySaveService energySaveService;
	
	@RequestMapping("/getAllData")
	@ResponseBody
	public JSONObject getAllData(String orgId, String startDate, String endDate, int data) {
		if (StringUtils.isBlank(startDate)) {
			startDate = "2018-01-01 01:01:01";
		}
		if (StringUtils.isBlank(endDate)) {
			endDate = "2020-12-01 01:01:01";
		}
		JSONObject jsonObject = new JSONObject();
		try {
			if (data == 1) {
				double light = energySaveService.countMonitoring(orgId, 7, startDate, endDate);
				jsonObject.put("light", light);
			} else if (data == 2) {
				double aircondition = energySaveService.countMonitoring(orgId, 8, startDate, endDate);
				jsonObject.put("air", aircondition);
			} else if (data == 3) {
				double socket = energySaveService.countMonitoring(orgId, 9, startDate, endDate);
				jsonObject.put("socket", socket);
			} else if (data == 4) {
				Map map = energySaveService.historyMoniroring(orgId, startDate, endDate);
				jsonObject.put("history", map);
			} else if (data == 5) {
				double light = energySaveService.countMonitoring(orgId, 7, startDate, endDate);
				double aircondition = energySaveService.countMonitoring(orgId, 8, startDate, endDate);
				double socket = energySaveService.countMonitoring(orgId, 9, startDate, endDate);
				Map map1 = new HashMap();
				map1.put("name", "照明能耗");
				map1.put("value", light);
				Map map2 = new HashMap();
				map2.put("name", "空调能耗");
				map2.put("value", aircondition);
				Map map3 = new HashMap();
				map3.put("name", "插座能耗");
				map3.put("value", socket);
				List<Map> typeDataList = new ArrayList();
				typeDataList.add(map1);
				typeDataList.add(map2);
				typeDataList.add(map3);
				jsonObject.put("typeDataList", typeDataList);
			} else if (data == 6) {
				Map countYearMonByHour = energySaveService.countYearMonByHour(orgId);
				Map countYearMonByMonth = energySaveService.countYearMonByMonth(orgId);
				Map yearMon = new HashMap();
				yearMon.put("countYearMonByHour", countYearMonByHour);
				yearMon.put("countYearMonByMonth", countYearMonByMonth);
				jsonObject.put("yearMon", yearMon);// 本年能耗分布统计
			} else if (data == 7) {
				List<Map> yesHour = energySaveService.getYesHour(orgId);
				List<Map> yesDay = energySaveService.getYesDay(orgId);
				List<Map> yesYear = energySaveService.getYesYear(orgId);
				Map realTimeMon = new HashMap();
				realTimeMon.put("yesHour", yesHour);
				realTimeMon.put("yesDay", yesDay);
				realTimeMon.put("yesYear", yesYear);
				jsonObject.put("realTimeMon", realTimeMon);// 实时能耗对比
			} else if (data == 8) {
				List<Map> lightList = energySaveService.countYearTop10(orgId, 7);
				List<Map> airList = energySaveService.countYearTop10(orgId, 8);
				List<Map> socketList = energySaveService.countYearTop10(orgId, 9);
				Map top10 = new HashMap();
				top10.put("lightList", lightList);
				top10.put("airList", airList);
				top10.put("socketList", socketList);
				jsonObject.put("top10", top10);// 本年能耗分布统计
			}
//          Map map = energySaveService.getAllData(orgId,startDate,endDate);
			jsonObject.put("success", true);
//            jsonObject.put("data",map);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return jsonObject;
	}

	@RequestMapping("/getYesHour")
	@ResponseBody
	public JSONObject getYesHour(String orgId) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<Map> list = energySaveService.getYesHour(orgId);
			jsonObject.put("success", true);
			jsonObject.put("data", list);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return jsonObject;
	}

	@RequestMapping("/getYesDay")
	@ResponseBody
	public JSONObject getYesDay(String orgId) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<Map> list = energySaveService.getYesDay(orgId);
			jsonObject.put("success", true);
			jsonObject.put("data", list);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return jsonObject;
	}

	@RequestMapping("/getYesYear")
	@ResponseBody
	public JSONObject getYesYear(String orgId) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<Map> list = energySaveService.getYesYear(orgId);
			jsonObject.put("success", true);
			jsonObject.put("data", list);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());			
			e.printStackTrace();
		}
		return jsonObject;
	}

	@RequestMapping("/countYearTop10")
	@ResponseBody
	public JSONObject countYearTop10(String orgId, int type) {
		JSONObject jsonObject = new JSONObject();
		try {
			List<Map> list = energySaveService.countYearTop10(orgId, type);
			jsonObject.put("success", true);
			jsonObject.put("data", list);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return jsonObject;
	}
}
