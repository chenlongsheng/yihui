/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.enterprise.entity.TAlarmLog;
import com.jeeplus.modules.enterprise.dao.TAlarmLogDao;

/**
 * 报警日志Service
 * @author ywk
 * @version 2017-05-25
 */
@Service
@Transactional(readOnly = true)
public class TAlarmLogService extends CrudService<TAlarmLogDao, TAlarmLog> {
	
	@Autowired
	TAlarmLogDao tAlarmLogDao;

	public TAlarmLog get(String id) {
		return super.get(id);
	}
	
	public List<TAlarmLog> findList(TAlarmLog tAlarmLog) {
		return super.findList(tAlarmLog);
	}
	
	public Page<TAlarmLog> findPage(Page<TAlarmLog> page, TAlarmLog tAlarmLog) {
		return super.findPage(page, tAlarmLog);
	}
	
	@Transactional(readOnly = false)
	public void save(TAlarmLog tAlarmLog) {
		super.save(tAlarmLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(TAlarmLog tAlarmLog) {
		super.delete(tAlarmLog);
	}

	public Page<MapEntity> findPageOfAlarmLog(Page<MapEntity> page,
			MapEntity mapEntity) {
		mapEntity.setPage(page);
		return page.setList(tAlarmLogDao.findPageOfAlarmLog(mapEntity));
	}

	public List<Map<String, Object>> getChannelAlarmLog(String chid) {
		return tAlarmLogDao.getChannelAlarmLog(chid);
	}

	public List<Map<String, Object>> getTodayAlarmLog() {
		return tAlarmLogDao.getTodayAlarmLog();
	}

	public List<Map<String, Object>> getWeekAlarmLog() {
		return tAlarmLogDao.getWeekAlarmLog();
	}
	
	
	
	
}