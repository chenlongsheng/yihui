package com.jeeplus.modules.starnet.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface EnergyOpenApiDao {

	String todayTotalEnergyConsumption(@Param("orgId")String orgId,@Param("day")String day);

	List<Map<String, Object>> getLoopVoltage(String loopId);

	List<Map<String, Object>> getLoopElectricity(String loopId);

	List<Map<String, Object>> getLoopByPdfId(String pdfId);

	List<Map<String, Object>> getLoopByUnitId(String unitId);

	List<Map<String, Object>> getAlarmList(String pdfId);

	List<Map<String, Object>> getLoopByOrgId(String orgId);

	String todayTotalEnergyConsumptionByUnitId(@Param("unitId")String unitId,@Param("day")String day);

	List<Map<String, Object>> deviceStateList();

	List<MapEntity> getDeviceDetails();

}
