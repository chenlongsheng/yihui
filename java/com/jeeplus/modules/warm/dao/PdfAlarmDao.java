package com.jeeplus.modules.warm.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface PdfAlarmDao {

	List<Map<String, Object>> getAlarmListGroupByChType(@Param("code")String code);

	List<Map<String, Object>> getAlarmListByChType(@Param("chType")int chType, @Param("typeId")int typeId, @Param("code")String code);

	void confirmAlarm(@Param("id")String id);

	List<MapEntity> getAlarmListByPdfId(@Param("code")String code);
	


}
