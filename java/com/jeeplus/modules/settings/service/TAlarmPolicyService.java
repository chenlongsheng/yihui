/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;
import com.jeeplus.modules.settings.dao.TAlarmPolicyDao;
import com.jeeplus.modules.settings.entity.TAlarmPolicy;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TCodeType;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 数据配置Service
 * 
 * @author long
 * @version 2018-10-23
 */
@Service
@Transactional(readOnly = true)
public class TAlarmPolicyService extends CrudService<TAlarmPolicyDao, TAlarmPolicy> {

	public TAlarmPolicy get(String id) {
		return super.get(id);
	}

	public List<MapEntity> findList(String chType, String typeId, Integer devType) {

		return dao.findTypeListByUserId(UserUtils.getUser().getId(), typeId, chType, devType);
	}

	public Page<TAlarmPolicy> findPage(Page<TAlarmPolicy> page, TAlarmPolicy tAlarmPolicy) {

		return super.findPage(page, tAlarmPolicy);
	}

	// 添加修改
	@Transactional(readOnly = false)
	public void save(JSONArray ja, String devType, String devIds) {
		System.out.println(UserUtils.getUser().getId());
		dao.deleteAlarmPolicy(UserUtils.getUser().getId(), null, devType);
		if (StringUtils.isNotBlank(devIds)) {// 删除特殊设备阈值
			dao.deletePolicyByDeviceIds(devIds);
		}
		for (int i = 0; i < ja.size(); i++) {
			TAlarmPolicy tAlarmPolicy = JSONObject.parseObject(ja.get(i).toString(), TAlarmPolicy.class);
			if (tAlarmPolicy.getChId() == null) {
				tAlarmPolicy.setChId(Long.parseLong("0"));
			}
			tAlarmPolicy.setDevType(devType);
			System.out.println(UserUtils.getUser().getId());
			List<String> userOrgList = dao.getUserOrgList(UserUtils.getUser().getId());
			for (String orgId : userOrgList) {
				tAlarmPolicy.setOrgId(Long.parseLong(orgId));
				tAlarmPolicy.setId(null);
				super.save(tAlarmPolicy);// 保存
			}
		}
	}

	// 保存设备表阈值管理
	@Transactional(readOnly = false)
	public void saveAlarmPolicyByDevId(JSONArray ja, String deviceIds, Long orgId) {

		dao.deleteAlarmPolicyByChId(deviceIds);
		String[] deviceList = deviceIds.split(",");
		for (int j = 0; j < deviceList.length; j++) {
			for (int i = 0; i < ja.size(); i++) {
				TAlarmPolicy tAlarmPolicy = JSONObject.parseObject(ja.get(i).toString(), TAlarmPolicy.class);
				tAlarmPolicy.setOrgId(orgId);
				System.out.println(tAlarmPolicy.toString());
				List<String> chIdList = dao.getChIdByDevId(deviceList[j], tAlarmPolicy.getTypeId() + "",
						tAlarmPolicy.getChType() + "", null);
				for (String chId : chIdList) {
					tAlarmPolicy.setChId(Long.parseLong(chId));
					super.save(tAlarmPolicy);// 保存
				}
			}
		}
	}

	public List<MapEntity> getAlarmPolicyList(String devId, String typeId, String chType) {

		List<MapEntity> alarmPolicyList = dao.getAlarmPolicyList(devId, typeId, chType);

		if (alarmPolicyList == null || alarmPolicyList.size() == 0) {// 没有特殊的时候
			String devType = dao.devTypeByDevId(devId);
			System.out.println(devType);
			if (StringUtils.isNotBlank(devType)) {
				alarmPolicyList = dao.findTypeListByUserId(UserUtils.getUser().getId(), typeId, chType,
						Integer.parseInt(devType));
			}
		}
		return alarmPolicyList;
	}

//	@Transactional(readOnly = false)
//	public void delete(JSONArray ja)  {
//		dao.deleteAlarmPolicy(UserUtils.getUser().getId(),null);
//		this.save(ja);
//	}

//	@Transactional(readOnly = false)
//	public void delete2(JSONArray ja,String deviceIds)  {
//		dao.deleteAlarmPolicy(UserUtils.getUser().getId(),"1");
//		this.saveAlarmPolicyByDevId(ja,deviceIds);
//	}

	public List<String> getChIdByDevId(String devIds, String typeId, String chType, String chNo) {

		return dao.getChIdByDevId(devIds, typeId, chType, chNo);
	}

	// ------------------------
	// 获取级别
	public List<TCode> codeList(String codeTypeId) {
		return dao.codeList(codeTypeId);
	}

	// 通道主类型集合
	public List<TCodeType> codeTypeList() {
		return dao.codeTypeList();
	}

	public MapEntity getDeviceByPolicy(String devType) {
		MapEntity entity= new MapEntity();
		Set<MapEntity> orgAllList = new HashSet<MapEntity>();
		List<String> orgPidsBypolicy = dao.getOrgPidsBypolicy(UserUtils.getUser().getId(), devType);
		for (String pIds : orgPidsBypolicy) {
			Set<MapEntity> orgList = dao.getOrgList(pIds);
			orgAllList.addAll(orgList);
		}
		entity.put("orgAllList", orgAllList);
		entity.put("getDeviceByPolicy", dao.getDeviceByPolicy(UserUtils.getUser().getId(), devType));
		return entity;

	}

}