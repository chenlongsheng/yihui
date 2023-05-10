/**
 * 
 */
package com.jeeplus.modules.sys.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.sys.service.BureauService;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * @author admin
 *
 */
@Controller
@RequestMapping(value = "sys/bureau")
public class BureauController extends BaseController {

	@Autowired
	private BureauService bureauService;

	/*
	 * 用户归属所有供电所和配电房
	 */
	@RequestMapping(value = { "getBureauList" })
	@ResponseBody
	public String getBureauList() {
		return ServletUtils.buildRs(true, "用户归属供电所集合", bureauService.getBureauList());
	}

	@ResponseBody
	@RequestMapping(value = "deleteBureauById")
	public String deleteBureauById(String bureauId) {

		try {
			bureauService.deleteBureauById(bureauId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "删除失败", "");
		}
		return ServletUtils.buildRs(true, "成功删除", "");
	}

	@ResponseBody
	@RequestMapping(value = "saveBureauById")
	public String saveBureauById(String bureauId, String oldParentId, String parentId, String name, String code,
			String type) {
		try {
			bureauService.saveBureauById(bureauId, oldParentId, parentId, name, code);
			UserUtils.saveLog("添加" + name + "供电所", "操作");

		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "修改失败", "");
		}
		return ServletUtils.buildRs(true, "修改成功", "");
	}

	@RequestMapping(value = "updateOrderNo")
	@ResponseBody
	public String updateOrderNo(String orderList) {

		String list = orderList.replace("&quot;", "'");
		JSONArray ja = JSONArray.parseArray(list);
		try {
			bureauService.updateOrderNo(ja);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "修改失败", "");
		}
		return ServletUtils.buildRs(true, "修改成功", "");
	}

}
