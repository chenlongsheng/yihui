/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

/**
 * 数据配置DAO接口
 * 
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface LoopDao extends CrudDao<MapEntity> {

    List<MapEntity> loopListBypdfId(@Param("pdfId") String pdfId);

    List<MapEntity> getRealvalueByOrgId(@Param("pdfId") String pdfId);

    List<MapEntity> loopListByBureauId(@Param("bureauId") String bureauId);

    List<MapEntity> loopCodes();

    List<MapEntity> getLoopOrgId(@Param("bureauId") String bureauId);

    void insertBureauLoop(@Param("orgId") String orgId, @Param("bureauId") String bureauId);

    void deleteBureauLoop(@Param("bureauId") String bureauId);

    void updateLoopOrgImage(@Param("loopId") String loopId, @Param("image") String image);

    void updateOrderNo(@Param("loopId") String loopId, @Param("orderNo") String orderNo, @Param("parentCode") String parentCode);

    List<MapEntity> loopPdfList();

    void updateTypes(@Param("delType") Integer delType, @Param("orgId") String orgId);
    
   void  updateCodes(@Param("oldCode") String oldCode, @Param("code") String code);

}