package com.jeeplus.modules.starnet.dao;

import java.util.List;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.starnet.entity.ElecUnitStrategy;

@MyBatisDao
public interface ElecUnitStrategyDao extends CrudDao<ElecUnitStrategy> {

	String getUnitIdByLoopOrgCode(String orgCode);


}
