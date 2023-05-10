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
public interface PowerAnalysisDao extends CrudDao<MapEntity> {

	List<MapEntity> analysisReports(@Param("chIds") String chIds, @Param("beginTime") String beginTime,@Param("endTime") String endTime, @Param("numList") List<String> numList);

	List<MapEntity> daypartData(@Param("chIds") String chIds, @Param("beginTime") String beginTime,@Param("endTime") String endTime);
	
    void deleteChanger();
    
	void modifyCharges(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("state") String state,@Param("price") String price);
	
	List<MapEntity> selectCharges();
	
	List<MapEntity> getBreauOrgId(@Param("beginTime") String beginTime,@Param("endTime") String endTime);
	
	List<MapEntity> analysisReportByLoopId(@Param("loopId")String loopId, @Param("startTime")String startTime, @Param("endTime")String endTime);
	
	List<MapEntity> analysisReportByUnitId(@Param("unitId")String unitId, @Param("startTime")String startTime, @Param("endTime")String endTime);

	MapEntity electricReportByLoopId(@Param("loopId")String loopId, @Param("startTime")String startTime,@Param("endTime") String endTime);
	
	MapEntity electricReportByUnitId(@Param("unitId")String unitId, @Param("startTime")String startTime, @Param("endTime")String endTime);

	MapEntity getStarElectricityUnit(@Param("unitId")String unitId);
	
	List<MapEntity> getLoopChannel(String orgType);
	
	List<MapEntity> getUnitLoopList();

	List<MapEntity> getPdfList();

	List<MapEntity> getLoopMonthConsumption(@Param("month") String month);

	List<MapEntity> getUnitList();
	
	
	
	
	
	List<MapEntity> getLineLossData(@Param("beginTime") String beginTime,@Param("endTime") String endTime, @Param("orgType") String orgType);
	
	/*
	List<MapEntity> getLineLossDataBegin(@Param("ids")String ids);
	
	List<MapEntity> getLineLossDataEnd(@Param("ids")String ids);
	*/
	
	List<MapEntity> getHistoryData(@Param("chId") String chId,@Param("historyTime") String historyTime);
	
	List<Map<String, String>> getLineLossMaxDataChIdAndHistoryTime(@Param("beginTime")String beginTime, @Param("endTime")String endTime,@Param("orgType")String orgType);

	List<Map<String, String>> getLineLossMinDataChIdAndHistoryTime(@Param("beginTime")String beginTime, @Param("endTime")String endTime,@Param("orgType")String orgType);

	List<MapEntity> getOutLoopList();

}