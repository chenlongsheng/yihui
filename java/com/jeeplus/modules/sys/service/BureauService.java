/**
 * 
 */
package com.jeeplus.modules.sys.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.TreeService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.StatisticsDao;
import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.sys.dao.AreaDao;
import com.jeeplus.modules.sys.dao.BureauDao;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * @author admin
 *
 */
@Service
@Transactional(readOnly = true)
public class BureauService extends TreeService<BureauDao, Area> {

	@Autowired
	BureauDao bureauDao;

	StatisticsDao statisticsDao;

	public MapEntity getBureauList() {

		MapEntity mapEntity = new MapEntity();
		Set<MapEntity> bureauList = new HashSet<MapEntity>();

		List<MapEntity> getBureauIds = bureauDao.getBureauIds(UserUtils.getUser().getId());// 获取所有用户的供电所
		if (UserUtils.getUser().getId().equals("1")) {
			mapEntity.put("bureauList", getBureauIds);
			mapEntity.put("bureaus", getBureauIds);
			return mapEntity;
		}
		for (MapEntity entity : getBureauIds) {// 补全非admin下所有的供电单位树形结构
			String pIds = (String) entity.get("pIds");
			System.out.println(pIds + "=================");
			List<MapEntity> orgListByPId = bureauDao.getOrgListByPId(pIds);
			bureauList.addAll(orgListByPId);
		}
		mapEntity.put("bureauList", bureauList);
		mapEntity.put("bureaus", getBureauIds);

		return mapEntity;
	}

	@Transactional(readOnly = false)
	public void deleteBureauById(String bureauId) {
		bureauDao.deleteBureauById(bureauId);
	}

	@Transactional(readOnly = false)
	public void saveBureauById(String bureauId, String oldParentId, String parentId, String name, String code) {

		if (StringUtils.isBlank(bureauId)) {// 添加
			MapEntity entity = new MapEntity();
			entity.put("parentId", parentId);
			entity.put("name", name);
			entity.put("code", code);
			bureauDao.insertBureau(entity);
			bureauDao.inserUserBureau(UserUtils.getUser().getId(), (Long) entity.get("id"));

		} else {
			if (parentId.equals(oldParentId)) {// 编辑时候父ID没有改变
				bureauDao.updateBureauById(bureauId, null, null, null, name);
			} else {
				List<MapEntity> bureauList = bureauDao.getBureauList(bureauId);// 获取所有的供电所和子供电所
				for (MapEntity entity : bureauList) {
					String id = (String) entity.get("id");
					String oldPId = (String) entity.get("parentId");
					String parentIds = (String) entity.get("parentIds");
					bureauDao.updateBureauById(id, oldParentId, parentId, parentIds, name);// 修改parent_ids和name
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void updateOrderNo(JSONArray ja) {// 修改同级排序
		for (int i = 0; i < ja.size(); i++) {
			MapEntity mapEntity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
			Integer orderNo = (Integer) mapEntity.get("orderNo");
			String bureauId = (String) mapEntity.get("bureauId");
			bureauDao.updateOrderNo(orderNo, bureauId);
		}
	}

	public List<MapEntity> getBureauListByUserId() {

		return bureauDao.getBureauListByUserId(UserUtils.getUser().getId());

	}

}
