/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao; 

/**
 * 数据配置DAO接口
 * 
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface PowerDataDao extends CrudDao<MapEntity> {

	List<MapEntity> powerDataList(@Param("devId") String devId, @Param("time") String time,@Param("ids") String ids, @Param("type") String type);
	 
	List<MapEntity> powerCodes();
	 
	void updateThresholdNum(@Param("id") String id, @Param("num") String num);
	 
	
	
	
	
	
	
	

	List<MapEntity> getDayDataList(@Param("orgId")String orgId,@Param("time")String time);
	
	List<MapEntity> extremalDatas(@Param("orgId") String orgId,@Param("startTime") String startTime, @Param("endTime") String endTime,@Param("chType") String chType);

}