package com.jeeplus.modules.bim.dao;

import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.bim.entity.PDFOrg;
import com.jeeplus.modules.bim.entity.SourceStorey;
import com.jeeplus.modules.settings.entity.TOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018-12-27.
 */
@MyBatisDao
public interface SourceStoreyDao {
    void delByBuildingUuid(@Param(value = "buildingUuid") String buildingUuid);

    void insert(SourceStorey sourceStorey);

    List<SourceStorey> findList(SourceStorey sourceStorey);

    void insertPDFOrg(PDFOrg pdfOrg);

    TOrg findPDFOrgByProperty(@Param(value = "propertyName") String name, @Param(value = "value") String value);
}
