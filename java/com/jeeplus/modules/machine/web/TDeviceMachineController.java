/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.machine.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.machine.service.TDeviceMachineService;
import com.jeeplus.modules.settings.entity.TChannel;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "settings/tDeviceMachine")
public class TDeviceMachineController extends BaseController {

    @Autowired
    private TDeviceMachineService tDeviceMachineService;

    @Value("${deviceId}")
    private String deviceId;

    @RequestMapping(value = { "test" })
    @ResponseBody
    public String test(@RequestBody String message) {

        String orgName = "潮安电力大楼8楼备调机房";

        try {
            message = message.replace("&quot;", "'");
            System.out.println(message);
            JSONObject devAlarm = JSONObject.parseObject(message);
            String phone = "1825";
            String addr = "正门";
            String devname = "烟感";
            String orgId = "";
            String alarmResume = "发生报警";
            List<String> phoneList = new ArrayList<String>();
            JSONArray items = devAlarm.getJSONArray("items");
            JSONArray resumeItems = devAlarm.getJSONArray("resume_items");
            Integer online = devAlarm.getInteger("online");

            String shortMessage = "{" + "\"command\":12," + "\"Tel\":\"" + phone + "\"," + "\"Sms\":\"" + orgName + "("
                    + addr + devname + ")" + alarmResume + "\"" + "}";

            String shortMessage1 = "{" + "\"command\":12," + "\"Tel\":\"" + phone + "\"," + "\"Sms\":\"通道:" + devname
                    + " 发生报警\"" + "}";

            System.out.println(shortMessage);
            System.out.println("-------------" + deviceId);
            if (online != null && online == 0) {
                // 设备离线
                System.out.println();
                String devId = devAlarm.getString("dev_id");

            } else if (items != null) {// 发生报警

                for (int i = 0; i < items.size(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String chId = item.getString("ch_id");
                    String alarmLevel = item.getString("alarm_level");
                    alarmResume = "发生" + alarmLevel + "级报警";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "111";
    }

    @RequestMapping(value = { "tDeviceMachineList" })
    @ResponseBody
    public String tDeviceMachineList(String orgId) {
        return ServletUtils.buildRs(true, "广东机房设备数据", tDeviceMachineService.tDeviceMachineList(orgId, null));
    }

    @RequestMapping(value = { "warmMachineListByDevId" })
    @ResponseBody
    public String warmMachineListByDevId(String deviceId) {
        return ServletUtils.buildRs(true, "广东机房设备数据", tDeviceMachineService.tDeviceMachineList(null, deviceId));
    }

    @RequestMapping(value = { "allVideoList" })
    @ResponseBody
    public String allVideoList(String orgId) {
        return ServletUtils.buildRs(true, "获取用户视频", tDeviceMachineService.allVideoList(orgId));

    }

    @RequestMapping(value = { "insertPdfUserId" })
    @ResponseBody
    public String insertPdfUserId(String chIds) {

        return ServletUtils.buildRs(true, "添加用户视频", tDeviceMachineService.insertPdfUserId(chIds));

    }

    @RequestMapping(value = { "getCodeList" })
    @ResponseBody
    public String getCodeList() {

        return ServletUtils.buildRs(true, "设备类型", tDeviceMachineService.getCodeList());

    }

    @RequestMapping(value = { "getHistoryList1" })
    @ResponseBody
    public String getHistoryList1(String orgId, String time, String devType, String chType, String typeId) {

        return ServletUtils.buildRs(true, "获取历史数据",
                tDeviceMachineService.getHistoryList1(orgId, time, devType, chType, typeId));
    }

    @RequestMapping(value = { "getHistoryList" })
    @ResponseBody
    public String getHistoryList(String orgId, String time, String devType, String chType, String typeId) {

        return ServletUtils.buildRs(true, "获取历史数据",
                tDeviceMachineService.getHistoryList(orgId, time, devType, chType, typeId));
    }

    @RequestMapping(value = { "getDeviceCodeByorgId" })
    @ResponseBody
    public String getDeviceCodeByorgId(String orgId) {

        return ServletUtils.buildRs(true, "获取设备类型选项", tDeviceMachineService.getDeviceCodeByorgId(orgId));
    }

    @RequestMapping(value = { "getDeviceCodeList" })
    @ResponseBody
    public String getDeviceCodeList(String devType) {

        return ServletUtils.buildRs(true, "设备类型通道选项", tDeviceMachineService.getDeviceCodeList(devType));

    }

    @RequestMapping(value = { "updateDeviceCodeByDevType" })
    @ResponseBody
    public String updateDeviceCodeByDevType(String devType, String jsonDevData) {

        System.out.println(jsonDevData);
        String list = jsonDevData.replace("&quot;", "'");
        JSONArray ja = JSONArray.parseArray(list);

        tDeviceMachineService.updateDeviceCodeByDevType(devType, ja);

        return ServletUtils.buildRs(true, "修改设备类型通道选项", "");
    }

//    @RequestMapping(value = { "tDeviceMachineList1" })
//    @ResponseBody
//    public String tDeviceMachineList1(String orgId, String jsonDevData) {
//
//        return ServletUtils.buildRs(true, "", tDeviceMachineService.tDeviceMachineList1(orgId, jsonDevData));
//
//    }

}