package com.jeeplus.modules.pdfData.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TOrg;

import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/28.
 */
@MyBatisDao
public interface EnvironmentDao extends CrudDao<TOrg> {

    List<Map> getAvgTop10(Map map);//获取类型平均数top10配电房

    List<Map> countAlarmByType(Map map);//报警类型占比

    Double countMonthMonitoring(Map map);//获取本月能耗

    List<Map> countMonTop10(Map map);//能耗排行top10

	List<Map> getAvgTop10_2(Map map);

	int getDeviceOnlineCount(Map map);

	int getDeviceCount(Map map);

	int getWaterDeviceDayAlarm(int type, int type_id);

	int getWaterDeviceDayAlarm(Map map);

	int getDeviceOfflineCount(Map map);
}
