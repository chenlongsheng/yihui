package com.jeeplus.modules.warm.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.modules.warm.dao.PdfPrincipalDao;

@Service
public class PdfPrincipalService {

    @Autowired
    PdfPrincipalDao pdfPrincipalDao;

    public List<MapEntity> getPrincipalPhoneBychId(String chId) {

        return pdfPrincipalDao.getPrincipalPhoneBychId(chId);
    }

    public List<MapEntity> getPrincipalPhoneBydevId(String devId) {

        return pdfPrincipalDao.getPrincipalPhoneBydevId(devId);
    }
    // ----------------

    @Transactional(readOnly = false)
    public void insertMessageLog(MapEntity entity) {
        try {
            pdfPrincipalDao.insertMessageLog(entity);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Integer checkExpirationTime(String status, String chId, String level) {

        return pdfPrincipalDao.checkExpirationTime(status, chId, level);

    }

    public String checkExpirationByChId(String chId) {

        return pdfPrincipalDao.checkExpirationByChId(chId);

    }

    public Integer getCodeCount(String chId) {

        return pdfPrincipalDao.getCodeCount(chId);

    }

    public String selecthis() {
        
        Date sdate = new Date();
        // 执⾏时间（1s）
        pdfPrincipalDao.selecthis();
        // 结束时间
        Date edate = new Date();
        //  统计执⾏时间（毫秒）
        System.out.printf("执⾏时长：%d 毫秒." , (edate.getTime() - sdate.getTime()));
 
        
     
        
        return "";
    }

}
