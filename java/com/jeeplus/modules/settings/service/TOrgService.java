package com.jeeplus.modules.settings.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TOrgDao;
import com.jeeplus.modules.settings.entity.TOrg;

import java.util.List;

/**
 * Created by Administrator on 2018-08-17.
 */
@Service
@Transactional(readOnly = true)
public class TOrgService extends CrudService<TOrgDao,TOrg> {
    @Autowired
    TOrgDao tOrgDao;

    public List<TOrg> findListByParentId(Long parendId){
        return tOrgDao.findListByParentId(parendId);
    };

    //查询所有小区信息  type = 5
    public List<TOrg> selectAllHouse(){
        return tOrgDao.selectAllHouse();
    }

    public List<TOrg> getAllCityByCode(String code){
        return tOrgDao.getAllCityByCode(code);
    }

    public TOrg getOrgByCode(String code){
        return tOrgDao.getOrgByCode(code);
    }
}
