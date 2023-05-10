/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.machine.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.OverviewDao;
import com.jeeplus.modules.machine.dao.TDeviceMachineDao;
import com.jeeplus.modules.machine.entity.TDevChannel;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.service.TDeviceConfigService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.sys.dao.AreaDao;
import com.jeeplus.modules.sys.entity.User;
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
public class TDeviceMachineService extends CrudService<TDeviceMachineDao, TDeviceMachine> {
    @Autowired
    private TDeviceMachineDao tDeviceMachineDao;

    @Autowired
    private RedisTemplate redisTemplate;

    // 设备管理查询
    public List<MapEntity> tDeviceMachineList(String orgId, String deviceId) {

        List<MapEntity> tDeviceMachineList = tDeviceMachineDao.tDeviceMachineList(orgId, deviceId);

        List<MapEntity> tChannelMachineList = tDeviceMachineDao.tChannelMachineList(orgId, deviceId);

        MapEntity en = new MapEntity();
        for (MapEntity entity : tDeviceMachineList) {
            String devId = (String) entity.get("id");
            String chNos = (String) entity.get("chNos");

            ArrayList<MapEntity> aary = new ArrayList<MapEntity>();
            for (MapEntity channel : tChannelMachineList) {
                en = new MapEntity();
                String dev_id = (String) channel.get("devId");
                String allWarn = "0";
                if (devId.equals(dev_id)) {

                    Integer chType = Integer.parseInt(channel.get("ch_type").toString());
                    String real = "0";
                    String warn = "0";
                    String chId = "0";
                    Object realTime = "";

                    real = (String) channel.get("realValue");
                    warn = (String) channel.get("warn");
                    realTime = (String) channel.get("realTime");
                    chId = (String) channel.get("chId");
                    entity.put("realTime", realTime);
                    Integer chNo = (Integer) channel.get("ch_no");

                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                    en.put("type", channel.get("type").toString());
                    en.put("warn", warn);
                    en.put("real", real);
                    en.put("chNo", chNo);
                    en.put("name", (String) channel.get("chName"));
                    en.put("monad", (String) channel.get("monad"));
                    en.put("chType", chType);
                    en.put("stateName", (String) channel.get("stateName"));
                    en.put("chId", chId);
                    entity.put("loopOrgId", (String) channel.get("loopOrgId"));

                    aary.add(en);
                }
            }
            entity.put("channel", aary);
        }
        return tDeviceMachineList;
    }

    public List<MapEntity> allVideoList(String orgId) {
        return tDeviceMachineDao.allVideoList(orgId, UserUtils.getUser().getId());
    }

    @Transactional(readOnly = false)
    public String insertPdfUserId(String chIds) {

        tDeviceMachineDao.deleteUserVideo(UserUtils.getUser().getId());
        if (StringUtils.isNotBlank(chIds)) {
            String[] split = chIds.split(",");
            for (int i = 0; i < split.length; i++) {
                tDeviceMachineDao.insertPdfUserId(split[i], UserUtils.getUser().getId());
            }
        }
        return "";
    }

    public List<MapEntity> getCodeList() {

        return tDeviceMachineDao.getCodeList();
    }

    public List<ArrayList<MapEntity>> getHistoryList1(String orgId, String time, String devType, String chType,
                                                      String typeId) {

        List<MapEntity> historyList = tDeviceMachineDao.getHistoryList(orgId, time, devType, chType);
        Set<String> set = new HashSet<String>();
        for (MapEntity enti : historyList) {
            set.add(enti.get("chId").toString());
        }
        List<ArrayList<MapEntity>> list = new ArrayList<ArrayList<MapEntity>>();
        for (String str : set) {
            ArrayList<MapEntity> aary = new ArrayList<MapEntity>();
            for (MapEntity user : historyList) {
                if (str.equals(user.get("chId").toString())) {
                    aary.add(user);
                }
            }
            list.add(aary);
        }
        return list;
    }


    public Map<String, List<MapEntity>> getHistoryList(String orgId, String time, String devType, String chType,
                                                       String typeId) {

        List<MapEntity> historyList = tDeviceMachineDao.getHistoryList(orgId, time, devType, chType);
        Map<String, List<MapEntity>> map = new HashMap<>();
        for (MapEntity user : historyList) {

            String key = user.get("chId").toString();
            if (map.containsKey(key)) {
                // map中存在以此id作为的key，将数据存放当前key的map中
                map.get(key).add(user);
            } else {
                // map中不存在以此id作为的key，新建key用来存放数据
                List<MapEntity> userList = new ArrayList<>();
                userList.add(user);
                map.put(key, userList);
            }
        }
        return map;
    }

    public List<MapEntity> getDeviceCodeByorgId(String orgId) {

        return tDeviceMachineDao.getDeviceCodeByorgId(orgId);
    }

    public List<MapEntity> getDeviceCodeList(String devType) {

        return tDeviceMachineDao.getDeviceCodeList(devType);
    }

    @Transactional(readOnly = false)
    public void updateDeviceCodeByDevType(String devType, JSONArray ja) {

        if (ja != null) {
            // 删除用户配电房
            tDeviceMachineDao.updateDeviceCodeByDevType(devType);
            for (int i = 0; i < ja.size(); i++) {
                MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
                String orderNo = entity.get("orderNo").toString();
                String notUse = entity.get("notUse").toString();
                String id = entity.get("id").toString();
                tDeviceMachineDao.updateDeviceCodeById(id, notUse, orderNo);
            }
        }

    }

}