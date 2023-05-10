/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;

import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.starnet.service.LoopService;
import com.jeeplus.modules.starnet.service.PowerDataService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "Loop")
public class LoopController extends BaseController {

    @Autowired
    private LoopService loopService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AreaService areaService;

    @RequestMapping("/testredis")
    @ResponseBody
    public String testredis() {

        List<String> keysList = new ArrayList<>();
        keysList.add("starnet_devdataproc_device_mac_" + "e01020306620_1");
        keysList.add("starnet_devdataproc_device_mac_" + "e01020306620_2");
        keysList.add("starnet_devdataproc_device_mac_" + "e01020306620_3");
        keysList.add("starnet_devdataproc_device_mac_" + "e01020306620_4");

        List<Object> valuesList = redisTemplate.opsForValue().multiGet(keysList);

        for (int i = 0; i < valuesList.size(); i++) {
            System.out.println(valuesList.get(i).toString());
        }
        return ServletUtils.buildRs(true, "树形", valuesList);
    }
    
    
    
    

    @RequestMapping("/loopListBypdfId")
    @ResponseBody
    public String loopListBypdfId(String pdfId) {
        System.out.println("iiiii");
        return ServletUtils.buildRs(true, "树形", loopService.loopListBypdfId(pdfId));
    }

    @RequestMapping("/loopPdfList") // 报表新树形
    @ResponseBody
    public String loopPdfList() {

        return ServletUtils.buildRs(true, "报表新树形", loopService.loopPdfList());
    }

    @RequestMapping("/loopListByBureauId")
    @ResponseBody
    public String loopListByBureauId(String pdfId, String bureauId) {

        return ServletUtils.buildRs(true, "树形", loopService.loopListByBureauId(bureauId));
    }

    @RequestMapping("/loopCodes")
    @ResponseBody
    public String loopCodes() {

        return ServletUtils.buildRs(true, "下拉框", loopService.loopCodes());
    }

    @RequestMapping("/getLoopOrgId")
    @ResponseBody
    public String getLoopOrgId(String bureauId) {

        return ServletUtils.buildRs(true, "获取供电所下回路", loopService.getLoopOrgId(bureauId));
    }

    @RequestMapping("/insertBureauLoop")
    @ResponseBody
    public String insertBureauLoop(String loopOrgIds, String bureauId) {

        return ServletUtils.buildRs(true, "获取供电所下回路", loopService.insertBureauLoop(loopOrgIds, bureauId));
    }

    // 添加
    @RequestMapping(value = "save")
    @ResponseBody
    public String save(Area area, Model model, RedirectAttributes redirectAttributes) {

        if (StringUtils.isNotBlank(area.getParentId())) {
            Area a = new Area();
            a.setId(area.getParentId());
            area.setParent(a);
        }
        System.out.println(area.getParent().getId() + "------区域父id-----------");
        // 获取区域底下最大code
        String addCode = "";
        String str = "";
        String code = "10";
        String maxCode = "10";
        if (!"0".equals(area.getParentId())) {
            code = areaService.selectCode(area.getParentId());
            maxCode = areaService.maxCode(area.getParentId());
        }
        if (maxCode != null) {
            // &&maxCode.length()%2!=0
            str = maxCode.substring(maxCode.length() - 2);
            System.out.println(str);
        }
        if (maxCode == null) {
            addCode = code + "01";
        } else {
            addCode = code + String.format("%02d", (Long.parseLong(str) + 1));
        }
        // 获取父级type ,,来计算下级type
        Area parentA = areaService.get(area.getParentId());
        String type = (Integer.parseInt(parentA.getType()) + 1) + "";// 下级type

        // 修改区域时候变更父id改变code
        Integer delType = 0;
        if (area.getId() != null) {
            Area a = areaService.get(area.getId());
            System.out.println("加入area---" + area.getParentIds());
            System.out.println("getParentIds--" + a.getParentIds());
            if (!a.getParentId().equals(area.getParentId())) {
                area.setCode(addCode);
                area.setOldCode(a.getCode());
                area.setParentIds(a.getParentIds());
                area.setType(type);
            }
            delType = Integer.parseInt(a.getType()) - Integer.parseInt(parentA.getType()) - 1;
            if (delType != 0) {
                // 子集的type - 去 这 delType
                System.out.println("delType = 不同级别的type" + delType);
            }
        } else {
            area.setCode(addCode);
            area.setType(type);
        }
        try {
            loopService.save(area, delType);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "保存区域'" + area.getName() + "'失败", null);
        }
        return ServletUtils.buildRs(true, "保存区域'" + area.getName() + "'成功", null);
    }

    @RequestMapping(value = "updateLoopOrgImage")
    @ResponseBody
    public String updateLoopOrgImage(String loopId, String image) {
        try {
            loopService.updateLoopOrgImage(loopId, image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServletUtils.buildRs(true, "修改回路图片", null);
    }

    @RequestMapping(value = { "modifyOrgOrderNo" }) // 修改回路排序 @RequestBody
    @ResponseBody
    public String modifyOrgOrderNo(String orderNoList) {

        JSONArray jaNVR = null;
        if (StringUtils.isNotBlank(orderNoList)) {
            String chList = orderNoList.replace("&quot;", "'");
            jaNVR = JSONArray.parseArray(chList);
            System.out.println(jaNVR);
        }
        try {
            loopService.modifyOrgOrderNo(jaNVR);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "修改回路排序失败", null);
        }
        return ServletUtils.buildRs(true, "修改回路排序成功", null);
    }

}