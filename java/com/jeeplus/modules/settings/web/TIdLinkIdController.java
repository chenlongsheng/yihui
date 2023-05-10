/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.httpclient.util.TimeoutController.TimeoutException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.mq.rabbitmq.RabbitMQConnection;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.settings.entity.PdfLink;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.entity.TIdLinkId;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TCodeService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.settings.service.TIdLinkIdService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.rabbitmq.client.Channel;

/**
 * 联动管理Controller
 * 
 * @author long
 * @version 2018-08-08
 */
@Controller
@RequestMapping(value = "settings/tIdLinkId")
public class TIdLinkIdController extends BaseController {

	@Autowired
	private TIdLinkIdService tIdLinkIdService;
	@Autowired
	private TChannelService tChannelService;
	@Autowired
	private TCodeService tCodeService;
	@Autowired
	private AreaService areaService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TDeviceDetailService tDeviceDetailService;

	@Autowired
	RabbitMQConnection rabbitMQProducer;

	/**
	 * 联动配置列表页面
	 */
	@ResponseBody
	@RequestMapping(value = { "list" })
	public String list(String linkCheckId, String status, HttpServletRequest request, HttpServletResponse response) {

		MapEntity entity = new MapEntity();
		entity.put("linkCheckId", linkCheckId);
		entity.put("status", status);
		Page<MapEntity> page = tIdLinkIdService.findPage(new Page<MapEntity>(request, response), entity);
		return ServletUtils.buildRs(true, "查询成功", page);
	}

	/**
	 * 回调设备类型
	 */
	@ResponseBody
	@RequestMapping(value = "getCodeList") // 1设备联动回调
	public String getCodeList() {
		return ServletUtils.buildRs(true, "成功", tIdLinkIdService.getCodeList());
	}

	@ResponseBody
	@RequestMapping(value = "getCodeTypeList") // 1通知联动回调
	public String getCodeTypeList() {
		return ServletUtils.buildRs(true, "成功", tIdLinkIdService.getCodeTypeList());
	}

	// 回调设备数量,和通道name
	@ResponseBody
	@RequestMapping(value = "getDeviceListByType")
	public String getDeviceListByType(String orgId, String devType, String status) {
		return ServletUtils.buildRs(true, "成功", tIdLinkIdService.getDeviceListByType(orgId, devType, status));
	}

	// 摄像头通道
	@ResponseBody
	@RequestMapping(value = "getVideoList")
	public String getVideoList(String orgId, int typeId) { // 联动id
		return ServletUtils.buildRs(true, "成功", tIdLinkIdService.getVideoList(orgId, typeId));
	}

	// 被联动的,通道个数联动,三个水泵
	@ResponseBody
	@RequestMapping(value = "linkChannelist")
	public String linkChannelist(String orgId, Integer chType, Integer typeId) {
		MapEntity entity = new MapEntity();
		List<MapEntity> pdfCodeParam3 = tIdLinkIdService.pdfCodeParam("3");// 开关
		List<MapEntity> pdfCodeParam4 = tIdLinkIdService.pdfCodeParam("4");// 除湿
		List<MapEntity> linkChannelist = tIdLinkIdService.linkChannelist(orgId, chType + "", typeId + "");
		entity.put("button", pdfCodeParam3);
		entity.put("linkChannelist", linkChannelist);// 被联动通道list
		entity.put("conditon", pdfCodeParam4);
		return ServletUtils.buildRs(true, "成功", entity);
	}

	// 发送的用户集合
	@ResponseBody
	@RequestMapping(value = "userList")
	public String userList() {
		List<MapEntity> userList = areaService.userList();
		return ServletUtils.buildRs(true, "成功", userList);
	}

	// 保存联动策略1
	@ResponseBody
	@RequestMapping(value = "saveLink")
	public String saveLink(PdfLink pdfLink) {
		try {
			tIdLinkIdService.insertLink(pdfLink);
			UserUtils.saveLog(areaService.get(pdfLink.getOrgId()).getName() + "创建了一个联动策略组", "新增");
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
		return ServletUtils.buildRs(true, "成功", null);
	}


	@ResponseBody
	@RequestMapping(value = "updateNotUse")
	public String updateNotUse(String notUse, String id) {// 修改启用
		try {
			tIdLinkIdService.updateNotUse(notUse, id);
			MapEntity orgNameBypdfLinkId = tIdLinkIdService.orgNameBypdfLinkId(id);
			if (Integer.parseInt(notUse) == 0) {
				UserUtils.saveLog((String) orgNameBypdfLinkId.get("orgName") + "启用了一个联动策略组", "修改");
			} else {
				UserUtils.saveLog((String) orgNameBypdfLinkId.get("orgName") + "禁用了一个联动策略组", "修改");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
		return ServletUtils.buildRs(true, "成功", null);
	}

	@ResponseBody
	@RequestMapping(value = "linkList")
	public String linkList(String orgId) {// 某配电房下的,策略查询
		return ServletUtils.buildRs(true, "成功", tIdLinkIdService.linkList(orgId));
	}

	// 删除策略联动
	@ResponseBody
	@RequestMapping(value = "deleteLink")
	public String deleteLink(String id) {
		tIdLinkIdService.deleteLink(id);
		return ServletUtils.buildRs(true, "删除成功", null);
	}

	/**
	 * 保存联动配置具体的
	 */
	@ResponseBody
	@RequestMapping(value = "save")
	public String save(String linkList, String pdfLinkId, String status) {

		String statusName = null;
		if (Integer.parseInt(status) == 0) {
			statusName = "[控制联动]";
		} else {
			statusName = "[通知联动]";
		}
		String list = linkList.replace("&quot;", "'");// 替换json的乱码
		JSONArray ja = JSONArray.parseArray(list);
		try {
			tIdLinkIdService.save(ja, pdfLinkId, status);// 保存	       
			remoteControl();
			
			MapEntity orgNameBypdfLinkId = tIdLinkIdService.orgNameBypdfLinkId(pdfLinkId);
			UserUtils.saveLog((String) orgNameBypdfLinkId.get("orgName") + "的"
					+ (String) orgNameBypdfLinkId.get("linkName") + "策略组的" + statusName + "类命令被修改", "修改");

		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "添加/修改失败", null);
		}
		return ServletUtils.buildRs(true, "添加/修改成功", null);
	}

	public String remoteControl() throws TimeoutException {

		Channel rmqChannel = null;

		try {
			// 创建一个通道
			rmqChannel = rabbitMQProducer.createChannel();

			String message = "{\"change_type\":\"4\"}";
			System.out.println(message);

			// 发送消息到队列中
			rmqChannel.basicPublish("org.10", "db.datachange.10", null, message.getBytes("UTF-8"));
			logger.debug("/**============================================================**/");
			logger.debug("Producer Send +'" + message + "'  to " + "db.datachange.10");
			logger.debug("/**============================================================**/");
			// 关闭通道和连接
			rmqChannel.close();

			return ServletUtils.buildRs(true, "成功", "");
		} catch (Exception e) {
			try {
				rmqChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (java.util.concurrent.TimeoutException e1) {
				e1.printStackTrace();
			}
			return ServletUtils.buildRs(false, "失败", "");
		}
	}

	// 启用禁用修改
	@ResponseBody
	@RequestMapping(value = "updateCheckNotUse")
	public String updateCheckNotUse(String notUse, String checkId) {
		try {
			tIdLinkIdService.updateCheckNotUse(notUse, checkId);
			// 添加日志
			MapEntity linkCheck = tIdLinkIdService.getLinkCheck(checkId);
			// notUse = Integer.parseInt(notUse) == 0 ? "启用" : "禁用";
			if (Integer.parseInt(notUse) == 0) {
				notUse = "启用";
			} else {
				notUse = "禁用";
			}
			UserUtils.saveLog((String) linkCheck.get("orgName") + "的" + (String) linkCheck.get("linkName") + "策略组的"
					+ (String) linkCheck.get("statusName") + "类命令被" + notUse, "修改");
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "修改失败", "");
		}
		return ServletUtils.buildRs(true, "修改成功", "");
	}

	// 删除联动下所有的数据
	@ResponseBody
	@RequestMapping(value = "deleteLinkCheck")
	public String deleteLinkCheck(String pdfLinkId, String status) {
		try {
			tIdLinkIdService.deleteLinkCheck(pdfLinkId, status);
			MapEntity orgNameBypdfLinkId = tIdLinkIdService.orgNameBypdfLinkId(pdfLinkId);
			UserUtils.saveLog((String) orgNameBypdfLinkId.get("orgName") + "删除了一个联动策略组", "删除");
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", "");
		}
		return ServletUtils.buildRs(true, "修改成功", "");
	}

}