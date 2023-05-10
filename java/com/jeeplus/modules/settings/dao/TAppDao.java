/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TApp;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceDetail;

/**
 * 数据配置DAO接口
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface TAppDao extends CrudDao<TApp> {

	
	
	List<MapEntity> getAppList(TApp tApp);
	
	
	void insertApp(TApp tApp);
	 
	
	void updateApp(TApp tApp);
	
	
	void deleteApp(TApp tApp);
	
	List<MapEntity> getMessageList(@Param(value = "status") String status);
}