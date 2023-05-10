/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctc.wstx.util.StringUtil;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TCodeDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.dao.TIdLinkIdDao;
import com.jeeplus.modules.settings.entity.PdfLink;
import com.jeeplus.modules.settings.entity.TAlarmPolicy;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TIdLinkId;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.warm.service.PdfOrderRecorderService;

/**
 * 联动管理Service
 * 
 * @author long
 * @version 2018-08-08
 */
@Service
@Transactional(readOnly = true)
public class TIdLinkIdService extends CrudService<TIdLinkIdDao, TIdLinkId> {
	@Autowired
	private TCodeDao tCodeDao;
	@Autowired
	private TIdLinkIdDao tIdLinkIdDao;

	@Autowired
	PdfOrderRecorderService pdfOrderRecorderService;

	public TIdLinkId get(String id) {
		return super.get(id);
	}

	public List<TIdLinkId> findList(TIdLinkId tIdLinkId) {
		return super.findList(tIdLinkId);
	}

	public Page<MapEntity> findPage(Page<MapEntity> page, MapEntity entity) {

		entity.setPage(page);
		page.setList(tIdLinkIdDao.linkCheckList(entity));
		return page;
	}

	@Transactional(readOnly = false)
	public void save(JSONArray ja, String pdfLinkId, String status) {

		// 先删除pdfLinkId,后添加
		tIdLinkIdDao.deleteLinkCheckByCheckIds(pdfLinkId, status);

		for (int i = 0; i < ja.size(); i++) {
			TIdLinkId tIdLinkId = JSONObject.parseObject(ja.get(i).toString(), TIdLinkId.class);
			tIdLinkId.setPdfLinkId(pdfLinkId);
			tIdLinkId.setStatus(status);
			if (tIdLinkId.getNumber() != null) {
			}
			if (StringUtils.isBlank(tIdLinkId.getLevel())) {
				tIdLinkId.setLevel("1");
			}
			tIdLinkIdDao.insertLinkCheck(tIdLinkId);// 插入联动二级表
			String id = tIdLinkId.getId();// 二级表id
			System.out.println(tIdLinkId.toString());
			tIdLinkId.setPdfLinkId("1");
			tIdLinkId.getChType();
			tIdLinkId.getTypeId();

			String devIds = tIdLinkId.getDevIds();
			String devLinkIds = tIdLinkId.getDevLinkIds();// 通道集合id
			String[] devIdList = devIds.split(",");// 源设备id
			String[] chLinkIdList = devLinkIds.split(",");// 联动通道
			for (String devId : devIdList) {// 插入联动三级表
				String chId = tIdLinkIdDao.getChId(devId, tIdLinkId.getChType(), tIdLinkId.getTypeId());// 查询源通道id
				System.out.println(chId + "======");
				tIdLinkId.setSrcId(Long.parseLong(chId));
				for (String chLinkId : chLinkIdList) {
					tIdLinkId.setDestId(Long.parseLong(chLinkId));
					System.out.println(tIdLinkId.toString());
					tIdLinkId.preInsert();
					String number ="";
					if (StringUtils.isNotBlank(tIdLinkId.getNumber())) {
						  number = ","+tIdLinkId.getNumber();
					}					
					if (StringUtils.isBlank(tIdLinkId.getActionId())) {
						tIdLinkId.setActionId(null);
					}
					tIdLinkId.setExtParams(tIdLinkId.getAirId()+number);				
					tIdLinkIdDao.insert(tIdLinkId);// 联动表
					tIdLinkIdDao.insertPdfLinkTLink(id, tIdLinkId.getId()); // 插入关联表
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void delete(TIdLinkId tIdLinkId) {
		super.delete(tIdLinkId);
	}

	// -------------------------------

	public List<MapEntity> codeList() {
		return tIdLinkIdDao.codeList();
	}

	// 更改启用禁用
	@Transactional(readOnly = false)
	public Integer saveUse(TIdLinkId tIdLinkId) {
		return tIdLinkIdDao.saveUse(tIdLinkId);
	}

	// 区域 通道底下
	public List<Area> findAreaList(String name) {
		return tIdLinkIdDao.findAreaList(name);
	}

	// ----------------------------------------

	// 回调添加联动,设备类型 1
	public MapEntity getCodeList() {
		MapEntity entity = new MapEntity();
		entity.put("devTypeList", tIdLinkIdDao.getCodeList());// 设备类型 1
		entity.put("TypeIdList", tIdLinkIdDao.getCodeTypeList());// 联动类型 4
		return entity;

	}

	// 回调添加联动,设备类型 1
	public MapEntity getCodeTypeList() {
		MapEntity entity = new MapEntity();
		entity.put("devTypeList", tIdLinkIdDao.getCodeList());// 设备类型 1
		entity.put("levelist", tIdLinkIdDao.pdfCodeParam("8"));// 等级集合
		entity.put("getUserList", tIdLinkIdDao.getUserList());// 用户集合
		entity.put("orderList", pdfOrderRecorderService.findPage(new MapEntity()));// 用户集合
		return entity;
	}

	// 回调中
	public MapEntity getDeviceListByType(String orgId, String devType, String status) {
		MapEntity entity = new MapEntity();
		entity.put("channelName", tIdLinkIdDao.getPdfCodeList(devType, status));// 3 选择通道
		entity.put("devList", tIdLinkIdDao.getDeviceListByType(orgId, devType));// 根据类型获取设备ids 2

		if (devType.equals("168")) {
			entity.put("levelist", tIdLinkIdDao.pdfCodeParam("7"));// 触点集合
		} else if (devType.equals("169")) {
			entity.put("levelist", tIdLinkIdDao.pdfCodeParam("8"));// 等级集合
		} else {
			entity.put("levelist", tIdLinkIdDao.pdfCodeParam("7"));
		}
		if (status.equals("1")) {
			entity.put("levelist", tIdLinkIdDao.pdfCodeParam("8"));// 等级集合
		}
		return entity;
	}

	// 回调获取被联动摄像头,等
	public MapEntity getVideoList(String orgId, int chType) {
		MapEntity entity = new MapEntity();
		if (chType == 2) {
			entity.put("videoList", tIdLinkIdDao.getVideoList(orgId));// 摄像头被联动
		} else if (chType == 6 || chType == 9) {
			entity.put("videoList", tIdLinkIdDao.getVideoList(orgId));// 摄像头被联动
			entity.put("positionList", tIdLinkIdDao.getPositionList());// 摄像头被联动
		} else if (chType == 8) {
			entity.put("linkChannelTypeList", tIdLinkIdDao.getCodeLinkList()); // 被联动,空调,水泵灯类型
		}
		return entity;
	}

	public List<MapEntity> linkChannelist(String orgId, String chType, String typeId) {
		return tIdLinkIdDao.linkChannelist(orgId, chType, typeId);// 几个空调设备id等.
	}

	@Transactional(readOnly = false)
	public void insertLink(PdfLink pdfLink) {
		tIdLinkIdDao.insertLink(pdfLink);
	}

	// 启用禁用策略1
	@Transactional(readOnly = false)
	public void updateNotUse(String notUse, String id) {
		tIdLinkIdDao.updateNotUse(notUse, id);
	}

	// 启用禁用联动2
	@Transactional(readOnly = false)
	public void updateCheckNotUse(String notUse, String id) {

		tIdLinkIdDao.updateCheckNotUse(notUse, id);// 2级
		tIdLinkIdDao.updateTLinkNotUse(notUse, id);// 3级

	}

	public List<MapEntity> linkList(String orgId) {
		return tIdLinkIdDao.linkList(orgId);
	}

	@Transactional(readOnly = false)
	public void deleteLink(String id) {

		tIdLinkIdDao.deleteLink(id);

	}

	// 联动开关空调参数
	public List<MapEntity> pdfCodeParam(String status) {

		return tIdLinkIdDao.pdfCodeParam(status);
	}

	// 删除联动表等
	@Transactional(readOnly = false)
	public void deleteLinkCheck(String pdfLinkId, String status) {

		tIdLinkIdDao.deleteLinkCheck(pdfLinkId, status);
	}

	public List<MapEntity> getUserList(String pdfLinkId, String status) {

		return tIdLinkIdDao.getUserList();
	}

	// 获取配电房名称
	public MapEntity orgNameBypdfLinkId(String pdfLinkId) {

		return tIdLinkIdDao.orgNameBypdfLinkId(pdfLinkId);
	}

	public MapEntity getLinkCheck(String checkId) {

		return tIdLinkIdDao.getLinkCheck(checkId);
	}


	public static void main(String[] args) {

		String s = -49 + "";
		if (s.length() == 4) {
			DecimalFormat df = new DecimalFormat("#,##");
			String d = df.format(Integer.parseInt(s));
			System.out.println(d);
		} else if (s.length() == 3) {
			DecimalFormat df = new DecimalFormat("#,#");
			String d = df.format(Integer.parseInt(s));
			System.out.println(d);
		}

	}

}