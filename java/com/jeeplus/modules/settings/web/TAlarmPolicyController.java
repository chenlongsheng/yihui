/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.commons.httpclient.util.TimeoutController.TimeoutException;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drew.lang.StringUtil;
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
import com.jeeplus.modules.settings.dao.TCodeDao;
import com.jeeplus.modules.settings.dao.TIdLinkIdDao;
import com.jeeplus.modules.settings.entity.TAlarmPolicy;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TCodeType;
import com.jeeplus.modules.settings.service.TAlarmPolicyService;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TCodeService;
import com.jeeplus.modules.settings.service.TCodeTypeService;
import com.jeeplus.modules.settings.service.TDeviceService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.rabbitmq.client.Channel;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-10-23
 */
@Controller
@RequestMapping(value = "settings/tAlarmPolicy")
public class TAlarmPolicyController extends BaseController {

	@Autowired
	RabbitMQConnection rabbitMQProducer;

	@Autowired
	private TAlarmPolicyService tAlarmPolicyService;
	@Autowired
	private TChannelService tChannelService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private TCodeDao tCodeDao;
	@Autowired
	private TIdLinkIdDao tIdLinkIdDao;
	@Autowired
	TDeviceService tDeviceService;

	@ModelAttribute("tAlarmPolicy")
	public TAlarmPolicy get(@RequestParam(required = false) String id) {
		TAlarmPolicy entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = tAlarmPolicyService.get(id);
		}
		if (entity == null) {
			entity = new TAlarmPolicy();
		}
		return entity;
	}

	/**
	 * 数据配置列表页面
	 */
	@ResponseBody
	@RequestMapping(value = { "list" })
	public String list(String chName, String chType, Long typeId, String orgTreeId, Long level, String signLogo,
			HttpServletRequest request, HttpServletResponse response, Model model) {

		String orgId = UserUtils.getUser().getArea().getId();
		// 获取级别
		List<TCode> levelList = tAlarmPolicyService.codeList("23");
		model.addAttribute("levelList", levelList);
		// 通道主类型集合
		List<TCodeType> codeTypeList = tAlarmPolicyService.codeTypeList();
		model.addAttribute("codeTypeList", codeTypeList);
		// 获取通道子类型表集合
		List<TCode> typeList = tAlarmPolicyService.codeList(null);
		model.addAttribute("typeList", typeList);
		return "modules/settings/tAlarmPolicyList";
	}

	// 设备类型阈值回调
	@ResponseBody
	@RequestMapping(value = "form")
	public String form(Integer devType) {
		MapEntity entity = new MapEntity();
		String chType = "0";
		String typeId = "3";
		if (devType == 169) { // 温湿度
			chType = "101,102,121";
			entity.put("chType", chType);
			entity.put("chName", "温度阈值,湿度阈值,剩余电量");
			entity.put("domain", "(阈值范围-20℃~60℃),(阈值范围0-100%),(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		} else if (devType == 162) { // 门磁
			chType = "145,121";
			entity.put("chType", chType);
			entity.put("chName", "开门时长阈值,剩余电量");
			entity.put("domain", "(阈值范围10~255秒),(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		} else if (devType == 168) { // 水浸
			chType = "149,121";
			entity.put("chType", chType);
			entity.put("chName", "水浸绳状态,剩余电量");
			entity.put("domain", " ,(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
			entity.put("pointList", tIdLinkIdDao.pdfCodeParam("7"));// 触点集合
			entity.put("levelist", tIdLinkIdDao.pdfCodeParam("9"));// 等级集合
		} else if (devType == 172) {//
			chType = "101,121";
			entity.put("chType", chType);
			entity.put("chName", "温度阈值,剩余电量");
			entity.put("domain", "(阈值范围0℃~125℃),(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		} else if (devType == 174) { // 电器采集
			chType = "105,106,107,166,167,168,186";
			entity.put("chType", chType);
			entity.put("chName", "A相电压,B相电压,C相电压,A相电流,B相电流,C相电流,总视在功率");
			entity.put("domain",
					"(阈值范围0V~600V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0W~1000000W)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		}
		entity.put("list", tAlarmPolicyService.findList(chType, typeId, devType));
		return ServletUtils.buildRs(true, "阈值回调成功", entity);
	}

	/**
	 * 保存类型阈值管理
	 */
	@ResponseBody
	@RequestMapping(value = "save")
	public String save(String tAlarmPolicyList, String devType, String devIds) {
		UserUtils.getUser().getId();
		String list = tAlarmPolicyList.replace("&quot;", "'");// 替换json的乱码
		JSONArray ja = JSONArray.parseArray(list);
		try {
			tAlarmPolicyService.save(ja, devType, devIds);// 保存
			UserUtils.saveLog("设备类型：" + tCodeDao.getCode(devType, "1").getName() + "的阈值被修改", "修改");
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "添加/修改失败", null);
		}
		try {
			this.policyControl();// =================================99999
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return ServletUtils.buildRs(true, "添加/修改成功", null);
	}

	// 设备回调
	@ResponseBody
	@RequestMapping(value = "alarmPolicyForm")
	public String alarmPolicyForm(String devId, Integer devType) {

		MapEntity entity = new MapEntity();
		String chType = "0";
		String typeId = "3";
		if (devType == 169) {
			chType = "101,102,121";
			entity.put("chType", chType);
			entity.put("chName", "温度阈值,湿度阈值,剩余电量");
			entity.put("domain", "(阈值范围-20℃~60℃),(阈值范围0-100%),(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		} else if (devType == 162) {
			chType = "145,121";
			entity.put("chType", chType);
			entity.put("chName", "开门时长阈值,剩余电量");
			entity.put("domain", "(阈值范围10~255秒),(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		} else if (devType == 168) {
			chType = "149,121";
			entity.put("chType", chType);
			entity.put("chName", "水浸绳状态,剩余电量");
			entity.put("domain", " ,(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
			entity.put("pointList", tIdLinkIdDao.pdfCodeParam("7"));// 触点集合
			entity.put("levelist", tIdLinkIdDao.pdfCodeParam("9"));// 等级集合
		} else if (devType == 172) { // 烟感
			chType = "101,121";
			entity.put("chType", chType);
			entity.put("chName", "温度阈值,剩余电量");
			entity.put("domain", "(阈值范围0℃~125℃),(阈值范围0-100%)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		} else if (devType == 174) {
			chType = "105,106,107,166,167,168,186";
			entity.put("chType", chType);
			entity.put("chName", "A相电压,B相电压,C相电压,A相电流,B相电流,C相电流,总视在功率");
			entity.put("domain",
					"(阈值范围0V~600V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0V~65535V),(阈值范围0W~1000000W)");
			entity.put("devType", devType);
			entity.put("typeId", typeId);
		}
		entity.put("list", tAlarmPolicyService.getAlarmPolicyList(devId, typeId, chType));
		return ServletUtils.buildRs(true, "添加/修改回调成功", entity);
	}

	// 设备中修改
	@ResponseBody
	@RequestMapping(value = "saveAlarmPolicyByDevId")
	public String saveAlarmPolicyByDevId(String tAlarmPolicyList, String deviceIds, Long orgId) {
		String list = tAlarmPolicyList.replace("&quot;", "'");// 替换json的乱码
		JSONArray ja = JSONArray.parseArray(list);
		try {
			tAlarmPolicyService.saveAlarmPolicyByDevId(ja, deviceIds, orgId);
			String[] deviceList = deviceIds.split(",");
			for (int j = 0; j < deviceList.length; j++) {
				MapEntity entity = tDeviceService.getOrgName(deviceList[j]);
				UserUtils.saveLog((String) entity.get("orgName") + "的" + (String) entity.get("addr")
						+ (String) entity.get("devTypeName") + "修改了阈值", "修改");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "添加/修改失败", null);
		} // 保存
		return ServletUtils.buildRs(true, "添加/修改成功", null);
	}

	/**
	 * 删除数据配置
	 */
	@ResponseBody
	@RequestMapping(value = "delete")
	public String delete(TAlarmPolicy tAlarmPolicy, String tAlarmPolicyList, RedirectAttributes redirectAttributes) {
		String list = tAlarmPolicyList.replace("&quot;", "'");// 替换json的乱码
		JSONArray ja = JSONArray.parseArray(list);
		try {
			// tAlarmPolicyService.delete(ja);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "删除失败", null);
		}
		return ServletUtils.buildRs(true, "删除成功", null);
	}

	public static void timer1() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				System.out.println("-------设定要指定任务--------");
			}
		}, 5000);// 设定指定的时间time,此处为2000毫秒
	}

	// 新写的接口
	@RequestMapping(value = { "policyControl1" })
	@ResponseBody
	public String policyControl1() throws TimeoutException {

		String date = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		Channel rmqChannel = null;
		try {
			// 创建一个通道
			rmqChannel = rabbitMQProducer.createChannel();
		
			String message = "{\"change_type\":\"" + "4" + "\",\"change_time\":\"" + date + "\",\"channels\":{\"" + "3"
					+ "\":{\"value\":\"" + "4" + "\"}}}";
			// 发送消息到队列中
			rmqChannel.basicPublish("org.10", "dev.command.10.01", null, message.getBytes("UTF-8"));
			logger.debug("/**============================================================**/");
			logger.debug("Producer Send +'" + message + "'  to " + "dev.command.10.01");
			logger.debug("/**============================================================**/");
		
			// 关闭通道和连接
			rmqChannel.close();
			System.out.println("发送成功!");
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
	
	
	// 新写的接口
	@RequestMapping(value = { "policyControl" })
	@ResponseBody
	public String policyControl() throws TimeoutException {

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
			System.out.println("阈值发送成功!");
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

	@RequestMapping(value = { "getDeviceByPolicy" }) // 添加中获取设备集合
	@ResponseBody
	public MapEntity getDeviceByPolicy(String devType) {
		return tAlarmPolicyService.getDeviceByPolicy(devType);
	}

	public static void main(String[] args) {
		timer1();
		System.out.println("2222");
	}
}