/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.jeeplus.common.persistence.CrudDao;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.annotation.MyBatisDao;
import com.jeeplus.modules.settings.entity.PdfLink;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TIdLinkId;
import com.jeeplus.modules.sys.entity.Area;

/**
 * 联动管理DAO接口
 * @author long
 * @version 2018-08-08
 */
@MyBatisDao
public interface TIdLinkIdDao extends CrudDao<TIdLinkId> {

	//t_code中的name集合==5
		public List<MapEntity> codeList();
		
		public List<MapEntity> TIdLinkIdList(MapEntity entity);
		
		//更改启用禁用
		public Integer saveUse(TIdLinkId tIdLinkId);
		
		//区域  通道底下
		public List<Area> findAreaList(@Param(value="name")String name);
		
		public Set<String> parentIdsList(@Param(value="orgId")String orgId);
		
		//-------------------------------------
		
	   //添加联动等表
		void insertLink(PdfLink pdfLink);
		
		void insertLinkCheck(TIdLinkId tIdLinkId);
		
		void insertPdfLinkTLink(@Param(value="checkId")String checkId,@Param(value="tLinkId")String tLinkId);
		
		
		//策略启用禁用
		void updateNotUse(@Param(value="notUse")String notUse,@Param(value="id")String id);
		// 2级启用禁用
		void updateCheckNotUse(@Param(value="notUse")String notUse,@Param(value="id")String id);
		
		void updateTLinkNotUse(@Param(value="notUse")String notUse,@Param(value="id")String id);
		
		
		List<MapEntity> getCodeList();
		
		List<MapEntity> getDeviceListByType(@Param(value="orgId")String orgId,@Param(value="devType")String devType);
		
		List<MapEntity> getCodeTypeList();
		
		List<MapEntity> getVideoList(@Param(value="orgId")String orgId);
		//预置位集合
		List<MapEntity>  getPositionList();
		//获 设备类型下的通道
		List<MapEntity> getPdfCodeList(@Param(value="devType")String devType,@Param(value="status")String status);
		
		List<MapEntity> linkList(@Param(value="orgId")String orgId);
		
		void deleteLink(@Param(value="id")String id);
		
		String getChId(@Param(value="devId")String devId,@Param(value="chType")String chType,@Param(value="typeId")String typeId);
		
		List<MapEntity> linkCheckList(MapEntity entity);
		
		List<MapEntity> getCodeLinkList();//被联动,空调,水泵灯类型
		//回调中被联动几个通道
		List<MapEntity> linkChannelist(@Param(value="orgId")String orgId,@Param(value="chType")String chType,@Param(value="typeId")String typeId);
		
		List<MapEntity> pdfCodeParam(@Param(value="status")String status);
		
		void deleteLinkCheck(@Param(value="pdfLinkId")String pdfLinkId,@Param(value="status")String status);
		//修改时候,先删除checkIds,后添加
		void deleteLinkCheckByCheckIds(@Param(value="pdfLinkId")String pdfLinkId,@Param(value="status")String status);		
		
		List<MapEntity> getUserList();//获取所有用户
		//获取配电房名称
		MapEntity orgNameBypdfLinkId(@Param(value="pdfLinkId")String pdfLinkId );
		
		MapEntity getLinkCheck(@Param(value="checkId")String checkId);
		
		MapEntity airconList();
		
		
}