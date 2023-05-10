package com.jeeplus.modules.starnet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface LoadUnitAdjustProportionDao {

	List<MapEntity> loadAllLoopAdjustProportion();

	List<MapEntity> loadAllUnitAdjustStrategy();

	MapEntity getUnitLoopOrg(@Param("unitId")String unitId, @Param("orgId")String orgId);
	
}
