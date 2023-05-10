/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.homepage.web;

import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.homepage.service.HomepagePdfService;
import com.jeeplus.modules.homepage.service.StatisticsService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 机构Controller
 *
 * @author jeeplus
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "homepagePdf")
public class homepagePdfController extends BaseController {

    @Autowired
    private HomepagePdfService homepagePdfService;


    @ResponseBody
    @RequestMapping(value = "getTypeList")
    public String getTypeList() {

        return ServletUtils.buildRs(true, "配电房类型详情", homepagePdfService.getTypeList());
    }


    @ResponseBody
    @RequestMapping(value = "getDeviceNumList")
    public String getDeviceNumList() {

        return ServletUtils.buildRs(true, "设备详情", homepagePdfService.getDeviceNumList());
    }

    @ResponseBody
    @RequestMapping(value = "getDevTypeNames")
    public String getDevTypeNames() {

        return ServletUtils.buildRs(true, "历史设备下拉框", homepagePdfService.getDevTypeNames());
    }

    @ResponseBody
    @RequestMapping(value = "getHistorysByDevId")
    public String getHistorysByDevId(String devId, String devType,String time) {

        return ServletUtils.buildRs(true, "历史详情", homepagePdfService.getHistorysByDevId(devId, devType,time));
    }

    @ResponseBody
    @RequestMapping(value = "getTailById")
    public String getTailById(String devId) {

        return ServletUtils.buildRs(true, "详情", homepagePdfService.getTailById(devId));
    }



}
