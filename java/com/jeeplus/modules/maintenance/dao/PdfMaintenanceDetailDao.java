package com.jeeplus.modules.maintenance.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceCode;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2018-12-25.
 */
@MyBatisDao
public interface PdfMaintenanceDetailDao extends CrudDao<PdfMaintenanceDetail> {

	void delByMaintId(@Param(value = "maintenanceId") String maintenanceId);
    //添加维保公司  类型表
	Integer saveMaintenanceCode(PdfMaintenanceCode pdfMaintenanceCode);

	// 获取第一维保公司
	String maxOrderNo(@Param(value = "codeId") String codeId, @Param(value = "typeId") String typeId,
			@Param(value = "orgParentId") String orgParentId,@Param(value = "maintenanceId") String maintenanceId);
	//跟新维保人员的的时间
//	Integer updateMainDate(@Param(value = "newDate") String newDate,@Param(value = "codeId") String codeId, @Param(value = "typeId") String typeId,
//			@Param(value = "orgId") String orgId,@Param(value = "mainDetailId") String mainDetailId,@Param(value = "oldMainDetailId)") String oldMainDetailId);
	
	//跟新维保人员的的时间
	Integer updateMaintainDate(@Param(value = "newDate") String newDate,@Param(value = "codeId") String codeId, @Param(value = "typeId") String typeId,
			@Param(value = "orgParentId") String orgParentId,@Param(value = "mainDetailId") String mainDetailId
			);
	
//	MapEntity selectDetail(@Param(value = "id") String id);
	
	Integer deleteDetail(@Param(value = "mainDatailId") String mainDatailId,
			@Param(value = "time") String time);
	//获取区域下所有集合
	List<String> selectOrgIds(@Param(value = "orgParentId") String orgParentId);
	//公司明细
	List<PdfMaintenanceDetail> selectMaintenanceDetail(@Param(value = "mainDatailId") String mainDatailId);
    //区域下所有类型
	List<MapEntity> codeMainList(@Param(value = "orgParentId") String orgParentId,@Param(value = "mainDetailId") String mainDetailId);
//    维保人员负责的的类型
	List<MapEntity> selectPdfListBycontact(@Param(value = "contacts") String contacts);
	List<MapEntity> selectCodeListByDetail(@Param(value = "mainDetailId") String mainDetailId);
	
	List<MapEntity> getMainDetailList(@Param(value = "id") String id);
}
