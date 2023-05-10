package com.jeeplus.modules.homepage.service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.homepage.dao.StatisticsDao;
import com.jeeplus.modules.homepage.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Administrator on 2018-12-20.
 */
@Service
@Transactional(readOnly = true)
public class HomepagePdfService extends CrudService<StatisticsDao, Statistics> {
    @Autowired
    com.jeeplus.modules.homepage.dao.HomepageDao homepageDao;

    public List<MapEntity> getTypeList() {

        Integer typeId = 1;
        String path = null;
        if (typeId.equals("1")) {
            path = "/static_modules/device/";
        } else {
            path = "/static_modules/channel/";
        }
        return homepageDao.getTypeList();
    }

    public List<MapEntity> getDeviceNumList() {

        return homepageDao.getDeviceNumList();
    }

    public List<MapEntity> getDevTypeNames() {

        return homepageDao.getDevTypeNames();
    }

    public List<MapEntity> getHistorysByDevId(String devId, String devType, String time) {

        List<MapEntity> getHistorysByDevId = homepageDao.getHistorysByDevId(devId, devType, time);

        return getHistorysByDevId;
    }

    public List<MapEntity> getTailById(String devId) {

        List<MapEntity> getTailById = homepageDao.getTailById(devId);

        return getTailById;

    }


}
