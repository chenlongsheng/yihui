package com.jeeplus.modules.maintenance.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.IdGenSnowFlake;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.maintenance.dao.PdfMaintenanceAptitudeDao;
import com.jeeplus.modules.maintenance.dao.PdfMaintenanceDao;
import com.jeeplus.modules.maintenance.dao.PdfMaintenanceDetailDao;
import com.jeeplus.modules.maintenance.dao.PdfUserDao;
import com.jeeplus.modules.maintenance.dao.PdfUserMaintenanceMessDao;
import com.jeeplus.modules.maintenance.entity.PdfMaintenance;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceAptitude;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceCode;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;
import com.jeeplus.modules.maintenance.entity.PdfUser;
import com.jeeplus.modules.maintenance.entity.PdfUserMaintenanceMess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018-12-25.
 */
@Service
@Transactional(readOnly = true)
public class PdfUserMaintenanceMessService extends CrudService<PdfUserMaintenanceMessDao, PdfUserMaintenanceMess> {
	@Autowired
	PdfMaintenanceDao pdfMaintenanceDao;
	@Autowired
	PdfMaintenanceDetailDao pdfMaintenanceDetailDao;
	@Autowired
	PdfUserMaintenanceMessDao pdfUserMaintenanceMessDao;
	@Autowired
	private PdfUserService pdfUserService;

	public Page<PdfUserMaintenanceMess> findPage(Page<PdfUserMaintenanceMess> page,
			PdfUserMaintenanceMess pddfUserMaintenanceMess) {

		pddfUserMaintenanceMess.setPage(page);
		List<PdfUserMaintenanceMess> list = pdfUserMaintenanceMessDao.messagelist(pddfUserMaintenanceMess);
		for (PdfUserMaintenanceMess pdfUserMaintenanceMess : list) {

			String orgParentId = pdfUserMaintenanceMess.getOrgParentId();
			String orgParentName = pdfUserService.orgName(orgParentId);
			pdfUserMaintenanceMess.setOrgParentName(orgParentName);
		}
		page.setList(list);
		return page;
	}

	// 我方人员信息
	public List<MapEntity> userDetail(PdfUserMaintenanceMess pdfUserMaintenanceMess) {

		return pdfUserMaintenanceMessDao.userDetail(pdfUserMaintenanceMess);

	}

	// 维保方人员信息
	public List<MapEntity> maintenanceDetail(PdfUserMaintenanceMess pdfUserMaintenanceMess) {

		return pdfUserMaintenanceMessDao.maintenanceDetail(pdfUserMaintenanceMess);

	}

	// 我方排序改变
	@Transactional(readOnly = false)
	public Integer changeUserOrder(MapEntity entity) {

		return pdfUserMaintenanceMessDao.changeUserOrder(entity);
	}

	// 维包方排序改变
	@Transactional(readOnly = false)
	public Integer changeMainOrder(MapEntity entity) {

		return pdfUserMaintenanceMessDao.changeMainOrder(entity);
	}

}
