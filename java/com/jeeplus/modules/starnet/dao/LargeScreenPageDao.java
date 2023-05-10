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
public interface LargeScreenPageDao extends CrudDao<MapEntity> {

	List<MapEntity> getChNames();

	List<MapEntity> getHistoryByChId(@Param("chId") String chId, @Param("time") String time);

	List<MapEntity> getOrgNums();

	List<MapEntity> getEnergys(@Param("time") String time);

	List<MapEntity> getAmounts(@Param("time") String time);

}