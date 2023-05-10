/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.TreeDao;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.sys.entity.Area;

/**
 * 区域DAO接口
 * 
 * @author jeeplus
 * @version 2014-05-16
 */
@MyBatisDao
public interface AreaDao extends TreeDao<Area> {



	// 区域user下的集合
	public List<Area> userOrgList(@Param(value = "orgId") String orgId);

	// 区域code获取
	public String selectCode(@Param(value = "orgId") String orgId);

	// 区域底下子集的最大code获取
	public String maxCode(@Param(value = "orgId") String orgId);
	
	public Integer updateSonOrg(@Param(value = "type") Integer type, @Param(value = "parentId") String parentId);

	
	
	//以下新寫的
	//所有行政區域
     List<MapEntity> orgList(@Param(value = "orgId") String orgId);
	//用戶集合
     List<MapEntity> userList();
	
     MapEntity getOrg(@Param(value = "orgId") String orgId);
     
//     void insertUserOrg(@Param(value="id")String id,@Param(value="userId")String userId,@Param(value="orgId")String orgId);
	
//     void updateUserOrg(@Param(value="userId")String userId,@Param(value="orgId")String orgId);
     
     Set<MapEntity> elceOrgList(Area area);
     
     Set<MapEntity>  orgEditList(@Param(value="orgId")String orgId,@Param(value="parentIds")String parentIds);
     
//     void deleteUserOrg(@Param(value="orgId")String orgId);
     
     Long insertPdfOrg(Area area);
     
     void updatePdfOrg(Area area);
     
     void deletePdfOrg(@Param(value="orgId")String orgId);
     
 	//行政区域部分
 	List<MapEntity> getorgList();
 	
 	int deviceByOrgCount(@Param(value="orgId")String orgId);

 	int count(@Param(value="orgId")String orgId);
 	//获取配电房集合
 	List<MapEntity> getOrgListById(@Param(value="userId")String userId,@Param(value="orgId")String orgId); 
 	
 	//获取配电房集合
 	List<MapEntity> pdfUserList(@Param(value="userId")String userId); 
 	
 	void savePdfPrincipal(@Param(value="userId")String userId,@Param(value="pdfId")String pdfId);
 	
 	void updatePdfPrincipal(@Param(value="userId")String userId,@Param(value="pdfId")String pdfId);
 	
 	void deletePdfPrincipal(@Param(value="pdfId")String pdfId);
 	
 	int saveImage(MapEntity entity);
 	
 	List<MapEntity> findImageList(@Param(value="orgId")String orgId);
 	
 	List<MapEntity> getImage(@Param(value="id")String id);
 	//
 	void deleteImage(@Param(value="id")String id);
 	
 	void updateUrlImage(@Param(value="picUrl")String picUrl,@Param(value="imageName")String imageName,@Param(value="orgId")String orgId);
 	
 	MapEntity getOnceImage(@Param(value="orgId")String orgId);
 	
 	void updateImageName(@Param(value="name")String name,@Param(value="imageId")String imageId);
 	
 	void insertUserBureau(@Param(value="userId")String userId,@Param(value="bureauId")String bureauId);

 	List<MapEntity> test123(Area area);
}
