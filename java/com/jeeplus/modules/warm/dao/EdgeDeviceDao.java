package com.jeeplus.modules.warm.dao;

import java.util.List;
import java.util.Map;

import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface EdgeDeviceDao {

	Map<String, Object> getChannelByCameraId(String cameraId);

	long insertAlarmLog(Map<String, Object> alarm);

	void insertAiAlarmLog(Map<String, Object> alarm);

	long getMaxAlarmLogId();

	Map<String, Object> getAiAlarmDetail(String alarmId);

	List<Map<String, Object>> getAiAlarmByType(int alarmType);

}











