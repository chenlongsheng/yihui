/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.enterprise.entity.TAlarmLog;

/**
 * 报警日志DAO接口
 * @author ywk
 * @version 2017-05-25
 */
@MyBatisDao
public interface TAlarmLogDao extends CrudDao<TAlarmLog> {

	List<MapEntity> findPageOfAlarmLog(MapEntity tAlarmLog);

	List<Map<String, Object>> getChannelAlarmLog(@Param("chId")String chId);

	List<Map<String, Object>> getTodayAlarmLog();

	List<Map<String, Object>> getWeekAlarmLog();
	
}