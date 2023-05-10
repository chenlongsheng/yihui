package com.jeeplus.modules.maintenance.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.maintenance.entity.PdfMaintenance;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2018-12-25.
 */
@MyBatisDao
public interface PdfMaintenanceDao extends CrudDao<PdfMaintenance> {

	void deleteById(@Param(value = "id") String id);

	// 添加公司
	Integer saveMaintenance(PdfMaintenance maintenance);

	// 分页查询公司
	List<PdfMaintenance> findMaintenList(PdfMaintenance entity);

	// 查询图片地址
	List<String> findPicList(@Param(value = "maintenanceId") String maintenanceId);
	//删除改变第二责任人为第一
	List<MapEntity> selectMainCodeList(@Param(value ="maintenanceId")String maintenanceId);
	
	List<MapEntity> secondOrder(@Param(value = "codeId") Integer codeId, @Param(value = "typeId") Integer typeId,
			@Param(value = "orgId") Long orgId);
	
	Integer updateMainOrder(@Param(value = "id") String id);
	
	
	
	
	
	
	
	
	
	
	
	
}
