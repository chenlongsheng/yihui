package com.jeeplus.modules.warm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface PdfPrincipalDao {

	List<MapEntity> getPrincipalPhoneBychId(String chId); 
	
	   List<MapEntity> getPrincipalPhoneBydevId(String devId); 
	
	   //--------------------
	   
	   
	   
	   List<MapEntity> getPrincipalPhoneByTime();

	  
	    void insertMessageLog(MapEntity entity);
	    
	    Integer checkExpirationTime(@Param("status") String status, @Param("chId") String chId, @Param("level") String level);
	    
	    String checkExpirationByChId(@Param("chId") String chId);
	    
	    Integer getCodeCount(@Param("chId") String chId);
	    
	    
	    List<MapEntity> selecthis();
	    

}
