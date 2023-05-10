package com.jeeplus.modules.warm.service;

import java.util.List;
import java.util.Map;

import groovyjarjarantlr.collections.impl.LList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.modules.warm.dao.PdfAlarmDao;

@Service
public class PdfAlarmService {

    @Autowired
    PdfAlarmDao pdfAlarmDao;

    public List<Map<String, Object>> getAlarmListGroupByChType(String code) {
        return pdfAlarmDao.getAlarmListGroupByChType(code);
    }

    public List<Map<String, Object>> getAlarmListByChType(int chType, int typeId, String code) {
        return pdfAlarmDao.getAlarmListByChType(chType, typeId, code);
    }

    public void confirmAlarm(String id) {
        pdfAlarmDao.confirmAlarm(id);
    }

    public List<MapEntity> getAlarmListByPdfId(String code) {
        return pdfAlarmDao.getAlarmListByPdfId(code);
    }


}
