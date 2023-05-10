package com.jeeplus.modules.bim.service;

import com.jeeplus.modules.bim.dao.SourceElementDao;
import com.jeeplus.modules.bim.entity.SourceElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018-12-27.
 */
@Service
public class SourceElementService {
    @Autowired
    SourceElementDao sourceElementDao;

    public void delByBuildingUuid(String buildingUuid){
        sourceElementDao.delByBuildingUuid(buildingUuid);
    }

    public void insert(SourceElement sourceElement){
        sourceElementDao.insert(sourceElement);
    }

    public List<SourceElement> findList(SourceElement sourceElement){
        return sourceElementDao.findList(sourceElement);
    }
}
