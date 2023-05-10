/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.chaoan.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.machine.entity.TDeviceMachine;

/**
 * 数据配置DAO接口
 * 
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface RemoteDao extends CrudDao<TDeviceMachine> {

	public List<MapEntity> powerCodes(@Param("status") String status);
	
	public List<MapEntity>  getDeviceById(@Param("devType") String devType);
	
	public List<MapEntity>  getHistoryValue( MapEntity entity);
	
	

}