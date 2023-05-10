/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.machine.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.machine.service.TDeviceMachineService;
import com.jeeplus.modules.machine.service.TDeviceRportService;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "settings/TDeviceReport")
public class TDeviceReportController extends BaseController {

    @Autowired
    private TDeviceRportService tDeviceRportService;

    @RequestMapping(value = { "tDeviceReportList" })
    @ResponseBody
    public String tDeviceReportList(String orgId, String name) {

        return ServletUtils.buildRs(true, "广东机房设备数据", tDeviceRportService.tDeviceRportList(orgId, name));
    }

    @RequestMapping(value = { "tChannelReportList" })
    @ResponseBody
    public String tChannelReportList(String id, String str, String time) {

        return ServletUtils.buildRs(true, "广东机房历史数据", tDeviceRportService.tChannelRportList(id, str, time));
    }

    @RequestMapping("/exportChannelReportList")
    @ResponseBody
    public void exportChannelReportList(String id, String str, String time, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws Exception {

        tDeviceRportService.exportChannelRportList(httpServletRequest, httpServletResponse,
                tDeviceRportService.tChannelRportList(id, str, time));

    }

   

}