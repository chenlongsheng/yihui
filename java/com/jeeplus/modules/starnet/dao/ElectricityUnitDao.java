/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.starnet.entity.ElectricityUnit;

/**
 * 数据配置DAO接口,
 * 
 * @author long
 * @version 2018-07-24
 */
@MyBatisDao
public interface ElectricityUnitDao extends CrudDao<MapEntity> {

    List<MapEntity> selectElectricityUnit(MapEntity entity);

    void update(@Param(value = "id") String id, @Param(value = "orderNo") String orderNo);

    void insertElectricityUnit(ElectricityUnit electricityUnit);
    
    void updateElectricityUnit(ElectricityUnit electricityUnit);
    
    void  insertUnitLoop(MapEntity entity);
        
    List<MapEntity>   selectLoopOrg();
        
    List<MapEntity>  getUnitLoopList(@Param(value = "unitId") String unitId);
    
   void deleteUnitLoopByLoopId(@Param(value = "id") String id);
   
   List<MapEntity>   getLoopsBypId(@Param(value = "parentId") String parentId);
   
   void  deleteElectricityUnit(@Param(value = "unitId") String unitId);
   
   void  updateLoopOrg(@Param(value = "value") Integer value,@Param(value = "values") Integer values,@Param(value = "id") String id);
   
   List<MapEntity>  getTypes(@Param(value = "type") String type);
   
//   获取用电单位下公共回路id
   List<String> getLoopsByunitId(@Param(value = "unitId") String unitId);
   
   List<MapEntity>  getVauleByLoopId(@Param(value = "loopId") String loopId);

}