/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TCodeTypeDao;
import com.jeeplus.modules.settings.entity.TCodeType;

/**
 * CodeType管理Service
 * @author ywk
 * @version 2018-06-22
 */
@Service
@Transactional(readOnly = true)
public class TCodeTypeService extends CrudService<TCodeTypeDao, TCodeType> {

	public TCodeType get(String id) {
		return super.get(id);
	}
	
	public List<TCodeType> findList(TCodeType tCodeType) {
		return super.findList(tCodeType);
	}
	
	public Page<TCodeType> findPage(Page<TCodeType> page, TCodeType tCodeType) {
		return super.findPage(page, tCodeType);
	}
	
	@Transactional(readOnly = false)
	public void save(TCodeType tCodeType) {
		super.save(tCodeType);
	}
	
	@Transactional(readOnly = false)
	public void delete(TCodeType tCodeType) {
		super.delete(tCodeType);
	}
	
	
	
	
}