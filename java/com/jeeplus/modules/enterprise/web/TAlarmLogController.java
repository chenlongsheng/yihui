/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.enterprise.entity.TAlarmLog;
import com.jeeplus.modules.enterprise.service.TAlarmLogService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 报警日志Controller
 * @author ywk
 * @version 2017-05-25
 */
@Controller
@RequestMapping(value = "enterprise/tAlarmLog")
public class TAlarmLogController extends BaseController {

	@Autowired
	private TAlarmLogService tAlarmLogService;
	
	@ModelAttribute("tAlarmLog")
	public TAlarmLog get(@RequestParam(required=false) String id) {
		TAlarmLog entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tAlarmLogService.get(id);
		}
		if (entity == null){
			entity = new TAlarmLog();
		}
		return entity;
	}
	
	
	//获得预警列表999
	@RequestMapping(value = {"listJson"})
	@ResponseBody
	public String listJson(TAlarmLog tAlarmLog, HttpServletRequest request, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
//		String orgId = currentUser.getArea().getId();
		String orgId = "7579";
		MapEntity mapEntity = new MapEntity();
		mapEntity.put("eid", orgId);
		Page<MapEntity> page = tAlarmLogService.findPageOfAlarmLog(new Page<MapEntity>(request, response), mapEntity);
		return ServletUtils.buildRs(true, "", page);
	}	
	

}