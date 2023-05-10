/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.hssf.util.Region;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.restlet.engine.local.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.OverviewDao;

import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.service.TDeviceConfigService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.starnet.dao.ElectricityUnitDao;
import com.jeeplus.modules.starnet.dao.EnergyAnalysisDao;
import com.jeeplus.modules.starnet.entity.ElectricityUnit;
import com.jeeplus.modules.sys.dao.AreaDao;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.dao.PdfOrderDao;

/**
 * 数据配置Service
 * 
 * @author long
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class ElectricityUnitService {
    @Autowired
    private ElectricityUnitDao electricityUnitDao;

    // 分页查询用电单位
    public Page<MapEntity> findPage(Page<MapEntity> page, MapEntity entity) {

        entity.setPage(page);
        page.setList(electricityUnitDao.selectElectricityUnit(entity));
        return page;

    }

    public List<MapEntity> selectElectricityUnit(MapEntity entity) {
        return electricityUnitDao.selectElectricityUnit(entity);
    }

    // 修改用电单位排序
    @Transactional(readOnly = false)
    public void updateOrderNo(JSONArray ja) {

        if (ja != null) {
            for (int i = 0; i < ja.size(); i++) {
                MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);

                String id = entity.get("id").toString();
                String orderNo = entity.get("orderNo").toString();
                electricityUnitDao.update(id, orderNo);

            }
        }
    }

    // 添加/修改用电单位
    @Transactional(readOnly = false)
    public void insertElectricityUnit(ElectricityUnit electricityUnit) {

        if (StringUtils.isNotBlank(electricityUnit.getId())) {

            electricityUnitDao.updateElectricityUnit(electricityUnit);// 修改用电单位
            // 修改用电单位后,更新回路公共比例****获取关联回路id集合
            List<String> loopsByunitIdList = electricityUnitDao.getLoopsByunitId(electricityUnit.getId());
            for (int i = 0; i < loopsByunitIdList.size(); i++) {
                String loopOrgId = loopsByunitIdList.get(i);
                // 更新关联回路重新计算比例
                updateLoopOrg(loopOrgId);
            }
        } else {
            electricityUnitDao.insertElectricityUnit(electricityUnit);// 添加用电单位
        }
    }
    
    

    // 添加单位下关联回路
    @Transactional(readOnly = false)
    public void insertUnitLoop(MapEntity entity) {
        electricityUnitDao.insertUnitLoop(entity);

        updateLoopOrg(entity.get("loopOrgId").toString());

    }

    public void updateLoopOrg(String loopId) {

        List<MapEntity> vauleByLoopId = electricityUnitDao.getVauleByLoopId(loopId);
        int values = 0;
        for (MapEntity mapEntity : vauleByLoopId) {
            int value = Integer.parseInt(mapEntity.get("loopValue").toString());
            values += value;
        }
        // 获取到总数一下更新表比例
        for (MapEntity mapEntity : vauleByLoopId) {
            int value = Integer.parseInt(mapEntity.get("loopValue").toString());
            String id = mapEntity.get("id").toString();
            electricityUnitDao.updateLoopOrg(value, values, id);
        }

    }

    public List<MapEntity> selectLoopOrg() {// 获取下拉框

        List<MapEntity> selectLoopOrg = electricityUnitDao.selectLoopOrg();
        for (MapEntity mapEntity : selectLoopOrg) {
            String type = mapEntity.get("type").toString();
            List<MapEntity> types = electricityUnitDao.getTypes(type);
            mapEntity.put("types", types);
        }

        return selectLoopOrg;
    }

    public List<MapEntity> getUnitLoopList(String unitId) {

        List<MapEntity> unitLoopList = electricityUnitDao.getUnitLoopList(unitId);
//        for (MapEntity mapEntity : unitLoopList) {
//            int notDeduction = (int) mapEntity.get("notDeduction");
//            if (notDeduction == 1) {
//                String parentId = mapEntity.get("loopOrgId").toString();
//                mapEntity.put("list", electricityUnitDao.getLoopsBypId(parentId));
//
//            }
//
//        }

        return unitLoopList;
    }

    // 删除绑定的回路
    @Transactional(readOnly = false)
    public void deleteUnitLoopByLoopId(String loopOrgId) {

        electricityUnitDao.deleteUnitLoopByLoopId(loopOrgId);
        // 删除回路后更新公共回路比例..
        updateLoopOrg(loopOrgId);

    }

//删除用电单位
    @Transactional(readOnly = false)
    public void deleteElectricityUnit(String unitId) {

        // 更新回路公共计算比例
        List<String> loopsByunitIdList = electricityUnitDao.getLoopsByunitId(unitId);

        for (int i = 0; i < loopsByunitIdList.size(); i++) {
            String loopOrgId = loopsByunitIdList.get(i);
            // 先删除关联回路
            deleteUnitLoopByLoopId(loopOrgId);

            // 更新这条回路所有其他回路的计算比例
            updateLoopOrg(loopOrgId);
        }

        // 删除单位
        electricityUnitDao.deleteElectricityUnit(unitId);

    }

}