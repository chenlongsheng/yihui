/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.enterprise.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.enterprise.entity.TChGroup;
import com.jeeplus.modules.enterprise.entity.TPreAlarmSettings;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.enterprise.dao.TPreAlarmSettingsDao;

/**
 * 预警配置Service
 * @author ywk
 * @version 2017-04-28
 */
@Service
@Transactional(readOnly = true)
public class TPreAlarmSettingsService extends CrudService<TPreAlarmSettingsDao, TPreAlarmSettings> {

	@Autowired
	TPreAlarmSettingsDao tPreAlarmSettingsDao;
	
	public TPreAlarmSettings get(String id) {
		return super.get(id);
	}
	
	public List<TPreAlarmSettings> findList(TPreAlarmSettings tPreAlarmSettings) {
		return super.findList(tPreAlarmSettings);
	}
	
	public Page<TPreAlarmSettings> findPage(Page<TPreAlarmSettings> page, TPreAlarmSettings tPreAlarmSettings) {
		return super.findPage(page, tPreAlarmSettings);
	}
	
	@Transactional(readOnly = false)
	public void save(TPreAlarmSettings tPreAlarmSettings) {
		super.save(tPreAlarmSettings);
	}
	
	@Transactional(readOnly = false)
	public void delete(TPreAlarmSettings tPreAlarmSettings) {
		super.delete(tPreAlarmSettings);
	}

	
	
	
	
	
	public List<TPreAlarmSettings> getPreAlarmSetttingByOrgId(Long id) {
		return tPreAlarmSettingsDao.getPreAlarmSetttingByOrgId(id);
	}

	public void getPreAlarmRemindPerson(Long id) {
		
	}
	
	public void savePreAlarmRemindPerson(Long orgId){
		
	}
	
	@Transactional(readOnly = false)
	public void saveRemindPersons(Long orgId,String remindPerson) {
		tPreAlarmSettingsDao.saveRemindPersons(orgId,remindPerson);
	}
	
	public List<TPreAlarmSettings> getRemindPerson(Long id) {
		return tPreAlarmSettingsDao.getRemindPerson(id);
	}

	public Page<MapEntity> findEnterprisePreAlarmSettingsByPage(Page<MapEntity> page,MapEntity mapEntity) {
		mapEntity.setPage(page);
		page.setList(tPreAlarmSettingsDao.findEnterprisePreAlarmSettingsByPage(mapEntity));
		return page;
	}

	public List<TPreAlarmSettings> getSencePrealarmSettings(Long orgid,Long codeid, Long typeid) {
		return tPreAlarmSettingsDao.getSencePrealarmSettings(orgid,codeid,typeid);
	}

	public List<TPreAlarmSettings> getPreAlarmSetttingByTypeAndOrgId(Long logicOrgId, Long chType,Long typeId) {
		return tPreAlarmSettingsDao.getPreAlarmSetttingByTypeAndOrgId(logicOrgId,chType,typeId);
	}

	public TPreAlarmSettings getPreAlarmSetttingByTypeAndLev(Long orgId,Long chType, Long typeId,Long alarmLev) {
		return tPreAlarmSettingsDao.getPreAlarmSetttingByTypeAndLev(orgId,chType,typeId,alarmLev);
	}
	
	//--------------------------------
	public List<Map<String, Object>> getOrgTreeByOrgId(Long orgId) {
		
		
		
		
		return tPreAlarmSettingsDao.getOrgTreeByOrgId(orgId);
	}
		
	public List<Map<String,Object>> getChannelListByScenceId(Long firstSenceId) {
		return tPreAlarmSettingsDao.getChannelListByScenceId(firstSenceId);
	}
	
	
	public List<TChGroup> getChGroupListBySenceId(Long senceId) {
		return tPreAlarmSettingsDao.getChGroupListBySenceId(senceId);
	}
	
	public TOrg getOrg(Long id) {
		return tPreAlarmSettingsDao.getOrg(id);
	}
	
	
	
}