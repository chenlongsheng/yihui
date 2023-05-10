package com.jeeplus.modules.bim.service;

import com.jeeplus.modules.bim.dao.SourceStoreyDao;
import com.jeeplus.modules.bim.entity.PDFOrg;
import com.jeeplus.modules.bim.entity.SourceStorey;
import com.jeeplus.modules.settings.entity.TOrg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018-12-27.
 */
@Service
public class SourceStoreyService {
    @Autowired
    SourceStoreyDao sourceStoreyDao;

    public void delByBuildingUuid(String buildingUuid){
        sourceStoreyDao.delByBuildingUuid(buildingUuid);
    }

    public void insert(SourceStorey sourceStorey){
        sourceStoreyDao.insert(sourceStorey);
    }

    public List<SourceStorey> findList(SourceStorey sourceStorey){
        return sourceStoreyDao.findList(sourceStorey);
    }

    public void insertPDFOrg(PDFOrg pdfOrg){
        sourceStoreyDao.insertPDFOrg(pdfOrg);
    }

    public TOrg findPDFOrgByProperty(String name,String value){
        return sourceStoreyDao.findPDFOrgByProperty(name,value);
    }
}
