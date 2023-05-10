package com.jeeplus.modules.bim.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;

@MyBatisDao
public interface BimDataDao {

	Map<String,Object> getRealDataByChId(@Param("chid")String chid);

}
