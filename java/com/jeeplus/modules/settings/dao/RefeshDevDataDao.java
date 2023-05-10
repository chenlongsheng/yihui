package com.jeeplus.modules.settings.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TOrg;

/**
 * Created by Administrator on 2018-08-17.
 */
@MyBatisDao
public interface RefeshDevDataDao extends CrudDao<TOrg> {
    
    List<MapEntity> getDeviceOnline();

    void onlineList(List<String> list);

    void notOnlineList(List<String> list);
    
    
    List<String> getMacsByDevId(@Param("id") String id);

}
