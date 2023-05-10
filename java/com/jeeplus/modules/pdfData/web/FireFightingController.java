package com.jeeplus.modules.pdfData.web;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.pdfData.service.FireFightingService;
import com.jeeplus.modules.pdfData.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2018/12/26.
 */
@Controller
@RequestMapping("/pdf/fire")
public class FireFightingController extends BaseController {
    @Autowired
    FireFightingService fireFightingService;
    @Autowired
    SecurityService securityService;

    /**
     * 消防系统烟感获取所有数据
     * @param orgId
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/getAllData")
    @ResponseBody
    public JSONObject getAllData(String orgId,int type,String startDate,String endDate,int data,String start1,String end1,String start2,String end2,String start3,String end3){
        JSONObject jsonObject = new JSONObject();
        try {
            if(data==1){
                //int savaDays = securityService.countSaveDays(orgId,type);//安全运行天数
            	int safeDays = 30;
                jsonObject.put("safeDays",safeDays);
            }else if(data==2){
                String countOnline = countOnline(orgId,type);//烟感在线率
                jsonObject.put("countOnline",countOnline);
            }else if(data==3){
                int dayAlarm = countDayAlarm(orgId,type);//当日新增报警数
                jsonObject.put("dayAlarm",dayAlarm);
            }else if(data==4){
                int countMonthAlarm = countMonthAlarm(orgId,type);//本月新增报警数
                jsonObject.put("countMonthAlarm",countMonthAlarm);
            }else if(data==5){
                List<Map> OutlineTop10 = getOutlineTop10(orgId,type);//烟感离线率top10
                jsonObject.put("OutlineTop10",OutlineTop10);
            }else if(data==6){
                Map countElectric = countElectric(orgId,type,start1,end1,start2,end2,start3,end3);//烟感实时电量占比
                jsonObject.put("countElectric",countElectric);
            }else if(data==7){
                List<Map> alarmTop10 = alarmTop10(orgId,startDate,endDate,type);//报警次数top10
                jsonObject.put("alarmTop10",alarmTop10);
            }

//            Map map = fireFightingService.getAllData(orgId,type,startDate,endDate);
            jsonObject.put("success",true);
//            jsonObject.put("data",map);
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 消防系统电气火灾获取所有数据
     * @param orgId
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/getElcAllData")
    @ResponseBody
    public JSONObject getElcAllData(String orgId,int type,String startDate,String endDate,int data,String start1,String end1,String start2,String end2,String start3,String end3){
        JSONObject jsonObject = new JSONObject();
        try {
            if(data==1){
                //int savaDays = securityService.countSaveDays(orgId,type);//安全运行天数
            	int safeDays = 30;
                jsonObject.put("safeDays",safeDays);
            }else if(data==2){
                Map wireTemperature = fireFightingService.wiretemperature(orgId,type,10009,3);//电线温度
                jsonObject.put("wireTemperature",wireTemperature);
            }else if(data==3){
                Map lineCurrent = fireFightingService.wiretemperature(orgId,type,100010,3);//过线电流
                jsonObject.put("lineCurrent",lineCurrent);
            }else if(data==4){
                Map rasidueCurrent = fireFightingService.wiretemperature(orgId,type,100011,3);//剩余电流
                jsonObject.put("rasidueCurrent",rasidueCurrent);
            }else if(data==5){
                int countMonthAlarm = countMonthAlarm(orgId,type);//本月新增报警数
                jsonObject.put("countMonthAlarm",countMonthAlarm);
            }else if(data==6){
                Map Hourresidue = fireFightingService.countHour(orgId,startDate,endDate,type,100011, 3);//各时段剩余电流平均值
                Map HourrTemperature = fireFightingService.countHour(orgId,startDate,endDate,type,10009, 3);//各时段电线温度平均值
                Map resultMap = new HashMap();
                resultMap.put("Hourresidue",Hourresidue);
                resultMap.put("HourrTemperature",HourrTemperature);
                jsonObject.put("HourAvg",resultMap);
            }else if(data==7){
                List<Map> residueTop10 = fireFightingService.residueTop10(orgId,type,100011,3);//剩余电流实时峰值top10
                jsonObject.put("residueTop10",residueTop10);
            }else if(data==8){
                List<Map> countAlarmByType = fireFightingService.countAlarmByType(orgId);//电气火灾设备页报警类型统计
                jsonObject.put("countAlarmByType",countAlarmByType);
            }else if(data==9){
                Map countTemperature = fireFightingService.countTemperature(orgId,type,10009,3,start1,end1,start2,end2,start3,end3);//各温度区间探测器实时数量占比
                jsonObject.put("countTemperature",countTemperature);
            }else if(data==10){
                List<Map> temperatureTop5 = fireFightingService.temperatureTop5(orgId,type,10009,3);//高温配电房Top5
                jsonObject.put("temperatureTop5",temperatureTop5);
            }
//            Map map = fireFightingService.getAllData(orgId,type,startDate,endDate);
            jsonObject.put("success",true);
//            jsonObject.put("data",map);
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 水池水位页
     * @param orgId
     * @param type
     * @param data
     * @param start1
     * @param end1
     * @param start2
     * @param end2
     * @param start3
     * @param end3
     * @param wstart1
     * @param wend1
     * @param wstart2
     * @param wend2
     * @param wstart3
     * @param wend3
     * @return
     */
    @RequestMapping("/getWaterAllData")
    @ResponseBody
    public JSONObject getWaterAllData(String orgId,int type,int data,String start1,String end1,String start2,String end2,String start3,String end3
    ,String wstart1,String wend1,String wstart2,String wend2,String wstart3,String wend3){
        JSONObject jsonObject = new JSONObject();
        try{
            if(data==1){
                //int savaDays = securityService.countSaveDays(orgId,type);//安全运行天数
            	int safeDays = 30;
                jsonObject.put("safeDays",safeDays);
            }else if(data==2){
                Map waterLevel = fireFightingService.wiretemperature(orgId,type,100012,3);//消防水池水位
                jsonObject.put("waterLevel",waterLevel);
            }else if(data==3){
                Map waterGage = fireFightingService.waterGage(orgId,type,100018,3,2);//管网水压
                jsonObject.put("waterGage",waterGage);
            }else if(data==4){
                Map endWaterGage = fireFightingService.waterGage(orgId,type,100018,3,3);//末端水压
                jsonObject.put("endWaterGage",endWaterGage);
            }else if(data==5){
                int countMonthAlarm = countMonthAlarm(orgId,type);//本月新增报警数
                jsonObject.put("countMonthAlarm",countMonthAlarm);
            }else if(data==6){
                List<Map> waterGageTop5 = fireFightingService.waterGageTop5(orgId,type,100018,3,2);//管网水压top5
                jsonObject.put("waterGageTop5",waterGageTop5);
            }else if(data==7){
                Map devGage = fireFightingService.devGage(orgId,type,100018,3,2,start1,end1,start2,end2,start3,end3);
                jsonObject.put("devGage",devGage);
            }else if(data==8){
                List<Map> temperatureTop5 = fireFightingService.temperatureTop5(orgId,type,100012,3);//消防水池水位Top5
                jsonObject.put("temperatureTop5",temperatureTop5);
            }else if(data==9){
                Map waterLine = fireFightingService.waterLine(orgId,type,100012,3,1,wstart1,wend1,wstart2,wend2,wstart3,wend3);//水池水位值
                jsonObject.put("waterLine",waterLine);
            }
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 消防系统烟感切换日期
     * @param orgId
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/changeDate")
    @ResponseBody
    public JSONObject changeDate(String orgId,int type,String startDate,String endDate){
        JSONObject jsonObject = new JSONObject();
        try {
            Map map = fireFightingService.changeDate(orgId,type,startDate,endDate);
            jsonObject.put("success",true);
            jsonObject.put("data",map);
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
        }
        return jsonObject;
    }

    @RequestMapping("/countOnline")
    @ResponseBody
    public String countOnline(String orgId,int type){
        return fireFightingService.countOnline(orgId,type);
    }

    /**
     * 当天新增报警数
     * @param orgId
     * @return
     */
    @RequestMapping("/countDayAlarm")
    @ResponseBody
    public int countDayAlarm(String orgId,int type){
        return fireFightingService.countDayAlarm(orgId,type);
    }

    /**
     * 本月新增报警数
     * @param orgId
     * @return
     */
    @RequestMapping("/countMonthAlarm")
    @ResponseBody
    public int countMonthAlarm(String orgId,int type){
        return fireFightingService.countMonthAlarm(orgId,type);
    }

    /**
     * 烟感实时离线率top10
     * @param orgId
     * @return
     */
    @RequestMapping("/getOutlineTop10")
    @ResponseBody
    public List<Map> getOutlineTop10(String orgId,int type){
        return fireFightingService.getOutlineTop10(orgId,type);
    }

    /**
     * 烟感实时电量
     * @param orgId
     * @return
     */
    @RequestMapping("/countElectric")
    @ResponseBody
    public Map countElectric(String orgId,int type,String start1,String end1,String start2,String end2,String start3,String end3){
        return fireFightingService.countElectric(orgId,type,start1,end1,start2,end2,start3,end3);
    }

    /**
     * 报警次数top10
     * @param orgId
     * @param startDate
     * @param endDate
     * @param type
     * @return
     */
    @RequestMapping("/alarmTop10")
    @ResponseBody
    public List<Map> alarmTop10(String orgId,String startDate,String endDate,int type){
        return fireFightingService.alarmTop10(orgId,startDate,endDate,type);
    }


}
