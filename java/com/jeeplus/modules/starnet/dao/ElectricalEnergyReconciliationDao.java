package com.jeeplus.modules.starnet.dao;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.starnet.entity.ElectricalEnergyReconciliation;

@MyBatisDao
public interface ElectricalEnergyReconciliationDao extends CrudDao<ElectricalEnergyReconciliation> {

	Long getLoopEnergyReconciliationSum(@Param("loopId")String loopId);

}
