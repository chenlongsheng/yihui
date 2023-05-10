package com.jeeplus.modules.starnet.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.modules.starnet.dao.DataDao;

@Service
public class DataService {
	
	@Autowired
	DataDao dataDao;
	
	public List<MapEntity> getLoopElecChannelList() {
		return dataDao.getLoopElecChannelList();
	}

	public MapEntity getLoopLatestElecData(String chId) {
		return dataDao.getLoopLatestElecData(chId);
	}

	public MapEntity getNewData(String chId) {
		return dataDao.getNewData(chId);
	}

	public List<MapEntity> getNewDatas(String historyTime, String chId) {
		return dataDao.getNewDatas(historyTime,chId);
	}

	public List<MapEntity> getLoopList() {
		return dataDao.getLoopList();
	}
	
	
	
	
	
	

	public MapEntity getPhaseAVolChannelByOrgId(String orgId) {
		int type = 105;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}

	public MapEntity getPhaseBVolChannelByOrgId(String orgId) {
		int type = 106;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}

	public MapEntity getPhaseCVolChannelByOrgId(String orgId) {
		int type = 107;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}

	


	public MapEntity getPhaseACurChannelByOrgId(String orgId) {
		int type = 166;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}

	public MapEntity getPhaseBCurChannelByOrgId(String orgId) {
		int type = 167;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}

	public MapEntity getPhaseCCurChannelByOrgId(String orgId) {
		int type = 168;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}
	
	public MapEntity getPhaseATotalPowerChannelByOrgId(String orgId) {
		int type = 382;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}

	public MapEntity getPhaseBTotalPowerChannelByOrgId(String orgId) {
		int type = 383;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}
	
	public MapEntity getPhaseCTotalPowerChannelByOrgId(String orgId) {
		int type = 384;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}
	
	
	public MapEntity getEnergyChannelByOrgId(String orgId) {
		int type = 403;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}
	
	public MapEntity getTotalPowerChannelByOrgId(String orgId) {
		int type = 385;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}
	
	
	public MapEntity getPowerFactorChannelByOrgId(String orgId) {
		int type = 385;
		return dataDao.getChannelByOrgIdAndType(orgId,type);
	}
	
	
	
	
	public MapEntity getChannelMaxValueInADay(String chId, String todayDatetime) {
		return dataDao.getChannelMaxValueInADay(chId,todayDatetime);
	}

	public MapEntity getChannelMinValueInADay(String chId, String todayDatetime) {
		return dataDao.getChannelMinValueInADay(chId,todayDatetime);
	}

	public MapEntity getChannelAVGValueInADay(String chId, String todayDatetime) {
		return dataDao.getChannelAVGValueInADay(chId,todayDatetime);
	}

	public Double getFirstMeterDataOfDay(String chId,String todayDatetime) {
		return dataDao.getFirstMeterDataOfDay(chId,todayDatetime);
	}

	public void addMeterData(String orgId, String chId, String todayDatetime, Double value) {
		dataDao.addMeterData(orgId,chId,todayDatetime,value);
	}

	public List<MapEntity> getMetryVlaueOfDate(String date) {
		List<MapEntity> datas = new ArrayList<>();
		
		//获取回路所关联的 有功电能 通道
    	List<MapEntity> loopElecChannelList = getLoopElecChannelList();
    	//查询 增量表该回路最后一条数据 ,历史数据表该通道的最后一条数据, 并将该数据id,缓存11分钟.
    	for(MapEntity elecChannel : loopElecChannelList) {
    		String chId = elecChannel.get("id").toString();
    		String orgId = elecChannel.get("orgId").toString();
    		MapEntity data = dataDao.getMetryVlaueOfDate(orgId,chId,date);
    		if(data != null) {
    			datas.add(data);	
    		}
    	}
    	
		return datas;
	}

	public void addJumpMetry(String orgId, String chId, int normal) {
		dataDao.addJumpMetry(orgId,chId,normal);
	}







	
}
