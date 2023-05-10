/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.dao.TDeviceDetailDao;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceDetail;

/**
 * 数据配置Service
 * 
 * @author long
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class TDeviceDetailService extends CrudService<TDeviceDetailDao, TDeviceDetail> {

	@Autowired
	private TDeviceDao tDeviceDao;
	@Autowired
	private TDeviceDetailDao tDeviceDetailDao;
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TRealDataService tRealDataService;

	public TDeviceDetail get(String id) {
		return super.get(id);
	}

	public List<TDeviceDetail> findList(TDeviceDetail tDeviceDetail) {
		return super.findList(tDeviceDetail);
	}

	public Page<TDeviceDetail> findPage(Page<TDeviceDetail> page, TDeviceDetail tDeviceDetail) {
		return super.findPage(page, tDeviceDetail);
	}

	@Transactional(readOnly = false)
	public void save(TDeviceDetail tDeviceDetail) {
		super.save(tDeviceDetail);
	}

	@Transactional(readOnly = false)
	public void delete(TDeviceDetail tDeviceDetail) {
		super.delete(tDeviceDetail);
	}

	// -----------------------------------
	@Transactional(readOnly = false)
	public void saveDeviceDetail(TDeviceDetail tDeviceDetail) {
		if (tDeviceDetail.getIsNewRecord()) {
			tDeviceDetail.preInsert();
			dao.insert(tDeviceDetail);
		} else {
			tDeviceDetail.preUpdate();
			dao.update(tDeviceDetail);
		}
	}

	public List<MapEntity> selectSnByOrgId(String orgId) {

		List<MapEntity> selectSnByOrgId = tDeviceDetailDao.selectSnByOrgId(orgId);
		return selectSnByOrgId;

	}

	// 地埋线
	public List<MapEntity> tCatgutList(String orgId) {
		List<MapEntity> deviceList = tDeviceDetailDao.tCatgutList(orgId);
		for (int i = 0; i < deviceList.size(); i++) {
			MapEntity entity = deviceList.get(i);
			Integer devType = (Integer) entity.get("dev_type");
			String devId = (String) entity.get("id");
			int devOnline = (int) entity.get("online");
			String channelName = "";
			String addrName = "";
			List<MapEntity> channelList = tDeviceDetailDao.deviceChannelList(devId);
			for (int j = 0; j < channelList.size(); j++) {
				MapEntity channel = channelList.get(j);
				Integer chType = (Integer) channel.get("ch_type");
				Integer typeId = (Integer) channel.get("type_id");
				Integer channelType = (Integer) channel.get("channelType");

				String chName = (String) channel.get("chName");
				String addr = (String) channel.get("addr");
				String real = "0";
				String warn = "0";
			}
			entity.put("channelList", channelList);

		}

		return deviceList;
	}

	public List<MapEntity> selectFireware() {

		return tDeviceDetailDao.selectFireware();
	}

}