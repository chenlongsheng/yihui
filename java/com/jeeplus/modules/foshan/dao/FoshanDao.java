package com.jeeplus.modules.foshan.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface FoshanDao {

	public List<Map<String, Object>> getDeviceAndData();
	
	public List<Map<String, Object>> gethsitorylists(@Param("pstatus") String pstatus);
	

}
