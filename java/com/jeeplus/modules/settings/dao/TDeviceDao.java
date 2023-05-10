/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.sys.entity.Area;

/**
 * 设备管理DAO接口
 * 
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface TDeviceDao extends CrudDao<TDevice> {

	public MapEntity getDeviceEn(String id);

	public List<MapEntity> deviceList(MapEntity entity);

	public List<TDevice> deviceAllList();

	// t_code中的name集合==1
	public List<MapEntity> codeList();

	// 删除t_device_detail
	public int deleteDetail(TDevice tDevice);

	public int insert(TDeviceDetail tDeviceDetail);

	public int update(TDeviceDetail tDeviceDetail);

	// 更新t_channel的orgId
	public int updateChannel(@Param(value = "orgId") String orgId, @Param(value = "devId") String devId);

//	public List<MapEntity> nameList(@Param(value ="nameList")String nameList);
	// 获取from表单字段
	public MapEntity getDeviceFrom(@Param(value = "id") String id,
			@Param(value = "deviceFromList") String deviceFromList);

	// 更改启用禁用
	public Integer saveUse(TDevice tDevice);

	// 修改设备图片
	public Integer updatePic(@Param(value = "id") String id, @Param(value = "picturePath") String picturePath);

	// 获取区域底下设备小图标
	public List<MapEntity> devicePic(@Param(value = "orgId") String orgId, @Param(value = "coldId") String coldId);

	Integer updateCoords(@Param(value = "id") String id, @Param(value = "coordX") String coordX,
			@Param(value = "coordY") String coordY);

	// 新的sql
	List<MapEntity> tDeviceList(@Param(value = "orgId") String orgId);

	List<MapEntity> deviceChannelList(@Param(value = "devId") String devId);

	// 修改设备地址区域
	void midifyDevice(@Param(value = "id") String id, @Param(value = "addr") String addr,
			@Param(value = "orgId") String orgId, @Param(value = "pId") String pId);

	// 同时修改通道
	void midifyChannel(@Param(value = "devId") String devId, @Param(value = "orgId") String orgId);

	void deleteChannelbyDevid(@Param(value = "devId") String devId);

	List<MapEntity> deviceByMac(@Param(value = "mac") String mac);

	// 回调通道传统烟感
	List<MapEntity> getChSmokeList(@Param(value = "devId") String devId);

	List<MapEntity> getSmokeType();

	List<MapEntity> getVideoType();

	List<MapEntity> getwireName();

	void updateVideoByChId(@Param(value = "channelType") String channelType, @Param(value = "name") String name,
			@Param(value = "orgId") String orgId, @Param(value = "addr") String addr,
			@Param(value = "param0") String param0, @Param(value = "remarks") String remarks,
			@Param(value = "chId") String chId);

	MapEntity selectMac(@Param(value = "mac") String mac);

	MapEntity getUserDetail(@Param(value = "orgId") String orgId);

	// 网关实时状态
	List<MapEntity> macStateList(@Param(value = "orgId") String orgId);

	MapEntity homepageCount(@Param(value = "orgId") String orgId);

	List<MapEntity> leveDevice(@Param(value = "orgId") String orgId);

	List<MapEntity> getChannelsById(@Param(value = "devId") String devId);
	List<MapEntity> detaiDevice(@Param(value = "orgId") String orgId);

	// 新网关识别集合
	List<MapEntity> GatewayList(@Param(value = "orgId") String orgId);

	void updateOrgBydevId(@Param(value = "id") String id);

	// 設備個數
	int deviceCount(@Param(value = "orgId") String orgId);

	public List<MapEntity> getDevicebyTypeAndOrgId(@Param(value = "devType") int devType,
			@Param(value = "pdfId") String pdfId);

	public List<MapEntity> getTempHumChannelByDevId(@Param(value = "devId") String devId);

	public MapEntity getLastDataBeforeToday(@Param(value = "chId") String chId);

	public List<MapEntity> getTodayData(@Param(value = "chId") String id);

	// 获取远程通道设备 灯空调集合
	List<MapEntity> remoteChannelist(@Param(value = "orgId") String orgId);

	List<MapEntity> pdfCodeList();

	// 回调继电器
	List<MapEntity> getRelayList(@Param(value = "devId") String devId);

	// 回调灯等
	List<MapEntity> getLightList();

	// 已绑定的电器等
	List<MapEntity> getElectricList(@Param(value = "devId") String devId);

	void updateChannelCode(@Param(value = "name") String name, @Param(value = "addr") String addr,
			@Param(value = "channelType") String channelType, @Param(value = "chId") String chId);

	MapEntity getOrgName(@Param(value = "devId") String devId);

	public List<MapEntity> videolist(@Param(value = "orgId") String orgId);

	List<MapEntity> getDevTypeList(@Param(value = "orgId") String orgId);

	// 获取智能辅控设备总览
	List<MapEntity> getDevicTypeList(@Param(value = "orgId") String orgId);

	void deleteImageDev(@Param(value = "devId") String devId);

	void updateChannelByDevId(@Param(value = "devId") String devId);

	void updateChannelBychId(@Param(value = "chId") String chId, @Param(value = "addr") String addr,
			@Param(value = "channelType") String channelType, @Param(value = "name") String name);

	List<MapEntity> getOrgListById(@Param(value = "orgId") String orgId);

	void deleteChanByChId(@Param(value = "chId") String chId);

	// 获取旧烟感集合,设备管理
	List<MapEntity> getSmokeList(@Param(value = "orgId") String orgId);

	List<MapEntity> getAlarmRateList(@Param(value = "orgId") String orgId);

	List<MapEntity> NVRList(@Param(value = "orgId") String orgId);

	void deleteNvr(@Param(value = "chId") String chId);

	void modifyNvr(@Param(value = "channelType") String channelType, @Param(value = "chId") String chId);

	List<MapEntity> selectNVRList(@Param(value = "orgId") String orgId);

	// 删除摄像头
	void deleteVideoBychId(@Param(value = "chId") String chId);

	void updateOrderNo(@Param(value = "chId") String chId, @Param(value = "param0") String param0);

	void updateVideo(@Param(value = "channelType") String channelType, @Param(value = "name") String name,
			@Param(value = "addr") String addr, @Param(value = "remarks") String remarks,
			@Param(value = "chId") String chId, @Param(value = "orgId") String orgId);
	
	String selectOrgByChId(@Param(value = "chId") String chId);
	
	
	List<MapEntity> getNVRList(@Param(value = "orgId") String orgId);
	
	void deleteNvrByDevId(@Param(value = "devId") String devId);	
	//地锁---
	List<MapEntity> selectLockList(@Param(value = "devId") String devId);
	
	List<MapEntity>  getDevIds(@Param(value = "orgId") String orgId);
	
	List<MapEntity>  getLockList(@Param(value = "orgId") String orgId);
	
	
List<MapEntity> getLoopByOrgId(@Param(value = "orgId") String orgId);
    
    void updateOrgIdByChId(@Param(value = "chIds") String chIds, @Param(value = "loopId") String loopId);
    

}