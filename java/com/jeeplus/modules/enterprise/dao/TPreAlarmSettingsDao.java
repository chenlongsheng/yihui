/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.enterprise.entity.TChGroup;
import com.jeeplus.modules.enterprise.entity.TPreAlarmSettings;
import com.jeeplus.modules.settings.entity.TOrg;


/**
 * 预警配置DAO接口
 * @author ywk
 * @version 2017-04-28
 */
@MyBatisDao
public interface TPreAlarmSettingsDao extends CrudDao<TPreAlarmSettings> {
	
	List<MapEntity> findEnterprisePreAlarmSettingsByPage(MapEntity mapEntity);
	
	List<TPreAlarmSettings> getSencePrealarmSettings(@Param("orgid")Long orgid,@Param("codeid")Long codeid,@Param("typeid")Long typeid);
	
	List<TPreAlarmSettings> getRemindPerson(Long id);
	
	void saveRemindPersons(@Param("orgId")Long orgId,@Param("remindPerson")String remindPerson);
	
	List<TPreAlarmSettings> getPreAlarmSetttingByOrgId(Long id);
	
	List<TPreAlarmSettings> getPreAlarmSetttingByTypeAndOrgId(@Param("logicOrgId")Long logicOrgId,@Param("chType")Long chType,@Param("typeId")Long typeId);
	
	TPreAlarmSettings getPreAlarmSetttingByTypeAndLev(@Param("orgId")Long orgId, @Param("chType")Long chType,@Param("typeId")Long typeId,@Param("alarmLev")Long alarmLev);

    //-------------------------------
	public List<Map<String, Object>> getOrgTreeByOrgId(Long orgId);

	List<Map<String,Object>> getChannelListByScenceId(Long firstSenceId);

	List<TChGroup> getChGroupListBySenceId(@Param("senceId")Long senceId);

    TOrg getOrg(@Param("id")Long id);

    

}