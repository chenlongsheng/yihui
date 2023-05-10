package com.jeeplus.modules.pdfData.web;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.pdfData.service.SecurityService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/26. 安防系统
 */
@Controller
@RequestMapping("/pdf/security")
public class SecurityController extends BaseController {
	@Autowired
	SecurityService securityService;

	@Autowired
	TOrgService orgService;

	/*
	 * @RequestMapping("/countSaveDays")
	 * 
	 * @ResponseBody public int countSaveDays(String orgId,int type){ return
	 * securityService.countSaveDays(orgId,type); }
	 */

	@RequestMapping("/countHourOpen")
	@ResponseBody
	public Map countHourOpen(String orgId, String startDate, String endDate) {
		if (orgId == null || orgId.length() == 0) {
			orgId = OrgUtil.getOrgId();
		}
		TOrg org = orgService.get(orgId);
		String code = "00";
		if (org != null) {
			code = org.getCode();
		}
		return securityService.countHourOpen(code, startDate, endDate, 1, 5, 2);
	}

	@RequestMapping("/countOpenTop")
	@ResponseBody
	public Map countOpenTop(String orgId, String startDate, String endDate) {
		return securityService.countOpenTop(orgId, startDate, endDate);
	}

	/**
	 * 安防系统获取所有数据
	 * 
	 * @param orgId
	 * @param type
	 * @return
	 */
	@RequestMapping("/getAllData")
	@ResponseBody
	public JSONObject getAllData(String orgId, int type, String startDate, String endDate, int data) {
		if (orgId == null || orgId.length() == 0) {
			orgId = "7579";
		}
		TOrg org = orgService.get(orgId);
		String code = "00";
		if (org != null) {
			code = org.getCode();
		}
		JSONObject jsonObject = new JSONObject();
		try {
			if (data == 1) {
				int days = securityService.countSaveDays(code, 162, 1);// 安全运行天数
				jsonObject.put("safeDays", days);
			} else if (data == 2) {
				int open = securityService.countOpen(orgId);// 开门次数
				jsonObject.put("open", open);
			} else if (data == 3) {
				int alarm = securityService.countAlarm(code, type);// 本月新增报警数
				jsonObject.put("newAlarm", alarm);
			} else if (data == 4) {
				Map HourOpen = securityService.countHourOpen(code, startDate, endDate, type, 5, 2);// 各时段开门平均数
				jsonObject.put("HourOpen", HourOpen);
			} else if (data == 5) {
				Map OpenTop10 = securityService.countOpenTop(orgId, startDate, endDate);// 开门次数top10
				jsonObject.put("OpenTop10", OpenTop10);
			}
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	    *    安防页面切换日期
	 * @param orgId
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public JSONObject changeDate(String orgId, int type, String startDate, String endDate) {
		JSONObject jsonObject = new JSONObject();
		try {
			Map map = securityService.changeDate(orgId, type, startDate, endDate);
			jsonObject.put("success", true);
			jsonObject.put("data", map);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}
}
