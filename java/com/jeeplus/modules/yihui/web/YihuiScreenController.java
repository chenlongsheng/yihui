package com.jeeplus.modules.yihui.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.outwarddata.service.DeviceOutService;
import com.jeeplus.modules.sys.dao.UserDao;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.yihui.Utils.SignUtils;
import com.jeeplus.modules.yihui.service.YihuiScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;


@Controller
@RequestMapping(value = " YihuiScreen")
public class YihuiScreenController extends BaseController {

    @Autowired
    private YihuiScreenService yihuiScreenService;

    @Autowired
    private UserDao userDao0;

    private  String orgId = UserUtils.getOrgId();

    //
    @RequestMapping(value = {"carDetails"})
    @ResponseBody
    public String carDetails() {
        MapEntity entity = yihuiScreenService.getTorg();

        entity.put("todayCars",Intocar.intocar()); //今日进场
        entity.put("presenceCars",Todaycar.todaycar());  //现场车辆


//        entity.put("totalCars", entity.get(""));   //总车位
//        entity.put("batteryCars",  entity.get(""));
//        entity.put("todayCars", entity.get(""));    //今日车位
//        entity.put("presenceCars", entity.get(""));
        return ServletUtils.buildRs(true, "场地数据", entity);

    }

    @RequestMapping(value = {"getRealAlarmlog"})
    @ResponseBody
    public String getRealAlarmlog(String alarmType, HttpServletRequest request, HttpServletResponse response) {
        MapEntity entity = new MapEntity();
        entity.put("alarmType", alarmType);
        System.out.println(UserUtils.getUser().getId());
        entity.put("userId", UserUtils.getUser().getId());
        Page<MapEntity> page = yihuiScreenService.getRealAlarmogs(new Page<MapEntity>(request, response), entity);

        return ServletUtils.buildRs(true, "实时报警数据", page);

    }

    @RequestMapping(value = {"tDeviceLoopList"})
    @ResponseBody
    public String tDeviceLoopList() {

        return ServletUtils.buildRs(true, "获取数据成功", yihuiScreenService.tDeviceLoopList(orgId));

    }

    @RequestMapping(value = {"getDeviceOnlines"})
    @ResponseBody
    public String getDeviceOnlines() {

        List<MapEntity> page = yihuiScreenService.getDeviceOnlines(orgId);

        return ServletUtils.buildRs(true, "充电桩统计", page);
    }

    @RequestMapping(value = {"getHistorys"})
    @ResponseBody
    public String getHistorys() {
        String orgId0 =  userDao0.getOrgId(UserUtils.getUser().getId());
        return ServletUtils.buildRs(true, "获取月份数据成功", yihuiScreenService.getHistorys(orgId0));

    }

    @RequestMapping(value = {"getAlarmLogs"})
    @ResponseBody
    public String getAlarmLogs() {
        String orgId0 =  userDao0.getOrgId(UserUtils.getUser().getId());
        return ServletUtils.buildRs(true, "获取月份数据成功", yihuiScreenService.getHistorys(orgId0));

    }

    //
    @RequestMapping(value = {"getOrgLoopsVideo"})
    @ResponseBody
    public JSONObject getOrgLoopsVideo() {
        System.out.println(orgId);
        System.out.println(UserUtils.getUser().getId());
        return ServletUtils.buildJsonRs(true, "整流柜和视频下拉框", yihuiScreenService.getOrgLoopsVideo(orgId));
    }

    @RequestMapping(value = {"getOrgLoops"})
    @ResponseBody
    public JSONObject getOrgLoops() {


        String orgId0 =  userDao0.getOrgId(UserUtils.getUser().getId());

        return ServletUtils.buildJsonRs(true, "整流柜", yihuiScreenService.getOrgLoops(orgId0));
    }



}



