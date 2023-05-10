/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.outwarddata.service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.machine.dao.TDeviceMachineDao;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
import com.jeeplus.modules.outwarddata.Entity.RealData;
import com.jeeplus.modules.outwarddata.dao.DeviceOutDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据配置Service
 *
 * @author long
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class DeviceOutService extends CrudService<TDeviceMachineDao, TDeviceMachine> {
    @Autowired
    private DeviceOutDao deviceOutDao;


    // 设备管理查询
    public List<MapEntity> tDeviceMachineList(String orgId) {

        List<MapEntity> tDeviceMachineList = deviceOutDao.deviceOutList(orgId);

        List<MapEntity> tChannelMachineList = deviceOutDao.channelOutList(orgId);

        MapEntity en = new MapEntity();
        for (MapEntity entity : tDeviceMachineList) {
            String devId = (String) entity.get("deviceId");

            ArrayList<MapEntity> aary = new ArrayList<MapEntity>();
            for (MapEntity channel : tChannelMachineList) {
                en = new MapEntity();
                String deviceId = (String) channel.get("deviceId");

                if (devId.equals(deviceId)) {

                    String real = "0";
                    String chId = "0";
                    Object realTime = "";

                    real = channel.get("realValue").toString();
                    realTime = channel.get("realTime").toString();
                    chId = channel.get("chId").toString();

                    en.put("chId", chId);
                    en.put("deviceId", deviceId);
                    en.put("chName", (String) channel.get("chName"));
                    en.put("realValue", real);
                    en.put("realTime", realTime);
                    en.put("chType", channel.get("chType").toString());
                    en.put("typeId", channel.get("typeId").toString());
                    en.put("logicOrgId", channel.get("logicOrgId").toString());
                    aary.add(en);
                }
            }
            entity.put("channel", aary);
        }
        return tDeviceMachineList;
    }

    @Transactional(readOnly = false)
    public void updateRealDatas(RealData real) {
        deviceOutDao.updateRealDatas(real);
    }

    public List<MapEntity> getOrgListById(String orgId) {
        return deviceOutDao.getOrgListById(orgId);
    }


}