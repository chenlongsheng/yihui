/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.chaoan.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.jeeplus.common.mq.mqtt.MqttProducer;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.chaoan.dao.RemoteDao;
import com.jeeplus.modules.chaoan.service.EnvironmentalService;
import com.jeeplus.modules.chaoan.service.RemoteService;
import com.jeeplus.modules.machine.service.TDeviceMachineService;
import com.jeeplus.modules.machine.service.TDeviceRportService;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "app/")
public class RemotelController extends BaseController {

    @Autowired
    private RemoteService remoteService;

    @RequestMapping(value = { "getDeviceById" })
    @ResponseBody
    public String getDeviceById(String devType) {
        return ServletUtils.buildRs(true, "获取所有设备根据类型", remoteService.getDeviceById(devType));
    }

    @RequestMapping(value = { "getHistoryValue" })
    @ResponseBody
    public String getHistoryValue(HttpServletRequest request, HttpServletResponse response, String chId,
            String beginTime, String endTime) {

        MapEntity entity = new MapEntity();
        entity.put("beginTime", beginTime);
        entity.put("endTime", endTime);
        entity.put("chId", chId);
        Page<MapEntity> page = remoteService.findPageMessage(new Page<MapEntity>(request, response), entity);
        return ServletUtils.buildRs(true, "获取历史数据根据chId", page);

    }

}
