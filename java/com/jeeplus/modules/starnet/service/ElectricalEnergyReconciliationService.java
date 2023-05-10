package com.jeeplus.modules.starnet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.starnet.dao.ElectricalEnergyReconciliationDao;
import com.jeeplus.modules.starnet.entity.ElectricalEnergyReconciliation;

@Service
@Transactional(readOnly = false)
public class ElectricalEnergyReconciliationService extends CrudService<ElectricalEnergyReconciliationDao, ElectricalEnergyReconciliation> {

	public Long getLoopEnergyReconciliationSum(String loopId) {
		return dao.getLoopEnergyReconciliationSum(loopId);
	}

}
