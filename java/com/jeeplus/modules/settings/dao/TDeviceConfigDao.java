/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TDeviceConfig;

/**
 * 数据配置DAO接口
 * @author long
 * @version 2018-10-24
 */
@MyBatisDao
public interface TDeviceConfigDao extends CrudDao<TDeviceConfig> {
    
	//获取
	public List<TDeviceConfig> configList(@Param(value= "projectName")String projectName);
	
	public int updateConfig(TDeviceConfig tDeviceConfig);
	
	public List<TDeviceConfig> deviceFromList(@Param(value= "projectName")String projectName);
	//获取的工程名
	public List<String> configName();
	
	
}