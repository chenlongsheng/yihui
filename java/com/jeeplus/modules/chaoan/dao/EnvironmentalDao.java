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
public interface EnvironmentalDao extends CrudDao<TDeviceMachine> {
	
	
	
	public List<MapEntity> powerCodes(@Param("status") String status); 
	
	public List<MapEntity> tDeviceTypes(@Param("orgId") String orgId);

	public List<MapEntity> tDeviceListByType(@Param("devType") String devType,@Param("orgId") String orgId);

	public List<MapEntity> wenshiList(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
			@Param("devId") String devId);

	public List<MapEntity> ph2and10(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
			@Param("devId") String devId);

	public List<MapEntity> getConditioner(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
			@Param("devId") String devId);

	public List<MapEntity> gasConcentration(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
			@Param("devId") String devId);


	public List<MapEntity> IncomingCabinet(@Param("beginDate") String beginDate, @Param("endDate") String endDate,@Param("name") String name,
			@Param("devId") String devId, @Param("chTypes") String chTypes, @Param("chTypeList") List<String> chTypeList);
	
	public List<MapEntity> outgoingCabinet(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
			@Param("devId") String devId, @Param("chTypes") String chTypes, @Param("chTypeList") List<String> chTypeList);
	
	
	public List<MapEntity> changeCymbols(@Param("chTypes") String chTypes);
	

	public List<MapEntity> getPdfEntranceLogs(@Param("beginTime") String beginTime, @Param("endTime") String endTime,
			@Param("userName") String userName, @Param("employeeNo") String employeeNo);
	
	
	void insertPdfEntranceLog(@Param("user_name") String user_name,@Param("unlock_type") String unlock_type,@Param("picture_url") String picture_url,
			@Param("device_sn") String device_sn,
			@Param("result") String result,	@Param("employee_no") String employee_no,@Param("event_time") String event_time,@Param("channel_no") String channel_no);
	
	
	List<MapEntity> getMessageLogs(MapEntity entity);

	
}