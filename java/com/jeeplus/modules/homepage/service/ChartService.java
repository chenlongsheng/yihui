package com.jeeplus.modules.homepage.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.ChartDao;
import com.jeeplus.modules.homepage.dao.StatisticsDao;
import com.jeeplus.modules.homepage.entity.*;
import com.jeeplus.modules.settings.dao.TOrgDao;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.utils.UserUtils;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018-12-20.
 */
@Service
@Transactional(readOnly = true)
public class ChartService extends CrudService<ChartDao, Statistics> {
	@Autowired
	ChartDao chartDao;

	public MapEntity doorSecurityList(String devType, String bureauId, String orgId, String beginDate, String endDate) {
		String userId = UserUtils.getUser().getId();
		MapEntity entity = new MapEntity();

		if (devType.equals("162")) {// 门磁
			List<MapEntity> alarmTypeList = chartDao.alarmTypeList(userId, devType, bureauId, orgId, "1"); // 当天或者单月新增报警
			List<MapEntity> saveDays = chartDao.saveDays(userId, devType, bureauId, orgId);// 安全天数
			List<MapEntity> doorRateList = chartDao.doorRateList(userId, devType, bureauId, orgId); // 门磁开启率
			List<MapEntity> devTypeTop10 = chartDao.devTypeTop10(userId, devType, bureauId, orgId, beginDate, endDate);// 开门次数top10
			List<MapEntity> avgDoorAlarmList = chartDao.avgDoorAlarmList(userId, devType, bureauId, orgId, beginDate,
					endDate);// 门磁平均报警次数
			entity.put("alarmTypeList", alarmTypeList);
			entity.put("devTypeRate", doorRateList);
			entity.put("saveDays", saveDays);
			entity.put("devTypeTop10", devTypeTop10);
			entity.put("avgDoorAlarmList", avgDoorAlarmList);
		} else if (devType.equals("172") || devType.equals("168")) {// 烟感,,水浸
			List<MapEntity> todayAlarmTypeList = chartDao.alarmTypeList(userId, devType, bureauId, orgId, "0"); // 当天新增报警
			List<MapEntity> alarmTypeList = chartDao.alarmTypeList(userId, devType, bureauId, orgId, "1"); // 当天或者单月新增报警
			List<MapEntity> devTypeRateList = chartDao.devTypeRate(userId, devType, bureauId, orgId); // 设备类型开启率
			List<MapEntity> devTypeOffRateListTop10 = chartDao.devTypeOffRateListTop10(userId, devType, bureauId,
					orgId);// 设备类型离线率集合
			List<MapEntity> saveDays = chartDao.saveDays(userId, devType, bureauId, orgId);// 安全天数
			List<MapEntity> devTypeTop10 = chartDao.devTypeTop10(userId, devType, bureauId, orgId, beginDate, endDate);// 报警次数top10
			List<MapEntity> electricityThan = chartDao.getElectricityThan(userId, devType, bureauId, orgId);//获取电量百分比
			entity.put("todayAlarmTypeList", todayAlarmTypeList);
			entity.put("alarmTypeList", alarmTypeList);
			entity.put("devTypeRate", devTypeRateList);
			entity.put("devTypeOffRateListTop10", devTypeOffRateListTop10);
			entity.put("saveDays", saveDays);
			entity.put("devTypeTop10", devTypeTop10);// 烟感报警次数top10
			entity.put("electricityThan", electricityThan);
		} else if (devType.equals("169")) {// 温湿度图表
			List<MapEntity> alarmTypeList = chartDao.alarmTypeList(userId, devType, bureauId, orgId, "1"); // 单月新增报警
			List<MapEntity> saveDays = chartDao.saveDays(userId, devType, bureauId, orgId);// 安全天数
			List<MapEntity> getHumitures = chartDao.getHumitures(userId, devType, bureauId, orgId);//// 温湿度最高和最低区间值
			List<MapEntity> getTemperatureTop10 = chartDao.getHumituresTop10(userId, devType, "101", bureauId, orgId);// 温度报警top10
			List<MapEntity> getHumidityTop10 = chartDao.getHumituresTop10(userId, devType, "102", bureauId, orgId);// 湿度报警top10
			entity.put("alarmTypeList", alarmTypeList);
			entity.put("getHumitures", getHumitures);
			entity.put("getTemperatureTop10", getTemperatureTop10);
			entity.put("getHumidityTop10", getHumidityTop10);
			entity.put("saveDays", saveDays);
		}
		return entity;
	}

}
