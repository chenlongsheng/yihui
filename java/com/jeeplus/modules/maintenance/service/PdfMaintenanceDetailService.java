package com.jeeplus.modules.maintenance.service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.maintenance.dao.PdfMaintenanceAptitudeDao;
import com.jeeplus.modules.maintenance.dao.PdfMaintenanceDao;
import com.jeeplus.modules.maintenance.dao.PdfMaintenanceDetailDao;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2018-12-25.
 */
@Service
@Transactional(readOnly = true)
public class PdfMaintenanceDetailService extends CrudService<PdfMaintenanceDetailDao, PdfMaintenanceDetail> {

	@Autowired
	PdfMaintenanceDao pdfMaintenanceDao;
	@Autowired
	PdfMaintenanceDetailDao pdfMaintenanceDetailDao;
	@Autowired
	PdfMaintenanceAptitudeDao pdfMaintenanceAptitudeDao;
	@Autowired
	private PdfUserService pdfUserService;

	// 公司明细
	public List<PdfMaintenanceDetail> selectMaintenanceDetail(String maintenanceId) {

		List<PdfMaintenanceDetail> list = pdfMaintenanceDetailDao.selectMaintenanceDetail(maintenanceId);
		for (PdfMaintenanceDetail pdfMaintenanceDetail : list) {

			List<MapEntity> strList = pdfMaintenanceDetailDao.codeMainList(pdfMaintenanceDetail.getOrgParentId(),
					pdfMaintenanceDetail.getId());
			String orgParentName = pdfUserService.orgName(pdfMaintenanceDetail.getOrgParentId());
			pdfMaintenanceDetail.setOrgParentName(orgParentName);
			pdfMaintenanceDetail.setList(strList);
		}
		return list;
	}

	// 维保设备类型
	public List<MapEntity> selectCodeList(String contacts) {

		List<MapEntity> PdfList = pdfMaintenanceDetailDao.selectPdfListBycontact(contacts);//
		
		for (MapEntity mapEntity : PdfList) {
			String parentId = (Long) mapEntity.get("parentId") + "";
			String mainDetailId = (Long) mapEntity.get("mainDetailId") + "";
//			String orgId = (Long) mapEntity.get("orgId") + "";

			List<MapEntity> selectCodeList = pdfMaintenanceDetailDao.selectCodeListByDetail(mainDetailId);

			mapEntity.put("codeList", selectCodeList);
			String parentName = pdfUserService.orgName(parentId);
			mapEntity.put("parentName", parentName);
		}
		return PdfList;
	}

	// 公司明细集合
	public List<MapEntity> getMainDetailList(String id) {

		return pdfMaintenanceDetailDao.getMainDetailList(id);

	}

}
