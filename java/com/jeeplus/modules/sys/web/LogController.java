/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.enterprise.entity.TOperLog;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.qxz.excel.OrderExcel;
import com.jeeplus.modules.sys.dao.LogDao;
import com.jeeplus.modules.sys.entity.Log;
import com.jeeplus.modules.sys.service.LogService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 日志Controller
 * @author jeeplus
 * @version 2013-6-2
 */
@Controller
@RequestMapping(value = "sys/log")
public class LogController extends BaseController {

	@Autowired
	private LogService logService;

	
	@Autowired
	private LogDao logDao;
	
	@RequestMapping(value = {"list"})
	@ResponseBody
	public String list(String loginName, String name,String id,String params,String beginDate,String endDate,
			HttpServletRequest request, HttpServletResponse response) {
		MapEntity entity = new MapEntity();
		entity.put("loginName", loginName);
		entity.put("name", name);
		entity.put("id", id);
		entity.put("params", params);
		entity.put("beginDate", beginDate);
		entity.put("endDate", endDate);
        Page<MapEntity> page = logService.findPage(new Page<MapEntity>(request, response), entity);        	
		MapEntity en = new MapEntity();
		en.put("paramsList", logDao.getParamsList());
		en.put("page", page);
		
		return ServletUtils.buildRs(true, "成功", en);
	}

		
	
	// 工单导出
	@RequestMapping("/logExport")
	@ResponseBody
	public void logExport(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
        
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		Page page = new Page(httpServletRequest, httpServletResponse);
//		pdfOrder.setPage(page);
		JSONObject jsonObject = new JSONObject();
		List<MapEntity> findLogList = logDao.findLogList(new MapEntity());
		UserUtils.saveLog("导出操作日志", "导出");
		ExcelUtil.LogReport(httpServletRequest, httpServletResponse, findLogList); 
		
	}

	
	
	
	
	
	
	/**
	 * 批量删除
	 */
	@RequiresPermissions("sys:log:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			logService.delete(logService.get(id));
		}
		addMessage(redirectAttributes, "删除日志成功");
		return "redirect:"+Global.getAdminPath()+"/sys/log/?repage";
	}
	
	/**
	 * 批量删除
	 */
	@RequiresPermissions("sys:log:del")
	@RequestMapping(value = "empty")
	public String empty(RedirectAttributes redirectAttributes) {
		logService.empty();
		addMessage(redirectAttributes, "清空日志成功");
		return "redirect:"+Global.getAdminPath()+"/sys/log/?repage";
	}
}
