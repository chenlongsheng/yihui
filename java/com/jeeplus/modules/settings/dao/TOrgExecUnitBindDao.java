/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.CdzChargePot;
import com.jeeplus.modules.settings.entity.CpCarpark;
import com.jeeplus.modules.settings.entity.TOrgExecUnitBind;
import com.jeeplus.modules.sys.entity.Area;

/**
 * 运营管理DAO接口
 * @author long
 * @version 2018-08-17
 */
@MyBatisDao
public interface TOrgExecUnitBindDao extends CrudDao<TOrgExecUnitBind> {
    
	public List<CpCarpark> carParkList();
	
	public List<CdzChargePot> cdzChargePotList();
	
	public List<MapEntity> findBindList(MapEntity entity);
	//获取运行单元管理的bean
	public TOrgExecUnitBind getTorg(@Param(value="id") String id,
			@Param(value="orgId") String orgId);
	
	public List<TOrgExecUnitBind> findNotList(TOrgExecUnitBind entity);
	

	public List<Area> findAreaList();
	
}