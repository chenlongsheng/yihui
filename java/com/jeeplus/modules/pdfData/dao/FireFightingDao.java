package com.jeeplus.modules.pdfData.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TOrg;

import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/26.
 */
@MyBatisDao
public interface FireFightingDao extends CrudDao<TOrg> {
    //烟感

    int countOnline(Map map);//统计烟感

    int countOnline1(Map map);

    int countDayAlarm(Map map);//统计当天新增报警数

    int countMonthAlarm(Map map);//统计本月新增报警数

    List<Map> getOutlineTop10(Map map);//烟感实时离线率top10

    List<Map> getOutlineTp101(Map map);//烟感实时离线率top10

    Map countElectric(Map map);//烟感实时电量

    List<Map> alarmTop10(Map map);

    //以下电器火灾监控探测器

    Map wiretemperature(Map map);//电线温度，过线电流，剩余电流等

    List<Map> countHour(Map map);//各时段

    List<Map> residueTop10(Map map);//剩余电流实时峰值top10

    List<Map> countAlarmByType(Map map);//报警类型实时占比

    Map countTemperature(Map map);//各温度区间探测器实时数量占比

    List<Map> temperatureTop5(Map map);//高温配电房Top5

    Map waterGage(Map map);//水压区间

    List<Map> waterGageTop5(Map map);//管网水压排行实时Top5

    Map devGage(Map map);//设备压力实时占比
    
    
    
    

	Map wiretemperature2(Map map);
}
