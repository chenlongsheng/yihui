/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.entity;


import com.jeeplus.common.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 川大设备code关联Entity
 * @author ywk
 * @version 2018-07-05
 */
public class CpCarpark extends DataEntity<CpCarpark> {
	
	private static final long serialVersionUID = 1L;
	private String name;		//名称
	
	public CpCarpark() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
}