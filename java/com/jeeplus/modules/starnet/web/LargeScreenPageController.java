/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;

import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.starnet.service.LargeScreenPageService;
import com.jeeplus.modules.starnet.service.PowerDataService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "Screen")
public class LargeScreenPageController extends BaseController {

	@Autowired
	private LargeScreenPageService largeScreenPageService;
//获取下拉设备通道
	@RequestMapping("/getChNames")
	@ResponseBody
	public String getChNames() {

		return ServletUtils.buildRs(true, " ", largeScreenPageService.getChNames());
	}
	
    //获取历史数据
	@RequestMapping("/getHistoryByChId")
	@ResponseBody
	public String getHistoryByChId(String chId, String time) {

		return ServletUtils.buildRs(true, " ", largeScreenPageService.getHistoryByChId(chId, time));
	}
	
	//设备总览
	@RequestMapping("/getOrgNums")
	@ResponseBody
	public String getOrgNums() {

		return ServletUtils.buildRs(true, " ", largeScreenPageService.getOrgNums());
	}
	

}