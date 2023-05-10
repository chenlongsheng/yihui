/**
 * Copyright &copy; 2015-2020 
 */
package com.jeeplus.modules.enterprise.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.security.Digests;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.Encodes;
import com.jeeplus.modules.enterprise.entity.TOperLog;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.enterprise.dao.TOperLogDao;

/**
 * 操作日志Service
 * 
 * @author ywk
 * @version 2017-04-11
 */
@Service
@Transactional(readOnly = true)
public class TOperLogService extends CrudService<TOperLogDao, TOperLog> {

	@Autowired
	TOperLogDao tOperLogDao;

	public List<Map<String, Object>> getRealDataListByOrgId(Long id) {
		return tOperLogDao.getRealDataListByOrgId(id);
	}

	public List<TOrg> getOrgListByLikeName(TOrg org) {
		return tOperLogDao.getOrgListByLikeName(org);
	}

	public Map<String, Object> getOrgById(String id) {
		return tOperLogDao.getOrgById(id);
	}

	public Map<String, Object> login(String username) {
		
		return tOperLogDao.login(username);
	}

}