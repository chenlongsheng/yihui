/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TCodeDao;
import com.jeeplus.modules.settings.entity.TCode;

/**
 * code管理Service
 *
 * @author long
 * @version 2018-08-09
 */
@Service
@Transactional(readOnly = true)
public class TCodeService extends CrudService<TCodeDao, TCode> {

    @Autowired
    private TCodeDao tCodeDao;
    private List<MapEntity> findCodeList;

    public TCode get(String id, String typeId) {

        return tCodeDao.getCode(id, typeId);

    }

    public List<TCode> findList(TCode tCode) {
        return super.findList(tCode);
    }

    //新写的
    public List<TCode> getCodeList() {
        return tCodeDao.getCodeList();
    }

    // public Page<TCode> findPage(Page<TCode> page, TCode tCode) {
    // return super.findPage(page, tCode);
    // }

    @Transactional(readOnly = false)
    public void save(TCode tCode) {
        super.save(tCode);
    }

    @Transactional(readOnly = false)
    public void delete(TCode tCode) {
        super.delete(tCode);
    }

    // --------------------------------------
    // 分页
    public Page<TCode> findPage(Page<TCode> page, TCode entity) {
//        String skin = str;
        entity.setPage(page);
        List<TCode> findCodeList = tCodeDao.findCodeList(entity);
        for (int i = 0; i < findCodeList.size(); i++) {
            TCode tCode = findCodeList.get(i);
//			if(tCode.getIconSkin()=="")tCode.setIconSkin(skin);
            String path = path(tCode.getTypeId() + "") + tCode.getIconSkin();
            tCode.setIconSkin(path);

//			if(tCode.getWarnIconSkin()=="")tCode.setWarnIconSkin(skin);			
            path = path(tCode.getTypeId() + "") + tCode.getWarnIconSkin();
            tCode.setWarnIconSkin(path);

//			if(tCode.getOfflineIconSkin()=="")tCode.setOfflineIconSkin(skin);						
            path = path(tCode.getTypeId() + "") + tCode.getOfflineIconSkin();
            tCode.setOfflineIconSkin(path);

//			if(tCode.getDefenceIconSkin()=="")tCode.setDefenceIconSkin(skin);	
            path = path(tCode.getTypeId() + "") + tCode.getDefenceIconSkin();
            tCode.setDefenceIconSkin(path);

//			if(tCode.getWithdrawingIconSkin()=="")tCode.setWithdrawingIconSkin(skin);	
            path = path(tCode.getTypeId() + "") + tCode.getWithdrawingIconSkin();
            tCode.setWithdrawingIconSkin(path);

//			if(tCode.getSidewayIconSkin()=="")tCode.setSidewayIconSkin(skin);	
            path = path(tCode.getTypeId() + "") + tCode.getSidewayIconSkin();
            tCode.setSidewayIconSkin(path);

        }
        page.setList(findCodeList);
        return page;
    }

    public List<TCode> devTypeList() {

        List<TCode> findCodeList = tCodeDao.devTypeList();
        for (int i = 0; i < findCodeList.size(); i++) {
            TCode tCode = findCodeList.get(i);
//			if(tCode.getIconSkin()=="")tCode.setIconSkin(skin);
            String path = path(tCode.getTypeId() + "") + tCode.getIconSkin();
            tCode.setIconSkin(path);

//			if(tCode.getWarnIconSkin()=="")tCode.setWarnIconSkin(skin);			
            path = path(tCode.getTypeId() + "") + tCode.getWarnIconSkin();
            tCode.setWarnIconSkin(path);

//			if(tCode.getOfflineIconSkin()=="")tCode.setOfflineIconSkin(skin);						
            path = path(tCode.getTypeId() + "") + tCode.getOfflineIconSkin();
            tCode.setOfflineIconSkin(path);

//			if(tCode.getDefenceIconSkin()=="")tCode.setDefenceIconSkin(skin);	
            path = path(tCode.getTypeId() + "") + tCode.getDefenceIconSkin();
            tCode.setDefenceIconSkin(path);

//			if(tCode.getWithdrawingIconSkin()=="")tCode.setWithdrawingIconSkin(skin);	
            path = path(tCode.getTypeId() + "") + tCode.getWithdrawingIconSkin();
            tCode.setWithdrawingIconSkin(path);

//			if(tCode.getSidewayIconSkin()=="")tCode.setSidewayIconSkin(skin);	
            path = path(tCode.getTypeId() + "") + tCode.getSidewayIconSkin();
            tCode.setSidewayIconSkin(path);

        }
        return findCodeList;

    }


    public String path(String typeId) {
        String path = null;
        if (typeId.equals("1")) {
            path = "/static_modules/device/";
        } else {
            path = "/static_modules/channel/";
        }
        return path;
    }

    // 添加t_code
    @Transactional(readOnly = false)
    public void saveAdd(TCode tCode) {
        String sking = "46d78cc6-f888-45ea-9b21-143a244f11cb.png";
        tCode.setIconSkin(sking);
        tCode.setWarnIconSkin(sking);
        tCode.setOfflineIconSkin(sking);
        tCode.setDefenceIconSkin(sking);
        tCode.setWithdrawingIconSkin(sking);
        tCode.setSidewayIconSkin(sking);
        tCode.preUpdate();
        dao.insert(tCode);
    }

}