/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.THistoryDataFinal;

/**
 * 历史数据DAO接口
 * @author ywk
 * @version 2017-06-08
 */
@MyBatisDao
public interface THistoryDataFinalDao extends CrudDao<THistoryDataFinal> {

	List<THistoryDataFinal> getChannelHistoryChartData(@Param("id")Long id, @Param("loadDataRange")String loadDataRange);

	List<THistoryDataFinal> getChannelHistoryChartDataBySenceId(@Param("id")Long id,@Param("loadDataRange")String loadDataRange);

	List<THistoryDataFinal> getChannelHistoryChartDataBySenceIdAndTime(@Param("id")Long id,@Param("startTime")String startTime, @Param("endTime")String endTime);

	
	
}