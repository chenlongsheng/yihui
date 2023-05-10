/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TDeviceConfigDao;
import com.jeeplus.modules.settings.entity.TDeviceConfig;

/**
 * 数据配置Service
 * 
 * @author long
 * @version 2018-10-24
 */
@Service
@Transactional(readOnly = true)
public class TDeviceConfigService extends CrudService<TDeviceConfigDao, TDeviceConfig> {

	@Autowired
	private TDeviceConfigDao tDeviceConfigDao;

	public TDeviceConfig get(String id) {
		return super.get(id);
	}

	public List<TDeviceConfig> findList(TDeviceConfig tDeviceConfig) {
		return super.findList(tDeviceConfig);
	}

	public Page<TDeviceConfig> findPage(Page<TDeviceConfig> page, TDeviceConfig tDeviceConfig) {
		return super.findPage(page, tDeviceConfig);
	}

	@Transactional(readOnly = false)
	public void save(TDeviceConfig tDeviceConfig) {
		super.save(tDeviceConfig);
	}

	@Transactional(readOnly = false)
	public void delete(TDeviceConfig tDeviceConfig) {
		super.delete(tDeviceConfig);
	}

	// -----------------------------------------
	// 获取字段配置的全部信息
	public List<TDeviceConfig> configList(String projectName) {
		return tDeviceConfigDao.configList(projectName);
	}

	@Transactional(readOnly = false)
	public int updateConfig(TDeviceConfig tDeviceConfig) {

		return tDeviceConfigDao.updateConfig(tDeviceConfig);
	}

	public List<TDeviceConfig> deviceFromList(String projectName) {
		return tDeviceConfigDao.deviceFromList(projectName);
	}

	// 获取的工程名
	public List<String> configName() {
		return tDeviceConfigDao.configName();
	}

}