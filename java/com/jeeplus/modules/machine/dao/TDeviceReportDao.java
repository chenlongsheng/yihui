/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.machine.dao;

 

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
 

/**
 * 数据配置DAO接口
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface TDeviceReportDao extends CrudDao<TDeviceMachine> {
	
	
	public List<MapEntity> tDeviceRportList(@Param("name") String name);
	
	
	public List<MapEntity> tChannelRportList(@Param("devId") String devId,@Param("str") String str,@Param("time") String time);
	
	
	List<String> numberList();
}