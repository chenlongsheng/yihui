package com.jeeplus.modules.bim.service;

import com.jeeplus.modules.bim.dao.SourceSpaceDao;
import com.jeeplus.modules.bim.entity.SourceSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018-12-27.
 */
@Service
public class SourceSpaceService {
    @Autowired
    SourceSpaceDao sourceSpaceDao;

    public void delByBuildingUuid(String buildingUuid){
        sourceSpaceDao.delByBuildingUuid(buildingUuid);
    }

    public void insert(SourceSpace sourceSpace){
        sourceSpaceDao.insert(sourceSpace);
    }

    public List<SourceSpace> findList(SourceSpace sourceSpace){
       return sourceSpaceDao.findList(sourceSpace);
    }
}
