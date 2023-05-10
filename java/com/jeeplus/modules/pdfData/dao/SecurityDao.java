package com.jeeplus.modules.pdfData.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TOrg;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * Created by ZZUSER on 2018/12/26.
 */
@MyBatisDao
public interface SecurityDao extends CrudDao<TOrg> {

    List<Map> getDevList(Map map);//获取某个区域下某种类型的设备

    String getMaxfaultTime(Map map);//获取出现故障的最大时间

    int countOpen(@Param(value ="orgId")String orgId);//获取门开启个数

    int countAlarm(Map map);//本月新增报警数

    List<Map> countHourOpen(Map map);//获取时间段平均开门次数

    List<Map> countOpenTop(Map map);//开门次数top10

    String getMinCreateDate(Map map);//获取最早添加的设备的时间

	String getLatestAlarmTime(Map map);
}
