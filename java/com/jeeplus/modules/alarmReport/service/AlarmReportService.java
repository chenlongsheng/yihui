package com.jeeplus.modules.alarmReport.service;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.alarmReport.dao.AlarmReportDao;
import com.jeeplus.modules.warm.entity.PdfOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/24.
 */
@Service
public class AlarmReportService extends CrudService<AlarmReportDao,PdfOrder> {
    @Autowired
    AlarmReportDao alarmReportDao;


    public Map countAlarmType(PdfOrder pdfOrder){
    	
    	    	
        List<Map> list = alarmReportDao.countAlarmType(pdfOrder);
        List legend = new ArrayList();
        List series  = new ArrayList();
        Map resultMap = new HashMap();
        if (list.size()==0){
            return resultMap;
        }
        for(int i=0;i<list.size();i++){
            Map map = new HashMap();
            legend.add(list.get(i).get("name"));
            map.put("name",list.get(i).get("name"));
            map.put("type","bar");
            List list1 = new ArrayList();
            list1.add(list.get(i).get("total"));list1.add(list.get(i).get("state0"));list1.add(list.get(i).get("state1"));
            list1.add(list.get(i).get("state2"));list1.add(list.get(i).get("state3"));list1.add(list.get(i).get("state4"));
            list1.add(list.get(i).get("state5"));
            
            map.put("data",list1);
            series.add(map);
        }        
        resultMap.put("table",alarmReportDao.countAlarmType1(pdfOrder));
        resultMap.put("legend",legend);
        resultMap.put("series",series);
        return resultMap;
    }
    
    
    
    public Map countPartmentAlarm(PdfOrder pdfOrder){
        if(pdfOrder.getAlarmAddr()==null){
            pdfOrder.setAlarmAddr("");
        }
        Map resultMap = new HashMap();
        List<Map> list = alarmReportDao.countPartmentAlarm(pdfOrder);
        resultMap.put("data",list);
        pdfOrder.setPage(null);
        List list1 =  alarmReportDao.countPartmentAlarm(pdfOrder);
        resultMap.put("length",list1.size());
        return resultMap;
    }

    public List<Map> getOrgList(){
        return alarmReportDao.getOrgList();
    }
}
