package com.jeeplus.modules.maintenance.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceAptitude;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2018-12-26.
 */
@MyBatisDao
public interface PdfMaintenanceAptitudeDao extends CrudDao<PdfMaintenanceAptitude> {
    void delByMaintId(@Param(value = "maintenanceId") String maintenanceId);
}
