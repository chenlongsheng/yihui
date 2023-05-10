package com.jeeplus.modules.homepage.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.service.ChartService;
import com.jeeplus.modules.homepage.service.StatisticsService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;

/**
 * Created by Administrator on 2018-12-20.
 */
@Controller
@RequestMapping(value = "/chartSecurity")
public class ChartController {
	@Autowired
	private ChartService chartService;

	@ResponseBody
	@RequestMapping(value = "getSecurityList")
	public String getSecurityList(String devType, String bureauId, String orgId, String beginDate, String endDate) {

		if (StringUtils.isBlank(beginDate)) {
//			beginDate = DateUtils.getDate("yyyy/MM/dd");
			beginDate ="2018/01/01";
		}
		if (StringUtils.isBlank(endDate)) {
			endDate = DateUtils.getDate("yyyy/MM/dd HH:mm:ss");
		}
		MapEntity doorSecurityList = null;
		try {
			doorSecurityList = chartService.doorSecurityList(devType, bureauId, orgId, beginDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "查询失败", null);
		}
		return ServletUtils.buildRs(true, "查询成功", doorSecurityList);
	}

}
