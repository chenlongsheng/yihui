/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.OverviewDao;
import com.jeeplus.modules.homepage.entity.OrgPDFInfo;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;
import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceConfigDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.dao.TDeviceDetailDao;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceConfig;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.entity.TRealData;
import com.jeeplus.modules.sys.dao.AreaDao;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.dao.PdfOrderDao;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 设备管理Service
 * 
 * @author long
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class TDeviceService extends CrudService<TDeviceDao, TDevice> {

    @Autowired
    private TDeviceDao tDeviceDao;
    @Autowired
    PdfOrderDao pdfOrderDao;
    @Autowired
    OverviewDao overviewDao;

    @Autowired
    private TDeviceDetailService tDeviceDetailService;

    @Autowired
    private TChannelDao tChannelDao;

    @Autowired
    private TDeviceConfigService tDeviceConfigService;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TRealDataService tRealDataService;

    public TDevice get(String id) {
        return super.get(id);
    }

    public List<TDevice> findList(TDevice tDevice) {
        return super.findList(tDevice);
    }

    public Page<TDevice> findPage(Page<TDevice> page, TDevice tDevice) {
        return super.findPage(page, tDevice);
    }

    // ---------------------------------------------------------

    public MapEntity getOrgName(String devId) {

        return tDeviceDao.getOrgName(devId);
    }

    // 修改设备图片
    @Transactional(readOnly = false)
    public Integer updatePic(String id, String picturePath) {
        return tDeviceDao.updatePic(id, picturePath);
    }

    // 获取from表单字段
    public MapEntity getDeviceFrom(String id, String deviceFromList) {

        return tDeviceDao.getDeviceFrom(id, deviceFromList);
    }

    public List<MapEntity> getSmokeList(String orgId) {

        return tDeviceDao.getSmokeList(orgId);
    }

    // 更新t_channel的orgId
    @Transactional(readOnly = false)
    public int updateChannel(String orgId, String devId) {

        return tDeviceDao.updateChannel(orgId, devId);
    }

    public MapEntity getDeviceEn(String id) {

        return tDeviceDao.getDeviceEn(id);
    }

    public Page<MapEntity> findPage(Page<MapEntity> page, MapEntity entity) {
        entity.setPage(page);
        List<MapEntity> list = tDeviceDao.deviceList(entity);
        page.setList(list);
        return page;
    }

    public List<MapEntity> deviceList(MapEntity entity) {
        List<MapEntity> deviceList = tDeviceDao.deviceList(entity);
        return deviceList;
    }

    // t_code中的name集合
    public List<MapEntity> codeList() {
        List<MapEntity> codeList = tDeviceDao.codeList();
        return codeList;
    }

    // 有用
    @Transactional(readOnly = false)
    public void delete(TDevice tDevice) {
        super.delete(tDevice);
        tDeviceDao.deleteDetail(tDevice);
        tDeviceDao.deleteChannelbyDevid(tDevice.getId());
        tDeviceDao.deleteImageDev(tDevice.getId());
    }

    @Transactional(readOnly = false)
    public String saveDevice(TDevice tDevice, TDeviceDetail tDeviceDetail) {
        int key = 0;
        if (tDevice.getIsNewRecord()) {
            tDevice.preInsert();
            key = dao.insert(tDevice);
        } else {
            tDevice.preUpdate();
            key = dao.update(tDevice);
        }
        // super.save(tDevice);
        String id = tDevice.getId();
        tDeviceDetail.setDeviceId(Integer.parseInt(id + ""));
        tDeviceDetailService.saveDeviceDetail(tDeviceDetail);
        return id;
    }

    public List<TDevice> findAllList() {
        return tDeviceDao.deviceAllList();
    }

//	public List<GatewayInfo> getGatewayInfo(GatewayInfo gatewayInfo) {
//		return tDeviceDao.getGatewayInfo(gatewayInfo);
//	}

    // 更改启用禁用
    @Transactional(readOnly = false)
    public int saveUse(TDevice tDevice) {

        return tDeviceDao.saveUse(tDevice);
    }

    // 获取区域底下设备小图标
    public List<MapEntity> devicePic(String orgId, String coldId) {

        return tDeviceDao.devicePic(orgId, coldId);
    }

    @Transactional(readOnly = false)
    public Integer updateCoords(String id, String coordX, String coordY) {

        return tDeviceDao.updateCoords(id, coordX, coordY);
    }

    // 删除网关
    @Transactional(readOnly = false)
    public String delGateway(String id) {

        if (StringUtils.isBlank(id)) {
            return "网关id不能为空";
        }
        TDevice device = this.get(id);
        if (device == null) {
            return "网关不存在";
        } else if (device.getDevType() != 150) {
            return "该设备不是网关";
        }
        this.delete(device);

        TDevice d = new TDevice();
        d.setParentId(Long.valueOf(id));

        // 查询充电桩
        List<TDevice> charges = this.findList(d);
        if (charges == null) {
            return "删除成功";
        }
        // 查询插头
        for (TDevice charge : charges) {
            this.delete(charge);
            TDevice d2 = new TDevice();
            d2.setParentId(Long.valueOf(charge.getId()));
            List<TDevice> plus = this.findList(d2);
            // 删除插头等信息
            if (plus == null) {
                continue;
            }
            for (TDevice p : plus) {
                this.delete(p);
                tChannelDao.deleteByDevId(p.getId());
            }
        }
        return "删除成功";
    }

    // 删除充电桩
    @Transactional(readOnly = false)
    public void delPowerCharge(String id) {
        TDevice tDevice = tDeviceDao.get(id);
        this.delete(tDevice);
        TDevice d2 = new TDevice();
        d2.setParentId(Long.valueOf(id));
        List<TDevice> plus = this.findList(d2);
        // 删除插头等信息
        if (plus == null) {
            return;
        }
        for (TDevice p : plus) {
            this.delete(p);
            tChannelDao.deleteByDevId(p.getId());
        }
    }

    // ------------------------------------

    public MapEntity getUserDetail(String orgId) {

        return tDeviceDao.getUserDetail(orgId);
    }

    // 修改addr
    @Transactional(readOnly = false)
    public void midifyDevice(String id, String addr) {
        tDeviceDao.midifyDevice(id, addr, null, null);
    }

    @Transactional(readOnly = false)
    public String addModifyDevice(JSONArray ja, JSONArray jaCh, JSONArray jaNVR) {
        String orgId = null;
        if (ja != null) {
            for (int i = 0; i < ja.size(); i++) {
                MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
                orgId = (String) entity.get("orgId");
                tDeviceDao.midifyDevice((String) entity.get("id"), (String) entity.get("addr"),
                        (String) entity.get("orgId"), null);
                entity.put("pId", entity.get("id"));

                tDeviceDao.midifyDevice(null, "", (String) entity.get("orgId"), (String) entity.get("pId"));
                // 修改通道区域id
                tDeviceDao.midifyChannel((String) entity.get("id"), (String) entity.get("orgId"));
            }
        }
        // 控制器
        if (jaCh != null) {
            for (int i = 0; i < jaCh.size(); i++) {
                MapEntity en = JSONObject.parseObject(jaCh.get(i).toString(), MapEntity.class);
                orgId = (String) en.get("orgId");

                System.out.println(en.toString());
                tDeviceDao.updateChannelCode((String) en.get("name"), (String) en.get("addr"),
                        (String) en.get("channelType"), (String) en.get("chId"));
            }
        }
        if (jaNVR != null) {
            for (int i = 0; i < jaNVR.size(); i++) {
                MapEntity en = JSONObject.parseObject(jaNVR.get(i).toString(), MapEntity.class);
                orgId = (String) en.get("orgId");
                System.out.println(en.toString());
                tDeviceDao.updateVideoByChId((String) en.get("channelType"), (String) en.get("name"),
                        (String) en.get("orgId"), (String) en.get("addr"), (String) en.get("param0"),
                        (String) en.get("remarks"), (String) en.get("chId"));
            }
        }
        return orgId;
    }

    // 添加按mac換回
    public List<MapEntity> deviceByMac(String mac) {
        List<MapEntity> list = tDeviceDao.deviceByMac(mac);
        for (MapEntity entity : list) {
            int devType = (int) entity.get("devType");
            String devId = (String) entity.get("id");
            if (devType == 170 || devType == 171 || devType == 183) {
                // 回调继电器
                List<MapEntity> relayList = tDeviceDao.getRelayList(devId + "");
                // 回调灯等
                List<MapEntity> lightList = tDeviceDao.getLightList();
                entity.put("relayList", relayList);
                entity.put("lightList", lightList);
            } else if (devType == 131 || devType == 178) {
                List<MapEntity> getChSmokeList = tDeviceDao.getChSmokeList(devId + "");// 传统烟感
                entity.put("getChSmokeList", getChSmokeList);
                List<MapEntity> getSmokeType = tDeviceDao.getSmokeType(); // 烟感手报下拉框
                entity.put("getSmokeType", getSmokeType);
            } else if (devType == 109) {
                List<MapEntity> getVideoType = tDeviceDao.getVideoType(); // 视频下拉框
                List<MapEntity> getwireName = tDeviceDao.getwireName();
                entity.put("getVideoType", getVideoType);
                entity.put("getwireName", getwireName);
            }
        }
        return list;
    }

    public MapEntity getElectricList(String devId) {

        MapEntity entity = new MapEntity();
        List<MapEntity> lightList = tDeviceDao.getLightList();
        List<MapEntity> getElectricList = tDeviceDao.getElectricList(devId);
        entity.put("lightList", lightList);
        entity.put("getElectricList", getElectricList);
        return entity;
    }

    // 设备管理查询
    public List<MapEntity> deviceList(String orgId) {
        List<MapEntity> deviceList = tDeviceDao.tDeviceList(orgId);
        for (int i = 0; i < deviceList.size(); i++) {
            MapEntity entity = deviceList.get(i);
            Integer devType = (Integer) entity.get("dev_type");
            String devId = (String) entity.get("id");
//			int devOnline = (int) entity.get("online");
            String channelName = "";
            String addrName = "";
            List<MapEntity> channelList = tDeviceDao.deviceChannelList(devId);
            for (int j = 0; j < channelList.size(); j++) {
                MapEntity channel = channelList.get(j);
                Integer chType = (Integer) channel.get("ch_type");
                Integer typeId = (Integer) channel.get("type_id");
                Integer channelType = (Integer) channel.get("channelType");

                String chName = (String) channel.get("chName");
                String addr = (String) channel.get("addr");

                Long param0 = Long.parseLong(channel.get("param0").toString());
                Double param1 = Double.parseDouble(channel.get("param1").toString());
                Double param2 = Double.parseDouble(channel.get("param2").toString());

                String real = "0";
                String warn = "0";
                Object realTime = "";
                Object objRealData = redisTemplate.opsForValue().get("cdpdf_devdataproc_realdata_" + channel.get("id"));
                if (objRealData != null) {
                    JSONObject jobj_real_data = JSONObject.parseObject(objRealData.toString());
                    String real_value = jobj_real_data.getString("real_value");
                    realTime = jobj_real_data.getString("real_time");
                  //  System.out.println(realTime + "===============");
                    Long l_real_value = Long.parseLong(real_value);
                    double dvalue = (l_real_value - param2) / param1;
                    real = String.format("%." + param0 + "f", dvalue);
                    warn = jobj_real_data.getString("warn");
                }
                entity.put("realTime", realTime);
                // String real = (String) channel.get("real_value");
                // String warn = (String) channel.get("warn");

                Integer chNo = (Integer) channel.get("ch_no");
                if (chNo == null) {
                    chNo = 0;
                }
                if (StringUtils.isBlank(warn)) {
                    warn = "0";
                }
                String allWarn = "0";// 以下烟感类型

                if (chType == 30 && typeId == 2) {// 高温报警
                    entity.put("highTemperatureWarn", warn);
                    entity.put("highTemValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chNo == 20 && chType == 103 && typeId == 3) {// 电池电压
                    entity.put("VoltageWarn1", warn);
                    entity.put("VolValue1", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 103 && typeId == 3) {// 电压
                    entity.put("VoltageWarn", warn);
                    entity.put("VolValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 3 && typeId == 2) {// 烟雾报警
                    entity.put("SmokeWarn", warn);
                    entity.put("SmokeValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 161 && typeId == 3) {// 温度灵敏度
                    entity.put("TemSensitiveWarn", warn);
                    entity.put("TemSensitiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 162 && typeId == 3) {// 液体压力
                    entity.put("PressureWarn", warn);
                    entity.put("PressureValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 124 && typeId == 3) {// 液位高度
                    entity.put("HeightWarn", warn);
                    entity.put("HeightValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 29 && typeId == 2) {// 温度传感器异常
                    entity.put("TemSensorErrorWarn", warn);
                    entity.put("TemSensorErrorValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 31 && typeId == 2) {// 按键报警
                    entity.put("keyWarn", warn);
                    entity.put("keyValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 160 && typeId == 3) {// 烟雾灵敏度
                    entity.put("smokeKeenWarn", warn);
                    entity.put("smokeKeenValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 28 && typeId == 2) {// 烟雾传感器异常
                    entity.put("sensitiveWarn", warn);
                    entity.put("senValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 19 && typeId == 2) {// 电池电压低
                    entity.put("batteryWarn", warn);
                    entity.put("batteryValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chNo == 120 && chType == 126 && typeId == 3) {// 网关接收信号
                    entity.put("gatewayWarn", warn);
                    entity.put("gatewayValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chNo == 121 && chType == 126 && typeId == 3) {// 节点接收信号
                    entity.put("nodeWarn", warn);
                    entity.put("nodeValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 126 && typeId == 3) {// 信号强度
                    entity.put("signalWarn", warn);
                    entity.put("signalValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chNo == 5 && chType == 147 && typeId == 3) {// [温湿度|数据上报]周期
                    entity.put("temBeatWarn", warn);
                    entity.put("temBeatValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chNo == 122 && chType == 147 && typeId == 3) {// [心跳|数据上报]周期
                    entity.put("hearBeatWarn", warn);
                    entity.put("hearBeatValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                // 以下水禁
                else if (chType == 148 && typeId == 3) {// 水浸深度
                    entity.put("WaterWarn", warn);
                    entity.put("WaterValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 149 && typeId == 3) {// 水浸绳状态
                    entity.put("WaterRopeWarn", warn);
                    entity.put("WaterRopeValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 121 && typeId == 3) {// 剩余电量--电池状态
                    entity.put("electricWarn", warn);
                    entity.put("electricValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 145 && typeId == 3) {// 超时开门时长
                    entity.put("TimeoutWarn", warn);
                    entity.put("TimeoutValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 146 && typeId == 3) {// 发射功率
                    entity.put("powerWarn", warn);
                    entity.put("powerValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 101 && typeId == 3) {// 温度
                    entity.put("wenduWarn", warn);
                    entity.put("wenduValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 102 && typeId == 3) {// 湿度
                    entity.put("shiduWarn", warn);
                    entity.put("shiduValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chNo == 3 && chType == 150 && typeId == 3) {// 温度预警值
                    entity.put("temwarningWarn", warn);
                    entity.put("temwarningValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chNo == 4 && chType == 150 && typeId == 3) {// 湿度预警值
                    entity.put("moiswarningWarn", warn);
                    entity.put("moiswarningValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 153 && typeId == 3) {// 功率因数
                    entity.put("powerFactorWarn", warn);
                    entity.put("powerFactorValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 24 && typeId == 2) {// 温度报警
                    entity.put("tempWarn", warn);
                    entity.put("tempValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 25 && typeId == 2) {// 湿度报警
                    entity.put("humidityWarn", warn);
                    entity.put("humiditypValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 26 && typeId == 2) {// 传感器异常
                    entity.put("sensorWarn", warn);
                    entity.put("sensorValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 137 && typeId == 3) {// 亮度调节
                    entity.put("brightnessWarn", warn);
                    entity.put("brightnessValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 5 && typeId == 2) {// 门磁
                    entity.put("gateWarn", warn);
                    entity.put("gateValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chType == 23 && typeId == 2) {// 开门超时
                    entity.put("opendoorWarn", warn);
                    entity.put("opendoorValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                // 灯控/////////////
                else if (chNo == 3 && chType == 201 && typeId == 4) {// 继电器状态1
                    entity.put("electriWarn1", warn);
                    entity.put("electriValue1", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chNo == 4 && chType == 154 && typeId == 3) {// 打开时长1
                    entity.put("openingDurationWarn1", warn);
                    entity.put("openingValue1", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chNo == 5 && chType == 155 && typeId == 3) {// 关闭时长1
                    entity.put("closeDurationWarn1", warn);
                    entity.put("closeValue1", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chNo == 6 && chType == 201 && typeId == 4) {// 继电器状态2
                    entity.put("electriWarn2", warn);
                    entity.put("electriValue2", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chNo == 7 && chType == 154 && typeId == 3) {// 打开时长2
                    entity.put("openingDurationWarn2", warn);
                    entity.put("openingValue2", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chNo == 8 && chType == 155 && typeId == 3) {// 关闭时长2
                    entity.put("closeDurationWarn2", warn);
                    entity.put("closeValue2", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chNo == 9 && chType == 201 && typeId == 4) {// 继电器状态3
                    entity.put("electriWarn3", warn);
                    entity.put("electriValue3", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chNo == 10 && chType == 154 && typeId == 3) {// 打开时长3
                    entity.put("openingDurationWarn3", warn);
                    entity.put("openingValue3", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }

                } else if (chNo == 11 && chType == 155 && typeId == 3) {// 关闭时长3
                    entity.put("closeDurationWarn3", warn);
                    entity.put("closeValue3", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 104 && typeId == 3) {// 电流
                    entity.put("ElecurrentWarn", warn);
                    entity.put("ElecurrentValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 156 && typeId == 3) {// 当前功率
                    entity.put("CurrentPowerWarn", warn);
                    entity.put("CurrentPowerValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 157 && typeId == 3) {// 电能累积值
                    entity.put("energyWarn", warn);
                    entity.put("energyValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 158 && typeId == 3) {// 电能累积时长
                    entity.put("energyTimeWarn", warn);
                    entity.put("energyTimeValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 159 && typeId == 3) {// 元器件故障代码
                    entity.put("FaultCodeWarn", warn);
                    entity.put("FaultCodeValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 27 && typeId == 2) {// 直流电源状态
                    entity.put("DcStateWarn", warn);
                    entity.put("DcStateValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                // 以下电量
                else if (chType == 105 && typeId == 3) {// A向电压
                    entity.put("AVoltageWarn", warn);
                    entity.put("AVoltageValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 106 && typeId == 3) {// b向电压
                    entity.put("BVoltageWarn", warn);
                    entity.put("BVoltageValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 107 && typeId == 3) {// c向电压
                    entity.put("CVoltageWarn", warn);
                    entity.put("CVoltageValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 163 && typeId == 3) {// AB电压

                    entity.put("ABvoltageWarn", warn);
                    entity.put("ABvoltageValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 164 && typeId == 3) {// BC电压
                    entity.put("BCvoltageWarn", warn);
                    entity.put("BCvoltageValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 165 && typeId == 3) {// ca电压
                    entity.put("CAvoltageWarn", warn);
                    entity.put("CAvoltageValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 166 && typeId == 3) {// a向电流
                    entity.put("AElectWarn", warn);
                    entity.put("AElectValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 167 && typeId == 3) {// b向电流
                    entity.put("BElectWarn", warn);
                    entity.put("BElectValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 168 && typeId == 3) {// c向电流
                    entity.put("CElectWarn", warn);
                    entity.put("CElectValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 169 && typeId == 3) {// 电流总和
                    entity.put("TotalElectWarn", warn);
                    entity.put("TotalElectValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 170 && typeId == 3) {// a功率因素
                    entity.put("APowerWarn", warn);
                    entity.put("APowerValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 171 && typeId == 3) {// b功率因素
                    entity.put("BPowerWarn", warn);
                    entity.put("BPowerValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 172 && typeId == 3) {// c功率因素
                    entity.put("CPowerWarn", warn);
                    entity.put("CPowerValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 173 && typeId == 3) {// 总功率因素
                    entity.put("TotalPowerWarn", warn);
                    entity.put("TotalPowerValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 174 && typeId == 3) {// 电源频率
                    entity.put("FrequencyWarn", warn);
                    entity.put("FrequencyValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 175 && typeId == 3) {// a有功功率
                    entity.put("AactiveWarn", warn);
                    entity.put("AactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 176 && typeId == 3) {// b有功功率
                    entity.put("BactiveWarn", warn);
                    entity.put("BactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 177 && typeId == 3) {// c有功功率
                    entity.put("CactiveWarn", warn);
                    entity.put("CactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 178 && typeId == 3) {// 总有功功率
                    entity.put("TotalactiveWarn", warn);
                    entity.put("TotalactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 179 && typeId == 3) {// a无功功率
                    entity.put("AReactiveWarn", warn);
                    entity.put("AReactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 180 && typeId == 3) {// b无功功率
                    entity.put("BReactiveWarn", warn);
                    entity.put("BReactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 181 && typeId == 3) {// c无功功率
                    entity.put("CReactiveWarn", warn);
                    entity.put("CReactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 182 && typeId == 3) {// 总无功功率
                    entity.put("TotalReactiveWarn", warn);
                    entity.put("TotalReactiveValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }
                // -------------
                else if (chType == 110 && typeId == 3) {// a视在功率
                    entity.put("ChemicalWarn", warn);
                    entity.put("ChemicalValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }
                // ------------------

                else if (chType == 183 && typeId == 3) {// a视在功率
                    entity.put("AVisualWarn", warn);
                    entity.put("AVisualValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 184 && typeId == 3) {// b视在功率
                    entity.put("BVisualWarn", warn);
                    entity.put("BVisualValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 185 && typeId == 3) {// c视在功率
                    entity.put("CVisualWarn", warn);
                    entity.put("CVisualValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 186 && typeId == 3) {// 总视在功率
                    entity.put("TotalVisualWarn", warn);
                    entity.put("TotalVisualValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 187 && typeId == 3) {// 总用电量
                    entity.put("totalElecWarn", warn);
                    entity.put("totalElecValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 188 && typeId == 3) {// 正向有功用电量
                    entity.put("RightElecWarn", warn);
                    entity.put("RightElecValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 189 && typeId == 3) {// 反向有功用电量
                    entity.put("DireElecWarn", warn);
                    entity.put("DireElecValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 190 && typeId == 3) {// 正向无功用电量
                    entity.put("RightLessWarn", warn);
                    entity.put("RightLessValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 191 && typeId == 3) {// 反向无功用电量
                    entity.put("DireLessWarn", warn);
                    entity.put("DireLessValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 192 && typeId == 3) {// a总用电量
                    entity.put("AtotalElecWarn", warn);
                    entity.put("AtotalElecValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 197 && typeId == 3) {// b总用电量
                    entity.put("BtotalElecWarn", warn);
                    entity.put("BtotalElecValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 202 && typeId == 3) {// c总用电量
                    entity.put("CtotalElecWarn", warn);
                    entity.put("CtotalElecValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 193 && typeId == 3) {// a正向有功电量
                    entity.put("ArightWarn", warn);
                    entity.put("ArightValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 198 && typeId == 3) {// b正向有功电量
                    entity.put("BrightWarn", warn);
                    entity.put("BrightValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 203 && typeId == 3) {// c正向有功电量
                    entity.put("CrightWarn", warn);
                    entity.put("CrightValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 194 && typeId == 3) {// a反向有功电量
                    entity.put("AdirectionWarn", warn);
                    entity.put("AdirectionValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 199 && typeId == 3) {// b反向有功电量
                    entity.put("BdirectionWarn", warn);
                    entity.put("BdirectionValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 204 && typeId == 3) {// c反向有功电量
                    entity.put("CdirectionWarn", warn);
                    entity.put("CdirectionValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 195 && typeId == 3) {// a正向无功电量
                    entity.put("AWrightWarn", warn);
                    entity.put("AWrightValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 200 && typeId == 3) {// b正向无功电量
                    entity.put("BWrightWarn", warn);
                    entity.put("BWrightValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 205 && typeId == 3) {// c正向无功电量
                    entity.put("CWrightWarn", warn);
                    entity.put("CWrightValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 196 && typeId == 3) {// a反向无功电量
                    entity.put("AWdirectionWarn", warn);
                    entity.put("AWdirectionValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 201 && typeId == 3) {// b反向无功电量
                    entity.put("BWdirectionWarn", warn);
                    entity.put("BWdirectionValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 206 && typeId == 3) {// c反向无功电量
                    entity.put("CWdirectionWarn", warn);
                    entity.put("CWdirectionValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 207 && typeId == 3) {// a相温度
                    entity.put("AWdirectionTemWarn", warn);
                    entity.put("AWdirectionTemValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 208 && typeId == 3) {// b相温度
                    entity.put("BWdirectionTemWarn", warn);
                    entity.put("BWdirectionTemValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 41 && typeId == 2) {// 温度 新
                    entity.put("LowpowerWarn", warn);
                    entity.put("LowpowerValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                } else if (chType == 42 && typeId == 2) {// 温度 新
                    entity.put("ShierrorWarn", warn);
                    entity.put("ShierrorValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }

                else if (chType == 209 && typeId == 3) {// c相温度
                    entity.put("CWdirectionTemWarn", warn);
                    entity.put("CWdirectionTemValue", real);
                    if (!warn.equals("0")) {
                        allWarn = warn;
                        entity.put("allWarn", allWarn);
                    }
                }
                if (chType == 201 && typeId == 4) { // 灯
                    if (channelType != null) {
                        channelName += chName;
                        addrName += addr;
                    }
                }
            }
            entity.put("channelName", channelName);
            entity.put("addrName", addrName);
        }
        return deviceList;
    }

    // 网关实时状态
    public List<MapEntity> macStateList(String orgId) {

        List<MapEntity> macStateList = tDeviceDao.macStateList(orgId);
        for (MapEntity mapEntity : macStateList) {
            int online = Integer.parseInt(mapEntity.get("online").toString());
            Date date = (Date) mapEntity.get("time");
            if (date == null) {
                date = new Date();
            }
            if (online == 0) {
                long nd = 1000 * 24 * 60 * 60;
                long nh = 1000 * 60 * 60;
                long nm = 1000 * 60;
                // long ns = 1000;
                // 获得两个时间的毫秒时间差异
                long diff = new Date().getTime() - date.getTime();
                // 计算差多少天
                long day = diff / nd;
                // 计算差多少小时
                long hour = diff % nd / nh;
                // 计算差多少分钟
                long min = diff % nd % nh / nm;
                // 计算差多少秒//输出结果
                String days = day + "天" + hour + "小时" + min + "分钟";
                mapEntity.put("days", days);
            }
        }

        return tDeviceDao.macStateList(orgId);
    }

    public List<MapEntity> getChannelByDevId(String devId) {

        return tDeviceDao.getChannelsById(devId);
    }

    public List<MapEntity> GatewayList(String orgId) {

        return tDeviceDao.GatewayList(orgId);
    }

    public List<MapEntity> getNVRList(String orgId) {// 获取nvr设备主机

        return tDeviceDao.getNVRList(orgId);
    }

    // 设备管理中nvr删除
    @Transactional(readOnly = false)
    public void deleteNvrByDevId(String devId) {

        tDeviceDao.deleteNvrByDevId(devId);
    }

    // 设备管理中新网关删除
    @Transactional(readOnly = false)
    public void updateOrgBydevId(String id) {

        tDeviceDao.updateOrgBydevId(id);
    }

    // 以下总览
    public MapEntity allView(String orgId) {
        List<MapEntity> leveDevice = tDeviceDao.leveDevice(orgId);
        for (MapEntity en : leveDevice) {
            Date date = (Date) en.get("leveTime");
            if (date != null) {
                long nd = 1000 * 24 * 60 * 60;
                long nh = 1000 * 60 * 60;
                long nm = 1000 * 60;
                // 获得两个时间的毫秒时间差异
                long diff = new Date().getTime() - date.getTime();
                // 计算差多少天
                long day = diff / nd;
                // 计算差多少小时
                long hour = diff % nd / nh;
                // 计算差多少分钟
                long min = diff % nd % nh / nm;
                // 计算差多少秒//输出结果
                String days = day + "天" + hour + "小时" + min + "分钟";
                en.put("offTime", days);
            }
        }
        Set<MapEntity> orgEditList = areaDao.orgEditList("7579", orgId);
        for (MapEntity mapEntity : orgEditList) {
            int count = tDeviceDao.deviceCount(orgId);
            mapEntity.put("deviceCount", count);
        }

        // -----------------------------
//		List<MapEntity> detaiDevice = tDeviceDao.detaiDevice(orgId);
//		Iterator iter = detaiDevice.iterator();
//		while (iter.hasNext()) {
//			MapEntity entity = (MapEntity) iter.next();
//			if (entity == null) {
//				iter.remove();
//			}
//		}
        MapEntity entity = new MapEntity();
        entity.put("tOrg", orgEditList);// 配电房数据
        entity.put("getDevicTypeList", tDeviceDao.getDevicTypeList(orgId));// 智能辅控设备总览

//		entity.put("offLineCount", tDeviceDao.homepageCount(orgId));// count数字
//		entity.put("leveDevice", leveDevice);// 离线设备
//		entity.put("detaiDevice", detaiDevice);// 环境实时数据
        entity.put("TextDetailDevice", this.deviceList(orgId));// 烟感水浸实时数据

        entity.put("getAlarmRateList", tDeviceDao.getAlarmRateList(orgId));// 报警球状比例
        entity.put("codeList", overviewDao.codeList());// 设备类型集合

        PdfOrder pdfOrder = new PdfOrder();
        pdfOrder.setAlarmAddr(orgId);
        pdfOrder.setState(0);
        pdfOrder.setUserId(UserUtils.getUser().getId());
        entity.put("findOrder", pdfOrderDao.findOrder(pdfOrder));// 报警集合

        return entity;
    }

    public MapEntity videolist(String orgId) {
        List<MapEntity> videolist = tDeviceDao.videolist(orgId);
        MapEntity entity = new MapEntity();
        entity.put("videolist", videolist);
        return entity;
    }

    public List<MapEntity> getTodayDatasByPdfId(String pdfId) {

        // 获取所有温湿度设备, 以及名称.
        List<MapEntity> tempHumDevices = tDeviceDao.getDevicebyTypeAndOrgId(169, pdfId);
        List<MapEntity> channels = new ArrayList<MapEntity>();
        // 获取设备下的温湿度通道, 给予命名
        for (MapEntity device : tempHumDevices) {
            String devId = device.get("id").toString();
            String addr = (String) device.get("addr");
            String name = device.get("name").toString();

            List<MapEntity> devChannels = tDeviceDao.getTempHumChannelByDevId(devId);
            for (MapEntity channel : devChannels) {
                channel.put("name", addr + channel.get("name"));
                channels.add(channel);
            }
        }
        // 迭代通道列表,
        for (MapEntity channel : channels) {
            String id = channel.get("id").toString();
            TRealData trd = tRealDataService.get(id);
            Date uploadTime = trd.getRealTime();
            if (uploadTime == null) {
                uploadTime = new Date();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String uploadDate = sdf.format(new Date());
            String todayDate = sdf.format(uploadTime);

            String[] timeInterval = { "00:00-01:00", "01:00-02:00", "02:00-03:00", "03:00-04:00", "04:00-05:00",
                    "05:00-06:00", "06:00-07:00", "07:00-08:00", "08:00-09:00", "09:00-10:00", "10:00-11:00",
                    "11:00-12:00", "12:00-13:00", "13:00-14:00", "14:00-15:00", "15:00-16:00", "16:00-17:00",
                    "17:00-18:00", "18:00-19:00", "19:00-20:00", "20:00-21:00", "21:00-22:00", "22:00-23:00",
                    "23:00-24:00" };

            List<Double> valueList = new ArrayList<Double>();

            // 判断最后上传时间,如果最后上传时间是今天,
            if (uploadDate.equals(todayDate)) {
                // 查询该通道 今天之前最后一条历史数据 ,, 以及今天的所有数据
                MapEntity lastDataBeforeToday = tDeviceDao.getLastDataBeforeToday(id);
                List<MapEntity> todayData = tDeviceDao.getTodayData(id);

                // 分组每个小时内的数据,取平均值, 如果该组无 记录, 则获取, 上一个非空组的最后一条记录,作为平均值
                double prePeriodLastValue = 0;
                for (int i = 0; i < timeInterval.length; i++) {
                    String item = timeInterval[i];
                    String[] hour = item.split("-");
                    String beginHour = hour[0];
                    String endHour = hour[1];

                    String beginTime = todayDate + " " + beginHour;
                    String endTime = todayDate + " " + endHour;
                    Date beginDate = null;
                    Date endDate = null;
                    try {
                        beginDate = sdf2.parse(beginTime);
                        endDate = sdf2.parse(endTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Long beginTimestamp = beginDate.getTime();
                    Long endTimestamp = endDate.getTime();
                    Long nowTimestamp = new Date().getTime();

                    // 判断这个时间段的开始时间,是否小于现在, 如果 大于,说明,时间还没到
                    if (beginTimestamp < nowTimestamp) {
                        List<Double> values = getValueBetweenTime(todayData, beginTimestamp, endTimestamp);
                        if (i == 0) {
                            if (values.size() == 0) {
                                valueList.add(Double.parseDouble(lastDataBeforeToday.get("history_value").toString()));
                                prePeriodLastValue = Double
                                        .parseDouble(lastDataBeforeToday.get("history_value").toString());
                            } else {
                                double sum = 0;
                                for (Double d : values) {
                                    sum += d;
                                }
                                valueList.add(sum / values.size());
                                prePeriodLastValue = values.get(values.size() - 1);
                            }
                        } else {
                            if (values.size() == 0) {
                                valueList.add(prePeriodLastValue);
                            } else {
                                double sum = 0;
                                for (Double d : values) {
                                    sum += d;
                                }
                                valueList.add(sum / values.size());
                                prePeriodLastValue = values.get(values.size() - 1);
                            }
                        }
                    }
                }
            }
            channel.put("data", valueList);
        }
        return channels;
    }

    List<Double> getValueBetweenTime(List<MapEntity> datas, Long beginTimestamp, Long endTimestamp) {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Double> values = new ArrayList<Double>();
        for (MapEntity data : datas) {
            Double value = Double.parseDouble(data.get("history_value").toString());
            String historyTime = data.get("history_time").toString();
            try {
                Date historyTimeDate = sdf3.parse(historyTime);
                Long historyTimestamp = historyTimeDate.getTime();

                if (beginTimestamp < historyTimestamp) {
                    if (endTimestamp > historyTimestamp) {
                        values.add(value);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return values;
    }

    public List<MapEntity> getDevTypeList(String orgId) {
        return tDeviceDao.getDevTypeList(orgId);
    }

    @Transactional(readOnly = false)
    public void updateChannelBychId(String devId, JSONArray ja) {

        tDeviceDao.updateChannelByDevId(devId);
        if (ja != null) {
            for (int i = 0; i < ja.size(); i++) {
                MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
                String chId = (String) entity.get("id");
                String channelType = (String) entity.get("channelType");
                String addr = (String) entity.get("addr");
                String name = (String) entity.get("name");
                tDeviceDao.updateChannelBychId(chId, addr, channelType, name);
            }
        }
    }

    public List<MapEntity> getOrgListById(String orgId) {

        return tDeviceDao.getOrgListById(orgId);

    }

    @Transactional(readOnly = false)
    public void deleteChanByChId(String chId) {

        tDeviceDao.deleteChanByChId(chId);
    }

    @Transactional(readOnly = false)
    public String modifyVideoOrderNo(JSONArray jaNVR) {
        if (jaNVR != null) {
            for (int i = 0; i < jaNVR.size(); i++) {
                MapEntity en = JSONObject.parseObject(jaNVR.get(i).toString(), MapEntity.class);

                tDeviceDao.updateOrderNo((String) en.get("chId"), (String) en.get("param0"));
            }
        }

        return "";
    }

    @Transactional(readOnly = false)
    public void deleteVideoBychId(String chId) {

        tDeviceDao.deleteVideoBychId(chId);

    }

    public MapEntity getNVRFrom(String chId) {
        MapEntity entity = new MapEntity();

        List<MapEntity> getVideoType = tDeviceDao.getVideoType(); // 视频下拉框
        List<MapEntity> getwireName = tDeviceDao.getwireName();
        entity.put("getVideoType", getVideoType);
        entity.put("getwireName", getwireName);
        return entity;

    }

    @Transactional(readOnly = false)
    public void updateVideo(String channelType, String chId, String addr, String remarks, String chName) {

        tDeviceDao.updateVideo(channelType, chName, addr, remarks, chId, tDeviceDao.selectOrgByChId(chId));

    }

    public List<MapEntity> selectNVRList(String orgId) {

        return tDeviceDao.selectNVRList(orgId);
    }

    public List<MapEntity> selectLockList(String orgId) {

        List<MapEntity> devIds = tDeviceDao.getDevIds(orgId);

        for (MapEntity mapEntity : devIds) {

            String devId = (String) mapEntity.get("devId");
            List<MapEntity> selectLockList = tDeviceDao.selectLockList(devId);
            mapEntity.put("tchannel", selectLockList);

        }
        return devIds;
    }

    public List<MapEntity> selectLockList1(String orgId) {
        List<MapEntity> devIds = tDeviceDao.getDevIds(orgId);
        List<MapEntity> getLockList = tDeviceDao.getLockList(orgId);
        for (MapEntity mapEntity : devIds) {
            List<MapEntity> array = new ArrayList<MapEntity>();
            String devId = (String) mapEntity.get("devId");
            for (MapEntity entity : getLockList) {
                String dev_id = (String) entity.get("devId");
                if (devId.equals(dev_id)) {
                    array.add(entity);
                }
            }
            mapEntity.put("tchannel", array);
        }
        return devIds;
    }

    public List<MapEntity> getLoopByOrgId(String orgId) {

        // entity.put("getChannelByChId", tDeviceDao.getChannelByChId(devId));
//      entity.put("getLoopByOrgId", tDeviceDao.getLoopByOrgId(orgId));
        return tDeviceDao.getLoopByOrgId(orgId);

    }

    @Transactional(readOnly = false)
    public void updateOrgIdByChId(String chIds, String loopId) {

        tDeviceDao.updateOrgIdByChId(chIds, loopId);

    }

}