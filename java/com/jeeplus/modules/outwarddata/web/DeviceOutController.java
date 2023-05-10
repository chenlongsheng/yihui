/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.outwarddata.web;

import com.alibaba.fastjson.JSON;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.outwarddata.Entity.RealData;
import com.jeeplus.modules.outwarddata.service.DeviceOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 数据配置Controller
 *
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = " DeviceOut")
public class DeviceOutController extends BaseController {

    @Autowired
    private DeviceOutService deviceOutService;


    @RequestMapping(value = {"updateChannelDatas"})
    @ResponseBody
    public String updateChannelDatas(@RequestBody String data) {

        System.out.println(data);
        List<RealData> channels = JSON.parseArray(data, RealData.class);
        try {
            for (RealData channel : channels) {
                System.out.println(channel.getChId() + ": " + channel.getRealValue());
                deviceOutService.updateRealDatas(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "修改数据失败", "");
        }
        //   BatchInsertUtil.batchInsert(channels, DeviceOutDao.class, DeviceOutDao::updateRealDatas);
        return ServletUtils.buildRs(true, "修改数据成功", "");
    }


    @RequestMapping(value = {"getChannelDatas"})
    @ResponseBody
    public String getChannelDatas(String pdfId) {

        return ServletUtils.buildRs(true, "获取设备数据", deviceOutService.tDeviceMachineList(pdfId));
    }

    @RequestMapping(value = {"getOrgListById"})
    @ResponseBody
    public String getOrgListById(String pdfId) {

        return ServletUtils.buildRs(true, "获取站点下级所有数据", deviceOutService.getOrgListById(pdfId));
    }

}