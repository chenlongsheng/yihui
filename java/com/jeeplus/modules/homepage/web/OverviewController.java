package com.jeeplus.modules.homepage.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.service.ChartService;
import com.jeeplus.modules.homepage.service.OverviewService;
import com.jeeplus.modules.homepage.service.StatisticsService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * Created by Administrator on 2018-12-20.
 */
@Controller
@RequestMapping(value = "/overview")
public class OverviewController {
	@Autowired
	private OverviewService overviewService;
	@Autowired
	private StatisticsService statisticsService;

	/***
	 * 
	 * 首页树形接口
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getBureauListByHome")
	public String getBureauListByHome() {
		return ServletUtils.buildRs(true, "树形用户供电所", statisticsService.getBureauListByHome());

	}

	@ResponseBody
	@RequestMapping(value = "findHomePage")
	public String list(String name, String bureauId, HttpServletRequest request, HttpServletResponse response) {
		MapEntity entity = new MapEntity();
		entity.put("name", name);
		entity.put("bureauId", bureauId);
		entity.put("userId", UserUtils.getUser().getId());
		Page<MapEntity> page = statisticsService.findHomePage(new Page<MapEntity>(request, response), entity);
		return ServletUtils.buildRs(true, "树形配电房分页", page);

	}

	@ResponseBody
	@RequestMapping(value = "overviewDate")
	public String overviewDate() {

		return ServletUtils.buildRs(true, "总览页", overviewService.overviewDate());
	}

	@ResponseBody
	@RequestMapping(value = "pdfListById")
	public String pdfListById() {
		return ServletUtils.buildRs(true, "地图配电房集合", overviewService.pdfListById());
	}

	@ResponseBody
	@RequestMapping(value = "getDetailsByOrgId")
	public String getDetailsByOrgId(String orgId) { // 获取配电房详情
		return ServletUtils.buildRs(true, "配电房详情", overviewService.getDetailsByOrgId(orgId));
	}

	@ResponseBody
	@RequestMapping(value = "getOrgDetails")
	public String getOrgDetails() { // 设备总览
		return ServletUtils.buildRs(true, "设备总览", overviewService.getOrgDetails());

	}

	@ResponseBody
	@RequestMapping(value = "alarmCountTop10")
	public String alarmCountTop10(String beginDate, String endDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -7);
		Date monday = c.getTime();
		String preMonday = sdf.format(monday);// 默认前七天

		if (StringUtils.isBlank(beginDate)) {
			beginDate = preMonday;
		}
		if (StringUtils.isBlank(endDate)) {
			endDate = DateUtils.getDateTime();
		}
		return ServletUtils.buildRs(true, "总览页-报警Top10", overviewService.alarmCountTop10(beginDate, endDate));
	}

	@ResponseBody
	@RequestMapping(value = "getEnergyListByDay") // 能效总览
	public String getEnergyListByDay() {

		return ServletUtils.buildRs(true, "能效总览", overviewService.getEnergyList(""));
	}

	@ResponseBody
	@RequestMapping(value = "getDataByOrgId") // 能效总览(今日数据)
	public String getDataByOrgId(String orgId) {
		return ServletUtils.buildRs(true, "获取今日数据", overviewService.getDataByOrgId(""));

	}

}
