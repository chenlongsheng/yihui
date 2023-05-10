package com.jeeplus.modules.homepage.dao;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.homepage.entity.Statistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018-12-20.
 */
@MyBatisDao
public interface HomepageDao {


    List<MapEntity> getTypeList();

    List<MapEntity> getDeviceNumList();


    List<MapEntity> getDevTypeNames();


    List<MapEntity> getHistorysByDevId(@Param(value = "devId") String devId, @Param(value = "devType") String devType, @Param(value = "time") String time);

    List<MapEntity> getTailById(@Param(value = "devId") String devId);


}
