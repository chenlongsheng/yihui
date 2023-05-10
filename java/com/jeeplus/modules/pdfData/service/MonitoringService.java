package com.jeeplus.modules.pdfData.service;

import com.jeeplus.common.service.CrudService;
//import com.jeeplus.modules.pdfData.dao.SecurityDao;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.modules.pdfData.dao.MonitoringDao;
import com.jeeplus.modules.settings.entity.TOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2019/1/4.
 */
@Service
public class MonitoringService extends CrudService<MonitoringDao,TOrg> {
    @Autowired
    FireFightingService fireFightingService;

    @Autowired
    MonitoringDao monitoringDao;

    @Autowired
    EnergySaveService energySaveService;

    public Map getAllData(String orgId, int type){
        Map resultMap = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        String countOnline = fireFightingService.countOnline(orgId,type);//监控在线率
        int num = countMonitoring(orgId,type);//视频设备总数
        List<Map> OutlineTop10 = fireFightingService.getOutlineTop10(orgId,type);//监控离线率top10
        double countMonthEnergy = countMonthEnergy(orgId,type);//本月能耗
        List<Map> countYearTop10 = energySaveService.countYearTop10(orgId,type);//年度能耗排行
        resultMap.put("countOnline",countOnline);
        resultMap.put("num",num);
        resultMap.put("OutlineTop10",OutlineTop10);
        resultMap.put("countMonthEnergy",countMonthEnergy);
        resultMap.put("countYearTop10",countYearTop10);
        return resultMap;
    }

    public int countMonitoring(String orgId,int type){
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        Map map = new HashMap();
        map.put("orgId",orgId);
        map.put("type",type);
        return monitoringDao.countMonitoring(map);
    }

    public double countMonthEnergy(String orgId,int type){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        Map map = new HashMap();
        map.put("orgId",orgId);
        map.put("type",type);
        map.put("date",formatter.format(new Date()));
        return monitoringDao.countMonthEnergy(map);
    }

    //本年能耗top10
    public List<Map> yearEnergyTop10(String orgId,int type){
        return energySaveService.countYearTop10(orgId,type);
    }
}
