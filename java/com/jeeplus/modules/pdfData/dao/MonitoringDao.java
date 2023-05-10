package com.jeeplus.modules.pdfData.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TOrg;

import java.util.Map;

/**
 * Created by ZZUSER on 2019/1/4.
 */
@MyBatisDao
public interface MonitoringDao extends CrudDao<TOrg> {

    int countMonitoring(Map map);//统计视频设备总数

    double countMonthEnergy(Map map);//本月能耗
}
