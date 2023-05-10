/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.maintenance.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.pattern.IntegerPatternConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.IdGenSnowFlake;
import com.jeeplus.modules.maintenance.dao.PdfUserDao;
import com.jeeplus.modules.maintenance.entity.PdfUser;
import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.sys.dao.AreaDao;
import com.jeeplus.modules.sys.service.AreaService;
 

/**
 * 我方人员管理Service
 * 
 * @author long
 * @version 2019-01-09
 */
@Service
@Transactional(readOnly = true)
public class PdfUserService extends CrudService<PdfUserDao, PdfUser> {
	@Autowired
	private PdfUserDao pdfUserDao;
	@Autowired
	private AreaService areaService;

	public PdfUser get(String id) {
		return super.get(id);
	}

	public List<PdfUser> findList(PdfUser pdfUser) {
		return super.findList(pdfUser);
	}

	public Page<PdfUser> findPage(Page<PdfUser> page, PdfUser pdfUser) {
//		String codeIds = pdfUser.getCodeId();
		//分解前端codeIds为codeId和typeId类型
//		if (codeIds != null && codeIds != "") {
//			String[] codes = codeIds.split(",");
//			pdfUser.setCodeId(codes[0]);
//			pdfUser.setTypeId(codes[1]);
//		}
		pdfUser.setPage(page);
		List<PdfUser> list = dao.findList(pdfUser);
		for (PdfUser user : list) {
			user.setDepartOrgName(this.orgName(user.getDepartOrgId()));
		}
		page.setList(list);
		return page;
	}

	public String orgName(String orgId) {// 转化org中文名字

		MapEntity en = pdfUserDao.parentList(orgId);
		if(en==null) {
			return null;
		}
		Long id = (Long) en.get("id");
		String name = (String) en.get("name");
		en = pdfUserDao.parentList(id + "");
		Long parentId = (Long) en.get("id");
		String parentName = (String) en.get("name");
		en = pdfUserDao.parentList(parentId + "");
		String pParentName = (String) en.get("name");

		return pParentName + parentName + name;
	}

	public void saveElecCode(PdfUserOrg pdfUserOrg, String ourUserId) {
		
		String order = pdfUserOrg.getOrderNo();//获取用户的排序
		String orgIds = pdfUserOrg.getOrgIds();//得到用户拼接配电房的所有ids
		String orgParentId = pdfUserOrg.getOrgParentId();
		String codeIds = pdfUserOrg.getCodeIds();
		String[] idsList = codeIds.split(",");// code类型和主类型
		System.out.println(codeIds + "==设备类型的");
		MapEntity en = new MapEntity();
		String[] str = orgIds.split(",");//配电房
		en.put("ourUserId", ourUserId);
		en.put("orgParentId", orgParentId);
		en.put("orderNo", order);
		for (int i = 0; i < str.length; i++) {
			String id = IdGenSnowFlake.uuid().toString();
			en.put("id", id);
			en.put("orgId", str[i]);
//			System.out.println(id + "=====xuehuaid");
			int key = pdfUserDao.saveUserOrg(en);
			System.out.println(en.get("id") + "========jiaruhou ");
			if (key > 0) {// 添加配电房设备类型责任人
				for (String ids : idsList) {
					String codeId = ids;
					String typeId ="1";
					String maxOrderNo = pdfUserDao.maxOrderNo(codeId, typeId, str[i]);
					System.out.println(maxOrderNo + "entity");
					System.out.println(maxOrderNo + "==========maxOrderNo");
					if (maxOrderNo == null) {
						maxOrderNo = "0";
					}
					String newDate = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
					int result = pdfUserDao.updateDate(newDate, codeId, typeId, str[i], ourUserId);
					System.out.println(result + "======jieguo");
					if (result <= 0) {
						int orderNo = Integer.parseInt(maxOrderNo) + 1;
						pdfUserDao.insertCodeOrg(IdGenSnowFlake.uuid().toString(), codeId, typeId, ourUserId, str[i],
								orderNo, newDate);
					}
				}
			}
		}
	}

	// 添加用户
	@Transactional(readOnly = false)
	public void save(PdfUser pdfUser, JSONArray ja) {
		// pdfUser.preInsert();
		// dao.insert(pdfUser);
		super.save(pdfUser);
		String ourUserId = pdfUser.getId();
		if (ja != null) {
			// 删除用户配电房
			pdfUserDao.deleteUserOrg(ourUserId);
			// pdfUserDao.deleteCodeOrg(ourUserId);
			for (int i = 0; i < ja.size(); i++) {
				PdfUserOrg pdfUserOrg = JSONObject.parseObject(ja.get(i).toString(), PdfUserOrg.class);
				System.out.println(pdfUserOrg.toString());
				pdfUserOrg.getOrgIds();
				pdfUserOrg.getOrgParentId();
				pdfUserOrg.getCodeIds();
				this.saveElecCode(pdfUserOrg, ourUserId);
			}
		}
		// 删除用设备类型当前时间前三秒数据
		pdfUserDao.deleteCodeOrg(ourUserId, DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
	}


	
	
	// 详情    新改写----
	public List<MapEntity> details(String userId) {
		List<MapEntity> list = pdfUserDao.userDetails(userId);
		
		for (MapEntity mapEntity : list) {
			String orgId = (String) mapEntity.get("orgId");
			String parentId = (String) mapEntity.get("parentId");
			List<MapEntity> orgList = areaService.getOrgListById(parentId);
			List<MapEntity> userOrgDetail = pdfUserDao.userOrgDetail(userId,orgId);
			String orgName = this.orgName(parentId);
			System.out.println(orgName + "---orgName");
			mapEntity.put("areaName", orgName);
			mapEntity.put("codeList",userOrgDetail);
			mapEntity.put("orgList", orgList);
		}
		return list;
	}

	// 删除我方用户
	@Transactional(readOnly = false)
	public void delete(PdfUser pdfUser) {
		super.delete(pdfUser);
		// 删除用户第一,二责任人,当用户删除时候
		pdfUserDao.deleteUserOrg(pdfUser.getId());
		// --------------------------------------------------------------
		List<MapEntity> list = pdfUserDao.selectCodeOrgList(pdfUser.getId());
		for (MapEntity entity : list) {
			int orderNo = (int) entity.get("orderNo");
			if (orderNo == 1) {
				int codeId = (int) entity.get("codeId");
				int typeId = (int) entity.get("typeId");
				Long orgId = (Long) entity.get("orgId");
				System.out.println(codeId + "==" + typeId + "===" + orgId);
				List<MapEntity> li = pdfUserDao.secondOrder(codeId, typeId, orgId);
				System.out.println(li + "li==========");
				if (li != null) {
					for (MapEntity mapEntity : li) {
						if (mapEntity != null) {
							String id = (String) mapEntity.get("id");
							int key = pdfUserDao.updateOrder(id);
							System.out.println(key);
						}
					}
				}
			}
		}

		// -----------------------------------------------------------

		pdfUserDao.deleteCodeOrg(pdfUser.getId(), null);

	}

	// =================================

	// 我方员职位集合
	public List<MapEntity> posiList() {

		return pdfUserDao.posiList();
	}

	// 所有设备类型集合
	public List<MapEntity> tcodeList(String name) {

		return pdfUserDao.tcodeList(name);
	}

	// 删除职位
	@Transactional(readOnly = false)
	public Integer deletePosition(String id) {
		return pdfUserDao.deletePosition(id);
	}

	// 添加职位
	@Transactional(readOnly = false)
	public MapEntity insertPosition(String name) {
		MapEntity entity = new MapEntity();
		entity.put("name", name);
		pdfUserDao.insertPosition(entity);
		return entity;
	}

	// 辖区下的配电房
	public List<MapEntity> elceList(String orgId, String name) {
		List<MapEntity> list = pdfUserDao.elceList(orgId, name);
		return list;
	}

	// 添加用户的配电房
	public Integer saveUserOrg(MapEntity entity) {
		return pdfUserDao.saveUserOrg(entity);
	}

	


	
	//新加电业局集合   ++
	public Set<MapEntity> orgDepartList(String name,String orgId){
		Set<MapEntity> set = new HashSet<MapEntity>();
		Set<MapEntity> list = pdfUserDao.getElecList(name,orgId);
		set.addAll(list);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			MapEntity entity = (MapEntity) it.next();			
			Set<MapEntity> set1 = pdfUserDao.orgEditList((String)entity.get("parentIds"));
						set.addAll(set1);
		}
		return set;
	}
	
	

	// 获取修改的模糊查询区域===配电房查询
	public Set<MapEntity> orgList(String name, String orgId) {
		
		
		Set<MapEntity> set = new HashSet<MapEntity>();
		Set<MapEntity> list = pdfUserDao.getOrgList(name,orgId);
		set.addAll(list);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			MapEntity entity = (MapEntity) it.next();			
			Set<MapEntity> set1 = pdfUserDao.orgEditList((String)entity.get("parentIds"));
						set.addAll(set1);
		}
		return set;

	}

}