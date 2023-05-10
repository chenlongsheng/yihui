package com.jeeplus.modules.alarmReport.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.warm.entity.PdfOrder;

import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/24.
 */
@MyBatisDao
public interface AlarmReportDao extends CrudDao<PdfOrder> {

    List<Map> countAlarmType(PdfOrder pdfOrder);//报警类型统计表
    
    List<Map> countAlarmType1(PdfOrder pdfOrder);//报警类型统计表

    List<Map> countPartmentAlarm(PdfOrder pdfOrder);//单位处理情况表

    List<Map> getOrgList();//获取区域
    
    
}
