/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.maintenance.entity.PdfUser;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.starnet.entity.ElectricityUnit;
import com.jeeplus.modules.starnet.service.ElectricityUnitService;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "star/electricityUnit")
public class ElectricityUnitController extends BaseController {

    @Autowired
    private ElectricityUnitService electricityUnitService;
    
    /*
     * 
     * 获取用电单位集合分页显示
     * 
     */
    @RequestMapping(value = { "selectElectricityUnit" })
    @ResponseBody
    public String selectElectricityUnit(String name, HttpServletRequest request, HttpServletResponse response) {
        MapEntity entity = new MapEntity();
        entity.put("name", name);
        String size = request.getParameter("pageSize");
        if (size != null) {
            Page<MapEntity> page = electricityUnitService.findPage(new Page<MapEntity>(request, response), entity);
            return ServletUtils.buildRs(true, "所有用电单位", page);
        } else {
            List<MapEntity> list = electricityUnitService.selectElectricityUnit(entity);
            return ServletUtils.buildRs(true, "所有用电单位", list);
        }
    }

    /*
     * 
     * 修改排序
     */
    @RequestMapping(value = "modifyOrder")
    @ResponseBody
    public String modifyOrder(String orderNoList) {

        System.out.println(orderNoList);
        String list = orderNoList.replace("&quot;", "'");
        JSONArray ja = JSONArray.parseArray(list);
        try {
            electricityUnitService.updateOrderNo(ja);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "修改页面失败", "");
        }
        return ServletUtils.buildRs(true, "修改页面", "");

    }

    /*
     * 添加用电单位
     * 
     */
    @RequestMapping(value = "insertElectricityUnit")
    @ResponseBody
    public String insertElectricityUnit(ElectricityUnit electricityUnit) {

        try {
            electricityUnitService.insertElectricityUnit(electricityUnit);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "添加失败", "");
        }
        return ServletUtils.buildRs(true, "添加成功", "");

    }

    /*
     * 
     * 添加用电单位和回路关联
     * 
     */
    @RequestMapping(value = "insertUnitLoop") //
    @ResponseBody
    public String insertUnitLoop(String id, String unitId, String loopOrgId, String notDeduction, String notPublic,
            String loopArea, String loopNumber, String proportion, String sonLoopIds, String type) {

        MapEntity entity = new MapEntity();
        entity.put("unitId", unitId);
        entity.put("loopOrgId", loopOrgId);
        entity.put("notDeduction", notDeduction);// 扣减下级 0 否 , 1 是
        entity.put("notPublic", notPublic);
        entity.put("loopArea", loopArea);
        entity.put("loopNumber", loopNumber);
        if (notPublic.equals("0")) {// 0 是专属 1 都是公用
            proportion = "1";
        }
        entity.put("proportion", proportion);
        entity.put("sonLoopIds", sonLoopIds);// 扣减下级orgid拼接
        entity.put("type", type);// 0是专属 1按面积 2 按人数 3 按固定比例以及计算后的固定比例
        entity.put("id", id);
        if (StringUtils.isNotBlank(id)) {
            electricityUnitService.deleteUnitLoopByLoopId(id);
        }
        electricityUnitService.insertUnitLoop(entity);
        return ServletUtils.buildRs(true, "添加成功", "");

    }

    /*
     * 
     * 
     * 获取回路下拉框选项
     */
    @RequestMapping(value = "selectLoopOrg") // 下拉框回路
    @ResponseBody
    public String selectLoopOrg(String unitId) {

        return ServletUtils.buildRs(true, "获取下拉框成功", electricityUnitService.selectLoopOrg());
    }

    /*
     * 
     * 获取用电单位下所有绑定的回路列表
     */
    @RequestMapping(value = "getUnitLoopList") // 获取关联回路列表
    @ResponseBody
    public String getUnitLoopList(String unitId) {

        return ServletUtils.buildRs(true, "关联回路列表", electricityUnitService.getUnitLoopList(unitId));
    }

    /*
     * 
     * 
     * 删除用电单位关联的回路
     */
    @RequestMapping(value = "deleteUnitLoopByLoopId") //
    @ResponseBody
    public String deleteUnitLoopByLoopId(String id) {
        try {
            electricityUnitService.deleteUnitLoopByLoopId(id);

            electricityUnitService.updateLoopOrg(id);// 计算比例
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "删除关联回路失败", "");
        }
        return ServletUtils.buildRs(true, "删除关联回路", "");
    }

    /*
     * 
     * 删除用电单位
     * 
     */
    @RequestMapping(value = "deleteElectricityUnit")
    @ResponseBody
    public String deleteElectricityUnit(String unitId) {

        try {
            electricityUnitService.deleteElectricityUnit(unitId);
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "删除单位失败", "");
        }
        return ServletUtils.buildRs(true, "删除单位", "");
    }

}