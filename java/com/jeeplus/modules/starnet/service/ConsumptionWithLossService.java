package com.jeeplus.modules.starnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.common.utils.IdGenSnowFlake;
import com.jeeplus.modules.starnet.dao.ConsumptionWithLossDao;
import com.jeeplus.modules.starnet.entity.ConsumptionWithLoss;

@Service
public class ConsumptionWithLossService {

	@Autowired
	ConsumptionWithLossDao consumptionWithLossDao;

	public void updateMonthLossData(String orgId, String month, double consumption,double consumptionWithLoss) {
		
		ConsumptionWithLoss lossData = new ConsumptionWithLoss();
		lossData.setLoopOrgId(orgId);
		lossData.setConsumptionWithLoss(consumptionWithLoss);
		lossData.setConsumption(consumption);
		lossData.setMonth(month);
		
		ConsumptionWithLoss data = consumptionWithLossDao.getLossDataByLoopIdAndMonth(lossData);
		if(data != null) {
			lossData.setId(data.getId());
			consumptionWithLossDao.update(lossData);
		} else {
			lossData.setId(IdGenSnowFlake.uuid().toString());
			consumptionWithLossDao.insert(lossData);
		}
		
	}  
	
	
	public ConsumptionWithLoss getLossDataByLoopIdAndMonth(ConsumptionWithLoss data) {
		return consumptionWithLossDao.getLossDataByLoopIdAndMonth(data);
	}
	
}
