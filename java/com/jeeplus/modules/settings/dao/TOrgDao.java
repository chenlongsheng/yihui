package com.jeeplus.modules.settings.dao;

import java.util.List;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.TOrg;

/**
 * Created by Administrator on 2018-08-17.
 */
@MyBatisDao
public interface TOrgDao extends CrudDao<TOrg> {

    List<TOrg> findListByParentId(Long parendId);

    List<TOrg> selectAllHouse();

    List<TOrg> getAllCityByCode(String code);

    TOrg getOrgByCode(String code);
}
