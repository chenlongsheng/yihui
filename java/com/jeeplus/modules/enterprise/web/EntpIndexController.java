package com.jeeplus.modules.enterprise.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.jeeplus.common.mapper.JsonMapper;
import com.jeeplus.common.mq.rabbitmq.RabbitMQConnection;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.IdGen;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.enterprise.entity.TChGroup;
import com.jeeplus.modules.enterprise.entity.TPreAlarmSettings;
import com.jeeplus.modules.enterprise.service.TAlarmLogService;
import com.jeeplus.modules.enterprise.service.THistoryDataFinalService;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.enterprise.service.TPreAlarmSettingsService;

import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.THistoryDataFinal;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.entity.TRealData;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.settings.service.TRealDataService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.SystemService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.rabbitmq.client.Channel;


@Controller
@RequestMapping(value = "enterprise/")
public class EntpIndexController {

	@Autowired
	TChannelService channelService;

	@Autowired
	TOrgService orgService;

	@Autowired
	THistoryDataFinalService tHistoryDataFinalService;

	@Autowired
	TPreAlarmSettingsService preAlarmSettingsService;
    
	// @Autowired
	// TChGroupService chGroupService;
	@Autowired
	TAlarmLogService alarmLogService;

	@Autowired
	SystemService systemService;

	@Autowired
	TOperLogService operLogService;

	@Autowired
	RabbitMQConnection rabbitMqConnection;

	// 获取当前企业ID 999
	@RequestMapping(value = { "getEnterpriseId" })
	@ResponseBody
	public String getEnterpriseId( Model model) {
		User currentUser = UserUtils.get("1");
		String orgId = currentUser.getArea().getId();
        
		return ServletUtils.buildRs(true, "当前企业区域id", orgId);
	}

	// 1加载场景数据接口 999   
	@RequestMapping(value = { "getSenceData" })
	@ResponseBody
	public String getSenceData(TOrg org, Model model, String eid) {
		
		System.out.println(org.toString()+"====getsencedata");
		
//		org.setId("7579");
		User currentUser = UserUtils.get("1");
		Long orgId = Long.parseLong(currentUser.getArea().getId());
		if (eid != null) {
			orgId = Long.parseLong(eid);
		}
		List<Map<String, Object>> orgList = preAlarmSettingsService.getOrgTreeByOrgId(orgId);
		List<Map<String, Object>> weekAlarmLog = alarmLogService.getWeekAlarmLog();
        
		Long firstSenceId = Long.parseLong(org.getId());
        
		List<Map<String, Object>> channelList = preAlarmSettingsService.getChannelListByScenceId(firstSenceId);//wuyong
        
		List<TChGroup> chGroupList = preAlarmSettingsService.getChGroupListBySenceId(firstSenceId);//wuyong
		
		TOrg selectSceneObj = preAlarmSettingsService.getOrg(firstSenceId);
        
		List<TPreAlarmSettings> pasList = preAlarmSettingsService.getSencePrealarmSettings(firstSenceId, null, null);//wuyong

		Map<String, Object> retDataObject = new HashMap<String, Object>();
		retDataObject.put("selectedSence", firstSenceId);
		retDataObject.put("selectedSenceName", selectSceneObj.getName());
		retDataObject.put("selectedSenceChannellist", channelList);
		retDataObject.put("selectedSenceChGroupList", chGroupList);
		retDataObject.put("orgList", orgList);
		retDataObject.put("pasList", pasList);
		model.addAttribute("weekAlarmLog", weekAlarmLog);
		return ServletUtils.buildRs(true, "", retDataObject);
	}

	// 2加载场景通道曲线图数据接口 999
	@RequestMapping(value = { "getSenceHistoryChartData" })
	@ResponseBody
	public String getSenceHistoryChartData(TOrg org, Model model, String loadDataRange) {
		org.setId("7579");
		List<Map<String, Object>> channelList = preAlarmSettingsService
				.getChannelListByScenceId(Long.parseLong(org.getId()));

		List<THistoryDataFinal> hdatas = tHistoryDataFinalService
				.getChannelHistoryChartDataBySenceId(Long.parseLong(org.getId()), loadDataRange);
		Map<String, Object> datamap = new HashMap<String, Object>();
		datamap.put("channellist", channelList);
		datamap.put("hdatas", hdatas);
		return ServletUtils.buildRs(true, "", datamap);
	}

	// 3模拟量通道单通道数据曲线图 999
	@RequestMapping(value = { "getSingleAnalogChannelHistoryChartData" })
	@ResponseBody
	public String getSingleAnalogChannelHistoryChartData(TChannel channel, Model model, String loadDataRange) {
		List<THistoryDataFinal> datas = tHistoryDataFinalService
				.getChannelHistoryChartData(Long.parseLong(channel.getId()), loadDataRange);
		return ServletUtils.buildRs(true, "单通道数据曲线图", datas);
	}

	// 5基地详细页面， 加载基地信息、基地下 所有实时数据接口 999
	@RequestMapping(value = { "getSenceRealtimeData" })
	@ResponseBody
	public String getSenceRealtimeData(TOrg org, Model model) {

		if (StringUtils.isBlank(org.getId())) {
			org.setId("7579");
		}
		org = orgService.get(org.getId());
		System.out.println(org);
		List<Map<String, Object>> orgList = preAlarmSettingsService.getOrgTreeByOrgId(Long.parseLong(org.getId()));
		List<Map<String, Object>> realDataList = operLogService.getRealDataListByOrgId(Long.parseLong(org.getId()));

		Map<String, Object> retData = new HashMap<String, Object>();
		retData.put("org", org);
		retData.put("orgList", orgList);
		retData.put("realDataList", realDataList);
		return ServletUtils.buildRs(true, " 所有实时数据接口 ", retData);
	}

	// 6企业详情页面， 加载企业信息、企业下所有基地、及地下所有场景的实时数据接口 999   首页---
	@RequestMapping(value = { "enterpriseRealtimeData" })
	@ResponseBody
	public String enterpriseRealtimeData(TOrg org, Model model) {
		org.setId("7579");
		Map<String, Object> retData = new HashMap<String, Object>();
		List<Map<String, Object>> orgList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> realDataList = new ArrayList<Map<String, Object>>();

		org = orgService.get(org.getId());
		String likeNameSearch = org.getName();
		if (likeNameSearch != null && !likeNameSearch.trim().equals("")) {
			List<TOrg> searchOrgList = operLogService.getOrgListByLikeName(org);
			List<TOrg> resultOrgList = new ArrayList<TOrg>();
			List<String> codelist = new ArrayList<String>();
			for (TOrg eo : searchOrgList) {
				boolean hasSubCodeExist = false;
				for (int i = 0; i < codelist.size(); i++) {
					String code = codelist.get(i);
					String orgCode = eo.getCode();
					if (code.indexOf(orgCode) != -1) {
						hasSubCodeExist = true;
						codelist.remove(i);
						break;
					}
				}
				if (!hasSubCodeExist) {
					codelist.add(eo.getCode());
				}
			}
			for (String code : codelist) {
				for (TOrg eo : searchOrgList) {
					if (eo.getCode().equals(code)) {
						resultOrgList.add(eo);
					}
				}
			}
			for (TOrg ro : resultOrgList) {
				List<Map<String, Object>> tree = preAlarmSettingsService.getOrgTreeByOrgId(Long.parseLong(ro.getId()));//
				List<Map<String, Object>> addOrg = new ArrayList<Map<String, Object>>();
//				for (Map<String, Object> map : tree) {
//              
//					if (map.get("type").equals("7")) {
//						String parentId = map.get("parent_id").toString();
//						boolean hasParentNode = false;
//						for (Map<String, Object> map2 : tree) {
//							if (parentId.equals(map2.get("parent_id"))) {
//								hasParentNode = true;
//								break;
//							}
//						}
//						for (Map<String, Object> map2 : addOrg) {
//							if (parentId.equals(map2.get("parent_id"))) {
//								hasParentNode = true;
//								break;
//							}
//						}
//						if (!hasParentNode) {
//							Map<String, Object> orgmap = operLogService.getOrgById(map.get("id").toString());
//							addOrg.add(orgmap);
//						}
//					}
//				}
				tree.addAll(addOrg);
				orgList.addAll(tree);
				realDataList.addAll(operLogService.getRealDataListByOrgId(Long.parseLong(ro.getId())));
			}
		} else if (org.getId() != null) {
			orgList = preAlarmSettingsService.getOrgTreeByOrgId(Long.parseLong(org.getId()));
			realDataList = operLogService.getRealDataListByOrgId(Long.parseLong(org.getId()));
		}

		retData.put("org", org);
		retData.put("orgList", orgList);
		retData.put("realDataList", realDataList);

		return ServletUtils.buildRs(true, "", retData);

	}

	// 9下发控制命令 999
	@RequestMapping(value = { "sendChannelControlCommand" })
	@ResponseBody
	public String sendChannelControlCommand(TChannel channel, Long ctrlType, Long minute) {

		channel = channelService.get(channel.getId());
		controlSingleChannelCommand(channel, ctrlType, minute);
		return ServletUtils.buildRs(true, "控制成功!", null);
	}

	// 11关闭 场景下 所有通道 999
	@RequestMapping(value = { "closeAllSenceDevice" })
	@ResponseBody
	public String closeAllSenceDevice(TOrg org, Long minute) {
		List<Map<String, Object>> channels = preAlarmSettingsService
				.getChannelListByScenceId(Long.parseLong(org.getId()));
		for (Map<String, Object> map : channels) {
			TChannel channel = new TChannel();
			channel.setId(map.get("id").toString());
			channel.setChType(Long.parseLong(map.get("chType").toString()));
			channel.setTypeId(Long.parseLong(map.get("typeId").toString()));
			channel.setDevId(Long.parseLong(map.get("devId").toString()));
			channel.setNotUse(Long.parseLong(map.get("notUse").toString()));
			controlSingleChannelCommand(channel, 0L, minute);
		}
		return ServletUtils.buildRs(true, "控制成功!", null);
	}

	// 开启场景下 所有通道 999
	@RequestMapping(value = { "openAllSenceDevice" })
	@ResponseBody
	public String openAllSenceDevice(TOrg org, Long minute) {
		List<Map<String, Object>> channels = preAlarmSettingsService.getChannelListByScenceId(Long.parseLong(org.getId()));
		for (Map<String, Object> map : channels) {
			TChannel channel = new TChannel();
			channel.setId(map.get("id").toString());
			channel.setChType(Long.parseLong(map.get("chType").toString()));
			channel.setTypeId(Long.parseLong(map.get("typeId").toString()));
			channel.setDevId(Long.parseLong(map.get("devId").toString()));
			channel.setNotUse(Long.parseLong(map.get("notUse").toString()));
			controlSingleChannelCommand(channel, 1L, minute);
		}
		return ServletUtils.buildRs(true, "控制成功!", null);
	}

	private boolean controlSingleChannelCommand(TChannel channel, Long ctrlType, Long minute) {
		if (4 != channel.getTypeId())
			return false;
		if (channel.getNotUse() == null)
			return false;
		if (channel.getNotUse() == 1)
			return false;

		Channel rmqChannel = null;
		try {
			// 创建一个通道
//			rmqChannel = rabbitMqConnection.createChannel();

			String message = "{\"dev_id\":" + channel.getDevId() + ",\"ch_id\":" + channel.getId()
					+ ",\"cmd_type\":1,\"seq_uuid\":\"" + IdGen.uuid() + "\",\"params\":{\"off_on\":" + ctrlType
					+ ",\"open_minute\":" + minute + "}}";
			// 发送消息到队列中
			rmqChannel.basicPublish("org.35", "dev.command.35.01", null, message.getBytes("UTF-8"));
			System.out.println("Producer Send +'" + message + "'  to " + "dev.command.35.01");

			// String updateMessage = "{\"change_type\":1,\"change_time\":\"2017-03-17
			// 13:17:30\",\"object_type\":\"gsu3000\",\"object_id\":\"\"}";
			// rmqChannel.basicPublish("org.35", "db.datachange.35.01", null,
			// updateMessage.getBytes("UTF-8"));
			// System.out.println("Producer Send +'" + updateMessage + "' to " +
			// "db.datachange");

			// 关闭通道和连接
			rmqChannel.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			try {
				rmqChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (TimeoutException e1) {
				e1.printStackTrace();
			}
			return false;
		} catch (TimeoutException e) {
			e.printStackTrace();
			try {
				rmqChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (TimeoutException e1) {
				e1.printStackTrace();
			}
			return false;
		}

	}
}
