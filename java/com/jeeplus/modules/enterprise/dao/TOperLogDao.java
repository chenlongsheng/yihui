/**
 * Copyright &copy; 2015-2020 
 */
package com.jeeplus.modules.enterprise.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.enterprise.entity.TOperLog;
import com.jeeplus.modules.settings.entity.TOrg;

/**
 * 操作日志DAO接口
 * @author ywk
 * @version 2017-04-11
 */
@MyBatisDao
public interface TOperLogDao extends CrudDao<TOperLog> {

	List<Map<String, Object>> getRealDataListByOrgId(Long id);
	
	List<TOrg> getOrgListByLikeName(TOrg org);
	
	Map<String, Object> getOrgById(@Param(value ="id")String id);
	
	Map<String, Object> login(@Param(value ="username")String username);
	
}