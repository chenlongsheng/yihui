package com.jeeplus.modules.bim.dao;

import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.bim.entity.SourceElement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018-12-27.
 */
@MyBatisDao
public interface SourceElementDao {
    void delByBuildingUuid(@Param(value = "buildingUuid") String buildingUuid);

    void insert(SourceElement sourceElement);

    List<SourceElement> findList(SourceElement sourceElement);
}
