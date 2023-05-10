/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

/**
 * 数据配置DAO接口
 * 
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface EnergyAnalysisDao extends CrudDao<MapEntity> {

	List<MapEntity> tDeviceVoltageList(@Param("devType") String devType);

	List<MapEntity> tDeviceIncomingList(@Param("orgId") String orgId);

	// 柱状型
	List<MapEntity> historyListByHour(@Param("devId") String devId, @Param("time") String time,@Param("ids") String ids, @Param("type") String type);

	// 成本
	public List<MapEntity> historyListBymoney(@Param("devId") String devId, @Param("time") String time,	@Param("ids") String ids, @Param("type") String type);

	String getChId(@Param("devId") String devId, @Param("pId") String pId);




	// 企业看板今日今月今年
	List<String> historyTrendByTiPrefix(@Param("devId") String devId, @Param("time") String time,@Param("ids") String ids, @Param("type") String type);
	
	// 成本 企业看板今日今月今年/
	List<String> historyTrendBymoney(@Param("devId") String devId, @Param("time") String time, @Param("ids") String ids,@Param("type") String type);

	void insertStarEnergy(@Param("startTime") String startTime, @Param("endTime") String endTime,@Param("state") String state, @Param("price") String price);

	String getPrice(@Param("time") String time, @Param("value") String value);
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	List<Map<String, Object>> historyListByLoopId(@Param("orgId")String orgId, @Param("type") String type, @Param("time") String time);

	List<Map<String, Object>> historyListByUnitId(@Param("unitId")String unitId, @Param("type") String type, @Param("time") String time);
	

	String dayMonthYear(@Param("orgId")String orgId, @Param("type")int type);
	
	String preDayMonthYear(@Param("orgId")String loopId, @Param("type")int type);

	// 条形线
	
	List<MapEntity> historyPicsByLoopId(@Param("orgId") String orgId, @Param("time") String time, @Param("type") String type);

	List<MapEntity> historyPicsByUnitId(@Param("unitId") String unitId, @Param("time") String time, @Param("type") String type);
	

	//获取区域列表
	List<MapEntity> getOrgListByOrgId(@Param("orgId")String orgId);

	//获取回路耗电能
	String getSumEnergyByLoopIdAndTime(@Param("loopId")String loopId, @Param("startTime")String startTime, @Param("endTime")String endTime);

	
	
	String dayMonthYearByLoopId(@Param("loopId")String loopId, @Param("type")int type);
	
	String dayMonthYearByUnitId(@Param("unitId")String unitId, @Param("type")int type);

	String preDayMonthYearByLoopId(@Param("loopId")String loopId, @Param("type")int type);

	String preDayMonthYearByUnitId(@Param("unitId")String unitId, @Param("type")int type);

	








	
}