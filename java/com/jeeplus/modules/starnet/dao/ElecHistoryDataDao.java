package com.jeeplus.modules.starnet.dao;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.starnet.entity.ElecHistoryData;

@MyBatisDao
public interface ElecHistoryDataDao extends  CrudDao<MapEntity> {

	void add(ElecHistoryData ehd);

	Long getMaxElecHistoryDataIdLessThan10000000();

	Double getSumValueByTimeBetween(@Param("loopId")String loopId, @Param("lastWeekStart")String lastWeekStart, @Param("lastWeekToday")String lastWeekToday);

}
