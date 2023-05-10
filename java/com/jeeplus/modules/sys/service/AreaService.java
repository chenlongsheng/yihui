/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.sys.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctc.wstx.util.StringUtil;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.TreeService;
import com.jeeplus.common.utils.IdGenSnowFlake;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.maintenance.dao.PdfUserDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.sys.dao.AreaDao;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 区域Service
 * 
 * @author jeeplus
 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class AreaService extends TreeService<AreaDao, Area> {
	@Autowired
	private PdfUserDao pdfUserDao;
	@Autowired
	private AreaDao areaDao;

	@Autowired
	private TDeviceDao tDeviceDao;

	public List<Area> findAll() {
		return UserUtils.getAreaList();
	}

	// 保存配电房
	@Transactional(readOnly = false)
	public void save(Area area) {
		String org = area.getId();
		super.save(area);
		int type = Integer.parseInt(area.getType()) + 1;
		String orgId = area.getId();

		if (StringUtils.isBlank(org)) {
			areaDao.insertPdfOrg(area);// 添加配点房附表
			areaDao.savePdfPrincipal(area.getUserId(), area.getId());// 添加负责人表

		} else {
			areaDao.updatePdfOrg(area);
			areaDao.updatePdfPrincipal(area.getUserId(), area.getId());
		}
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}

	// 保持区域
	@Transactional(readOnly = false)
	public void saveOrg(Area area) {

		super.save(area);
		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}

	@Transactional(readOnly = false)
	public int deviceByOrgCount(String id) {
		return areaDao.deviceByOrgCount(id);
	}

	@Transactional(readOnly = false)
	public void delete(Area area) {
		super.delete(area);

		areaDao.deletePdfOrg(area.getId());// 删除拓展配电房
		areaDao.deletePdfPrincipal(area.getId());// 删除负责人配电房

		UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
	}
	// ----------------------------------

	@Transactional(readOnly = false)
	public int saveImage(MapEntity entity) {
		return areaDao.saveImage(entity);
	}

	// 区域user下的集合
	public List<Area> userOrgList(String orgId) {

		return areaDao.userOrgList(orgId);
	}

	// 区域code获取
	public String selectCode(String orgId) {

		return areaDao.selectCode(orgId);
	}

	// 区域底下子集的最大code获取
	public String maxCode(String orgId) {
		return areaDao.maxCode(orgId);
	}
	// ------------------新寫

	// 所有行政區域
	public List<MapEntity> orgList(String orgId) {

		return areaDao.orgList(orgId);
	}

	// 用戶集合
	public List<MapEntity> userList() {

		return areaDao.userList();
	}

	public MapEntity getOrg(String orgId) {
		return areaDao.getOrg(orgId);
	}

	public Set<MapEntity> pdfList(String orgId) {
		Area area = new Area();
		area.setId(orgId);
		area.setUserId(UserUtils.getUser().getId());
		Set<MapEntity> list = areaDao.elceOrgList(area);
		return list;
	}

	// 获取修改的模糊查询区域===配电房查询
	public Set<MapEntity> elecOrgList(Area area) {
		if (area == null) {
			area = new Area();
		}
		String orgId = UserUtils.getUser().getArea().getId();
		System.out.println(UserUtils.getUser().getId());
		area.setUserId(UserUtils.getUser().getId());

		if (StringUtils.isBlank(area.getId())) {
			area.setId(orgId);
		}
		if (area.getOrgId() != null) {
			area.setId(area.getOrgId() + "");
		}
		Set<MapEntity> list = areaDao.elceOrgList(area);

		System.out.println(list.size() + "====");
		Set<MapEntity> set = new HashSet<MapEntity>();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			MapEntity entity = (MapEntity) it.next();
			String parentIds = (String) entity.get("parentIds");
			if (parentIds == "") {
				parentIds = null;
			}
			Set<MapEntity> set1 = areaDao.orgEditList(area.getId(), parentIds);			
			set.addAll(set1);
		}
		return set;
	}

	// -------------------行政部分
	public List<MapEntity> getorgList() {
		return areaDao.getorgList();
	}

	public int count(String orgId) {
		return areaDao.count(orgId);
	}

	public Set<MapEntity> orgEditList(String parentIds) {
		return areaDao.orgEditList(UserUtils.getUser().getArea().getId(), parentIds);
	}

	// 获取配电房集合
	public List<MapEntity> getOrgListById(String orgId) {
		return areaDao.getOrgListById(UserUtils.getUser().getId(), orgId);
	}
	// ---------------------------------

	public List<MapEntity> findImageList(String orgId) {
		return areaDao.findImageList(orgId);
	}

	public MapEntity getOnceImage(String orgId) {
		return areaDao.getOnceImage(orgId);
	}

	public List<MapEntity> getImage(String id) {
		return areaDao.getImage(id);
	}

	@Transactional(readOnly = false)
	public void deleteImage(String id) {
		areaDao.deleteImage(id);// 删除图片和小图标
	}

	@Transactional(readOnly = false)
	public void updateUrlImage(String picUrl, String imageName, String orgId) {
		areaDao.updateUrlImage(picUrl, imageName, orgId);
	}

	@Transactional(readOnly = false)
	public void updateImageName(String name, String imageId) {// 更新平面图名
		areaDao.updateImageName(name, imageId);
	}

	public List<MapEntity> test123(Area area){//测试新写配电房管理list
		area.setUserId(UserUtils.getUser().getId());
		return areaDao.test123(area);
		
	}
}
