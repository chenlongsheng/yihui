package com.jeeplus.modules.settings.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.IdGenSnowFlake;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;
import com.jeeplus.modules.settings.dao.EMapConfigDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.dao.TDeviceDetailDao;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.warm.service.PdfOrderRecorderService;

@Service
@Transactional(readOnly = true)
public class EMapConfigService {

	@Autowired
	EMapConfigDao eMapConfigDao;

	@Autowired
	TDeviceDao tDeviceDao;
	@Autowired
	TDeviceDetailDao tDeviceDetailDao;

	private List<MapEntity> dvacDevice;

	public List<Map<String, Object>> getChannelListByOrgId(String orgId) {
		return eMapConfigDao.getChannelListByOrgId(orgId);
	}

	public List<Map<String, Object>> getDeviceListByOrgId(String orgId) {
		return eMapConfigDao.getDeviceListByOrgId(orgId);
	}

	// 获取device地图的type类型
	public List<MapEntity> eMapSelect(String orgId) {
		return eMapConfigDao.eMapSelect(orgId);
	}

	// 获取channel地图的type类型
	public List<MapEntity> eMapChannelSelect(String orgId) {
		return eMapConfigDao.eMapChannelSelect(orgId);
	}

	// 获取类型集合
	public List<MapEntity> getCodeList(String orgId) {

		List<MapEntity> codeList = eMapConfigDao.getCodeList(orgId);
		for (MapEntity entity : codeList) {

			int devTypeId = (int) entity.get("id");
			int typeId = (int) entity.get("typeId");

			List<MapEntity> devList = null;
			if (typeId == 1) {
				devList = eMapConfigDao.devList(devTypeId + "", orgId);
			} else {
				devList = eMapConfigDao.chList(devTypeId + "", typeId + "", orgId);
			}
			entity.put("devList", devList);
		}

		return codeList;
	}

	// 获取暖通类型集合
	public List<MapEntity> getHavcTypeList() {

		return eMapConfigDao.getHavcTypeList();

	}

	// 设备通道的
	public List<MapEntity> devChList(String devTypeId, String orgId, Integer typeId) {

		if (typeId == 1) {
			return eMapConfigDao.devList(devTypeId, orgId);
		} else {
			return eMapConfigDao.chList(devTypeId, typeId + "", orgId);
		}

	}

	// 修改设备xy
	@Transactional(readOnly = false)
	public String updateDevCoords(String id, String pdfImageId, String coordsX, String coordsY, String devId,
			Integer typeId) {// 设备通道id,主类型typeId

		if (StringUtils.isNotBlank(id)) {// 修改时候
			eMapConfigDao.deleteImageDevIdById(id);// 删除图片中设备根据id
		}
		if (typeId == 1) {
//			if(StringUtils.isNotBlank(devId)) {				
			eMapConfigDao.updateDevCoords(coordsX, coordsY, devId);
//			}else {
//				TDevice tDevice =  new TDevice();
//				tDevice.setOrgId("100000");
//				tDevice.preInsert();				
//				tDeviceDao.insert(tDevice);
//				devId = tDevice.getId();
//				TDeviceDetail tDeviceDetail =  new TDeviceDetail();
//				tDeviceDetail.setId(devId);
//				tDeviceDetailDao.insert(tDeviceDetail);
//			}
		} else {
			eMapConfigDao.updateChCoords(coordsX, coordsY, devId);
		}
		String imageDevId = IdGenSnowFlake.uuid().toString();

		eMapConfigDao.insertImageDevId(imageDevId, pdfImageId, devId, typeId + "");// 添加设备归属图片

		return imageDevId;
	}

	// 修改暖通设备被绑定的xy
	@Transactional(readOnly = false)
	public String updateHvacDevCoords(String id, String hvacDevId, String coordsX, String coordsY, String devId,
			Integer typeId) {// 设备通道id,主类型typeId

		if (StringUtils.isNotBlank(id)) {// 修改时候
			eMapConfigDao.deleteHvacDevIdById(id);// 删除图片中设备根据id
		}
		if (typeId == 1) {
			eMapConfigDao.updateDevCoords(coordsX, coordsY, devId);

		} else {
			eMapConfigDao.updateChCoords(coordsX, coordsY, devId);
		}
		String hvacId = IdGenSnowFlake.uuid().toString();

		eMapConfigDao.insertHvacDevId(hvacId, hvacDevId, devId, typeId + "");// 添加设备归属的暖通主机

		return hvacId;
	}

	// 删除图片中设备id
	@Transactional(readOnly = false)
	public void deleteImageDevId(String devChId, Integer typeId) {

		eMapConfigDao.deleteImageDevId(devChId, typeId + "");// 删除图片中设备
		if (typeId == 1) {
			eMapConfigDao.updateDevCoords(null, null, devChId);// coordsX==null
		} else {
			eMapConfigDao.updateChCoords(null, null, devChId);
		}
	}

	// 删除图片中设备id
	@Transactional(readOnly = false)
	public void deleteHvacDevId(String devChId, Integer typeId) {

		eMapConfigDao.deleteHvacDevId(devChId, typeId + "");// 删除图片中设备
		if (typeId == 1) {
			eMapConfigDao.updateDevCoords(null, null, devChId);// coordsX==null
		} else {
			eMapConfigDao.updateChCoords(null, null, devChId);
		}
	}

	// 获取图片下配电房小图片集合
	public List<MapEntity> selectImageDevList(String pdfImageId, String orgId) {

		List<MapEntity> selectImageDevList = eMapConfigDao.selectImageDevList(pdfImageId, orgId);

		for (MapEntity entity : selectImageDevList) {

			String devTypeId = (String) entity.get("devTypeId");
			String typeId = (String) entity.get("typeId");
			List<MapEntity> devList = null;
			List<MapEntity> devChList = null;
			if (Integer.parseInt(typeId) == 1) {
				devList = eMapConfigDao.devList(devTypeId, orgId);
				devChList = eMapConfigDao.selectChNameByDevId((String) entity.get("devId"));

			} else if (Integer.parseInt(typeId) == 9) {
				devList = eMapConfigDao.chList(devTypeId, typeId + "", orgId);

				devChList = eMapConfigDao.selectVideoByChId((String) entity.get("devId"));

			} else {
				devList = eMapConfigDao.chList(devTypeId, typeId + "", orgId);

				devChList = eMapConfigDao.selectChNameByChId((String) entity.get("devId"));
			}
			entity.put("devList", devList);
			entity.put("devChList", devChList);
		}
		return selectImageDevList;
	}

	// 全部保存图片下小图标
	@Transactional(readOnly = false)
	public void saveDevPicture(JSONArray ja, String pdfImageId, String containerWidth, String containerHeight) {

		eMapConfigDao.deleteImageDev(pdfImageId);
		eMapConfigDao.updatePdfImageContain(pdfImageId, containerWidth, containerHeight);
		if (ja != null) {

			for (int i = 0; i < ja.size(); i++) {
				MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
				String typeId = (String) entity.get("typeId");
				if (Integer.parseInt(typeId) == 1) {
					eMapConfigDao.updateDevCoords((String) entity.get("coordsX"), (String) entity.get("coordsY"),
							(String) entity.get("devId") + "");
				} else {
					eMapConfigDao.updateChCoords((String) entity.get("coordsX"), (String) entity.get("coordsY"),
							(String) entity.get("devId") + "");
				}
				eMapConfigDao.insertImageDevId(IdGenSnowFlake.uuid().toString(), pdfImageId,
						(String) entity.get("devId") + "", typeId + "");// 添加设备归属图片
			}
		}
	}

	// 获取小图标集合 ,orgId
	public MapEntity getImageDev(String id) {
		return eMapConfigDao.getImageDev(id);
	}

	// 更新新图片
	@Transactional(readOnly = false)
	public void updateImageById(String image, String imageId) {

		eMapConfigDao.updateImageById(image, imageId);
	}

	/*
	 * 
	 * 暖通被绑定的设备类型
	 */
	public List<MapEntity> gethvacListByOrgId(String orgId) {

		List<MapEntity> gethvacListByOrgId = eMapConfigDao.gethvacListByOrgId(orgId);
		for (MapEntity entity : gethvacListByOrgId) {

			int devTypeId = (int) entity.get("devType");
			int typeId = (int) entity.get("typeId");

			List<MapEntity> devList = null;
			if (typeId == 1) {
				devList = eMapConfigDao.devHvacList(devTypeId + "", orgId);
			} else {
				devList = eMapConfigDao.chHvacList(devTypeId + "", typeId + "", orgId);
			}
			entity.put("devList", devList);
		}
		return gethvacListByOrgId;
	}

	@Transactional(readOnly = false)
	public String insertDevice(String pdfImageId, String name, String orgId, String devType, String typeId,
			String coordsX, String coordsY) {

		String devChId = IdGenSnowFlake.uuid().toString();
		eMapConfigDao.insertDevice(devChId, name, orgId, devType, coordsX, coordsY);
		eMapConfigDao.insertImageDevId(IdGenSnowFlake.uuid().toString(), pdfImageId, devChId, typeId);
		return devChId;

	}

	// 后台配置
	public MapEntity selectHvacDevList(String hvacDevId, String orgId) {
		MapEntity en = new MapEntity();
		List<MapEntity> selectHvacDevList = eMapConfigDao.selectHvacDevList(hvacDevId, orgId);
		for (MapEntity entity : selectHvacDevList) {
			String devTypeId = (String) entity.get("devTypeId");
			String typeId = (String) entity.get("typeId");
			List<MapEntity> devList = null;
			if (Integer.parseInt(typeId) == 1) {
				devList = eMapConfigDao.devList(devTypeId, orgId);
			} else {
				devList = eMapConfigDao.chList(devTypeId, typeId + "", orgId);
			}
			entity.put("devList", devList);
		}

		List<MapEntity> hvacDevice = eMapConfigDao.getDvacDevice(hvacDevId);
		en.put("selectHvacDevList", selectHvacDevList);
		en.put("hvacDevice", hvacDevice);

		return en;
	}

	@Transactional(readOnly = false)
	public List<MapEntity> modifyHvaceById(String hvacDevId, String name, String devImageName) {

		eMapConfigDao.modifyHvaceById(hvacDevId, name, devImageName);

		return null;
	}

	// 全部保存主机暖通图片下小图标
	@Transactional(readOnly = false)
	public void saveHvacDevPicture(JSONArray ja, String hvacDevId, String containerWidth, String containerHeight) {

		eMapConfigDao.deleteHvacDev(hvacDevId);
//			eMapConfigDao.updatePdfImageContain(hvacDevId, containerWidth, containerHeight);
		if (ja != null) {
			for (int i = 0; i < ja.size(); i++) {
				MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
				String typeId = (String) entity.get("typeId");
				if (Integer.parseInt(typeId) == 1) {
					eMapConfigDao.updateDevCoords((String) entity.get("coordsX"), (String) entity.get("coordsY"),
							(String) entity.get("devId"));
				} else {
					eMapConfigDao.updateChCoords((String) entity.get("coordsX"), (String) entity.get("coordsY"),
							(String) entity.get("devId"));
				}
				eMapConfigDao.insertHvacDevId(IdGenSnowFlake.uuid().toString(), hvacDevId,
						(String) entity.get("devId") + "", typeId + "");// 添加设备归属图片
			}
		}
	}

	// 全部保存主机暖通图片下小图标
	@Transactional(readOnly = false)
	public void delHvacDevs(String hvacDevId) {

		eMapConfigDao.delHvacDevs(hvacDevId);

	}

	// 首页配电房地图
	public MapEntity getHvacDevList(String hvacDevId, String orgId) {
		MapEntity en = new MapEntity();
		List<MapEntity> selectHvacDevList = eMapConfigDao.selectHvacDevList(hvacDevId, orgId);
		for (MapEntity entity : selectHvacDevList) {
			String devId = (String) entity.get("devId");
			String typeId = (String) entity.get("typeId");
			List<MapEntity> devList = null;
			if (Integer.parseInt(typeId) == 1) {
				devList = eMapConfigDao.getHavcChannelByDevId(devId);
				entity.put("devList", devList);
			}
		}

		List<MapEntity> hvacDevice = eMapConfigDao.getDvacDevice(hvacDevId);
		en.put("selectHvacDevList", selectHvacDevList);
		en.put("hvacDevice", hvacDevice);

		return en;
	}

}
