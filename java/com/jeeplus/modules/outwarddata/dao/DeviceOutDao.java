/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.outwarddata.dao;


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
public interface DeviceOutDao extends CrudDao<TDeviceMachine> {


    List<MapEntity> deviceOutList(@Param(value="orgId") String orgId);

    List<MapEntity> channelOutList(@Param(value="orgId") String orgId);

    void updateRealDatas(RealData r);


    List<MapEntity> getOrgListById(@Param(value="orgId") String orgId);

}