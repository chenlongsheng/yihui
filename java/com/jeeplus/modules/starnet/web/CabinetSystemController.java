/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;

import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.starnet.service.CabinetSystemService;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.starnet.service.PowerDataService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */

//柜内系统
@Controller
@RequestMapping(value = "cabinet/")
public class CabinetSystemController extends BaseController {

    @Autowired
    private CabinetSystemService cabinetSystemService;
    
    @Autowired
    private TOrgService tOrgService;

    // 树形出线柜
    @RequestMapping(value = { "tDeviceCabinetList" })
    @ResponseBody
    public String tDeviceCabinetList(String orgId) {
        return ServletUtils.buildRs(true, "柜内出线柜树形", cabinetSystemService.tDeviceCabinetList(orgId));
    }

    
    // 柜内温度表格
    @RequestMapping(value = { "selectTempChannel" })
    @ResponseBody
    public String selectCabinetTempChannel(String orgId) {
    	List<MapEntity> tempChannel = cabinetSystemService.selectCabinetTempChannel(orgId);
    	return ServletUtils.buildRs(true, "温度通道列表", tempChannel);
    }
    
    
    // 柜内温度表格
    @RequestMapping(value = { "selectCabinetTempData" })
    @ResponseBody
    public String selectCabinetData(String orgId, String time) {
    	TOrg org = tOrgService.get(orgId);
    	List<MapEntity> tempData = cabinetSystemService.selectCabinetTempData(orgId, time);
    	
    	Map< String,List<MapEntity> > formatData = new HashMap< String,List<MapEntity> >();
    	
    	for(MapEntity data : tempData) {
    		data.put("orgName", org.getName());
    		String history_time = data.get("history_time").toString();
    		List<MapEntity> formatDataList = null;
    		if(formatData.get(history_time) != null) {
    			formatDataList = formatData.get(history_time);
    		} else {
    			formatDataList = new ArrayList<MapEntity>();
    			formatData.put(history_time, formatDataList);
    		}
    		formatDataList.add(data);
    	}
    	
    	
    	List<MapEntity> sortList = new ArrayList<MapEntity>();
    	ArrayList<String> keyList = new ArrayList<>(formatData.keySet());
        Collections.sort(keyList);
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            MapEntity sortdata = new MapEntity();
            sortdata.put("key", key);
            sortdata.put("value", formatData.get(key));
            sortList.add(sortdata);
        }
    	
        return ServletUtils.buildRs(true, "温度", sortList);
    }

    /*
     * 
     * 导出柜内温度报表
     * 
     */
    @RequestMapping("/exportCabinetDataReports")
    @ResponseBody
    public void exportCabinetDataReports(String orgId, String time,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        cabinetSystemService.exportCabinetDataReports(httpServletRequest, httpServletResponse,
                cabinetSystemService.selectCabinetTempData(orgId, time));
    }
    
    //电缆温度表格
    @RequestMapping("/selectCableTempData")
    @ResponseBody
    public String selectCableData(String orgId, String time,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    	TOrg org = tOrgService.get(orgId);
    	List<MapEntity> tempData = cabinetSystemService.selectCableTempData(orgId, time);
    	
    	Map< String,List<MapEntity> > formatData = new HashMap< String,List<MapEntity> >();
    	
    	for(MapEntity data : tempData) {
    		data.put("orgName", org.getName());
    		String history_time = data.get("history_time").toString();
    		List<MapEntity> formatDataList = null;
    		if(formatData.get(history_time) != null) {
    			formatDataList = formatData.get(history_time);
    		} else {
    			formatDataList = new ArrayList<MapEntity>();
    			formatData.put(history_time, formatDataList);
    		}
    		formatDataList.add(data);
    	}
    	
    	List<MapEntity> sortList = new ArrayList<MapEntity>();
    	ArrayList<String> keyList = new ArrayList<>(formatData.keySet());
        Collections.sort(keyList);
        for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            MapEntity sortdata = new MapEntity();
            sortdata.put("key", key);
            sortdata.put("value", formatData.get(key));
            sortList.add(sortdata);
        }
    	
        return ServletUtils.buildRs(true, "温度", sortList);
    }
    
    //电缆温度报表
    @RequestMapping("/selectCableDataExport")
    @ResponseBody
    public void selectCableDataExport(String orgId, String time,
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        cabinetSystemService.exportCableDataReports(httpServletRequest, httpServletResponse,
                cabinetSystemService.selectCableTempData(orgId, time));
    }
    
}