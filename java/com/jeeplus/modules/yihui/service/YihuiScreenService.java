/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.yihui.service;

import com.alibaba.druid.sql.visitor.functions.If;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.machine.dao.TDeviceMachineDao;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
import com.jeeplus.modules.outwarddata.Entity.RealData;
import com.jeeplus.modules.outwarddata.dao.DeviceOutDao;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.yihui.dao.YihuiScreenDao;
import org.apache.ibatis.annotations.Param;
import org.nutz.lang.Loop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据配置Service
 *
 * @author long
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class YihuiScreenService {
    @Autowired
    private YihuiScreenDao yihuiScreenDao;
    public Logger logger = LoggerFactory.getLogger(getClass());


    public Page<MapEntity> getRealAlarmogs(Page<MapEntity> page, MapEntity entity) {
        entity.setPage(page);
        page.setList(yihuiScreenDao.getRealAlarmogs(entity));
        return page;
    }


    public List<MapEntity> getDeviceOnlines(String orgId) {

        return yihuiScreenDao.getDeviceOnlines(orgId);
    }

    public MapEntity getTorg() {

        return yihuiScreenDao.getTorg();
    }


    // 设备管理查询
    public List<MapEntity> tDeviceLoopList(String orgId) {

        List<MapEntity> tDeviceMachineList = yihuiScreenDao.getLoopDevices(orgId);

        List<MapEntity> tChannelMachineList = yihuiScreenDao.getLoopChannels(orgId);

        MapEntity en = new MapEntity();
        for (MapEntity entity : tDeviceMachineList) {
            String devId = (String) entity.get("devId");

            ArrayList<MapEntity> aary = new ArrayList<MapEntity>();
            for (MapEntity channel : tChannelMachineList) {
                en = new MapEntity();
                String deviceId = (String) channel.get("deviceId");

                if (devId.equals(deviceId)) {

                    String real = "0";
                    String chId = "0";

                    real = channel.get("realValue").toString();

                    chId = channel.get("chId").toString();

                    en.put("chId", chId);
                    en.put("chName", (String) channel.get("chName"));
                    en.put("realValue", real);

                    en.put("monad", channel.get("monad").toString());


                    aary.add(en);
                }
            }

            entity.put("channel", aary);
        }
        return tDeviceMachineList;
    }

    public List<MapEntity> getHistorys(String orgId) {

        return yihuiScreenDao.getHistorys(orgId);
    }

    //
    //iter
    public MapEntity getOrgLoopsVideo(String orgId) {
        if (orgId == null) {
            orgId = "855018047319052288";
        }

        MapEntity en = new MapEntity();

        en.put("getVideos", yihuiScreenDao.getVideos(orgId));   //视频下拉框

        return en;
    }

    public List<MapEntity> getOrgLoops(String orgId) {

        logger.debug("orgId====="+orgId);
        logger.debug("userId===="+ UserUtils.getUser().getId());

        System.out.println("8888888888888888888");
        if (orgId == null) {
            orgId = "855018047319052288";
        }

//        System.out.println("orgId2========" +orgId);
        MapEntity en = new MapEntity();
//
        List<MapEntity> loopType1List = yihuiScreenDao.getOrgLoops(orgId, "6");

        List<MapEntity> loopType2List = yihuiScreenDao.getOrgLoops(orgId, "7");


//        List<MapEntity> loopType1List = list.stream().filter(loop -> loop.get("loopType").toString().equals("6")).collect(Collectors.toList());
//        List<MapEntity> loopType2List = list.stream().filter(loop -> loop.get("loopType").toString().equals("7")).collect(Collectors.toList());

        for (MapEntity mapEntity1 : loopType1List) {

            List<MapEntity> li = new ArrayList<MapEntity>();

            for (MapEntity mapEntity2 : loopType2List) {
                if (mapEntity1.get("orgId").toString().equals(mapEntity2.get("pId").toString())) {
                    li.add(mapEntity2);
                }
            }
            mapEntity1.put("list", li);
        }
        return loopType1List;
    }


}