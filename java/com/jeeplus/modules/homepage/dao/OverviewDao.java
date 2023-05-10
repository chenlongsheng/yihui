package com.jeeplus.modules.homepage.dao;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.homepage.entity.SandTable;
import com.jeeplus.modules.homepage.entity.Statistics;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018-12-20.
 */
@MyBatisDao
public interface OverviewDao extends CrudDao<Statistics> {
	
	//福建下区域
	List<MapEntity> getOrgList();
	
	// 获取所有配电房--地图
	List<MapEntity> pdfListById(@Param(value = "userId") String userId);
	
	//获取配电房详情
	List<MapEntity>  getDetailsByOrgId(@Param(value = "orgId") String orgId);
	
	//获取设备类型
	List<MapEntity> codeList();	
	
	//设备top5
	List<MapEntity>	devCountTop5(@Param(value = "userId") String userId);	
	
	//报警top10
	List<MapEntity>	alarmCountTop10(@Param(value = "userId") String userId,@Param(value = "beginDate") String beginDate,
			@Param(value = "endDate") String endDate);
	
	
	
	List<MapEntity>	 statisticsCount(@Param(value = "userId") String userId);	
	
	List<MapEntity>	 getChIds(@Param(value = "orgId") String orgId);
	
	
	//效率低
	List<MapEntity>  getEnergyList(@Param(value = "orgId") String orgId);
	
    List<MapEntity>  getDataByOrgId(@Param(value = "orgId") String orgId);//配电房数据
 
    List<String> getchorglist();
    
    List<MapEntity> getOrgs();
    
    List<String> getPdfs();
    
    List<MapEntity>  getOrgDetails();
    
   
	
}
