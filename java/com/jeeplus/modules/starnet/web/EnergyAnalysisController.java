package com.jeeplus.modules.starnet.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.iterators.EntrySetMapIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.starnet.service.ElectricityUnitService;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.starnet.service.EnergyOpenApiService;
import com.jeeplus.modules.starnet.service.PowerAnalysisService;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
//能效数据
@Controller
@RequestMapping(value = "star/tDeviceVoltage")
public class EnergyAnalysisController extends BaseController {
	
	public static Logger logger = LoggerFactory.getLogger(EnergyAnalysisController.class);
	
    @Autowired
    EnergyAnalysisService energyAnalysisService;
    
	@Autowired
	TOrgService tOrgService;
	
	@Autowired
	EnergyOpenApiService energyOpenApiService;  
	
	@Autowired
	PowerAnalysisService powerAnalysisService;
	
    @Autowired
    ElectricityUnitService electricityUnitService;
	
	@Autowired
	RedisTemplate<String, String> redisTempldate;
    
    //能效数据 -> 企业看板历史数据
    @RequestMapping(value = { "historyListByHour" }) 
    @ResponseBody
    public String historyListByHour(String loopId, String unitId,final String time, String type, Integer state) {
    	List<Map<String,Object>> historyDataList = new ArrayList<>();
    	if(loopId != null && !loopId.equals("")) {
    		
    		List< List<Map<String,Object>> > loopDataListList = new ArrayList<>();
    		
    		String loopIds[] = loopId.split(",");
    		for(String loop: loopIds) {
    			if(!loop.equals("")) {
    				logger.debug("11 getHistoryListByTypeAndTime point:"+loop);
    				loopDataListList.add(energyAnalysisService.getHistoryListByTypeAndTime(time,type,loop));	
    			}
    		}
    		
    		//归并回路用电量
    		Map<String,Object> loopDataListSum = new HashMap<>();
    		for(List<Map<String,Object>> tempLoopDataList : loopDataListList) {
    			for(Map<String,Object> loopData : tempLoopDataList) {
    				String showTime1 = loopData.get("showTime").toString();
    				Double total1 = Double.parseDouble(loopData.get("total").toString());
    				Object valueObj = loopDataListSum.get(showTime1);
    				if(valueObj != null) {
    					double total2 = Double.parseDouble(valueObj.toString());
    					loopDataListSum.put(showTime1, total1 + total2);
    				} else {
    					loopDataListSum.put(showTime1, total1);
    				}
    			}
    		}
    		
			for (Map.Entry<String, Object> pair: loopDataListSum.entrySet()) {
				Map<String,Object> map = new HashMap<>();
	 			map.put("showTime", pair.getKey());
	 			map.put("total", pair.getValue());
	            historyDataList.add(map);
		    }
    		

    	} else if(unitId != null && !unitId.equals("")) {
    		//根据用电单位查询
    		//historyDataList = energyAnalysisService.historyListByUnitId(unitId,type,time);
    		
    		/*
    		List<List<Map<String,Object>>> dataList = new ArrayList<>();
    		List<MapEntity> loopList = electricityUnitService.getUnitLoopList(unitId);
    		for(MapEntity loopMap : loopList) {
    			String loop = loopMap.get("loopOrgId").toString();
    			dataList.add(getHistoryListByTypeAndTime(time,type,loop));
    		}
    		 */
			/*
				select concat(e.id) unitId, sl.id,o.name orgName,concat(o.id)
				loopOrgId,e.name,sl.not_deduction
				notDeduction,sl.not_public
				notPublic,proportion,son_loop_ids sonLoopIds,
				concat(sl.type) type,
				case when sl.type = 1 then loop_area
				when sl.type= 2
				then loop_number
				when sl.type = 3 then proportion else '' end loopValue,
				case when sl.type = 3 then
				(
				select ROUND(1 -(SUM(proportion)),2) from star_unit_loop_org where loop_org_id = o.id) else '1'
				end lev
				
				减完子回路, 在乘以百分比
			 */
    		List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitId);
			List<List<Map<String,Object>>> loopDataListList = new ArrayList<>(); 
    		for(final MapEntity unitMap : unitMapList) {
    			String unitLoopId = unitMap.get("loopOrgId").toString();
    			
    			logger.debug("analysisReport");
    			logger.debug("unitLoopId:"+unitLoopId);
    			
    			String notDeduction = "0";
    			Object notDeductionObj = unitMap.get("notDeduction");
    			if(notDeductionObj != null) {
    				notDeduction = notDeductionObj.toString();
    			}
    			String proportion = "0.0";
    			Object proportionObj = unitMap.get("proportion");
    			if(proportionObj != null) {
    				proportion = proportionObj.toString();
    			}
    			
    			
    			logger.debug("12 getHistoryListByTypeAndTime point:"+unitLoopId);
    			List<Map<String,Object>> loopDataList = energyAnalysisService.getHistoryListByTypeAndTime(time,type,unitLoopId);
    			
        		List<List<Map<String,Object>>> subLoopDataList = new ArrayList<>();
    			String sonLoopIds = "";
    			if(unitMap.get("sonLoopIds") != null) {
    				sonLoopIds = unitMap.get("sonLoopIds").toString();
    			}
    			
    			logger.debug("sonLoopIds:"+sonLoopIds);
    			
    			String loopArr[] = sonLoopIds.split(",");
        		for(String loop : loopArr) {
        			if(!loop.equals("")) {
        				logger.debug("13 getHistoryListByTypeAndTime point:"+loop);
        				subLoopDataList.add(energyAnalysisService.getHistoryListByTypeAndTime(time,type,loop));
        			}
        		}
        		
        		logger.debug("subLoopdataList:"+subLoopDataList);
        		
	    		//归并计算,子回路的使用电量
	    		Map<String,Object> subLoopDataListSum = new HashMap<>();
	    		for(List<Map<String,Object>> tempLoopDataList : subLoopDataList) {
	    			for(Map<String,Object> loopData : tempLoopDataList) {
	    				String showTime1 = loopData.get("showTime").toString();
	    				Double total1 = Double.parseDouble(loopData.get("total").toString());
	    				Object valueObj = subLoopDataListSum.get(showTime1);
	    				if(valueObj != null) {
	    					double total2 = Double.parseDouble(valueObj.toString());
	    					subLoopDataListSum.put(showTime1, total1 + total2);
	    				} else {
	    					subLoopDataListSum.put(showTime1, total1);
	    				}
	    			}
	    		}
	    		
	    		logger.debug("subLoopDataListSum:"+subLoopDataListSum);
	    		
	    		if(notDeduction.equals("1")) {
		    		//减去子回路用电,算出 用电单元的公共用电
					for(int k=0;k<loopDataList.size();k++) {
						Map<String,Object> loopData = loopDataList.get(k);
						logger.debug("loopData:"+loopData);
						String showTime1 = loopData.get("showTime").toString();
						Double total1 = Double.parseDouble(loopData.get("total").toString());
						Object valueObj = subLoopDataListSum.get(showTime1);
	    				if(valueObj != null) {
	    					double total2 = Double.parseDouble(valueObj.toString());
	    					logger.debug("valueObj:"+total2);
	    					loopData.put("total", total1 - total2 );
	    					logger.debug("total:"+(total1 - total2));
	    				}
					}
	    		}

				for(int k=0;k<loopDataList.size();k++) {
					Map<String,Object> loopData = loopDataList.get(k);
					Double total1 = Double.parseDouble(loopData.get("total").toString());
					loopData.put("total", total1 * Double.parseDouble(proportion) );
				}
				
				loopDataListList.add(loopDataList);
    			
    		}
    		
    		//归并回路用电量
    		Map<String,Object> loopDataListSum = new HashMap<>();
    		for(List<Map<String,Object>> tempLoopDataList : loopDataListList) {
    			for(Map<String,Object> loopData : tempLoopDataList) {
    				String showTime1 = loopData.get("showTime").toString();
    				Double total1 = Double.parseDouble(loopData.get("total").toString());
    				Object valueObj = loopDataListSum.get(showTime1);
    				if(valueObj != null) {
    					double total2 = Double.parseDouble(valueObj.toString());
    					loopDataListSum.put(showTime1, total1 + total2);
    				} else {
    					loopDataListSum.put(showTime1, total1);
    				}
    			}
    		}
    		
			for (Map.Entry<String, Object> pair: loopDataListSum.entrySet()) {
				Map<String,Object> map = new HashMap<>();
	 			map.put("showTime", pair.getKey());
	 			map.put("total", pair.getValue());
	            historyDataList.add(map);
		    }
    	}
    	return ServletUtils.buildRs(true, "能耗历史数据", historyDataList);
    }
    
    /**
     * 
     * @param loopId
     * @param unitId
     * @param state 0.能耗,1.成本
     */
    //能效数据 -> 企业看板今天今月今年, 昨天上月去年
    @RequestMapping(value = { "historyTrendByTime" })
    @ResponseBody
    public String historyTrendByTime(String loopId, String unitId, Integer state) throws Exception {
    	MapEntity resultData = new MapEntity();
    	
    	MapEntity result1 = new MapEntity();
    	MapEntity result2 = new MapEntity();
    	
    	if(loopId != null && !loopId.equals("")) {
    		
    		List<MapEntity> resultList = new ArrayList<>(); 
    		String loopIds[] = loopId.split(",");
    		for(String loop: loopIds) {
    			if(!loop.equals("")) {
    				resultList.add(energyAnalysisService.dayMonthYearByLoopId(loop));
    			}
    		}
    		
    		Double dayMap = 0.0;
    		Double monthMap = 0.0;
    		Double yearMap = 0.0;
    		for(MapEntity result : resultList) {
    			dayMap += Double.parseDouble(result.get("dayMap").toString());
    			monthMap += Double.parseDouble(result.get("monthMap").toString());
    			yearMap += Double.parseDouble(result.get("yearMap").toString());
    		}
    		
    		result1.put("dayMap", dayMap);
    		result1.put("monthMap", monthMap);
    		result1.put("yearMap", yearMap);
    		
    		if(state == 1) {
    			//计算 金额
    		}
    	} else if(unitId != null && !unitId.equals("")) {
    		result1 = energyAnalysisService.dayMonthYearByUnitId(unitId);
    		if(state == 1) {
    			//计算 金额
    		}
    	}
    	
    	if(loopId != null && !loopId.equals("")) {
    		
    		List<MapEntity> resultList = new ArrayList<>(); 
    		String loopIds[] = loopId.split(",");
    		for(String loop: loopIds) {
    			if(!loop.equals("")) {
    				resultList.add(energyAnalysisService.preDayMonthYearByLoopId(loop));	
    			}
    		}
    		
    		Double dayMap = 0.0;
    		Double monthMap = 0.0;
    		Double yearMap = 0.0;
    		for(MapEntity result : resultList) {
    			dayMap += Double.parseDouble(result.get("dayMap").toString());
    			monthMap += Double.parseDouble(result.get("monthMap").toString());
    			yearMap += Double.parseDouble(result.get("yearMap").toString());
    		}
    		
    		result2.put("dayMap", dayMap);
    		result2.put("monthMap", monthMap);
    		result2.put("yearMap", yearMap);
    		
    		if(state == 1) {
    			//计算 金额
    		}
    	} else if(unitId != null && !unitId.equals("")) {
    		result2 = energyAnalysisService.preDayMonthYearByUnitId(unitId);
    		if(state == 1) {
    			//计算 金额
    		}
    	}
    	
    	resultData.put("result1", result1);
    	resultData.put("result2", result2);
    	return ServletUtils.buildRs(true, "能源 今天今月今年", resultData);// 能源
    }
    
    //能效数据 -> 对比分析
    @RequestMapping(value = { "historyPicsByHour" }) 
    @ResponseBody
    public String historyPicsByHour(String loopId, String unitId,  String time, String type) throws Exception {
    	List<MapEntity> resultData = new ArrayList<MapEntity>();
    	if(loopId != null && !loopId.equals("")) {
    		String loopIds[] = loopId.split(",");
    		for(String loop : loopIds) {
    			if(!loop.equals("")) {
        			TOrg org = tOrgService.get(loop);
        			MapEntity data = new MapEntity();
        			//data.put(org.getId(), energyAnalysisService.historyPicsByLoopId(loop, time, type));
        			logger.debug("14 getHistoryListByTypeAndTime point:"+loop);
        			data.put(org.getId(), energyAnalysisService.getHistoryListByTypeAndTime(time,type,loop));
        			resultData.add(data);
    			}
    		}
    		
    	} else if(unitId != null && !unitId.equals("")) {
    		String unitIds[] = unitId.split(",");
    		Arrays.sort(unitIds,Collections.reverseOrder());
    		
    		for(String unitid : unitIds) {
    			MapEntity unitMap = powerAnalysisService.getStarElectricityUnit(unitid);
    			MapEntity data = new MapEntity();
    			//data.put(unitMap.get("name").toString(), energyAnalysisService.historyPicsByUnitId(unitid, time, type));
    			List<Map<String,Object>> historyDataList = new ArrayList<>();
				List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitid);
				
				List<List<Map<String,Object>>> loopDataListList = new ArrayList<>(); 
	
	    		for(final MapEntity unitItem : unitMapList) {
	
	    			String unitLoopId = unitItem.get("loopOrgId").toString();
	    			String notDeduction = "0"; 
	    			Object notDeductionObj = unitMap.get("notDeduction");
	    			if(notDeductionObj != null) {
	    				notDeduction = notDeductionObj.toString();
	    			}
	    			String proportion = "0.0";
	    			Object proportionObj = unitItem.get("proportion");
	    			if(proportionObj != null) {
	    				proportion = proportionObj.toString();
	    			}
	    			logger.debug("15 getHistoryListByTypeAndTime point:"+unitLoopId);
	    			List<Map<String,Object>> loopDataList = energyAnalysisService.getHistoryListByTypeAndTime(time,type,unitLoopId);
	    			
	    			
	        		List<List<Map<String,Object>>> subLoopdataList = new ArrayList<>();
	    			String sonLoopIds = "";
	    			if(unitItem.get("sonLoopIds") != null) {
	    				sonLoopIds = unitItem.get("sonLoopIds").toString();
	    			}
	        		String loopArr[] = sonLoopIds.split(",");
	        		for(final String loop : loopArr) {
	        			if(!loop.equals("")) {
	        				logger.debug("16 getHistoryListByTypeAndTime point:"+loop);
	        				subLoopdataList.add(energyAnalysisService.getHistoryListByTypeAndTime(time,type,loop));	
	        			}
	        		}
	
	    		
		    		//归并计算,子回路的使用电量
		    		Map<String,Object> subLoopDataListSum = new HashMap<>();
		    		for(List<Map<String,Object>> tempLoopDataList : subLoopdataList) {
		    			for(Map<String,Object> loopData : tempLoopDataList) {
		    				String showTime1 = loopData.get("showTime").toString();
		    				Double total1 = Double.parseDouble(loopData.get("total").toString());
		    				Object valueObj = subLoopDataListSum.get(showTime1);
		    				if(valueObj != null) {
		    					double total2 = Double.parseDouble(valueObj.toString());
		    					subLoopDataListSum.put(showTime1, total1 + total2);
		    				} else {
		    					subLoopDataListSum.put(showTime1, total1);
		    				}
		    			}
		    		}
					
		    		if(notDeduction.equals("1")) {
			    		//减去子回路用电,算出 用电单元的公共用电
						for(int k=0;k<loopDataList.size();k++) {
							Map<String,Object> loopData = loopDataList.get(k);
							Double total1 = Double.parseDouble(loopData.get("total").toString());
							Object valueObj = subLoopDataListSum.get("showTime");
		    				if(valueObj != null) {
		    					double total2 = Double.parseDouble(valueObj.toString());
		    					loopData.put("total", total1 - total2 );
		    				}
						}
		    		}

					for(int k=0;k<loopDataList.size();k++) {
						Map<String,Object> loopData = loopDataList.get(k);
						Double total1 = Double.parseDouble(loopData.get("total").toString());
						loopData.put("total", total1 * Double.parseDouble(proportion) );
					}
					
					loopDataListList.add(loopDataList);
	    		}
	    		
	    		//归并回路用电量
	    		Map<String,Object> loopDataListSum = new HashMap<>();
	    		for(List<Map<String,Object>> tempLoopDataList : loopDataListList) {
	    			for(Map<String,Object> loopData : tempLoopDataList) {
	    				String showTime1 = loopData.get("showTime").toString();
	    				Double total1 = Double.parseDouble(loopData.get("total").toString());
	    				Object valueObj = loopDataListSum.get(showTime1);
	    				if(valueObj != null) {
	    					double total2 = Double.parseDouble(valueObj.toString());
	    					loopDataListSum.put(showTime1, total1 + total2);
	    				} else {
	    					loopDataListSum.put(showTime1, total1);
	    				}
	    			}
	    		}
	    		
	    		loopDataListSum.forEach((k, v) -> {
	    			Map<String,Object> map = new HashMap<>();
	    			map.put("showTime", k);
	    			map.put("total", v);
	                historyDataList.add(map);
	            });   
    			
    			data.put(unitMap.get("name").toString(), historyDataList);
	    		resultData.add(data);
    		}
    	}
        return ServletUtils.buildRs(true, "条形数据", resultData);
    }
    
    //能效数据 -> 能源流向
    @RequestMapping(value = {"flowEnergyList"})
    @ResponseBody
    public String flowEnergyList(String enterpriseId,String startTime, String endTime) {
    	
    	List<MapEntity> orgList = energyAnalysisService.getOrgListByOrgId(enterpriseId);
    	for(MapEntity org : orgList) {
    		String type = org.get("type").toString();
    		int iType = Integer.parseInt(type);
    		
    		if(iType >= 9 || type.equals("7")) {
    	    	//String sumValue = energyAnalysisService.getSumEnergyByLoopIdAndTime(org.get("id").toString(),startTime, endTime);
    			List<Map<String,Object>> showTimeList = loadShowTimeDataByTimeRange(startTime+" 00:00:00",endTime+" 23:59:59",org.get("id").toString());
    			Double totalSum = 0.0;
    			for(Map<String,Object> showTime:showTimeList) {
    				totalSum += Double.parseDouble(showTime.get("total").toString());
    			}
    	    	org.put("sumValue", totalSum);
    		}
    	}
        return ServletUtils.buildRs(true, "能源流向", orgList);
    }
    
    
    
    

	public List<Map<String,Object>> loadShowTimeDataByTimeRange(String beginTime,String endTime,String loopId) {
		final DateTimeFormatter formatterDayHourMin = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
		final DateTimeFormatter formatterDayTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime beiginTimeLocalDateTime = LocalDateTime.parse(beginTime,formatterDayTime);
		LocalDate beiginTimeLocalDate = LocalDate.parse(beginTime,formatterDayTime);
		LocalDateTime endTimeLocalDateTime = LocalDateTime.parse(endTime,formatterDayTime);
		LocalDate endTimeLocalDate = LocalDate.parse(endTime,formatterDayTime);
		String beginTimeDay = beiginTimeLocalDateTime.format(formatterDay);
		String endTimeDay = endTimeLocalDateTime.format(formatterDay);
		int beginTimeHour = beiginTimeLocalDateTime.getHour();
		int endTimeHour = endTimeLocalDateTime.getHour();
		
		List<Map<String,Object>> showTimeList = new ArrayList<>();
		if(beginTimeDay.equals(endTimeDay)) {
			logger.debug("17 getHistoryListByTypeAndTime point:"+loopId);
			List<Map<String,Object>> showTimeListTemp = energyAnalysisService.getHistoryListByTypeAndTime(beginTimeDay,"0",loopId);

			int iBegin = beginTimeHour;
			int iEnd = endTimeHour;
			for(Map<String,Object> showTime:showTimeListTemp) {
				LocalDateTime timeLocalDateTime = LocalDateTime.parse(showTime.get("showTime").toString(), formatterDayHourMin);
				int iHour = timeLocalDateTime.getHour();
				if(iHour >= iBegin && iHour <= iEnd) {
					showTimeList.add(showTime);
				}
			}
			
		} else {
			{
				logger.debug("18 getHistoryListByTypeAndTime point:"+loopId);
    			List<Map<String,Object>> showTimeListTemp = energyAnalysisService.getHistoryListByTypeAndTime(beginTimeDay,"0",loopId);

				int iBegin = beginTimeHour;
    			for(Map<String,Object> showTime:showTimeListTemp) {
    				LocalDateTime timeLocalDateTime = LocalDateTime.parse(showTime.get("showTime").toString(), formatterDayHourMin);
    				int iHour = timeLocalDateTime.getHour();
    				if(iHour >= iBegin) {
    					showTimeList.add(showTime);
    				}
    			}
			}
			LocalDate nextDay = beiginTimeLocalDate.plusDays(1);
			while( !nextDay.isAfter(endTimeLocalDate) ) {
				if(nextDay.equals(endTimeLocalDate)) {
					logger.debug("19 getHistoryListByTypeAndTime point:"+loopId);
        			List<Map<String,Object>> showTimeListTemp = energyAnalysisService.getHistoryListByTypeAndTime(nextDay.format(formatterDay),"0",loopId);
    				int iEnd = endTimeHour;
        			for(Map<String,Object> showTime:showTimeListTemp) {
        				LocalDateTime timeLocalDateTime = LocalDateTime.parse(showTime.get("showTime").toString(), formatterDayHourMin);
        				int iHour = timeLocalDateTime.getHour();
        				if(iHour <= iEnd) {
        					showTimeList.add(showTime);
        				}
        			}
				} else {
					logger.debug("20 getHistoryListByTypeAndTime point:"+loopId);
        			List<Map<String,Object>> showTimeListTemp = energyAnalysisService.getHistoryListByTypeAndTime(nextDay.format(formatterDay),"0",loopId);
        			showTimeListTemp.addAll(showTimeListTemp);
				}
				nextDay = nextDay.plusDays(1);
			}
			
		}
		
		return showTimeList;
		
	}
	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @RequestMapping(value = { "tDeviceVoltageList" })
    @ResponseBody
    public String tDeviceVoltageList(String orgId) {
        return ServletUtils.buildRs(true, "设备数据", energyAnalysisService.tDeviceVoltageList(orgId));
    }



    @RequestMapping(value = {"insertStarEnergyChange"})
    @ResponseBody
    public String insertStarEnergyChange(String chargeJson) {
        return ServletUtils.buildRs(true, "添加电力差价数据", energyAnalysisService.insertStarEnergy(chargeJson));
    }

    
    
    
    
    
    
    @RequestMapping(value = { "loadDayDataByDate" })
    @ResponseBody
    public String loadDayDataByDate(String date) {
    	energyAnalysisService.loadDayDataByDate(date);
        return ServletUtils.buildRs(true, "loadDayDataByDate", date);
    }

    
    @RequestMapping(value = { "loadMonthDataByMonth" })
    @ResponseBody
    public String loadMonthDataByMonth(String month) {
    	energyAnalysisService.loadMonthDataByMonth(month);
        return ServletUtils.buildRs(true, "loadMonthDataByMonth", month);
    }

    
    
    


}