/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TChannel;

/**
 * 通道管理DAO接口
 * 
 * @author ywk
 * @version 2018-06-22
 */
@MyBatisDao
public interface TChannelDao extends CrudDao<TChannel> {
	// 获取设备位置集合
	public List<MapEntity> channelList(MapEntity entity);

	public MapEntity getChannel(@Param(value = "userType") Long id);

	public List<TChannel> findAllList();

	public List<MapEntity> orgList(@Param(value = "orgId") String orgId);

	public List<MapEntity> typeList();// 获取t_code集合

	public TChannel findByDevType(@Param(value = "devId") Long devId, @Param(value = "typeId") Long typeId);// 根据设备id与typeid获取通道

	public TChannel findByDevTypeCode(@Param(value = "devId") Long devId, @Param(value = "typeId") Long typeId,
			@Param(value = "Code") Long Code);// 根据设备id与typeid、Code获取通道

	public Map<String, Object> getRealDataByChId(@Param(value = "chId") String chId);

	// 根据设备id,修改区域
	public int updateOrg(@Param(value = "orgId") String orgId, @Param(value = "devId") String devId);

	// 获取区域底下通道小图标
	public List<MapEntity> channelPic(@Param(value = "orgId") String orgId, @Param(value = "coldId") String coldId,
			@Param(value = "typeId") String typeId);

	public Integer updateCoords(@Param(value = "id") String id, @Param(value = "coordX") String coordX,
			@Param(value = "coordY") String coordY);

	void deleteByDevId(String devId);// 根据设备id删除通道
	
	//-----------------------------------
	//联动的
	List<MapEntity> getDestList(@Param(value = "level")String level, @Param(value = "srcId")String srcId);
	
	//获取用户电话邮件
	MapEntity getUserMobile(@Param(value = "userId")String userId);
	
	
	MapEntity getOrderBySrcId(@Param(value = "srcId")String srcId);
	
	MapEntity getTemplate(@Param(value = "id")String id);
	
	   
    MapEntity getDeviveName(@Param(value = "devId") String devId);
    
	
}