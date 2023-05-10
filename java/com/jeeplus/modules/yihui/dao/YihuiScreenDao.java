/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.yihui.dao;


import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
import com.jeeplus.modules.outwarddata.Entity.RealData;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 数据配置DAO接口
 *
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface YihuiScreenDao extends CrudDao<TDeviceMachine> {


    List<MapEntity> getRealAlarmogs(MapEntity entity);


    List<MapEntity> getDeviceOnlines(@Param(value = "orgId") String orgId);


    List<MapEntity>  getLoopDevices(@Param(value = "orgId") String orgId);

    List<MapEntity>  getLoopChannels(@Param(value = "orgId") String orgId);

    List<MapEntity>  getHistorys(@Param(value = "orgId") String orgId);


    List<MapEntity>  getOrgLoops(@Param(value = "orgId") String orgId,@Param(value = "loop") String loop);

    List<MapEntity>  getVideos(@Param(value = "orgId") String orgId);

    MapEntity   getTorg();








}