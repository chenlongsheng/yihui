package com.jeeplus.modules.homepage.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.ChartDao;
import com.jeeplus.modules.homepage.dao.OverviewDao;
import com.jeeplus.modules.homepage.dao.StatisticsDao;
import com.jeeplus.modules.homepage.entity.*;
import com.jeeplus.modules.settings.dao.TOrgDao;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.sys.utils.UserUtils;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Created by Administrator on 2018-12-20.
 */
@Service
@Transactional(readOnly = true)
public class OverviewService extends CrudService<OverviewDao, Statistics> {
	@Autowired
	OverviewDao overviewDao;

	@Autowired
	RedisTemplate<String, String> redisTempldate;

	public MapEntity overviewDate() {

		String userId = UserUtils.getUser().getId();
		MapEntity entity = new MapEntity();
		entity.put("codeList", overviewDao.codeList());// 设备类型集合
		entity.put("pdfListById", overviewDao.pdfListById(userId)); // 用户归属配电房集合
		entity.put("devCountTop5", overviewDao.devCountTop5(userId)); // 设备数量top5
		entity.put("statisticsCount", overviewDao.statisticsCount(userId)); // 配电房统计

		return entity;
	}

	public List<MapEntity> getOrgDetails() {

		return overviewDao.getOrgDetails();

	}

	public MapEntity pdfListById() {

		String userId = UserUtils.getUser().getId();
		MapEntity entity = new MapEntity();
		entity.put("pdfListById", overviewDao.pdfListById(userId)); // 用户归属配电房集合

//		List<MapEntity> pdfListById = overviewDao.pdfListById(userId);
//		for (MapEntity en : pdfListById) {
//			Object objRealData = redisTemplate.opsForValue().get("cdpdf_devdataproc_realdata_" + "6651749099468251136");
//			String status = "0";
//			String allwarn = "0";
//			if (objRealData != null) {
//				JSONObject jobj_real_data = JSONObject.parseObject(objRealData.toString());
//				String real_value = jobj_real_data.getString("real_value");
//				Long l_real_value = Long.parseLong(real_value);
//				status = l_real_value + "";
//			}
//			en.put("status", status);
//			
//           //获取所有通道报警
//			List<MapEntity> chIds = overviewDao.getChIds((String) en.get("id"));//获取所的配电房下chId
//			for (MapEntity mapEntity : chIds) {
//				Object objchData = redisTemplate.opsForValue()
//						.get("cdpdf_devdataproc_realdata_" + mapEntity.get("chId"));
//				if (objRealData != null) {
//					JSONObject jobj_real_data = JSONObject.parseObject(objRealData.toString());
//					String warn = jobj_real_data.getString("warn");
//					if (!warn.equals("0")) {
//						allwarn = warn;
//					}
//				}
//			}
//			en.put("warn", allwarn);
//		}
		return entity;
	}

	public List<MapEntity> alarmCountTop10(String beginDate, String endDate) {

		return overviewDao.alarmCountTop10(UserUtils.getUser().getId(), beginDate, endDate);// 设备告警top10
	}

	public List<MapEntity> getDetailsByOrgId(String orgId) {

		return overviewDao.getDetailsByOrgId(orgId);
	}

	public List<MapEntity> getDataByOrgId(String orgId) {

		List<MapEntity> list = new ArrayList<MapEntity>();
		MapEntity entity = new MapEntity();
		List<MapEntity> orgs = overviewDao.getOrgs();
		List<String> getPdfs = overviewDao.getPdfs();

		Double sum = 0.00;
		Iterator<String> iterator = getPdfs.iterator();
		String orgName = "";
		String pdfId = "";
		String loadDayDate = "";
		String logicOrgId = "";
		while (iterator.hasNext()) {
			entity = new MapEntity();
			orgName = "";
			pdfId = "";
			logicOrgId = "";
			sum = 0.00;
			String str = iterator.next();
			for (int j = 0; j < orgs.size(); j++) {
				pdfId = orgs.get(j).get("pdfId").toString();
				orgName = orgs.get(j).get("orgName").toString();
				logicOrgId = orgs.get(j).get("orgId").toString();

				loadDayDate = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
				System.out.println("当前时间:" + loadDayDate);
				if (str.equals(pdfId)) {
					System.out.println("回路id==" + logicOrgId);
					Object object = redisTempldate.opsForHash().get("LoopDataDay:" + loadDayDate, logicOrgId);

//                  redisTempldate.opsForHash().get("LoopDataDay:" + "2022-06-28", logicOrgId);

					System.out.println("object===:" + object);
					if (object == null || Double.parseDouble((String) object) <= 0) {
						return overviewDao.getEnergyList("");
					} else {

						System.out.println("====进入" + object.toString());
						sum += Double.parseDouble((String) object);
					}

					entity.put("orgName", orgName);
					entity.put("orgId", pdfId);
				}
			}
			entity.put("totalPrice", sum);
			entity.put("allValue", sum);
			list.add(entity);
		}
		return list;
	}

	// 全年按月改造
	public List<MapEntity> getEnergyList(String orgId) {

		List<MapEntity> list = new ArrayList<MapEntity>();

		MapEntity entity = new MapEntity();

		List<String> getchorglist = overviewDao.getchorglist();

		DateTimeFormatter formatteryy = DateTimeFormatter.ofPattern("yyyy");
		DateTimeFormatter formattermm = DateTimeFormatter.ofPattern("MM");

		Date date = new Date();
		DateFormatUtils.format(new Date(), "yyyy");
		DateFormatUtils.format(new Date(), "MM");

		System.out.println(DateFormatUtils.format(new Date(), "yyyy"));

		int monthtime = Integer.parseInt(DateFormatUtils.format(new Date(), "MM"));

		String loadMonthDate = "";
		Double sum = 0.00;
		for (int i = 1; i <= monthtime; i++) {
			sum = 0.00;
			entity = new MapEntity();

			loadMonthDate = DateFormatUtils.format(new Date(), "yyyy") + "-" + String.format("%02d", i);
			System.out.println(loadMonthDate);
			for (String org : getchorglist) {

				Object object = redisTempldate.opsForHash().get("LoopDataMonth:" + loadMonthDate, org);

				if (object != null && Double.parseDouble((String) object) > 0) {

					System.out.println(object.toString());
					sum += Double.parseDouble((String) object);
				}

			}
			System.out.println("sum====" + sum);
			if (sum != 0) {
				entity.put("allValue", (double) Math.round(sum * 100) / 100);
				entity.put("month", i);
				entity.put("totalPrice", (double) Math.round(sum * 100) / 100);
				list.add(entity);
			}

		}
		return list;

	}

}
