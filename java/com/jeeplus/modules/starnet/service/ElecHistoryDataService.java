package com.jeeplus.modules.starnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.modules.starnet.dao.ElecHistoryDataDao;
import com.jeeplus.modules.starnet.entity.ElecHistoryData;

@Service
public class ElecHistoryDataService {

	@Autowired
	ElecHistoryDataDao elecHistoryDataDao;
	
	
	public void add(ElecHistoryData ehd) {
		elecHistoryDataDao.add(ehd);
	}


	public Long getMaxElecHistoryDataIdLessThan10000000() {
		return elecHistoryDataDao.getMaxElecHistoryDataIdLessThan10000000();
	}


	public Double getSumValueByTimeBetween(String loopId, String lastWeekStart, String lastWeekToday) {
		return elecHistoryDataDao.getSumValueByTimeBetween(loopId,lastWeekStart,lastWeekToday);
	}

}
