package com.jeeplus.modules.starnet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.modules.starnet.dao.LoadUnitAdjustProportionDao;

@Service
public class LoadUnitAdjustProportionService {
	
	@Autowired
	LoadUnitAdjustProportionDao loadUnitAdjustProportionDao;
	
	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	public void loadAllLoopAdjustProportion() {
		redisTemplate.delete("AdjustLoopMap");
		
		List<MapEntity> list = loadUnitAdjustProportionDao.loadAllLoopAdjustProportion();
		Map<String,Object> map = new HashMap<String,Object>();
		for(MapEntity loop : list) {
			String loopId = loop.get("loop_org_id").toString();
			
			Object loopsObj = map.get(loopId);
			if(loopsObj != null) {
				JSONArray loopArray = JSONArray.parseArray(loopsObj.toString());
				loopArray.add(JSONObject.parse(JSONObject.toJSONString(loop)));
				map.put(loopId, JSONObject.toJSONString(loopArray));
			} else {
				JSONArray newList = new JSONArray();
				newList.add(JSONObject.parse(JSONObject.toJSONString(loop)));
				map.put(loopId, JSONObject.toJSONString(newList));
			}
			
		}
		redisTemplate.opsForHash().putAll("AdjustLoopMap", map);
	}
	
	public void loadAllAdjustStrategy() {
		redisTemplate.delete("AdjustMap");
		
		List<MapEntity> list = loadUnitAdjustProportionDao.loadAllUnitAdjustStrategy();
		Map<String,Object> map = new HashMap<String,Object>();
		for(MapEntity unit : list) {
			String month = unit.get("month").toString();
			
			Object unitObj = map.get(month);
			if(unitObj != null) {
				JSONArray unitArray = JSONArray.parseArray(unitObj.toString());
				unitArray.add(JSONObject.parse(JSONObject.toJSONString(unit)));
				map.put(month, JSONObject.toJSONString(unitArray));
			} else {
				JSONArray unitArray = new JSONArray();
				unitArray.add(JSONObject.parse(JSONObject.toJSONString(unit)));
				map.put(month, JSONObject.toJSONString(unitArray));
			}
			
		}

		redisTemplate.opsForHash().putAll("AdjustMap", map);
	}

	public MapEntity getUnitLoopOrg(String unitId, String orgId) {
		return loadUnitAdjustProportionDao.getUnitLoopOrg(unitId,orgId);
	}
	
}
