/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TAppDao;
import com.jeeplus.modules.settings.dao.TCodeDao;
import com.jeeplus.modules.settings.entity.TApp;
import com.jeeplus.modules.settings.entity.TDevice;

/**
 * 数据配置Service
 * 
 * @author long
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class TAppService extends CrudService<TAppDao, TApp> {

	@Autowired
	private TAppDao tAppDao;

	public Page<TApp> findPage(Page<TApp> page, TApp tApp) {
		return super.findPage(page, tApp);
	}

	@Transactional(readOnly = false)
	public void insertApp(TApp tApp) {

		tAppDao.insertApp(tApp);
	}

	@Transactional(readOnly = false)
	public void updateApp(TApp tApp) {

		tAppDao.updateApp(tApp);
	}

	@Transactional(readOnly = false)
	public void deleteApp(TApp tApp) {

		tAppDao.deleteApp(tApp);

	}

	public List<MapEntity> getSystemList() {

		return tAppDao.getMessageList("12");
	}

	public List<MapEntity> getModelTypeList() {

		return tAppDao.getMessageList("13");
	}

}