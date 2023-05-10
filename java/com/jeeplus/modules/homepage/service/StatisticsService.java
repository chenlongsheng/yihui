package com.jeeplus.modules.homepage.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.StatisticsDao;
import com.jeeplus.modules.homepage.entity.*;
import com.jeeplus.modules.settings.dao.TOrgDao;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.utils.UserUtils;


import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2018-12-20.
 */
@Service
@Transactional(readOnly = true)
public class StatisticsService extends CrudService<StatisticsDao, Statistics> {
	@Autowired
	StatisticsDao statisticsDao;
	@Autowired
	TOrgDao tOrgDao;
	@Autowired
	private RedisTemplate redisTemplate;

	public Page<MapEntity> findHomePage(Page<MapEntity> page, MapEntity entity) {

		entity.setPage(page);

//		List<MapEntity> orgListByBureauId = statisticsDao.getOrgListByBureauId(entity);

		page.setList(statisticsDao.getOrgListByBureauId(entity));
		return page;

	}

	public List<MapEntity> orgList(String orgId) {
		return statisticsDao.orgList(orgId);
	}

	public List<MapEntity> getOrgList() {

		return statisticsDao.getOrgList();

	}

	public Statistics findDevTypeUse(Statistics statistics) {

		statistics.setUserId(UserUtils.getUser().getId());

		Statistics st = new Statistics();
		if (statistics.getDevType().equals("220")) {
			statistics.setTypeId("4");
			st = statisticsDao.findDevTypeUse2(statistics);
		} else if (statistics.getDevType().equals("1")) {
			statistics.setTypeId("9");
			statistics.setDevType("2,3");
			st = statisticsDao.findDevTypeUse2(statistics);
		} else {
			st = statisticsDao.findDevTypeUse1(statistics);
		}
		return st;
	}

	public List<MapEntity> findDevTypeUse9(String orgId) {// 999新写的.替换上面
		return statisticsDao.findDevTypeUse9(orgId);
	}

	public List<JSONObject> findAlarmType(String orgId) {
		JSONObject object = statisticsDao.findAlarmType(orgId, UserUtils.getUser().getId());
		JSONObject data = null;
		Integer door = object.getInteger("door");
		Integer fan = object.getInteger("dianbiao");
		Integer wather = object.getInteger("wather");
		Integer smoke = object.getInteger("smoke");
		Integer temperature = object.getInteger("temperature");
		Integer camera = object.getInteger("dianliu");
		List<JSONObject> list = new ArrayList<JSONObject>();

		data = new JSONObject();
		data.put("name", "门磁");
		if (door > 0) {
			data.put("value", door);
		} else {
			data.put("value", 0);
		}
		list.add(data);

		data = new JSONObject();
		data.put("name", "智能电测表");
		if (fan > 0) {
			data.put("value", fan);
		} else {
			data.put("value", 0);
		}
		list.add(data);

		data = new JSONObject();
		data.put("name", "水浸");
		if (wather > 0) {
			data.put("value", wather);
		} else {
			data.put("value", 0);
		}
		list.add(data);

		data = new JSONObject();
		data.put("name", "烟感");
		if (smoke > 0) {
			data.put("value", smoke);
		} else {
			data.put("value", 0);
		}
		list.add(data);

		data = new JSONObject();
		data.put("name", "温湿度");
		if (temperature > 0) {
			data.put("value", temperature);
		} else {
			data.put("value", 0);
		}
		list.add(data);

		data = new JSONObject();
		data.put("name", "电流微机保护器");
		if (camera > 0) {
			data.put("value", camera);
		} else {
			data.put("value", 0);
		}
		list.add(data);
		return list;
	}

	public Date findLastAlarm() {
	    
		return statisticsDao.findLastAlarm(null, UserUtils.getUser().getId());
	}

	public OrgPDFInfo findLastAlarm(String orgId) {
		// 配电房总数
		String PDFtotal = statisticsDao.countPDF(orgId, "5", UserUtils.getUser().getId());
		// 配电房报警总数
		String alarm = statisticsDao.countAlarmPDF(orgId, "5", UserUtils.getUser().getId());
		// 计算安全总天数
		Date date = statisticsDao.findLastAlarm(orgId, UserUtils.getUser().getId());

		if (date == null) {
			date = new Date();
		}
		Long t = new Date().getTime() - date.getTime();
		long days = t / (1000 * 60 * 60 * 24);
		OrgPDFInfo orgPDFInfo = new OrgPDFInfo();
		orgPDFInfo.setAlarmPDFTotal(alarm);
		orgPDFInfo.setPDFTotal(PDFtotal);
		orgPDFInfo.setSecurityDay(String.valueOf(days));
		return orgPDFInfo;
	}

	public JSONObject findHumitureRanking(Statistics statistics) {
		statistics.setUserId(UserUtils.getUser().getId());
		statistics.setDevType("169");
		statistics.setTypeId("1");
		statistics.setChType("101");
		JSONObject json = new JSONObject();
		List<JSONObject> object = statisticsDao.findHumitureRanking(statistics);
		statistics.setChType("102");
		List<JSONObject> objects = statisticsDao.findHumitureRanking(statistics);
		json.put("weather", object);
		json.put("humidity", objects);
		return json;
	}

	public Statistics findAccessOpen(Statistics statistics) {

		statistics.setUserId(UserUtils.getUser().getId());
		return statisticsDao.findAccessOpen(statistics);
	}

	public Page<MapEntity> findPage(Page<MapEntity> page, MapEntity entity) {
		entity.setPage(page);
		List<MapEntity> leveDevice = statisticsDao.doorList(entity);
		for (MapEntity en : leveDevice) {
			Date date = (Date) en.get("updateDate");
			if (date != null) {
				long nd = 1000 * 24 * 60 * 60;
				long nh = 1000 * 60 * 60;
				long nm = 1000 * 60;
				// 获得两个时间的毫秒时间差异
				long diff = new Date().getTime() - date.getTime();
				// 计算差多少天
				long day = diff / nd;
				// 计算差多少小时
				long hour = diff % nd / nh;
				// 计算差多少分钟
				long min = diff % nd % nh / nm;
				// 计算差多少秒//输出结果
				String days = day + "天" + hour + "小时" + min + "分钟";
				en.put("onTime", days);
			}
		}
		page.setList(leveDevice);
		return page;
	}

	public List<StatisData> findAlarmTrend(String orgId, String time, int length) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -length);
		Date beginDate = calendar.getTime();
		// String d = sdf.format(calendar.getTime());
		List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 0; i < length; i++) {
			calendar.setTime(beginDate);
			calendar.add(Calendar.DAY_OF_MONTH, i);
			System.out.println(calendar.getTime());
			JSONObject json = statisticsDao.findAlarmTrend(orgId, calendar.getTime(), date,
					UserUtils.getUser().getId());
			list.add(json);
		}

		StatisData shuijin = new StatisData("水浸");
		StatisData fengji = new StatisData("智能电测表");
		StatisData yangan = new StatisData("烟感");
		StatisData menjin = new StatisData("门磁");
		StatisData wendu = new StatisData("温湿度");
		StatisData camera = new StatisData("电流微机保护器");
		List<StatisData> d = new ArrayList<StatisData>();

		for (JSONObject object : list) {
			String t = object.getString("date");
			String shui = object.getString("shuijin");
			String feng = object.getString("dianbiao");
			String yan = object.getString("yangan");
			String men = object.getString("menjin");
			String wen = object.getString("wendu");
			String cam = object.getString("dianliu");
			JSONObject o = new JSONObject();
			o.put("date", t);
			o.put("value", shui);
			shuijin.dataAdd(o);
			o = new JSONObject();
			o.put("date", t);
			o.put("value", feng);
			fengji.dataAdd(o);
			o = new JSONObject();
			o.put("date", t);
			o.put("value", yan);
			yangan.dataAdd(o);
			o = new JSONObject();
			o.put("date", t);
			o.put("value", men);
			menjin.dataAdd(o);
			o = new JSONObject();
			o.put("date", t);
			o.put("value", wen);
			wendu.dataAdd(o);
			o = new JSONObject();
			o.put("date", t);
			o.put("value", cam);
			camera.dataAdd(o);
		}

		d.add(menjin);
		d.add(fengji);
		d.add(shuijin);
		d.add(yangan);
		d.add(wendu);
		d.add(camera);
		return d;
	}

	public List<MapEntity> sandTable1(String orgId) {

		List<MapEntity> list = statisticsDao.sandTable1(orgId, UserUtils.getUser().getId());
		return list;
	}

	public List<MapEntity> sandTable(String orgId) {
		List<MapEntity> warnList = new ArrayList<MapEntity>();
		List<MapEntity> list = statisticsDao.sandTable1(orgId, UserUtils.getUser().getId());
		Iterator it = list.iterator();
		while (it.hasNext()) {
			MapEntity entity = (MapEntity) it.next();
			String allWarn = (String) entity.get("allVarn");
			if (allWarn == null || allWarn.equals("0")) {
				it.remove();
			}
		}
		return list;
	}

	public List<PDFOrg> sandTable3(String orgId) {

		List<SandTable> list = statisticsDao.sandTable(orgId);
		List<String> org = new ArrayList<String>();
		Map<String, SandTable> map = new HashMap<String, SandTable>();
		for (SandTable sandTable : list) {
			if (org.indexOf(sandTable.getId()) > -1) {

			} else {
				org.add(sandTable.getId());
				map.put(sandTable.getId(), sandTable);
			}
			SandTable table = map.get(sandTable.getId());
			// 判断报警类型
			if (table.getOccurTime() != null) {
				String devType = table.getDevType();
				if (devType.equals("4") || devType.equals("159") || devType.equals("401") || devType.equals("1004")) {
					table.setFireControl(true);
				} else if (devType.equals("100") || devType.equals("105") || devType.equals("106")) {
					table.setSecurity(true);
				} else if (devType.equals("154") || devType.equals("157") || devType.equals("202")
						|| devType.equals("203")) {
					table.setHyperthermia(true);
				}
			}
			if (table.getProcTime() != null) {
				sandTable.setFault(true);
			}
			map.put(sandTable.getId(), table);
		}
		TOrg o = tOrgDao.get(orgId);
		if (o.getType() == 2) {
		}
		List<PDFOrg> data = new ArrayList<PDFOrg>();
		PDFOrg s = new PDFOrg();
		String name = "";
		for (String i : map.keySet()) {
			SandTable t = map.get(i);
			if (!name.equals(t.getParentName()) && name != "") {
				data.add(s);
				name = t.getParentName();
				s = new PDFOrg();
				s.setId(t.getParentId());
				s.setName(t.getParentName());
			}
			s.listAdd(t);
		}
		data.add(s);
		return data;
	}

	public MapEntity getBureauList() {

		MapEntity mapEntity = new MapEntity();
		List<MapEntity> list = new ArrayList<MapEntity>();
		Set<MapEntity> bureauList = new HashSet<MapEntity>();

		List<MapEntity> getBureauIds = statisticsDao.getBureauIds(UserUtils.getUser().getId());// 获取所有用户的供电所
		for (MapEntity entity : getBureauIds) {
			String pIds = (String) entity.get("pIds");
			List<MapEntity> orgListByPId = statisticsDao.getOrgListByPId(pIds);
			bureauList.addAll(orgListByPId);
		}

		for (MapEntity entity : getBureauIds) { // 添加供电所下所有配电房集合
			String bureauId = entity.get("id").toString();
			mapEntity.put("bureauId", bureauId);
			mapEntity.put("userId", UserUtils.getUser().getId());
			List<MapEntity> orgListByBureauId = statisticsDao.getOrgListByBureauId(mapEntity);
			System.out.println(orgListByBureauId);
			if (orgListByBureauId != null && orgListByBureauId.size() > 0) {
				list.addAll(orgListByBureauId);
			}
		}
		mapEntity.put("bureauList", bureauList);
		mapEntity.put("pdfList", list);
		return mapEntity;
	}

	public List<MapEntity> getBureauListByHome() {
		return statisticsDao.getBureauListByHome(UserUtils.getUser().getId());
	}

}
