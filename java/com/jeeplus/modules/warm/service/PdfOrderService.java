package com.jeeplus.modules.warm.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jeeplus.common.mq.rabbitmq.DevAlarm;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.IdGenSnowFlake;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.qxz.wx.entry.Moban;
import com.jeeplus.modules.qxz.wx.util.HttpUtils;
import com.jeeplus.modules.qxz.wx.util.WeChatApiUtil;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.dao.UserDao;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.dao.*;
import com.jeeplus.modules.warm.entity.PdfBind;
import com.jeeplus.modules.warm.entity.PdfOrder;
import com.jeeplus.modules.warm.entity.PdfOrderDeal;
import com.jeeplus.modules.warm.entity.PdfOrderRecorder;
import com.jeeplus.modules.warm.util.PdfTemplateUtil;


import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZZUSER on 2018/12/6.
 */
@Service
public class PdfOrderService extends CrudService<PdfOrderDao, TOrg> {
	@Autowired
	PdfOrderDao pdfOrderDao;
	@Autowired
	PdfOrderRecorderDao pdfOrderRecorderDao;
	@Autowired
	PdfOrderDealDao pdfOrderDealDao;
	@Autowired
	PdfSchedulingRuleDao pdfSchedulingRuleDao;
	@Autowired
	UserDao userDao;
	@Autowired
	PdfBindDao pdfBindDao;

	public Map findOrder(PdfOrder pdfOrder, int key) {

		String userId = UserUtils.getUser().getId();
		userId = (userId == null) ? "1" : userId;
		pdfOrder.setUserId(userId);
		if (StringUtils.isBlank(pdfOrder.getAlarmAddr())) {
			if (StringUtils.isNotBlank(pdfOrder.getOrgId())) {
				pdfOrder.setAlarmAddr(pdfOrder.getOrgId());
			}
		}
		List<Map> resultList = new ArrayList<Map>();
		if (key == 1) {
			resultList = pdfOrderDao.findOrder(pdfOrder);
		} else if (key == 2) {
			resultList = pdfOrderDao.findSendOrder(pdfOrder);
		} else if (key == 3) {
			System.out.println("type====:  "+pdfOrder.getAlarmType());

			resultList = pdfOrderDao.findRealOrder(userId,pdfOrder.getAlarmType());
		}
		for (int i = 0; i < resultList.size(); i++) {
			PdfOrderDeal pdfOrderDeal = new PdfOrderDeal();
			pdfOrderDeal.setType(0);
			pdfOrderDeal.setOrderId(String.valueOf(resultList.get(i).get("id")));
//			int num = pdfOrderDealDao.countUnRead(pdfOrderDeal);
//			if (num > 0) {
//				resultList.get(i).put("hasUnRead", true);
//			}
			List<User> userList = new ArrayList<>();
			List<User> sendUser = new ArrayList();
			List<User> confirmUser = new ArrayList<>();
			String ids = "";
			String[] arr = {};
			if (resultList.get(i).get("principal") != null) {
				ids = String.valueOf(resultList.get(i).get("principal"));
				arr = ids.split(",");
				userList = pdfOrderDao.getUserByIds(arr);
				resultList.get(i).put("principals", userList);
			}
			if (resultList.get(i).get("sendOrderUser") != null) {
				ids = String.valueOf(resultList.get(i).get("sendOrderUser"));
				arr = ids.split(",");
				sendUser = pdfOrderDao.getUserByIds(arr);
				resultList.get(i).put("userName", sendUser.get(0).getName());
				resultList.get(i).put("phone", sendUser.get(0).getPhone());
			}
			if (resultList.get(i).get("confirmUser") != null) {
				ids = String.valueOf(resultList.get(i).get("confirmUser"));
				arr = ids.split(",");
				confirmUser = pdfOrderDao.getUserByIds(arr);
				resultList.get(i).put("confirmUser", confirmUser.get(0).getName());
			}
		}
		// 新加报警次数
		if (pdfOrder != null && StringUtils.isNotBlank(pdfOrder.getAlarmNumber())) {
			Iterator<Map> it = resultList.iterator();
			while (it.hasNext()) {
				int count = (int) it.next().get("alarmNumber");
				if (!(count + "").equals(pdfOrder.getAlarmNumber())) {
					it.remove();
				}
			}
		}
		Map<String, List> map = new LinkedHashMap<>();
		List<Map> list1 = new ArrayList<>();
		for (int i = 0; i < resultList.size(); i++) {
			String time = String.valueOf(resultList.get(i).get("alarmTime"));
			String state = String.valueOf(resultList.get(i).get("state"));

			time = time.substring(0, 10);
			List<Map> list = map.get(time);
			if (!state.equals("5")) {
				list1.add(resultList.get(i));
			}
		}
		map.put("unfinishedOrder", list1);
		for (int i = 0; i < resultList.size(); i++) {
			String time = String.valueOf(resultList.get(i).get("alarmTime"));
			String state = String.valueOf(resultList.get(i).get("state"));
			time = time.substring(0, 10);
			List<Map> list = map.get(time);

			if (state.equals("5")) {
				if (list != null) {
					list.add(resultList.get(i));
					map.put(time, list);
				} else {
					list = new ArrayList<>();
					list.add(resultList.get(i));
					map.put(time, list);
				}
			}
		}
		pdfOrder.setPage(null);
		List list = null;
		if (key == 1) {
			list = pdfOrderDao.findOrder(pdfOrder);
		} else if (key == 2) {
			list = pdfOrderDao.findSendOrder(pdfOrder);
		} else if (key == 3) {
			list = pdfOrderDao.findRealOrder(userId,pdfOrder.getAlarmType());
		}
		Map map1 = new HashMap();
		map1.put("data", map);
		map1.put("length", list.size());
		map1.put("list", resultList);
		return map1;
	}

	@Transactional(readOnly = false)
	public void deleteOrderByIds(String ids) {
		String[] arr = ids.split(",");
		pdfOrderDao.deleteOrderByIds(arr);
	}

	/**
	 * 消息队列发送告警过来
	 * 
	 * @param devAlarm
	 */
	@Transactional(readOnly = false)
	public void addOrder(DevAlarm devAlarm) {
		TDevice tDevice = pdfOrderDao.getDevById(String.valueOf(devAlarm.getDev_id()));
		PdfOrder pdfOrder = new PdfOrder();
		pdfOrder.setDevId(String.valueOf(devAlarm.getDev_id()));
		pdfOrder.setAlarmType("1");
		pdfOrder.setPrec(devAlarm.getItems().get(0).getAlarm_info());
		pdfOrder.setAlarmLevel(devAlarm.getItems().get(0).getAlarm_level().intValue());
		pdfOrder.setAlarmTime(devAlarm.getItems().get(0).getOccur_time());
		pdfOrder.setAlarmAddr(tDevice.getOrgId());
		pdfOrder.setState(0);
		pdfOrder.setAlarmSource(1);
		pdfOrderDao.addOrder(pdfOrder);
	}

	@Transactional(readOnly = false)
	public long addOrder(PdfOrder pdfOrder) {
//        SystemAuthorizingRealm.Principal principal = (SystemAuthorizingRealm.Principal) UserUtils.getPrincipal();
//        String userId = principal.getId();
//        User user = UserUtils.get(userId);
		pdfOrder.setSendOrderUser(OrgUtil.getUserId());
//        pdfOrder.setSendOrderUser("277802135813361664");
		pdfOrder.setAlarmSource(2);

		pdfOrderDao.addOrder(pdfOrder);
		return Long.parseLong(pdfOrder.getId());
	}

	/**
	 * 小程序端提交工单
	 * 
	 * @param pdfOrder
	 */
	@Transactional(readOnly = false)
	public void addOrderByWx(PdfOrder pdfOrder) {
		String ids = pdfOrder.getPrincipal();
//		pdfOrder.setPrincipal(null);

		pdfOrder.setState(0);

		pdfOrder.setAlarmSource(2);
		pdfOrderDao.addOrder(pdfOrder);//
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setDate(formatStr);

		pdfOrderRecorder.setState(0);
		pdfOrderRecorder.setOrderId(pdfOrder.getId());
		String[] idList = ids.split(",");
		for (int i = 0; i < idList.length; i++) {
			String id = idList[i];
			pdfOrderRecorder.setUserId(id);
//			pdfOrderRecorderDao.addOrderRecorder(pdfOrderRecorder);
		}
	}

	/**
	 * 确认工单
	 * 
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void confirmOrder(String ids, String reason) {
		PdfOrder pdfOrder = new PdfOrder();
		pdfOrder.setConfirmUser(OrgUtil.getUserId());
//        pdfOrder.setConfirmUser("277802135813361664");
		pdfOrder.setState(1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setDate(formatStr);
		pdfOrderRecorder.setUserId(OrgUtil.getUserId());
//      pdfOrderRecorder.setUserId("277802135813361664");
		pdfOrderRecorder.setState(1);
		String[] arr = ids.split(",");
		for (int i = 0; i < arr.length; i++) {
			pdfOrder.setId(arr[i]);
			pdfOrder.setReason(reason);
			pdfOrderDao.updateOrder(pdfOrder);
			pdfOrderRecorder.setOrderId(arr[i]);
//			pdfOrderRecorderDao.addOrderRecorder(pdfOrderRecorder);//不需要
		}
	}

	@Transactional(readOnly = false)
	public void updateAlarmLog(String chId) {

		pdfOrderDao.updateAlarmLog(chId, "1");// 报警日志确定1
	}

	/**
	 * 派单
	 * 
	 * @param pdfOrder
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public void sendOrder(PdfOrder pdfOrder) {

		if (pdfOrder.getId() == null) {
			long id = addOrder(pdfOrder);
			confirmOrder(String.valueOf(id), pdfOrder.getReason());
			pdfOrder.setId(String.valueOf(id));
			pdfOrder.setOrderId(IdGenSnowFlake.uuid().toString());
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setDate(formatStr);
		pdfOrderRecorder.setOrderId(pdfOrder.getId());
		String principals = pdfOrder.getPrincipal();
//		pdfOrderRecorder.setUserId(OrgUtil.getUserId());

		Map map = pdfOrderDao.findOrderById(pdfOrder.getId());
		map.put("mobanMessage", pdfOrder.getMobanMessage());

		int state = (int) map.get("state");
		if (state == 0) {
			confirmOrder(pdfOrder.getId(), pdfOrder.getReason());
		}
		pdfOrderRecorder.setState(2);
		String[] principalList = principals.split(",");// 派单几个人等等----
		for (int i = 0; i < principalList.length; i++) {
			pdfOrderRecorder.setUserId(principalList[i]);
			pdfOrderRecorderDao.addOrderRecorder(pdfOrderRecorder);
		}
		pdfOrder.setState(2);
		pdfOrder.setSendOrderUser(OrgUtil.getUserId());

	
		pdfOrderDao.updateOrder(pdfOrder);

		map = pdfOrderDao.findOrderById(pdfOrder.getId());
		String ids = String.valueOf(map.get("principal"));
		List<PdfBind> list = pdfBindDao.findBindByIds(ids);// 维保用户ids
		String keys = pdfOrder.getType();
		String[] splits = keys.split(",");
		for (int j = 0; j < splits.length; j++) {
			int key = Integer.parseInt(splits[j]);
			for (int i = 0; i < list.size(); i++) {
				if (key == 2) {
					com.jeeplus.modules.warm.util.SendMessage.sendTextMail(list.get(i).getEmail(),
							pdfOrder.getMobanMessage());
				} else if (key == 1) {
					com.jeeplus.modules.warm.util.SendMessage.sendMobileMessage(list.get(i).getMobile(), "1");
//					pdfOrder.getMobanMessage()
				} else if (key == 0) {
//					Moban moban = PdfTemplateUtil.dataAlarmTemplate(map, list.get(i).getOpenId());
//					String api = "http://wx.cdsoft.cn/index.php/accesstoken";
//					String token = HttpUtils.sendGet(api, null, null);
//					Map tokenMap = JSONObject.parseObject(token);
//					String accessToken = String.valueOf(tokenMap.get("access_token"));
//					String json = JSONObject.toJSONString(moban);
//					System.out.println(json);
//					Gson gson = new Gson();
//					String json1 = gson.toJson(moban);
//					System.out.println(json1);
//					String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
//					url = url.replaceAll("ACCESS_TOKEN", accessToken);
//					String httpsRequest = WeChatApiUtil.httpsRequestToString(url, "POST", json1);
//					System.out.println(httpsRequest);
				}
			}
		}
	}
    
	// ==============发送公众号
	public void sendAPP(Map map, PdfBind pdfBind) {// 发送app,
		Moban moban = PdfTemplateUtil.dataAlarmTemplate(map, pdfBind.getOpenId());
		String api = "http://wx.cdsoft.cn/index.php/accesstoken";
		String token = HttpUtils.sendGet(api, null, null);
		Map tokenMap = JSONObject.parseObject(token);
		String accessToken = String.valueOf(tokenMap.get("access_token"));
		String json = JSONObject.toJSONString(moban);
		System.out.println(json);
		Gson gson = new Gson();
		String json1 = gson.toJson(moban);
		System.out.println(json1);
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
		url = url.replaceAll("ACCESS_TOKEN", accessToken);
		String httpsRequest = WeChatApiUtil.httpsRequestToString(url, "POST", json1);
		System.out.println(httpsRequest);
	}
	
	// 提交转维保工单
	@Transactional(readOnly = false)
	public void updateMainType(String id, String principal, String mobanMessage, int key) {
		pdfOrderDao.updateMainType(id, principal);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setDate(formatStr);
		pdfOrderRecorder.setOrderId(id);
		pdfOrderRecorder.setUserId(OrgUtil.getUserId());
		pdfOrderRecorder.setState(2);
		pdfOrderRecorderDao.addOrderRecorder(pdfOrderRecorder);

		Map map = pdfOrderDao.findOrderById(id);
		map.put("mobanMessage", mobanMessage);

		List<PdfBind> list = pdfBindDao.findBindByIds(principal);

		for (int i = 0; i < list.size(); i++) {
//			if (key == 3) {
//				com.jeeplus.modules.warm.util.SendMessage.sendTextMail(list.get(i).getEmail(),
//						mobanMessage);
//			} else if (key == 2) {
//				com.jeeplus.modules.warm.util.SendMessage.sendSMS(list.get(i).getMobile(), mobanMessage);
//			} else if (key == 1) {
			System.out.println(list.get(i));
			Moban moban = PdfTemplateUtil.dataAlarmTemplate(map, list.get(i).getOpenId());
			String api = "http://wx.cdsoft.cn/index.php/accesstoken";
			String token = HttpUtils.sendGet(api, null, null);
			Map tokenMap = JSONObject.parseObject(token);
			String accessToken = String.valueOf(tokenMap.get("access_token"));
			String json = JSONObject.toJSONString(moban);
			System.out.println(json);
			Gson gson = new Gson();
			String json1 = gson.toJson(moban);
			System.out.println(json1);
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
			url = url.replaceAll("ACCESS_TOKEN", accessToken);
			String httpsRequest = WeChatApiUtil.httpsRequestToString(url, "POST", json1);
			System.out.println("chenggong====" + httpsRequest);
		}
	}
//	}

	/**
	 * 接单
	 * 
	 * @param pdfOrder
	 */
	@Transactional(readOnly = false)
	public void recieveOrder(PdfOrder pdfOrder) {
		Map map = pdfOrderDao.findOrderById(pdfOrder.getId());
		int state = (int) map.get("state");
		String confirmUser = pdfOrder.getConfirmUser();
		if (state == 2) {
			pdfOrder.setState(3);
			pdfOrder.setConfirmUser(null);
			pdfOrderDao.updateOrder(pdfOrder);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setDate(formatStr);
		pdfOrderRecorder.setOrderId(pdfOrder.getId());
		pdfOrderRecorder.setUserId(confirmUser);
		pdfOrderRecorder.setState(3);
		pdfOrderRecorderDao.addOrderRecorder(pdfOrderRecorder);
	}

	/**
	 * 处理中
	 * 
	 * 
	 * @param pdfOrder
	 */
	@Transactional(readOnly = false)
	public void dealOrder(PdfOrder pdfOrder) {
		Map map = pdfOrderDao.findOrderById(pdfOrder.getId());
		int state = (int) map.get("state");
		String confirmUser = pdfOrder.getConfirmUser();
		if (state == 3) {
			pdfOrder.setConfirmUser(confirmUser);
			pdfOrder.setState(4);
			pdfOrderDao.updateOrder(pdfOrder);
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setDate(formatStr);
		pdfOrderRecorder.setOrderId(pdfOrder.getId());
		pdfOrderRecorder.setUserId(confirmUser);
		pdfOrderRecorder.setState(4);
		pdfOrderRecorderDao.addOrderRecorder(pdfOrderRecorder);
	}

	/**
	 * 维修人员提交工单处理
	 */
	@Transactional(readOnly = false)
	public void submitOrder(PdfOrderDeal pdfOrderDeal) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		pdfOrderDeal.setSendDate(formatStr);
		if (pdfOrderDeal.getType() != 0) {
			pdfOrderDeal.setType(1);
		}
		pdfOrderDealDao.addOrderDeal(pdfOrderDeal);
	}

	// 结单
	@Transactional(readOnly = false)
	public void updateState(String id, PdfOrderDeal pdfOrderDeal) {
		pdfOrderDealDao.updateState(id, pdfOrderDeal.getSendUser(), pdfOrderDeal.getContent());
		pdfOrderDeal.setType(0);
//		submitOrder(pdfOrderDeal);

		// 添加记录
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setDate(formatStr);
		pdfOrderRecorder.setOrderId(id);
		pdfOrderRecorder.setUserId(pdfOrderDeal.getSendUser());
		pdfOrderRecorder.setState(5);
		pdfOrderRecorderDao.addOrderRecorder(pdfOrderRecorder);

	}

	/**
	 * 管理人员回复
	 */
	@Transactional(readOnly = false)
	public void replyOrder(PdfOrderDeal pdfOrderDeal) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		pdfOrderDeal.setSendDate(formatStr);
		pdfOrderDeal.setType(0);
		pdfOrderDealDao.addOrderDeal(pdfOrderDeal);
		PdfBind pdfBind = new PdfBind();
		pdfBind.setUserId(pdfOrderDeal.getRecieveUser());
		List<PdfBind> list = pdfBindDao.findBind(pdfBind);
		Map map = pdfOrderDao.findOrderById(pdfOrderDeal.getOrderId());

		for (int i = 0; i < list.size(); i++) {
			Moban moban = PdfTemplateUtil.order(map, list.get(i).getOpenId());
			String api = "http://wx.cdsoft.cn/index.php/accesstoken";
			String token = HttpUtils.sendGet(api, null, null);
			Map tokenMap = JSONObject.parseObject(token);
			String accessToken = String.valueOf(tokenMap.get("access_token"));
			String json = JSONObject.toJSONString(moban);
			System.out.println(json);
			Gson gson = new Gson();
			String json1 = gson.toJson(moban);
			System.out.println(json1);
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
			url = url.replaceAll("ACCESS_TOKEN", accessToken);
			String httpsRequest = WeChatApiUtil.httpsRequestToString(url, "POST", json1);
			System.out.println(httpsRequest);
		}
	}

	// 最新回复工单
	@Transactional(readOnly = false)
	public void answerOrder(PdfOrderDeal pdfOrderDeal) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formatStr = formatter.format(new Date());
		pdfOrderDeal.setSendDate(formatStr);
		pdfOrderDeal.setType(0);
		pdfOrderDealDao.addOrderDeal(pdfOrderDeal);
		PdfBind pdfBind = new PdfBind();
		pdfBind.setUserId(pdfOrderDeal.getRecieveUser());
		List<PdfBind> list = pdfBindDao.findBind(pdfBind);
		Map map = pdfOrderDao.findOrderById(pdfOrderDeal.getOrderId());

		for (int i = 0; i < list.size(); i++) {
			Moban moban = PdfTemplateUtil.order(map, list.get(i).getOpenId());
			String api = "http://wx.cdsoft.cn/index.php/accesstoken";
			String token = HttpUtils.sendGet(api, null, null);
			Map tokenMap = JSONObject.parseObject(token);
			String accessToken = String.valueOf(tokenMap.get("access_token"));
			String json = JSONObject.toJSONString(moban);
			System.out.println(json);
			Gson gson = new Gson();
			String json1 = gson.toJson(moban);
			System.out.println(json1);
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
			url = url.replaceAll("ACCESS_TOKEN", accessToken);
			String httpsRequest = WeChatApiUtil.httpsRequestToString(url, "POST", json1);
			System.out.println(httpsRequest);
		}
	}

	/**
	 * 管理员获取工单处理过程
	 */
	public List<Map> getDealList(String orderId) {
		List<Map> list = pdfOrderDealDao.getDealList(orderId);
		return list;
	}

	/**
	 * 管理员获取工单处理过程
	 */
	@Transactional(readOnly = false)
	public Map getOrderRecorders(PdfOrder pdfOrder) {
		Map nowOrder = pdfOrderDao.findOrderById(pdfOrder.getId());
		int state = (int) nowOrder.get("state");
		PdfOrderDeal pdfOrderDeal = new PdfOrderDeal();
		pdfOrderDeal.setType(0);
		pdfOrderDeal.setOrderId(pdfOrder.getId());
		pdfOrderDealDao.setReaded(pdfOrderDeal);
		Map resultMap = new HashMap();
		PdfOrderRecorder pdfOrderRecorder = new PdfOrderRecorder();
		pdfOrderRecorder.setOrderId(pdfOrder.getId());
		List<Map> list = pdfOrderRecorderDao.getRecorderList(pdfOrderRecorder);
		List<Map> dealList = pdfOrderDealDao.getDealList(pdfOrder.getId());

		for (int i = 0; i < dealList.size(); i++) {
			// 改写
			String images = (String) dealList.get(i).get("image");
			if (StringUtils.isNotBlank(images)) {
				String[] imageList = images.split(",");
				dealList.get(i).put("image", imageList);
			}
			String replyImages = (String) dealList.get(i).get("replyImage");
			if (StringUtils.isNotBlank(replyImages)) {
				String[] replyImage = replyImages.split(",");
				dealList.get(i).put("replyImage", replyImage);
			}
		}
		List<String> maxDate = pdfOrderDealDao.getMaxDate(pdfOrder.getId());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < dealList.size(); i++) {
			String date = (String) dealList.get(i).get("send_date");
			for (int j = 0; j < maxDate.size(); j++) {
				if (date.equals(maxDate.get(j))) {
					List<Map> list1 = pdfOrderDealDao.getDealByReplyId(String.valueOf(dealList.get(i).get("id")));
					if (list1.size() == 0) {
						dealList.get(i).put("new", true);
					}
				}
			}
		}
		resultMap.put("recorderList", list);
		resultMap.put("dealList", dealList);
		resultMap.put("state", state);
		return resultMap;
	}

	/**
	 * 微信小程序端首页数据获取
	 * 
	 * @return
	 */
	public Map getWxData(String id, String name, String orgId, String state) {
		PdfOrder pdfOrder = new PdfOrder();
		Map resultMap = new HashMap();
		resultMap.put("name", name);
		pdfOrder.setPrincipal(id);
		pdfOrder.setAlarmAddr(orgId);
		if (state != null && state.length() != 0) {
			pdfOrder.setState(Integer.valueOf(state));
			List list = findOrder1(pdfOrder);
			if (state.equals("2")) {
				resultMap.put("unRecieve", list);
				resultMap.put("recieve", new ArrayList());
				resultMap.put("onDeal", new ArrayList());
				resultMap.put("history", new ArrayList());
			} else if (state.equals("3")) {
				resultMap.put("unRecieve", new ArrayList());
				resultMap.put("recieve", list);
				resultMap.put("onDeal", new ArrayList());
				resultMap.put("history", new ArrayList());
			} else if (state.equals("4")) {
				resultMap.put("unRecieve", new ArrayList());
				resultMap.put("recieve", new ArrayList());
				resultMap.put("onDeal", list);
				resultMap.put("history", new ArrayList());
			} else if (state.equals("5")) {
				resultMap.put("unRecieve", new ArrayList());
				resultMap.put("recieve", new ArrayList());
				resultMap.put("onDeal", new ArrayList());
				resultMap.put("history", list);
			}
			return resultMap;
		}

		pdfOrder.setState(2);
		List list = findOrder1(pdfOrder);
		resultMap.put("unRecieve", list);
		pdfOrder.setState(3);
		List list1 = findOrder1(pdfOrder);
		resultMap.put("recieve", list1);
		pdfOrder.setState(4);
		List list2 = findOrder1(pdfOrder);
		resultMap.put("onDeal", list2);
		pdfOrder.setState(5);
		List list3 = findOrder1(pdfOrder);
		resultMap.put("history", list3);
		return resultMap;
	}

	public List<Map> findOrder1(PdfOrder pdfOrder) {
		List<Map> resultList = new ArrayList();
		List<Map> unreadList = new ArrayList<>();
		List<Map> readList = new ArrayList();
		if (pdfOrder.getState() == 3 || pdfOrder.getState() == 4) {
			resultList = pdfOrderDao.getFirstData(pdfOrder);
		} else if (pdfOrder.getState() == 2) {
			resultList = pdfOrderDao.getUnRecieve(pdfOrder);
		} else if (pdfOrder.getState() == 5) {
			resultList = pdfOrderDao.getHistoryOrder(pdfOrder);
		}
		for (int i = 0; i < resultList.size(); i++) {
			List<User> userList = new ArrayList<>();
			List<User> sendUser = new ArrayList();
			List<User> confirmUser = new ArrayList<>();
			String ids = "";
			String[] arr = {};
			if (resultList.get(i).get("principal") != null) {
				ids = String.valueOf(resultList.get(i).get("principal"));
				arr = ids.split(",");
				userList = pdfOrderDao.getUserByIds(arr);
				resultList.get(i).put("principals", userList);
			}
			if (resultList.get(i).get("sendOrderUser") != null) {
				ids = String.valueOf(resultList.get(i).get("sendOrderUser"));
				arr = ids.split(",");
				sendUser = pdfOrderDao.getUserByIds(arr);
				resultList.get(i).put("userName", sendUser.get(0).getName());
				resultList.get(i).put("phone", sendUser.get(0).getPhone());
			}
			if (resultList.get(i).get("confirmUser") != null) {
				ids = String.valueOf(resultList.get(i).get("confirmUser"));
				arr = ids.split(",");
				confirmUser = pdfOrderDao.getUserByIds(arr);
				resultList.get(i).put("confirmUser", confirmUser.get(0).getName());
			}
			PdfOrderDeal pdfOrderDeal = new PdfOrderDeal();
			pdfOrderDeal.setOrderId(String.valueOf(resultList.get(i).get("id")));
			pdfOrderDeal.setType(1);
			pdfOrderDeal.setRecieveUser(pdfOrder.getPrincipal());
			int num = pdfOrderDealDao.countUnRead(pdfOrderDeal);
			Map map = pdfOrderDealDao.getUnRead(pdfOrderDeal);
			if (map == null) {
				readList.add(resultList.get(i));
			} else {
				Object recieveUser = map.get("recieve_user");
				if (recieveUser != null) {
					resultList.get(i).put("hasUnRead", true);
					resultList.get(i).put("sendDate", map.get("send_date"));
					unreadList.add(resultList.get(i));
				} else {
					readList.add(resultList.get(i));
				}
			}

		}
		// 返回数据按照出生日期降序排 (我比较懒，方法头就不写了~~)
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Collections.sort(unreadList, new Comparator<Map>() {
			@Override
			public int compare(Map arg0, Map arg1) {
				int mark = 1;
				try {
					Date date0 = sdf.parse(String.valueOf(arg0.get("sendDate")));
					Date date1 = sdf.parse(String.valueOf(arg1.get("sendDate")));
					if (date0.getTime() > date1.getTime()) {
						mark = -1;
					}
					if (arg0.get("sendDate").equals(arg1.get("sendDate"))) {
						mark = 0;
					}
				} catch (ParseException e) {
					logger.error("日期转换异常", e);
					e.printStackTrace();
				}
				return mark;
			} // compare
		});
		unreadList.addAll(readList);
		return unreadList;
	}

	/**
	 * 维修工人查看工单处理记录
	 * 
	 * @param pdfOrder
	 * @return
	 */
	public List<Map> getOrderDeal(PdfOrder pdfOrder) {
		PdfOrderDeal pdfOrderDeal = new PdfOrderDeal();
		pdfOrderDeal.setOrderId(pdfOrder.getId());
		pdfOrderDeal.setSendUser(pdfOrder.getPrincipal());
		pdfOrderDeal.setRecieveUser(pdfOrder.getPrincipal());
		List<Map> list = pdfOrderDealDao.getOrderDeal(pdfOrderDeal);
		return list;
	}

	/**
	 * 根据区域获取设备列表
	 */
	public List<Map> getDevByOrg(TOrg tOrg) {
		return pdfOrderDao.getDevByOrg(tOrg);
	}

	/**
	 * 获取报警人集合
	 */
	public List<Map> getSendUserList(String orgId) {
		Map map = new HashMap();
		map.put("orgId", orgId);
		return pdfOrderDao.getSendUserList(map);
	}

	// 维保人员
	public List<MapEntity> maintenUserList() {

		return pdfOrderDao.maintenUserList();
	}

	// 报警次数详情
	public List<MapEntity> alarmCountDetail(String chId) {

		return pdfOrderDao.alarmCountDetail(chId);
	}

	// 人工添加工单
	@Transactional(readOnly = false)
	public void addOrderByPerson(PdfOrder pdfOrder) {

		this.addOrderByWx(pdfOrder);
		this.sendOrder(pdfOrder);

	}

	public List<MapEntity> maintenanceList(String id) {

		List<MapEntity> maintenanceList = pdfOrderDao.maintenanceList(id);
		return maintenanceList;
	}

	public MapEntity getVedioLogByChId(String chId) {

		MapEntity entity = new MapEntity();
		List<MapEntity> vedioLogByChId = pdfOrderDao.getVedioLogByChId(chId);
		for (MapEntity mapEntity : vedioLogByChId) {
			String alarmLogId = (String) mapEntity.get("id");
			mapEntity.put("getVediorecorderByAlarmLogId", pdfOrderDao.getVediorecorderByAlarmLogId(alarmLogId));
		}
		entity.put("getVedioLogByChId", vedioLogByChId);
		return entity;

	}

}
