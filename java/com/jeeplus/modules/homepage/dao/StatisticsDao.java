package com.jeeplus.modules.homepage.dao;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.homepage.entity.SandTable;
import com.jeeplus.modules.homepage.entity.Statistics;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018-12-20.
 */
@MyBatisDao
public interface StatisticsDao extends CrudDao<Statistics> {

	List<MapEntity> orgList(@Param(value = "orgId") String orgId);

	// 设备启用率
	Statistics findDevTypeUse(Statistics statistics);

	// 设备启用率
	Statistics findDevTypeUse1(Statistics statistics);

	// 设备启用率
	Statistics findDevTypeUse2(Statistics statistics);

	// 报警类型统计
	JSONObject findAlarmType(@Param(value = "orgId") String orgId, @Param(value = "userId") String userId);

	// 根据地区查询配电房总个数
	String countPDF(@Param(value = "orgId") String orgId, @Param(value = "type") String type,
			@Param(value = "userId") String userId);

	// 根据地区查询报警配电房的总个数
	String countAlarmPDF(@Param(value = "orgId") String orgId, @Param(value = "type") String type,
			@Param(value = "userId") String userId);

	// 查询最后报警时间
	Date findLastAlarm(@Param(value = "orgId") String orgId, @Param(value = "userId") String userId);

	// 查询门禁实时状态
	Statistics findAccessOpen(Statistics statistics);

	// 温湿度排行
	List<JSONObject> findHumitureRanking(Statistics statistics);

	// 报警趋势图
	JSONObject findAlarmTrend(@Param(value = "orgId") String orgId, @Param(value = "timeStart") Date timeStart,
			@Param(value = "timeStop") Date timeStop, @Param(value = "userId") String userId);

	// 沙盘
	List<SandTable> sandTable(@Param(value = "orgId") String orgId);

	List<MapEntity> doorList(MapEntity entity);

	List<MapEntity> sandTable1(@Param(value = "orgId") String orgId, @Param(value = "userId") String userId);

	List<MapEntity> getOrgList();

	List<MapEntity> findDevTypeUse9(@Param(value = "orgId") String orgId);

	// 获取用户所有供电所
	List<MapEntity> getBureauListByHome(@Param(value = "userId") String userId);

	List<MapEntity> getOrgListByBureauId(MapEntity entity);

	// 非admin获取所属供电单位集合
	List<MapEntity> getBureauIds(@Param(value = "userId") String userId);

	// 获取单位所有上级供电单位
	List<MapEntity> getOrgListByPId(@Param(value = "pIds") String pIds);

}
