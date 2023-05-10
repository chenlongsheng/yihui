package com.jeeplus.modules.warm.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.modules.warm.dao.EdgeDeviceDao;

@Service
public class EdgeDeviceService {

	@Autowired
	EdgeDeviceDao edgeDeviceDao;
	
	public long getMaxAlarmLogId() {
		return edgeDeviceDao.getMaxAlarmLogId();
	}
	
	public long insertAlarmLog(Map<String,Object> alarm) {
		alarm.put("id", getMaxAlarmLogId());
		return edgeDeviceDao.insertAlarmLog(alarm);
	}

	public void insertAiAlarmLog(Map<String,Object> alarm) {
		edgeDeviceDao.insertAiAlarmLog(alarm);
	}


	public Map<String, Object> getChannelByCameraId(String cameraId) {
		return edgeDeviceDao.getChannelByCameraId(cameraId);
	}

	public Map<String, Object> getAiAlarmDetail(String alarmId) {
		return edgeDeviceDao.getAiAlarmDetail(alarmId);
	}

	public List<Map<String, Object>> getAiAlarmByType(int alarmType) {
		return edgeDeviceDao.getAiAlarmByType(alarmType);
	}

}
