/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TCodeType;

/**
 * CodeType管理DAO接口
 * @author ywk
 * @version 2018-06-22
 */
@MyBatisDao
public interface TCodeTypeDao extends CrudDao<TCodeType> {

	
}