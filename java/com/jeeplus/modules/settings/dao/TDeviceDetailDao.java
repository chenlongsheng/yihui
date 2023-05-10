/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceDetail;

/**
 * 数据配置DAO接口
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface TDeviceDetailDao extends CrudDao<TDeviceDetail> {
	
	//地埋线
	List<MapEntity> tCatgutList(@Param(value="orgId")String orgId); 
	
	List<MapEntity> deviceChannelList(@Param(value ="devId")String devId);
	//上传下载sn
	List<MapEntity> selectSnByOrgId(@Param(value = "orgId") String orgId);
	
	List<MapEntity> selectFireware();
	
}