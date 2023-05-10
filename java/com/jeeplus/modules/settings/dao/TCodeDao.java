/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TCode;

/**
 * code管理DAO接口
 * @author long
 * @version 2018-08-09
 */
@MyBatisDao
public interface TCodeDao extends CrudDao<TCode> {

	public List<TCode> findCodeList(TCode entity);
	
	public TCode getCode(@Param(value="id")String id,@Param(value="typeId")String typeId);
	
	//报警新写的类型接口
	public List<TCode> getCodeList();
	
	public List<TCode> devTypeList();

}