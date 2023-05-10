package com.jeeplus.modules.pdfData.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TOrg;

import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2019/1/4.
 */
@MyBatisDao
public interface EnergySaveDao extends CrudDao<TOrg> {

    double countMonitoring(Map map);//统计总能耗

    List<Map> historyMonitoring(Map map);//历史能耗

    double countTypeMonitoring(Map map);//统计类型能耗数

    double getYesHour(Map map);//同比昨日

    double getYesDay(Map map);//同比上月

    double getYesYear(Map map);//同比去年

    List<Map> countYearTop10(Map map);//本年度能耗排行top10

    List<Map> countYearMonByHour(Map map);//本年能耗分布按日

    List<Map> countYearMonByMonth(Map map);//本年能耗分布按月
    
    
}
