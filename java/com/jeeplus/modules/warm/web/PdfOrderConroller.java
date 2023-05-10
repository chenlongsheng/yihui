package com.jeeplus.modules.warm.web;

import com.alibaba.fastjson.JSONObject;
import com.ctc.wstx.util.StringUtil;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.maintenance.entity.PdfMaintenance;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.qxz.excel.OrderExcel;
import com.jeeplus.modules.qxz.excel.ReportExcel;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TCodeService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.dao.PdfOrderDao;
import com.jeeplus.modules.warm.dao.PdfOrderRecorderDao;
import com.jeeplus.modules.warm.entity.PdfOrder;
import com.jeeplus.modules.warm.entity.PdfOrderDeal;
import com.jeeplus.modules.warm.service.PdfOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ZZUSER on 2018/12/6.
 */
@Controller
@RequestMapping("/warm")
public class PdfOrderConroller extends BaseController {

	@Autowired
	PdfOrderService pdfOrderService;
	@Autowired
	TCodeService tCodeService;
	@Autowired
	PdfOrderDao pdfOrderDao;
	@Autowired
	PdfOrderRecorderDao pdfOrderRecorderDao;

	// 实时报警
	@RequestMapping("/findRealOrder")
	@ResponseBody
	public JSONObject findRealOrder(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		System.out.println("orgId======"+UserUtils.getOrgId());

		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		Page page = new Page(httpServletRequest, httpServletResponse);
		pdfOrder.setPage(page);
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println(1);
			Map map = pdfOrderService.findOrder(pdfOrder, 3);
			jsonObject.put("data", map);
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			System.out.println("================");
		}
		return jsonObject;
	}

	@RequestMapping("/findOrder")
	@ResponseBody
	public JSONObject findOrder(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		if (StringUtils.isBlank(pdfOrder.getAlarmAddr())) {
			pdfOrder.setAlarmAddr(pdfOrder.getOrgId());
		}
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		Page page = new Page(httpServletRequest, httpServletResponse);
		pdfOrder.setPage(page);
		JSONObject jsonObject = new JSONObject();
		try {
			Map map = pdfOrderService.findOrder(pdfOrder, 1);
			jsonObject.put("data", map);
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			System.out.println("================");
		}
		return jsonObject;
	}

	@RequestMapping("/alarmCountDetail")
	@ResponseBody
	public String alarmCountDetail(String chId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		return ServletUtils.buildRs(true, "报警次数详情", pdfOrderService.alarmCountDetail(chId));
	}

	// 工单查询
	@RequestMapping("/findSendOrder")
	@ResponseBody
	public JSONObject findSendOrder(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		if (StringUtils.isBlank(pdfOrder.getAlarmAddr())) {
			pdfOrder.setAlarmAddr(pdfOrder.getOrgId());
		}
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		Page page = new Page(httpServletRequest, httpServletResponse);
		pdfOrder.setPage(page);
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println(1);
			Map map = pdfOrderService.findOrder(pdfOrder, 2);
			jsonObject.put("data", map);
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			System.out.println("================");
		}
		return jsonObject;
	}

	@RequestMapping("/deleteOrder")
	@ResponseBody
	public JSONObject deleteOrder(String ids, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		JSONObject jsonObject = new JSONObject();
		try {
			pdfOrderService.deleteOrderByIds(ids);
			jsonObject.put("success", true);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	@RequestMapping("/addOrder")
	@ResponseBody
	public JSONObject addOrder(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		try {
			pdfOrderService.updateAlarmLog(pdfOrder.getChId());
			pdfOrderService.addOrder(pdfOrder);
			return ServletUtils.buildJsonRs(true, "成功", null);
		} catch (Exception e) {
			return ServletUtils.buildJsonRs(false, e.getMessage(), null);
		}
	}

	/**
	 * 确认工单
	 * 
	 * @param ids
	 */
	@RequestMapping("/confirmOrder")
	@ResponseBody
	public JSONObject confirmOrder(String ids, PdfOrder pdfOrder, String reason, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		JSONObject jsonObject = new JSONObject();
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		try {
			pdfOrderService.updateAlarmLog(pdfOrder.getChId());
			pdfOrderService.addOrder(pdfOrder);
			jsonObject.put("success", true);
			System.out.println(pdfOrder.getId() + "====");
			jsonObject.put("id", pdfOrder.getId());
			UserUtils.saveLog("报警单：" + pdfOrder.getOrderId() + "被确认", "操作");
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 派单
	 * 
	 * @param pdfOrder
	 */
	@RequestMapping("/sendOrder")
	@ResponseBody
	public JSONObject sendOrder(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		pdfOrder.setSendOrderUser(OrgUtil.getUserId());
		JSONObject jsonObject = new JSONObject();
		try {
			if (StringUtils.isNotBlank(pdfOrder.getSuggestion())) {
				new String(pdfOrder.getSuggestion().getBytes("UTF-8"), "GBK");
			}
			pdfOrderService.sendOrder(pdfOrder);
			UserUtils.saveLog("报警单：" + pdfOrder.getOrderId() + "生成了新工单：" + pdfOrder.getId() + "", "新增");
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	// 提交转维保工单
	@RequestMapping("/updateMainType")
	@ResponseBody
	public String updateMainType(String id, String principal, int type, String mobanMessage) {

		try {
			pdfOrderService.updateMainType(id, principal, mobanMessage, type);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", "");
		}
		return ServletUtils.buildRs(true, "成功", "");
	}

	// 人工添加工单
	@RequestMapping("/addOrderByPerson")
	@ResponseBody
	public JSONObject addOrderByPerson(PdfOrder pdfOrder, String type, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		JSONObject jsonObject = new JSONObject();
		try {
			pdfOrderService.addOrderByPerson(pdfOrder);

			UserUtils.saveLog("新工单：" + pdfOrder.getId() + "被创建", "新增");
			jsonObject.put("success", true);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 接单
	 * 
	 * @param pdfOrder
	 */
	@RequestMapping("/recieveOrder")
	@ResponseBody
	public JSONObject recieveOrder(PdfOrder pdfOrder) {
		JSONObject jsonObject = new JSONObject();
		try {
			pdfOrderService.recieveOrder(pdfOrder);
			jsonObject.put("success", true);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 处理中
	 * 
	 * @param pdfOrder
	 */
	@RequestMapping("/dealOrder")
	@ResponseBody
	public void dealOrder(PdfOrder pdfOrder) {
		pdfOrderService.dealOrder(pdfOrder);
	}

	/**
	 * 维修人员提交工单处理
	 * 
	 * @param pdfOrderDeal
	 */
	@RequestMapping("/submitOrder")
	@ResponseBody
	public String submitOrder(PdfOrderDeal pdfOrderDeal) {
		pdfOrderService.submitOrder(pdfOrderDeal);
		return ServletUtils.buildRs(true, "", "");
	}

	/**
	 * 维修人员提交工单处理 use
	 * 
	 * @param pdfOrderDeal
	 */
	@RequestMapping("/getDealList")
	@ResponseBody
	public String getDealList(PdfOrderDeal pdfOrderDeal) {
		List<Map> dealList = pdfOrderService.getDealList(pdfOrderDeal.getId());
		return ServletUtils.buildRs(true, "", dealList);
	}

	// 管理员结单 999
	@RequestMapping("/finishState")
	@ResponseBody
	public String finishState(PdfOrderDeal pdfOrderDeal, String orderId) {

		pdfOrderDeal.setSendUser(UserUtils.getUser().getId());
		try {
			pdfOrderService.updateState(orderId, pdfOrderDeal);
			UserUtils.saveLog("工单：" + orderId + "已结单", "操作");
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", "");
		}
		return ServletUtils.buildRs(true, "成功", "");
	}

	// 派单中的模板
	@RequestMapping("/getMobanList")
	@ResponseBody
	public String getMobanList(String id) {

		MapEntity en = new MapEntity();
		
		PdfOrder pdfOrder = new PdfOrder();
		pdfOrder.setOrderId(id);
		pdfOrder.setUserId(UserUtils.getUser().getId());
		List<Map> maps = pdfOrderDao.findSendOrder(pdfOrder);
		Map map = maps.get(0);
		System.out.println(map);
		List<MapEntity> list = pdfOrderRecorderDao.getTemplateList(new MapEntity());
		for (MapEntity entity : list) {
			String templateDtail = (String) entity.get("templateDetail");
			System.out.println(map);
			String alarmTime = (String) map.get("alarmTime");
			String alarmAddr = (String) map.get("alarmAddr");
			String devName = (String) map.get("devName");
			String devTypeName = (String) map.get("devTypeName");
			int alarmType = (int) map.get("alarmType");
			String alarm = "";
			if (alarmType == 0) {
				alarm = "设备故障";
			} else {
				alarm = "数据异常";
			}
			int alarmLevel = (int) map.get("alarmLevel");
			String level = "";
			if (alarmLevel == 1 || alarmLevel == 2) {
				level = alarmLevel + "(一般报警)";
			} else {
				level = alarmLevel + "(严重报警)";
			}
			templateDtail = templateDtail.replace("${createDate}", alarmTime);
			templateDtail = templateDtail.replace("${orgName}", alarmAddr);
			templateDtail = templateDtail.replace("${deviceName}", devName);
			templateDtail = templateDtail.replace("${devType}", devTypeName);
			templateDtail = templateDtail.replace("${alarmType}", alarm);
			templateDtail = templateDtail.replace("${alarmLevel}", level);
			entity.put("templateDetail", templateDtail);
		}
		return ServletUtils.buildRs(true, "模板回调集合", list);
	}

	// 转维保人员回调 模板
	@RequestMapping("/maintenanceList")
	@ResponseBody
	public String maintenanceList(String id) {
		MapEntity en = new MapEntity();
		List<MapEntity> maintenanceList = pdfOrderService.maintenanceList(id);
		if (maintenanceList == null || maintenanceList.size() == 0) {
			en.put("success", 1);
			return ServletUtils.buildRs(false, "没有配置维保单位", en);
		} else {
			PdfOrder pdfOrder = new PdfOrder();
			pdfOrder.setOrderId(id);
			pdfOrder.setUserId(UserUtils.getUser().getId());
			List<Map> maps = pdfOrderDao.findSendOrder(pdfOrder);
			Map map = maps.get(0);
			System.out.println(map);
			List<MapEntity> list = pdfOrderRecorderDao.getTemplateList(new MapEntity());
			for (MapEntity entity : list) {
				String templateDtail = (String) entity.get("templateDetail");

				System.out.println(map);
				String alarmTime = (String) map.get("alarmTime");
				String alarmAddr = (String) map.get("alarmAddr");
				String devName = (String) map.get("devName");
				String devTypeName = (String) map.get("devTypeName");
				int alarmType = (int) map.get("alarmType");
				String alarm = "";
				if (alarmType == 0) {
					alarm = "设备故障";
				} else {
					alarm = "数据异常";
				}
				int alarmLevel = (int) map.get("alarmLevel");
				String level = "";
				if (alarmLevel == 1 || alarmLevel == 2) {
					level = alarmLevel + "(一般报警)";
				} else {
					level = alarmLevel + "(严重报警)";
				}
				templateDtail = templateDtail.replace("${createDate}", alarmTime);
				templateDtail = templateDtail.replace("${orgName}", alarmAddr);
				templateDtail = templateDtail.replace("${deviceName}", devName);
				templateDtail = templateDtail.replace("${devType}", devTypeName);
				templateDtail = templateDtail.replace("${alarmType}", alarm);
				templateDtail = templateDtail.replace("${alarmLevel}", level);
				entity.put("templateDetail", templateDtail);
			}
			en.put("success", 0);
			en.put("templateList", list);
			en.put("maintenUserList", pdfOrderService.maintenUserList());

			return ServletUtils.buildRs(true, "已配置维保单位", en);
		}
	}

	/**
	 * 点击处理流程按钮
	 * 
	 * @param pdfOrder
	 */
	@RequestMapping("/getOrderRecorders")
	@ResponseBody
	public JSONObject getOrderRecorders(PdfOrder pdfOrder) {
		JSONObject jsonObject = new JSONObject();
		try {
			Map map = pdfOrderService.getOrderRecorders(pdfOrder);
			jsonObject.put("success", true);
			jsonObject.put("data", map);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 管理人员回复工单
	 */
	@RequestMapping("/replyOrder")
	@ResponseBody
	public JSONObject replyOrder(PdfOrderDeal pdfOrderDeal) {

		JSONObject jsonObject = new JSONObject();
		try {
			pdfOrderDeal.setSendUser(OrgUtil.getUserId());
			pdfOrderService.replyOrder(pdfOrderDeal);
			jsonObject.put("success", true);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	// 管理员最新回复工单
	@RequestMapping("/answerOrder")
	@ResponseBody
	public JSONObject answerOrder(@RequestParam(value = "images", required = false) MultipartFile[] images,
			PdfOrderDeal pdfOrderDeal, HttpServletRequest request) {
		JSONObject jsonObject = new JSONObject();
		String imageList = "";
		String spite = "";
		try {
			pdfOrderDeal.setSendUser(UserUtils.getUser().getId());

			if (images != null && images.length > 0) { // 遍历文件
				for (int i = 0; i < images.length; i++) {
					MultipartFile file = images[i];
					// 保存文件
					String image = saveFile(request, file);
					if (i != 0) {
						spite = ",";
					}
					imageList += spite + image;
				}
			}
			System.out.println(imageList + "=============");
			pdfOrderDeal.setImage(imageList);
			pdfOrderService.answerOrder(pdfOrderDeal);
			jsonObject.put("success", true);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	// 保存管理员回复图片
	public String saveFile(HttpServletRequest request, MultipartFile file) {
		String path = request.getSession().getServletContext().getRealPath("");
		// 修改图片的uuid
		String image = UUID.randomUUID()
				+ file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String filePath = "/static_modules/image/" + image;
		// 拼接图片的路径
		File saveDir = new File(path + filePath);
		if (!saveDir.getParentFile().exists()) {
			saveDir.getParentFile().mkdirs();
		}
		try {
			file.transferTo(saveDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filePath;
	}

	/**
	 * 根据区域获取设备
	 * 
	 * @param orgId
	 * @return
	 */
	@RequestMapping("/getDevByOrg")
	@ResponseBody
	public JSONObject getDevByOrg(String orgId, Integer devType, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		JSONObject jsonObject = new JSONObject();
		try {
			TOrg tOrg = new TOrg();
			tOrg.setId(orgId);
			tOrg.setType(devType);
			List<Map> list = pdfOrderService.getDevByOrg(tOrg);
			jsonObject.put("success", true);
			jsonObject.put("data", list);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 获取设备类型列表
	 */
	@RequestMapping("/getTypeList")
	@ResponseBody
	public JSONObject getTypeList(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		JSONObject jsonObject = new JSONObject();
		try {
			TCode tCode = new TCode();
			tCode.setTypeId(1l);
			tCode.setPage(null);
			List<TCode> list = tCodeService.findList(tCode);

//			List<MapEntity> mobanList = pdfOrderRecorderDao.getTemplateList(new MapEntity());
//			jsonObject.put("mobanList", mobanList);	//新加的			
			jsonObject.put("success", true);
			jsonObject.put("data", list);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 获取报警人集合
	 */
	@RequestMapping("/getSendUserList")
	@ResponseBody
	public JSONObject getSendUserList(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		JSONObject jsonObject = new JSONObject();
		try {
			String orgId = OrgUtil.getOrgId();
			List<Map> list = pdfOrderService.getSendUserList(orgId);
			jsonObject.put("data", list);
			jsonObject.put("success", true);
		} catch (Exception e) {
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());

		}
		return jsonObject;
	}

	/**
	 * 报警工单页头部 几个下拉控件初始化数据
	 * 
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return
	 */
	@RequestMapping("/initAlarmTop")
	@ResponseBody
	public JSONObject initAlarmTop(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		JSONObject jsonObject = new JSONObject();
		try {
			String orgId = OrgUtil.getOrgId();
			List<Map> list = pdfOrderService.getSendUserList(orgId);

			TCode tCode = new TCode();
			tCode.setTypeId(1l);
			tCode.setPage(null);
			List<TCode> list1 = tCodeService.getCodeList();
			Map resultMap = new HashMap();
			resultMap.put("typeList", list1);
			resultMap.put("sendUser", list);
			resultMap.put("period", list);
			jsonObject.put("data", resultMap);
			jsonObject.put("success", true);
		} catch (Exception e) {
			jsonObject.put("success", true);
			jsonObject.put("msg", e.getMessage());
		}
		return jsonObject;
	}

	/**
	 * 初始化添加报警弹框中的下拉框
	 * 
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @return
	 */
	@RequestMapping("/initAddAlarm")
	@ResponseBody
	public JSONObject initAddAlarm(String orgId, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println(orgId);
		} catch (Exception e) {
		}
		return jsonObject;
	}

	@RequestMapping("/maintenUserList")
	@ResponseBody
	public String maintenUserList() {
		return ServletUtils.buildRs(true, "维保处理人", pdfOrderService.maintenUserList());
	}

	@RequestMapping("/getVedioLogByChId")
	@ResponseBody
	public String getVedioLogByChId(String chId) {

		return ServletUtils.buildRs(true, "获取报警时间", pdfOrderService.getVedioLogByChId(chId));
	}

	// 报警导出
	@RequestMapping("/warnExport")
	@ResponseBody
	public void warnExport(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		Page page = new Page(httpServletRequest, httpServletResponse);
//		pdfOrder.setPage(page);
		JSONObject jsonObject = new JSONObject();

		Map map = pdfOrderService.findOrder(pdfOrder, 1);//

		List<Map> orderList = (List<Map>) map.get("list");
//		Map<String, List> orderList   =(Map<String, List>) map.get("data");

		List<OrderExcel> resultList = new ArrayList();
		for (int i = 0; i < orderList.size(); i++) {
			OrderExcel excel = new OrderExcel();
			excel.setId(String.valueOf(orderList.get(i).get("id")));
			excel.setDevName(String.valueOf(orderList.get(i).get("devName")));
			int isDispatch = (int) orderList.get(i).get("isDispatch");
			if (isDispatch == 1) {
				excel.setIsDispatch("已确认");
			} else {
				excel.setIsDispatch("未确认");
			}
			int alarmType = (int) orderList.get(i).get("alarmType");
			if (alarmType == 0) {
				excel.setAlarmType("设备故障");
			} else {
				excel.setAlarmType("数据异常");
			}
			int state = (int) orderList.get(i).get("state");
			if (state == 0) {
				excel.setState("已确认");
			} else {
				excel.setState("未确认");
			}
			excel.setAlarmLevel((int) orderList.get(i).get("alarmLevel") + "");
			excel.setAlarmTime(String.valueOf(orderList.get(i).get("alarmTime")));
			excel.setOrgName(String.valueOf(orderList.get(i).get("orgName")));
			excel.setAlarmNumber(String.valueOf(orderList.get(i).get("alarmNumber")));
			excel.setTypeId(String.valueOf(orderList.get(i).get("typeId")));
			excel.setId(String.valueOf(orderList.get(i).get("id")));
			excel.setPrec(String.valueOf(orderList.get(i).get("prec")));
			excel.setCodeName(String.valueOf(orderList.get(i).get("devTypeName")));
			resultList.add(excel);
		}

		UserUtils.saveLog("导出报警类型统计报表", "导出");
		ExcelUtil.warnReport(httpServletRequest, httpServletResponse, resultList);
		System.out.println("111111111111111111");
	}

	// 工单导出
	@RequestMapping("/orderExport")
	@ResponseBody
	public void orderExport(PdfOrder pdfOrder, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		/* 星号表示所有的域都可以接受， */
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST");
		Page page = new Page(httpServletRequest, httpServletResponse);
//		pdfOrder.setPage(page);
		JSONObject jsonObject = new JSONObject();

		Map map = pdfOrderService.findOrder(pdfOrder, 2);//

		List<Map> orderList = (List<Map>) map.get("list");
//		Map<String, List> orderList   =(Map<String, List>) map.get("data");

		List<OrderExcel> resultList = new ArrayList();
		for (int i = 0; i < orderList.size(); i++) {
			OrderExcel excel = new OrderExcel();
			excel.setId(String.valueOf(orderList.get(i).get("id")));
			excel.setDevName(String.valueOf(orderList.get(i).get("devName")));

			int alarmType = (int) orderList.get(i).get("alarmType");
			if (alarmType == 0) {
				excel.setAlarmType("设备故障");
			} else {
				excel.setAlarmType("数据异常");
			}
			Integer state = (Integer)orderList.get(i).get("state");

			if (state == 2) {
				excel.setState("待接单");
			} else if (state == 3) {
				excel.setState("已接单");
			} else if (state == 4) {
				excel.setState("处理中");
			} else if (state == 5) {
				excel.setState("已结单");
			}
			excel.setAlarmLevel((int) orderList.get(i).get("alarmLevel") + "");
			excel.setAlarmTime(String.valueOf(orderList.get(i).get("alarmTime")));
			excel.setOrgName(String.valueOf(orderList.get(i).get("orgName")));
			excel.setAlarmNumber(String.valueOf(orderList.get(i).get("alarmNumber")));
			excel.setTypeId(String.valueOf(orderList.get(i).get("typeId")));
			excel.setOrderId(String.valueOf(orderList.get(i).get("orderId")));
			excel.setPrec(String.valueOf(orderList.get(i).get("prec")));
			excel.setSuggestion(String.valueOf(orderList.get(i).get("suggestion")));
			excel.setUserName(String.valueOf(orderList.get(i).get("userName")));
			excel.setPhone(String.valueOf(orderList.get(i).get("phone")));
			excel.setCodeName(String.valueOf(orderList.get(i).get("devTypeName")));
			resultList.add(excel);
		}
		UserUtils.saveLog("导出报警处理情况报表", "导出");
		ExcelUtil.orderReport(httpServletRequest, httpServletResponse, resultList);
	}

}
