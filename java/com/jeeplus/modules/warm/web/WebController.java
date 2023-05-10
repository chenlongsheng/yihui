package com.jeeplus.modules.warm.web;

import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.websocket.onchat.ChatServer;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.service.PdfAlarmService;

import org.java_websocket.WebSocketImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import java.util.List;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by ZZUSER on 2018/12/13.
 */
@Controller
@RequestMapping("/warm")
public class WebController extends BaseController {

	@Autowired
	TOrgService orgService;

	@Autowired
	PdfAlarmService pas;
	
	@RequestMapping("/websoket")
	@ResponseBody
	public String websoket() throws UnknownHostException {

//		WebSocketImpl.DEBUG = false;
		System.out.println("---");
		int port = 8662; //端口
		ChatServer s = new ChatServer(port);
		s.start();
		System.out.println( "服务器的端口" + s.getPort());
		return ServletUtils.buildRs(true, "", "");
	}

	@RequestMapping("/countFirstPageAlarm")
	@ResponseBody
	public String countFirstPageAlarm(String orgId) {

		if (StringUtils.isBlank(orgId)) {

			orgId = UserUtils.getUser().getArea().getCode();
		}
		TOrg org = orgService.findUniqueByProperty("code", orgId);
		String code = org.getCode();
		List<Map<String, Object>> alarmlist = pas.getAlarmListGroupByChType(code);
		return ServletUtils.buildRs(true, "", alarmlist);
	}

	@RequestMapping("/getAlarmListDetail")
	@ResponseBody
	public String getAlarmListDetail(String orgId) {
		
		if (StringUtils.isBlank(orgId)) {
			orgId = UserUtils.getUser().getArea().getCode();
		}

		TOrg org = orgService.findUniqueByProperty("code", orgId);
		String code = org.getCode();

		// 门禁
		List<Map<String, Object>> doorAlarmList = pas.getAlarmListByChType(5, 2, code);
		for (Map<String, Object> map : doorAlarmList) {
			map.put("alarmDesc", map.get("chName") + " 触发");
		}

		// 温度
		List<Map<String, Object>> tempAlarmList = pas.getAlarmListByChType(101, 3, code);

		for (Map<String, Object> map : tempAlarmList) {
			Object value = map.get("real_value");
			String showValue = "未知值";
			if (value != null) {
				showValue = map.get("real_value").toString();
			}

			map.put("alarmDesc", "当前:" + showValue + map.get("monad") + "超过警戒值");
		}

		// 湿度度
		List<Map<String, Object>> humiAlarmList = pas.getAlarmListByChType(102, 3, code);
		for (Map<String, Object> map : humiAlarmList) {
			Object value = map.get("real_value");
			String showValue = "未知值";
			if (value != null) {
				showValue = map.get("real_value").toString();
			}
			map.put("alarmDesc", "当前:" + showValue + map.get("monad") + "超过警戒值");
		}

		// 水浸
		List<Map<String, Object>> waterAlarmList = pas.getAlarmListByChType(2, 2, code);
		for (Map<String, Object> map : waterAlarmList) {
			map.put("alarmDesc", map.get("chName") + " 触发");
		}

		// 烟感
		List<Map<String, Object>> smokeAlarmList = pas.getAlarmListByChType(4, 2, code);
		for (Map<String, Object> map : waterAlarmList) {
			map.put("alarmDesc", map.get("chName") + " 触发");
		}

		// 摄像头
		List<Map<String, Object>> cameraAlarmList = pas.getAlarmListByChType(1, 9, code);
		for (Map<String, Object> map : cameraAlarmList) {
			map.put("alarmDesc", map.get("chName") + " 触发");
		}

		// 电池电压低
		List<Map<String, Object>> voltageLowList = pas.getAlarmListByChType(19, 2, code);
		for (Map<String, Object> map : voltageLowList) {
			Object value = map.get("real_value");
			String showValue = "未知值";
			if (value != null) {
				showValue = map.get("real_value").toString();
			}
			map.put("alarmDesc", "当前:" + showValue + map.get("monad") + "超过警戒值");
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("door", doorAlarmList);
		result.put("tempAlarmList", tempAlarmList);
		result.put("water", waterAlarmList);
		result.put("smoke", smokeAlarmList);
		result.put("camera", cameraAlarmList);
		result.put("voltageLow", voltageLowList);
		result.put("humiAlarmList", humiAlarmList);
		return ServletUtils.buildRs(true, "", result);

	}

	@RequestMapping("/confirmAlarm")
	@ResponseBody
	public String confirmAlarm(String id) {
		pas.confirmAlarm(id);
		return ServletUtils.buildRs(true, "", null);
	}

	@RequestMapping("/schedulingRulePage")
	public String schedulingRulePage() {
		return "modules/warm/schedulingRulePage";
	}

	@RequestMapping("/schedulingPage")
	public String schedulingPage() {
		return "modules/warm/schedulingPage";
	}

}
