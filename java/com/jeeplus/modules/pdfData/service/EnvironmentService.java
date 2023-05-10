package com.jeeplus.modules.pdfData.service;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.modules.pdfData.dao.EnvironmentDao;
import com.jeeplus.modules.settings.entity.TOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/28.
 */
@Service
public class EnvironmentService extends CrudService<EnvironmentDao,TOrg> {
    @Autowired
    EnvironmentDao environmentDao;

    @Autowired
    SecurityService securityService;

    @Autowired
    FireFightingService fireFightingService;

    /*
    public Map getAllData(String orgId, int type, String startDate, String endDate) {
        Map resultMap = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        if(startDate ==null || startDate.length()==0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            startDate = formatter.format(new Date());
            endDate = formatter.format(new Date());
        }
        int savaDays = securityService.countSaveDays(orgId,type);//安全运行天数
        String countOnline = fireFightingService.countOnline(orgId,type);//动力系统在线率
        double countMonthMon = countMonthMontitoring(orgId,type,100013,3);
        int countMonthAlarm = fireFightingService.countMonthAlarm(orgId,type);//本月新增报警数
        Map HourOpen = securityService.countHourOpen(orgId,startDate,endDate,type,5,2);//各时段风机平均启动次数
        List<Map> countMonTop10 = countMonTop10(orgId,type,100013,3,startDate,endDate);
        resultMap.put("saveDays",savaDays);
        resultMap.put("countOnline",countOnline);
        resultMap.put("countMonthMon",countMonthMon);
        resultMap.put("countMonthAlarm",countMonthAlarm);
        resultMap.put("HourOpen",HourOpen);
        resultMap.put("countMonTop10",countMonTop10);
        return resultMap;
    }
    */

    /*
    public Map getTemAllData(String orgId,int type){
        Map resultMap = new HashMap();
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        int savaDays = securityService.countSaveDays(orgId,type);//安全运行天数
        Map tem = fireFightingService.wiretemperature(orgId,type,101,3);//温度区间
        Map humidity = fireFightingService.wiretemperature(orgId,type,102,3);//湿度区间
        int countMonthAlarm = fireFightingService.countMonthAlarm(orgId,type);//本月新增报警数
        List<Map> temAvg = getAvgTop10(orgId,type,101,3);//温度排行
        List<Map> humAvg = getAvgTop10(orgId,type,102,3);//湿度排行
        //List<Map> alarmType = countAlarmByType(orgId,type);
        resultMap.put("savaDays",savaDays);
        resultMap.put("tem",tem);
        resultMap.put("humidity",humidity);
        resultMap.put("countMonthAlarm",countMonthAlarm);
        resultMap.put("temAvg",temAvg);
        resultMap.put("humAvg",humAvg);
        //resultMap.put("alarmType",alarmType);
        return resultMap;
    }
	*/
    
    
    public List<Map> getAvgTop10(String code,int type,int chType,int typeId){
        Map map  = new HashMap();
        map.put("code",code);
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        List<Map> list = environmentDao.getAvgTop10_2(map);
        return list;
    }

    //温湿度计页报警类型统计
    public List<Map> countAlarmByType(String code,int type,int type_id){
        Map map  = new HashMap();
        map.put("code",code);
        map.put("type", type);
        map.put("type_id", type_id);
        List<Map> list = environmentDao.countAlarmByType(map);
        return list;
    }

    //本月能耗
    public double countMonthMontitoring(String orgId,int type,int chType,int typeId){
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        Map map = new HashMap();
        map.put("orgId",orgId);
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        map.put("date",new Date());
        Double result =  environmentDao.countMonthMonitoring(map);
        if(result==null){
            return 0;
        }
        return result;
    }

    //能耗top10
    public List<Map> countMonTop10(String orgId,int type,int chType,int typeId,String startDate,String endDate){
        if(orgId ==null || orgId.length()==0){
            orgId = OrgUtil.getOrgId();
//            orgId = "233993942926888973";
        }
        if(startDate ==null || startDate.length()==0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            startDate = formatter.format(new Date());
            endDate = formatter.format(new Date());
        }
        Map map = new HashMap();
        map.put("orgId",orgId);
        map.put("type",type);
        map.put("chType",chType);
        map.put("typeId",typeId);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        return environmentDao.countMonTop10(map);
    }

	public int getDeviceOnlinePercent(String code, int type, int type_id) {
		Map map = new HashMap();
		map.put("code", code);
		map.put("type", type);
		map.put("type_id", type_id);
		int onlineCount = environmentDao.getDeviceOnlineCount(map);
		int devCount = environmentDao.getDeviceCount(map);
		double percent = onlineCount/devCount;
		System.out.println(percent);
		return (int)percent*100;
	}

	public int getWaterDeviceDayAlarm(String code,int type, int type_id) {
		Map map = new HashMap();
		map.put("code", code);
		map.put("type", type);
		map.put("type_id", type_id);
		return environmentDao.getWaterDeviceDayAlarm(map);
	}

	public int getWaterOutlinePercentTop10(String code,int type, int type_id) {
		Map map = new HashMap();
		map.put("code", code);
		map.put("type", type);
		map.put("type_id", type_id);
		int offCount = environmentDao.getDeviceOfflineCount(map);
		int devCount = environmentDao.getDeviceCount(map);
		double percent = offCount/devCount;
		System.out.println(percent);
		return (int)percent*100;
	}
}
