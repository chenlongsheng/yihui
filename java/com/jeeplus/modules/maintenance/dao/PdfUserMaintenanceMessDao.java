package com.jeeplus.modules.maintenance.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.maintenance.entity.PdfMaintenance;
import com.jeeplus.modules.maintenance.entity.PdfUser;
import com.jeeplus.modules.maintenance.entity.PdfUserMaintenanceMess;

import scala.annotation.meta.param;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2018-12-25.
 */
@MyBatisDao
public interface PdfUserMaintenanceMessDao  extends CrudDao<PdfUserMaintenanceMess>   {
    //分页查询
	List<PdfUserMaintenanceMess> messagelist(PdfUserMaintenanceMess pdfUserMaintenanceMess);
	
	List<MapEntity> userDetail(PdfUserMaintenanceMess pdfUserMaintenanceMess);
	
	List<MapEntity> maintenanceDetail(PdfUserMaintenanceMess pdfUserMaintenanceMess);

//改变我方的排序
	Integer changeUserOrder(MapEntity entity);
	//改变维保排序
	Integer changeMainOrder(MapEntity entity);
	
	
}
