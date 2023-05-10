/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.entity.TChannel;

/**
 * 通道管理Service
 * 
 * @author ywk
 * @version 2018-06-22
 */
@Service
@Transactional(readOnly = true)
public class TChannelService extends CrudService<TChannelDao, TChannel> {

	@Autowired
	private TChannelDao tChannelDao;

	@Autowired
	private TDeviceDao tDeviceDao;

	public TChannel get(String id) {
		return super.get(id);
	}

	public List<TChannel> findList(TChannel tChannel) {
		return super.findList(tChannel);
	}

	public Page<TChannel> findPage(Page<TChannel> page, TChannel tChannel) {
		return super.findPage(page, tChannel);
	}

	@Transactional(readOnly = false)
	public void save(TChannel tChannel) {
		super.save(tChannel);
	}

	@Transactional(readOnly = false)
	public void delete(TChannel tChannel) {
		super.delete(tChannel);
	}

	// -------------------------------------------------

	// 根据设备id,修改区域
	@Transactional(readOnly = false)
	public int updateOrg(String orgId, String devId) {

		return tChannelDao.updateOrg(orgId, devId);

	}

	public List<MapEntity> channelList(MapEntity entity) {
		List<MapEntity> channelList = tChannelDao.channelList(entity);
		return channelList;
	}

	public Page<MapEntity> findPage(Page<MapEntity> page, MapEntity entity) {
		entity.setPage(page);
		page.setList(tChannelDao.channelList(entity));
		System.out.println("pagecount---channel" + page.getCount());
		return page;
	}

	   public MapEntity getDeviveName(String devId) {
	        return  tChannelDao.getDeviveName(devId);
	    }
	public List<TChannel> findAllList() {
		return tChannelDao.findAllList();
	}

	public List<MapEntity> orgList(String orgId) {

		return tChannelDao.orgList(orgId);
	}

	public List<MapEntity> typeList() {
		// 获取t_code集合
		return tChannelDao.typeList();
	}

	public TChannel findByDevType(Long devId, Long typeId) {
		return tChannelDao.findByDevType(devId, typeId);
	}

	public TChannel findByDevTypeCode(Long devId, Long typeId, Long Code) {
		return tChannelDao.findByDevTypeCode(devId, typeId, Code);
	}

	public Map<String, Object> getRealDataByChId(String chId) {
		return tChannelDao.getRealDataByChId(chId);
	}

	public void deleteByDevId(String devId) {
		tChannelDao.deleteByDevId(devId);
	}

	// 获取区域底下通道小图标
	public List<MapEntity> channelPic(String orgId, String coldId, String typeId) {

		return tChannelDao.channelPic(orgId, coldId, typeId);
	}

	@Transactional(readOnly = false)
	public Integer updateCoords(String id, String coordX, String coordY) {

		return tChannelDao.updateCoords(id, coordX, coordY);
	}

	// --------------------------------------------

	// 获取远程通道设备 灯空调集合
	public MapEntity remoteChannelist(String orgId) {
		MapEntity entity = new MapEntity();
		entity.put("pdfCodeList", tDeviceDao.pdfCodeList());
		entity.put("remoteChannelist", tDeviceDao.remoteChannelist(orgId));
		return entity;

	}

	// --------------------------

	// 联动的
	public List<MapEntity> getDestList(String level, String srcId) {

		return tChannelDao.getDestList(level, srcId);
	}
	
	//获取用户电话邮件
	public	MapEntity getUserMobile(String userId) {
		
		return tChannelDao.getUserMobile(userId);
	}
	
	public MapEntity getOrderBySrcId(String srcId) {//获取工单
		
		return tChannelDao.getOrderBySrcId(srcId);
		
	}
	
	public MapEntity getTemplate(String id) {//获取模板
		
		return tChannelDao.getTemplate(id);
		
	}

}