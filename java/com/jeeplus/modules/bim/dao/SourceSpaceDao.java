package com.jeeplus.modules.bim.dao;

import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.bim.entity.SourceSpace;
import com.jeeplus.modules.bim.entity.SourceStorey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018-12-27.
 */
@MyBatisDao
public interface SourceSpaceDao {
    void delByBuildingUuid(@Param(value = "buildingUuid") String buildingUuid);

    void insert(SourceSpace sourceSpace);

    List<SourceSpace> findList(SourceSpace sourceSpace);
}
