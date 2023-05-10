package com.jeeplus.modules.starnet.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.modules.starnet.dao.EnergyOpenApiDao;

@Service
public class EnergyOpenApiService {
	
	@Autowired
	EnergyOpenApiDao energyOpenApiDao; 

	public String todayTotalEnergyConsumption(String orgId,String day) {
		return energyOpenApiDao.todayTotalEnergyConsumption(orgId,day);
	}

	public List<Map<String, Object>> getLoopVoltage(String loopId) {
		return energyOpenApiDao.getLoopVoltage(loopId);
	}

	public List<Map<String, Object>> getLoopElectricity(String loopId) {
		return energyOpenApiDao.getLoopElectricity(loopId);
	}
	
	public List<Map<String, Object>> getLoopByPdfId(String pdfId) {
		return energyOpenApiDao.getLoopByPdfId(pdfId);
	}

	public List<Map<String, Object>> getLoopByUnitId(String unitId) {
		return energyOpenApiDao.getLoopByUnitId(unitId);
	}

	public List<Map<String, Object>> getAlarmList(String pdfId) {
		return energyOpenApiDao.getAlarmList(pdfId);
	}

	public List<Map<String, Object>> getLoopByOrgId(String orgId) {
		return energyOpenApiDao.getLoopByOrgId(orgId);
	}

	public String todayTotalEnergyConsumptionByUnitId(String unitId,String day) {
		return energyOpenApiDao.todayTotalEnergyConsumptionByUnitId(unitId,day);
	}

	public List<Map<String, Object>> deviceStateList() {
		return energyOpenApiDao.deviceStateList();
	}
	
	public List<MapEntity> getDeviceDetails(){
		
		return energyOpenApiDao.getDeviceDetails();
		
	}
	

}
