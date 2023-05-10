package com.jeeplus.modules.starnet.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.starnet.entity.ConsumptionWithLoss;

@MyBatisDao
public interface ConsumptionWithLossDao extends CrudDao<ConsumptionWithLoss>{

	ConsumptionWithLoss getLossDataByLoopIdAndMonth(ConsumptionWithLoss lossData);

}
