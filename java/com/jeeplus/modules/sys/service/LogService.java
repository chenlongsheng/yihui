/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.modules.sys.dao.LogDao;
import com.jeeplus.modules.sys.entity.Log;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 日志Service
 * 
 * @author jeeplus
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class LogService extends CrudService<LogDao, Log> {

	@Autowired
	private LogDao logDao;

	public Page<MapEntity> findPage(Page<MapEntity> page, MapEntity entity) {

		entity.setPage(page);
		entity.put("userId", UserUtils.getUserPId());
		page.setList(dao.findLogList(entity));
		return page;
	}

	@Transactional(readOnly = false)
	public void empty() {
		logDao.empty();
	}

}
