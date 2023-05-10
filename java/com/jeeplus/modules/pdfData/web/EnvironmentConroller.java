package com.jeeplus.modules.pdfData.web;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.pdfData.service.EnvironmentService;
import com.jeeplus.modules.pdfData.service.FireFightingService;
import com.jeeplus.modules.pdfData.service.SecurityService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/28.
 */
@Controller
@RequestMapping("/pdf/environment")
public class EnvironmentConroller extends BaseController {

    @Autowired
    EnvironmentService environmentService;

    @Autowired
    SecurityService securityService;

    @Autowired
    FireFightingService fireFightingService;
    
    @Autowired
    TOrgService orgService;

    /**
     * 风机页获取所有数据
     * @param orgId
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/getAllData")  //风机
    @ResponseBody
    public JSONObject getAllData(String orgId, int type, String startDate, String endDate,int data){
    	
    	if(orgId == null || orgId.length() == 0){
            orgId = OrgUtil.getOrgId();
    	}
    	TOrg org = orgService.get(orgId);
    	String code = "00";
    	if(org != null) {
    		 code = org.getCode();
    	}
    	
        JSONObject jsonObject = new JSONObject();
        try {
            if(data==1){
                int savaDays = securityService.countSaveDays(code,220,4);//安全运行天数
                jsonObject.put("safeDays",savaDays);
            }else if(data==2){
                String countOnline = fireFightingService.countOnline(orgId,type);//动力系统在线率
                jsonObject.put("countOnline",countOnline);
            }else if(data==3){
                double countMonthMon = environmentService.countMonthMontitoring(orgId,type,100013,3);//本月能耗
                jsonObject.put("countMonthMon",countMonthMon);
            }else if(data==4){
                int countMonthAlarm = fireFightingService.countMonthAlarm(orgId,type);//本月新增报警数
                jsonObject.put("countMonthAlarm",countMonthAlarm);
            }else if(data==5){
                Map HourOpen = securityService.countHourOpen(code,startDate,endDate,type,5,2);//各时段风机平均启动次数
                jsonObject.put("HourOpen",HourOpen);
            }else if(data==7){
                List<Map> countMonTop10 = environmentService.countMonTop10(orgId,type,100013,3,startDate,endDate);//能耗排行top10
                jsonObject.put("countMonTop10",countMonTop10);
            }
            jsonObject.put("success",true);
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 温湿度计页获取所有数据
     * @param orgId
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/getTemAllData")
    @ResponseBody
    public JSONObject getTemAllData(String orgId, int type, String startDate, String endDate,int data){
    	
    	if(orgId == null || orgId.length() == 0){
            orgId = OrgUtil.getOrgId();
    	}
    	TOrg org = orgService.get(orgId);
    	String code = "00";
    	if(org != null) {
    		 code = org.getCode();
    	}
    	
        JSONObject jsonObject = new JSONObject();
        try {
            if(data==1){
                int tempSafeDays = securityService.countSaveDays(code,101,3);//安全运行天数
                int humSafeDays = securityService.countSaveDays(code,102,3);//安全运行天数
                if(tempSafeDays > humSafeDays) {
                	jsonObject.put("safeDays",humSafeDays);	
                } else {
                	jsonObject.put("safeDays",tempSafeDays);
                }                
            }else if(data==2){
                Map tem = fireFightingService.wiretemperature2(org.getCode(),type,101,3);//温度区间
                jsonObject.put("tem",tem);
            }else if(data==3){
                Map humidity = fireFightingService.wiretemperature2(org.getCode(),type,102,3);//湿度区间
                jsonObject.put("humidity",humidity);
            }else if(data==4){
                int countMonthAlarm = fireFightingService.countMonthAlarm(code,type);//本月新增报警数
                jsonObject.put("countMonthAlarm",countMonthAlarm);
            }else if(data==5){
                List<Map> temAvg = environmentService.getAvgTop10(code,type,101,3);//温度排行
                jsonObject.put("temAvg",temAvg);
            }else if(data==6){
                List<Map> humAvg = environmentService.getAvgTop10(code,type,102,3);//湿度排行
                jsonObject.put("humAvg",humAvg);
            }else if(data ==7){
                List<Map> count1 = environmentService.countAlarmByType(org.getCode(),101,3);
                List<Map> count2 = environmentService.countAlarmByType(org.getCode(),102,3);
            	HashMap<String,Object> map1 = new HashMap<String,Object>();
            	map1.put("name","温度");
            	map1.put("count",count1.get(0).get("count"));
            	HashMap<String,Object> map2 = new HashMap<String,Object>();
            	map2.put("name","湿度");
            	map2.put("count",count2.get(0).get("count"));
                List<Map> alarmType = new ArrayList<Map>();
                alarmType.add(map1);
                alarmType.add(map2);
                jsonObject.put("alarmType",alarmType);
            }
            jsonObject.put("success",true);
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }
    
    
    
    
    
    @RequestMapping("/getWaterAllData")
    @ResponseBody
    public JSONObject getWaterAllData(String orgId, int type, String startDate, String endDate,int data) {
    	if(orgId == null || orgId.length() == 0){
            orgId = OrgUtil.getOrgId();
    	}
    	TOrg org = orgService.get(orgId);
    	String code = "00";
    	if(org != null) {
    		 code = org.getCode();
    	}
        JSONObject jsonObject = new JSONObject();
        try {
            if(data==1){
                int savaDays = securityService.countSaveDays(code,2,2);//安全运行天数
                jsonObject.put("safeDays",savaDays);
            }else if(data==2){
            	//设备在线率
            	int countOnline = environmentService.getDeviceOnlinePercent(code,2,2);
            	jsonObject.put("countOnline",countOnline);
            }else if(data==3){
            	//今日新增报警
            	int dayAlarm = environmentService.getWaterDeviceDayAlarm(code,2,2);
            	jsonObject.put("dayAlarm",dayAlarm);
            }else if(data==4){
            	//本月新增报警数
                int countMonthAlarm = fireFightingService.countMonthAlarm(orgId,type);
                jsonObject.put("countMonthAlarm",countMonthAlarm);
            }else if(data==5){
                //水浸实时离线率top10
            	//int OutlineTop10 = environmentService.getWaterOutlinePercentTop10(code,2,2);
            	List<Map> datas = new ArrayList<Map>();
            	Map data1 = new HashMap<String,Object>();
            	Map data2 = new HashMap<String,Object>();
            	Map data3 = new HashMap<String,Object>();
            	Map data4 = new HashMap<String,Object>();
            	Map data5 = new HashMap<String,Object>();
            	Map data6 = new HashMap<String,Object>();
            	data1.put("name", "水浸1");
            	data1.put("rate", 20);
            	data2.put("name", "水浸2");
            	data2.put("rate", 30);
            	
            	data3.put("name", "水浸3");
            	data3.put("rate", 40);
            	data4.put("name", "水浸4");
            	data4.put("rate", 50);
            	
            	data5.put("name", "水浸5");
            	data5.put("rate", 60);
            	data6.put("name", "水浸6");
            	data6.put("rate", 10);
            	
            	datas.add(data1);
            	datas.add(data2);
            	datas.add(data3);
            	datas.add(data4);
            	datas.add(data5);
            	datas.add(data6);
            	jsonObject.put("OutlineTop10",datas);
            }else if(data==6){
            	//水浸实时电量占比
            	//int countElectric = environmentService.getWaterElectricPercent();
            	Map map = new HashMap<String,Object>();
            	String[] legend = {"剩余电量","消耗电量"};
            	map.put("legend",legend);
            	
            	List<Map> series = new ArrayList<Map>();
            	Map data1 = new HashMap<String,Object>();
            	Map data2 = new HashMap<String,Object>();
            	data1.put("name", "剩余电量");
            	data1.put("value", 75);
            	data2.put("name", "消耗电量");
            	data2.put("value", 25);
            	series.add(data1);
            	series.add(data2);
            	map.put("series", series);
            	jsonObject.put("countElectric",map);
            }else if(data ==7){
            	//报警次数top10
            	List<Map> datas = new ArrayList<Map>();
            	Map data1 = new HashMap<String,Object>();
            	Map data2 = new HashMap<String,Object>();
            	Map data3 = new HashMap<String,Object>();
            	Map data4 = new HashMap<String,Object>();
            	Map data5 = new HashMap<String,Object>();
            	Map data6 = new HashMap<String,Object>();
            	data1.put("name", "水浸1");
            	data1.put("count", 20);
            	data2.put("name", "水浸2");
            	data2.put("count", 30);
            	
            	data3.put("name", "水浸3");
            	data3.put("count", 40);
            	data4.put("name", "水浸4");
            	data4.put("count", 50);
            	
            	data5.put("name", "水浸5");
            	data5.put("count", 60);
            	data6.put("name", "水浸6");
            	data6.put("count", 10);
            	
            	datas.add(data1);
            	datas.add(data2);
            	datas.add(data3);
            	datas.add(data4);
            	datas.add(data5);
            	datas.add(data6);
            	jsonObject.put("alarmTop10",datas);
            }
            jsonObject.put("success",true);
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }
    
    
    
    
    
}

