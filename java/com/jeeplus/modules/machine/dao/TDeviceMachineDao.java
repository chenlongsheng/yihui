/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.machine.dao;

 

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.machine.entity.TDevChannel;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
 

/**
 * 数据配置DAO接口
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface TDeviceMachineDao extends CrudDao<TDeviceMachine> {
	
	
	public List<MapEntity> tDeviceMachineList(@Param("orgId") String orgId,@Param("devId") String devId);
	
	public List<MapEntity> tChannelMachineList(@Param("orgId") String orgId,@Param("devId") String devId);
	
	 
	
	public List<MapEntity> allVideoList(@Param("orgId") String orgId,@Param("userId") String userId);
	
	void deleteUserVideo(@Param("userId") String userId);
	
	void insertPdfUserId(@Param("chId") String chId,@Param("userId") String userId);
	
	public List<MapEntity>  getHistoryList(@Param("orgId") String orgId,@Param("time") String time,@Param("devType")String devType, @Param("chType")String chType ) ;
	
	public List<MapEntity>  getCodeList();
	
	
	public List<MapEntity> getDeviceCodeByorgId(@Param("orgId") String orgId);

	
	  void updateDeviceCodeByDevType(@Param("devType") String devType);
    
    
    void updateDeviceCodeById(@Param("id") String id,@Param("notUse")String notUse, @Param("orderNo")String orderNo);
    
    List<MapEntity>  getDeviceCodeList(@Param("devType") String devType);
}