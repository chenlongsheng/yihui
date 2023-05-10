/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TAlarmPolicy;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TCodeType;

/**
 * 数据配置DAO接口
 * 
 * @author long
 * @version 2018-10-23
 */
@MyBatisDao
public interface TAlarmPolicyDao extends CrudDao<TAlarmPolicy> {
	// 获取级别和获取通道子类型3
	public List<TCode> codeList(@Param(value = "codeTypeId") String codeTypeId);

	// 通道主类型集合
	public List<TCodeType> codeTypeList();
	
	String devTypeByDevId(@Param(value = "devId") String devId);

	List<MapEntity> findTypeListByUserId(@Param(value = "userId") String userId, @Param(value = "typeId") String typeId,
			@Param(value = "chType") String chType,@Param(value = "devType") Integer devType);
	
	void deleteAlarmPolicy(@Param(value = "userId") String userId,@Param(value = "chId") String chId,@Param(value = "devType") String devType);
	
	void deleteAlarmPolicyByChId(@Param(value = "devIds") String devIds);
    //获取通道id集合做,添加修改
	List<String> getChIdByDevId(@Param(value = "devId") String devId, @Param(value = "typeId") String typeId,
			@Param(value = "chType") String chType, @Param(value = "chNo") String chNo);
	 //获取修改时 阈值展示
	List<MapEntity> getAlarmPolicyList(@Param(value = "devId") String devId,@Param(value = "typeId") String typeId,
			@Param(value = "chType") String chType);
	
	List<String> getUserOrgList(@Param(value = "userId") String userId);
	
	List<MapEntity> getDeviceByPolicy(@Param(value = "userId") String userId,@Param(value = "devType") String devType);
	
	List<String> getOrgPidsBypolicy(@Param(value = "userId") String userId,@Param(value = "devType") String devType);
	
	Set<MapEntity> getOrgList(@Param(value = "pIds") String pIds);
	
	void deletePolicyByDeviceIds(@Param(value = "devIds") String devIds);
}