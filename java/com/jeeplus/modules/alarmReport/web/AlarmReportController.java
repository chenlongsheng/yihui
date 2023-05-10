package com.jeeplus.modules.alarmReport.web;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.alarmReport.service.AlarmReportService;
import com.jeeplus.modules.qxz.entity.GzhUser;
import com.jeeplus.modules.qxz.excel.Excel;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.qxz.excel.ReportExcel;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/24.
 */
@Controller
@RequestMapping("/warm/report")
public class AlarmReportController extends BaseController {
	@Autowired
	AlarmReportService alarmReportService;
	
	
	
	/**
	 * 报警类型统计表
	 */
	@RequestMapping("/countAlarmType")
	@ResponseBody
	public JSONObject countAlarmType(PdfOrder pdfOrder) {
		JSONObject jsonObject = new JSONObject();
		pdfOrder.setUserId(UserUtils.getUser().getId());
		try {
			Map map = alarmReportService.countAlarmType(pdfOrder);
			jsonObject.put("success", true);
			jsonObject.put("data", map);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 部门报警情况统计表
	 */
	@RequestMapping("/countPartmentAlarm")
	@ResponseBody
	public JSONObject countPartmentAlarm(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		JSONObject jsonObject = new JSONObject();
		Page page = new Page(httpServletRequest, httpServletResponse);
		pdfOrder.setPage(page);
		try {
			Map map = alarmReportService.countPartmentAlarm(pdfOrder);
			jsonObject.put("success", true);
			jsonObject.put("data", map);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	@RequestMapping("/getOrgList")
	@ResponseBody
	public JSONObject getOrgList() {
		JSONObject jsonObject = new JSONObject();

		try {
			List<Map> list = alarmReportService.getOrgList();
			jsonObject.put("success", true);
			jsonObject.put("data", list);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	@RequestMapping("/exportOrderType")
	@ResponseBody
	public void exportOrderType(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		PdfOrder pdfOrder = new PdfOrder();
		pdfOrder.setUserId(UserUtils.getUser().getId());
		pdfOrder.setPage(null);
		Map map = alarmReportService.countAlarmType(pdfOrder);

		List<Map> orderList = (List<Map>) map.get("table");

		for (Iterator iterator = orderList.iterator(); iterator.hasNext();) {
			Map m = (Map) iterator.next();
			int alarmType =Integer.parseInt( m.get("alarmType").toString());
			if (alarmType == 0) {
				m.put("alarmName", "设备故障");
			} else {
				m.put("alarmName", "数据异常");
			}
			int alarmSource = Integer.parseInt(m.get("alarmSource").toString());
			if (alarmSource == 2) {
				m.put("alarmSourceName", "系统报警");
			} else {
				m.put("alarmSourceName", "人工报警");
			}
		}
		ExcelUtil.exportOrderType(httpServletRequest, httpServletResponse, orderList);
	}

	@RequestMapping("/export")
	@ResponseBody
	public void export(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
//        GzhUser gzhUser = new GzhUser();
		PdfOrder pdfOrder = new PdfOrder();
		pdfOrder.setPage(null);
		Map map = alarmReportService.countPartmentAlarm(pdfOrder);
		List<Map> orderList = (List<Map>) map.get("data");
		List<ReportExcel> resultList = new ArrayList();
		for (int i = 0; i < orderList.size(); i++) {
			ReportExcel excel = new ReportExcel();
			excel.setAlarmAddr(String.valueOf(orderList.get(i).get("pName")));
			excel.setPdfName(String.valueOf(orderList.get(i).get("pdfName")));
			int alarmSource =Integer.parseInt((orderList.get(i).get("alarmSource").toString()));
			if (alarmSource == 1) {
				excel.setAlarmSource("系统报警");
			} else {
				excel.setAlarmSource("人工报警");
			}
			int alarmType = Integer.parseInt(orderList.get(i).get("alarmType").toString());
		
			if (alarmType == 0) {
				excel.setAlarmType("设备故障");
			} else {
				excel.setAlarmType("数据异常");
			}
			excel.setTotal(Integer.parseInt((orderList.get(i).get("total")).toString()));
			excel.setState0(Integer.parseInt((orderList.get(i).get("state0")).toString()));
			excel.setState1(Integer.parseInt((orderList.get(i).get("state1")).toString()));
			excel.setState2(Integer.parseInt((orderList.get(i).get("state2")).toString()));
			excel.setState3(Integer.parseInt((orderList.get(i).get("state3")).toString()));
			excel.setState4(Integer.parseInt((orderList.get(i).get("state4")).toString()));
			excel.setState5(Integer.parseInt((orderList.get(i).get("state5")).toString()));
			excel.setOver(orderList.get(i).get("over") + "%");
			resultList.add(excel);
		}
		ExcelUtil.exportReport(httpServletRequest, httpServletResponse, resultList);
		System.out.println(11);
	}
}
