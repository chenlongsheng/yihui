package com.jeeplus.modules.starnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.modules.starnet.dao.MonthTotalDao;

@Service
public class MonthTotalService {

	@Autowired
	MonthTotalDao monthTotalDao;
	
	public Double getMonthTotalValue(String month) {
		Double value = monthTotalDao.getMonthTotalValue(month);
		Double dvalue = 0.0;
		if(value != null) {
			dvalue = value;
		}
		return dvalue;
	}
	
	public void setMonthTotalValue(String month,String value) {
		Double oriValue = monthTotalDao.getMonthTotalValue(month);
		if(oriValue == null) {
			monthTotalDao.addMonthTotalValue(month,value);
		} else {
			monthTotalDao.setMonthTotalValue(month,value);	
		}
		
	}
	
}
