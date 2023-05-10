package com.jeeplus.modules.warm.service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.warm.dao.PdfOrderRecorderDao;
import com.jeeplus.modules.warm.entity.PdfOrderRecorder;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ZZUSER on 2018/12/7.
 */
@Service
public class PdfOrderRecorderService extends CrudService<PdfOrderRecorderDao, PdfOrderRecorder> {

	@Autowired
	PdfOrderRecorderDao pdfOrderRecorderDao;
	
	
	
	public List<MapEntity> findPage(MapEntity entity) {
//		entity.setPage(page);
		List<MapEntity> list = pdfOrderRecorderDao.getTemplateList(entity);
		for (MapEntity mapEntity : list) {
			String templateDtail= (String) mapEntity.get("templateDetail");

//			templateDtail =  templateDtail.replace("${createDate}", "[[工单创建时间]]");
//			templateDtail=  templateDtail.replace("${orgName}", "[[具体区域]]");
//			templateDtail =  templateDtail.replace("${deviceName}", "[[设备名称]]");
//			templateDtail=  templateDtail.replace("${devType}", "[[设备类型]]");
//			templateDtail =  templateDtail.replace("${alarmType}", "[[报警类型]]");
//			templateDtail =  templateDtail.replace("${alarmLevel}", "[[报警级别]]");
			mapEntity.put("templateDetail",templateDtail);		
			
		}
//		page.setList(list);
		return list;
	}

	
	public List<MapEntity> mobanList(MapEntity entity) {

		List<MapEntity> list = pdfOrderRecorderDao.getTemplateList(entity);
		
		return list;
	}
	
	
	// 添加模板
	@Transactional(readOnly = false)
	public void insertTemplateDetail(String userId, String templateDetail) {

		pdfOrderRecorderDao.insertTemplateDetail(userId, templateDetail);

	}

	public List<MapEntity> getTemplateList(MapEntity entity) {

		return pdfOrderRecorderDao.getTemplateList(entity);
	}
	
	
	@Transactional(readOnly = false)
   public void deleteTemplate(String id) {
	   
	   pdfOrderRecorderDao.deleteTemplate(id);
   }
   
   @Transactional(readOnly = false)
   public void updateTemplate(String templateDetail,String id) {
	   
	   pdfOrderRecorderDao.updateTemplate(templateDetail, id);
	   
   }
}
