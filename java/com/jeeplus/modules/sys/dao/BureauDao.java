/**
 * 
 */
package com.jeeplus.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.TreeDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.sys.entity.Area;

import scala.annotation.meta.param;

/**
 * @author admin
 *
 */
@MyBatisDao
public interface BureauDao extends TreeDao<Area> {

	void deleteBureauById(@Param(value = "bureauId") String bureauId);

	void updateBureauById(@Param(value = "bureauId") String bureauId, @Param(value = "oldParentId") String oldParentId,
			@Param(value = "parentId") String parentId, @Param(value = "parentIds") String parentIds,
			@Param(value = "name") String name);

	void insertBureau(MapEntity entity);
	//添加
	void inserUserBureau(@Param(value = "userId") String userId, @Param(value = "bureauId")Long bureauId);

	List<MapEntity> getBureauList(@Param(value = "bureauId") String bureauId);

	// 非admin获取所属供电单位集合
	List<MapEntity> getBureauIds(@Param(value = "userId") String userId);

	// 获取单位所有上级供电单位
	List<MapEntity> getOrgListByPId(@Param(value = "pIds") String pIds);
	
	List<MapEntity> checkBureau(@Param(value = "bureauId") String bureauId);
	
	void updateOrderNo(@Param(value = "orderNo") Integer orderNo,@Param(value = "bureauId") String bureauId);
	
	  List<MapEntity> getBureauListByUserId(@Param(value = "userId") String userId);

	
}
