package com.jeeplus.modules.homepage.dao;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.homepage.entity.SandTable;
import com.jeeplus.modules.homepage.entity.Statistics;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018-12-20.
 */
@MyBatisDao
public interface ChartDao extends CrudDao<Statistics> {
	// 当天或者单月新增报警
	List<MapEntity> alarmTypeList(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId,
			@Param(value = "status") String status);

	// 门磁开启率
	List<MapEntity> doorRateList(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId);

	// 其他设备类型  在线  率
	List<MapEntity> devTypeRate(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId);
	
	// 其他设备类型  离线   率
	List<MapEntity> devTypeOffRateListTop10(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId);
	

	// 安全天数
	List<MapEntity> saveDays(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId);

	// 开门次数top10
	List<MapEntity> devTypeTop10(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId,
			@Param(value = "beginDate") String beginDate, @Param(value = "endDate") String endDate);
     //获取门磁平均开门次数24小时
	List<MapEntity> avgDoorAlarmList(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId,
			@Param(value = "beginDate") String beginDate, @Param(value = "endDate") String endDate);
    
	//获取 温湿度最高和最低值
	List<MapEntity> getHumitures(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId);
	
	//获取温湿度top10
	List<MapEntity> getHumituresTop10(@Param(value = "userId") String userId, @Param(value = "devType") String devType, @Param(value = "chType") String chType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId);
	
	
	List<MapEntity> getElectricityThan(@Param(value = "userId") String userId, @Param(value = "devType") String devType,
			@Param(value = "bureauId") String bureauId, @Param(value = "orgId") String orgId);
	
	
	
}
