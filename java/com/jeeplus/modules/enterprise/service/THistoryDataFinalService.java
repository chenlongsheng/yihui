/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.enterprise.dao.THistoryDataFinalDao;
import com.jeeplus.modules.settings.entity.THistoryDataFinal;

/**
 * 历史数据Service
 * @author ywk
 * @version 2017-06-08
 */
@Service
@Transactional(readOnly = true)
public class THistoryDataFinalService extends CrudService<THistoryDataFinalDao, THistoryDataFinal> {

	@Autowired
	THistoryDataFinalDao historyDataFinalDao;
	
	public THistoryDataFinal get(String id) {
		return super.get(id);
	}
	
	public List<THistoryDataFinal> findList(THistoryDataFinal tHistoryDataFinal) {
		return super.findList(tHistoryDataFinal);
	}
	
	public Page<THistoryDataFinal> findPage(Page<THistoryDataFinal> page, THistoryDataFinal tHistoryDataFinal) {
		return super.findPage(page, tHistoryDataFinal);
	}
	
	@Transactional(readOnly = false)
	public void save(THistoryDataFinal tHistoryDataFinal) {
		super.save(tHistoryDataFinal);
	}
	
	@Transactional(readOnly = false)
	public void delete(THistoryDataFinal tHistoryDataFinal) {
		super.delete(tHistoryDataFinal);
	}

	public List<THistoryDataFinal> getChannelHistoryChartData(Long id,
			String loadDataRange) {
		return historyDataFinalDao.getChannelHistoryChartData(id,loadDataRange);
	}

	public List<THistoryDataFinal> getChannelHistoryChartDataBySenceId(Long id,
			String loadDataRange) {
		return historyDataFinalDao.getChannelHistoryChartDataBySenceId(id,loadDataRange);
	}

	public List<THistoryDataFinal> getChannelHistoryChartDataBySenceIdAndTime(
			Long id, String startTime, String endTime) {
		return historyDataFinalDao.getChannelHistoryChartDataBySenceIdAndTime(id,startTime,endTime);
	}
	
	
	
}