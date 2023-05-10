package com.jeeplus.modules.starnet.dao;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface MonthTotalDao extends CrudDao<MapEntity> {

	Double getMonthTotalValue(@Param("month") String month);

	void setMonthTotalValue(@Param("month") String month, @Param("value") String value);

	void addMonthTotalValue(@Param("month")String month, @Param("value")String value);
	
}
