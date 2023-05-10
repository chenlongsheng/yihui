package com.jeeplus.modules.pdfData.web;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.pdfData.service.EnergySaveService;
import com.jeeplus.modules.pdfData.service.FireFightingService;
import com.jeeplus.modules.pdfData.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by ZZUSER on 2019/1/4.
 */
@Controller
@RequestMapping("/pdf/monitoring")
public class MonitoringController extends BaseController {
    @Autowired
    MonitoringService monitoringService;

    @Autowired
    FireFightingService fireFightingService;

    @Autowired
    EnergySaveService energySaveService;

    @RequestMapping("/getAllData")
    @ResponseBody
    public JSONObject getAllData(String orgId, int type,int data){
        JSONObject jsonObject = new JSONObject();
        try {
            if(data==1){
                String countOnline = fireFightingService.countOnline(orgId,type);//监控在线率
                jsonObject.put("countOnline",countOnline);
            }else if(data==2){
                int num = monitoringService.countMonitoring(orgId,type);//视频设备总数
                jsonObject.put("num",num);
            }else if(data==3){
                double countMonthEnergy = monitoringService.countMonthEnergy(orgId,type);//本月能耗
                jsonObject.put("countMonthEnergy",countMonthEnergy);
            }else if(data==4){
                List<Map> OutlineTop10 = fireFightingService.getOutlineTop10(orgId,type);//监控离线率top10
                jsonObject.put("OutlineTop10",OutlineTop10);
            }else if(data==5){
                List<Map> countYearTop10 = energySaveService.countYearTop10(orgId,type);//年度能耗排行
                jsonObject.put("countYearTop10",countYearTop10);
            }
//            Map map = monitoringService.getAllData(orgId,type);
            jsonObject.put("success",true);
//            jsonObject.put("data",map);
        }catch (Exception e){
            jsonObject.put("success",false);
            jsonObject.put("msg",e.getMessage());
            e.printStackTrace();
        }
        return jsonObject;
    }
}
