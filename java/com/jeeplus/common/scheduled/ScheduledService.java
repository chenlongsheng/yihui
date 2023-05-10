//package com.jeeplus.common.scheduled;
//
//import org.springframework.stereotype.Service;
//
//import com.alibaba.fastjson.JSONObject;
//import com.jeeplus.common.persistence.MapEntity;
//import com.jeeplus.modules.starnet.entity.ElecHistoryData;
//import com.jeeplus.modules.starnet.service.DataService;
//import com.jeeplus.modules.starnet.service.ElecHistoryDataService;
//import com.jeeplus.modules.starnet.service.PowerAnalysisService;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.TemporalAdjusters;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//@Service
//@Lazy(false)
//@EnableScheduling
//public class ScheduledService {
//
//	@Autowired
//	RedisTemplate<String, String> redisTempldate;
//	
//	@Autowired
//	DataService dataService;
//	
//	@Autowired
//	ElecHistoryDataService elecHistoryDataService;
//	
//	@Autowired
//	PowerAnalysisService powerAnalysisService;
//	
//    @Scheduled(cron="0 */10 * * * ?")
//    private void loadLoopData() {
//    	//获取回路所关联的 有功电能 通道
//    	List<MapEntity> loopElecChannelList = dataService.getLoopElecChannelList();
//    	//查询 增量表该回路最后一条数据 ,历史数据表该通道的最后一条数据, 并将该数据id,缓存11分钟.
//    	for(MapEntity elecChannel : loopElecChannelList) {
//    		String chId = elecChannel.get("id").toString();
//    		String orgId = elecChannel.get("orgId").toString();
//    		String chType = elecChannel.get("chType").toString();
//    		
//    		String historyData = redisTempldate.opsForValue().get("history_data:"+orgId);
//    		if(historyData != null) {
//    			
//    			JSONObject historyDataObj = JSONObject.parseObject(historyData);
//    			String dataId = historyDataObj.getString("id");
//    			String dataChId = historyDataObj.getString("ch_id");
//    			double cacheHistoryValue = Double.parseDouble( historyDataObj.getString("history_value") );
//    			String history_time = historyDataObj.getString("history_time");
//    			
//    			//同步最新数据到增量表, 只要同步最后一条, 就行, 缓存存在,说明之前的数据已经存过了
//    			MapEntity newData = dataService.getNewData(chId);
//    			//计算差值后存入增量表
//    			String historyDataId = newData.get("id").toString();
//    			double historyValue = Double.parseDouble( newData.get("history_value").toString() );
//    			
//    			if(!dataId.equals(historyDataId)) {
//	    			//检查chId是否一致, 用来判断是否更换了设备
//	    			if(chId.equals(dataChId)) {
//	    				double increment = historyValue - cacheHistoryValue;
//	    				if(chType.equals("108")) {
//	    					increment = increment / 1000;
//	    				}
//	    				//插入增量数据库
//	    				ElecHistoryData ehd = new ElecHistoryData();
//	    				ehd.setId(historyDataId);
//	    				ehd.setChId(dataChId);
//	    				ehd.setValue(increment);
//	    				ehd.setOrgId(orgId);
//	    				ehd.setTime(history_time);
//	    				elecHistoryDataService.add(ehd);
//	    			} else {
//	    				double increment = historyValue;
//	    				if(chType.equals("108")) {
//	    					increment = increment / 1000;
//	    				}
//	    				//插入增量数据库
//	    				ElecHistoryData ehd = new ElecHistoryData();
//	    				ehd.setId(historyDataId);
//	    				ehd.setChId(dataChId);
//	    				ehd.setValue(increment);
//	    				ehd.setOrgId(orgId);
//	    				ehd.setTime(history_time);
//	    				elecHistoryDataService.add(ehd);
//	    			}
//    			}
//    	        JSONObject dataJSON = new JSONObject(newData);
//    			//将最后一条数据,保存到redis
//    			redisTempldate.opsForValue().set("history_data:"+orgId,dataJSON.toJSONString(),11,TimeUnit.MINUTES);
//    			
//    		} else {
//    			
//    			//从数据库增量表获取最后一条数据
//    			MapEntity latestLoopElecData = dataService.getLoopLatestElecData(chId);
//    			
//    			String dataId = "0";
//    			//从历史数据表,获取未存入增量表的数据,并保存
//    			if(latestLoopElecData != null) {
//        			dataId = latestLoopElecData.get("id").toString();
//    			}
//				List<MapEntity> datalist = dataService.getNewDatas(dataId,chId);
//				
//				MapEntity lastData = null;
//				if(latestLoopElecData == null) {
//					//将历史表的数据列表写入增量表
//					for(MapEntity data : datalist) {
//						
//						//计算增量写入增量表 TODO
//	    				ElecHistoryData ehd = new ElecHistoryData();
//	    				ehd.setId(data.get("id").toString());
//	    				ehd.setChId(data.get("ch_id").toString());
//	    				
//	    				double historyValue = 0.0;
//	    				if(lastData != null)
//	    					historyValue = Double.parseDouble( lastData.get("history_value").toString() );
//	    				
//	    				double historyValue2 = Double.parseDouble( data.get("history_value").toString() );
//	    				
//	    				if(chType.equals("108")) {
//	    					ehd.setValue( (historyValue2 - historyValue) / 1000 );
//	    				} else {
//	    					ehd.setValue(historyValue2 - historyValue);	
//	    				}
//	    				
//	    				ehd.setOrgId(orgId);
//	    				ehd.setTime(data.get("history_time").toString());
//	    				elecHistoryDataService.add(ehd);
//						
//						
//						lastData = data;
//					}
//				} else {
//					//将历史表的数据列表写入增量表
//					for(MapEntity data : datalist) {
//						if(lastData == null) {
//							lastData = data;
//							continue;
//						}
//						
//						//计算增量写入增量表 TODO
//	    				ElecHistoryData ehd = new ElecHistoryData();
//	    				ehd.setId(data.get("id").toString());
//	    				ehd.setChId(data.get("ch_id").toString());
//	    				
//	    				double historyValue = Double.parseDouble( lastData.get("history_value").toString() );
//	    				double historyValue2 = Double.parseDouble( data.get("history_value").toString() );
//	    				
//	    				if(chType.equals("108")) {
//	    					ehd.setValue( (historyValue2 - historyValue) / 1000 );
//	    				} else {
//	    					ehd.setValue(historyValue2 - historyValue);	
//	    				}
//	    				
//	    				ehd.setOrgId(orgId);
//	    				ehd.setTime(data.get("history_time").toString());
//	    				elecHistoryDataService.add(ehd);
//						
//						
//						lastData = data;
//					}
//				}
//				
//				if(lastData == null) {
//					//如果没有新的数据,读取最新数据,放入缓存
//					lastData = dataService.getNewData(chId);
//				}
//				if(lastData != null) {
//					JSONObject dataJSON = new JSONObject(lastData);
//	    			//将最后一条数据,保存到redis
//	    			redisTempldate.opsForValue().set("history_data:"+orgId,dataJSON.toJSONString(), 11,TimeUnit.MINUTES);
//				}
//
//    		}
//    	}
//    }
//
//    
//    
//    
//    
//    
//    //每天晚上3点
//    //@Scheduled(cron="0 59 23 * * ?")
//    @Scheduled(cron="0 59 23 * * ?")
//    private void loadLoopMaxData() {
//    	
//    	List<MapEntity> loopList = dataService.getLoopList();
//    	for(MapEntity loop : loopList) {
//    		String orgId = loop.get("orgId").toString();
//    		//获取当天日期
//    		LocalDateTime now = LocalDateTime.now();
//    		//计算完整路径
//    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//    		String todayDatetime = now.format(formatter);
//
//    		
//    		
//
//    		//获取 A,B,C相 电压通道
//    		MapEntity phaseAVolChannel = dataService.getPhaseAVolChannelByOrgId(orgId);
//    		MapEntity phaseBVolChannel = dataService.getPhaseBVolChannelByOrgId(orgId);
//    		MapEntity phaseCVolChannel = dataService.getPhaseCVolChannelByOrgId(orgId);
//    		
//    		//根据当天日期, 获取A,B,C相 电压通道 的极值
//    		MapEntity aMaxVolValue = null;
//    		if(phaseAVolChannel != null) {
//    			aMaxVolValue = dataService.getChannelMaxValueInADay(phaseAVolChannel.get("id").toString(),todayDatetime);
//    		}
//
//    		MapEntity aMinVolValue = null;
//    		if(phaseAVolChannel != null) {
//    			aMinVolValue = dataService.getChannelMinValueInADay(phaseAVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity aAVGVolValue = null;
//    		if(phaseAVolChannel != null) {
//    			aAVGVolValue = dataService.getChannelAVGValueInADay(phaseAVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity bMaxVolValue = null;
//    		if(phaseBVolChannel != null) {
//    			bMaxVolValue = dataService.getChannelMaxValueInADay(phaseBVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity bMinVolValue = null;
//    		if(phaseBVolChannel != null) {
//    			bMinVolValue = dataService.getChannelMinValueInADay(phaseBVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity bAVGVolValue = null;
//    		if(phaseBVolChannel != null) {
//    			bAVGVolValue = dataService.getChannelAVGValueInADay(phaseBVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity cMaxVolValue = null;
//    		if(phaseCVolChannel != null) {
//    			cMaxVolValue = dataService.getChannelMaxValueInADay(phaseCVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity cMinVolValue = null;
//    		if(phaseCVolChannel != null) {
//    			cMinVolValue = dataService.getChannelMinValueInADay(phaseCVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity cAVGVolValue = null;
//    		if(phaseCVolChannel != null) {
//    			cAVGVolValue = dataService.getChannelAVGValueInADay(phaseCVolChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		//获取A,B,C相 电流通道
//    		MapEntity phaseACurChannel = dataService.getPhaseACurChannelByOrgId(orgId);
//    		MapEntity phaseBCurChannel = dataService.getPhaseBCurChannelByOrgId(orgId);
//    		MapEntity phaseCCurChannel = dataService.getPhaseCCurChannelByOrgId(orgId);
//    		
//    		//根据当天日期, 获取A,B,C相 电流通道 的极值
//    		MapEntity aMaxCurValue = null;
//    		if(phaseACurChannel != null) {
//    			aMaxCurValue = dataService.getChannelMaxValueInADay(phaseACurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity aMinCurValue = null; 
//    		if(phaseACurChannel != null) {
//    			aMinCurValue = dataService.getChannelMinValueInADay(phaseACurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity aAVGCurValue = null; 
//    		if(phaseACurChannel != null) {
//    			aAVGCurValue = dataService.getChannelAVGValueInADay(phaseACurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity bMaxCurValue = null;
//    		if(phaseBCurChannel != null) {
//    			bMaxCurValue = dataService.getChannelMaxValueInADay(phaseBCurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity bMinCurValue = null;
//    		if(phaseBCurChannel != null) {
//    			bMinCurValue = dataService.getChannelMinValueInADay(phaseBCurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity bAVGCurValue = null; 
//    		if(phaseBCurChannel != null) {
//    			bAVGCurValue = dataService.getChannelAVGValueInADay(phaseBCurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity cMaxCurValue = null;
//    		if(phaseCCurChannel != null) {
//    			cMaxCurValue = dataService.getChannelMaxValueInADay(phaseCCurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity cMinCurValue = null;
//    		if(phaseCCurChannel != null) {
//    			cMinCurValue = dataService.getChannelMinValueInADay(phaseCCurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity cAVGCurValue = null;
//    		if(phaseCCurChannel != null) {
//    			cAVGCurValue = dataService.getChannelAVGValueInADay(phaseCCurChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		//获取正向电能通道
//    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
//    		
//    		//根据当天日期, 获取 正向电能通道 的极值
//    		MapEntity maxEnergyValue = null;
//    		if(phaseEnergyChannel != null) {
//    			maxEnergyValue = dataService.getChannelMaxValueInADay(phaseEnergyChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity minEnergyValue = null;
//    		if(phaseEnergyChannel != null) {
//    			minEnergyValue = dataService.getChannelMinValueInADay(phaseEnergyChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity avgEnergyValue = null;
//    		if(phaseEnergyChannel != null) {
//    			avgEnergyValue = dataService.getChannelAVGValueInADay(phaseEnergyChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		//获取有功功率通道
////    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
//    		//获取视在功率通道
////    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
//    		//获取无功功率通道
////    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
//    		
//    		
//    		//获取功率因素通道
//    		MapEntity powerFactorChannel = dataService.getPowerFactorChannelByOrgId(orgId);
//    		
//    		
//    		//获取线电压通道
////    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
//    		//负载率
////    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
//    		//频率
////    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
//    		
//    		
//    		//根据当天日期, 获取 功率因素通道的极值
//    		MapEntity maxPowerFactorValue = null;
//    		if(powerFactorChannel != null) {
//    			maxPowerFactorValue = dataService.getChannelMaxValueInADay(powerFactorChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity minPowerFactorValue = null;
//    		if(powerFactorChannel != null) {
//    			minPowerFactorValue = dataService.getChannelMinValueInADay(powerFactorChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity avgPowerFactorValue = null;
//    		if(powerFactorChannel != null) {
//    			avgPowerFactorValue = dataService.getChannelAVGValueInADay(powerFactorChannel.get("id").toString(),todayDatetime);
//    		}
//    		
//    		MapEntity values = new MapEntity();
//    		values.put("max_a_volValue", aMaxVolValue);
//    		values.put("min_a_volValue", aMinVolValue);
//    		values.put("avg_a_volValue", aAVGVolValue);
//    		
//    		values.put("max_b_volValue", bMaxVolValue);
//    		values.put("min_b_volValue", bMinVolValue);
//    		values.put("avg_b_volValue", bAVGVolValue);
//    		
//    		values.put("max_c_volValue", cMaxVolValue);
//    		values.put("min_c_volValue", cMinVolValue);
//    		values.put("avg_c_volValue", cAVGVolValue);
//
//    		values.put("max_a_curValue", aMaxCurValue);
//    		values.put("min_a_curValue", aMinCurValue);
//    		values.put("avg_a_curValue", aAVGCurValue);
//    		
//    		values.put("max_b_curValue", bMaxCurValue);
//    		values.put("min_b_curValue", bMinCurValue);
//    		values.put("avg_b_curValue", bAVGCurValue);
//    		
//    		values.put("max_c_curValue", cMaxCurValue);
//    		values.put("min_c_curValue", cMinCurValue);
//    		values.put("avg_c_curValue", cAVGCurValue);
//    		
//    		values.put("max_energyValue", maxEnergyValue);
//    		values.put("min_energyValue", minEnergyValue);
//    		values.put("avg_energyValue", avgEnergyValue);
//    		
//    		values.put("max_powerFactorValue", maxPowerFactorValue);
//    		values.put("min_powerFactorValue", minPowerFactorValue);
//    		values.put("avg_powerFactorValue", avgPowerFactorValue);
//    		
//    		JSONObject jsonValues = new JSONObject(values);
//    		redisTempldate.opsForHash().put("ExtremalData:"+todayDatetime, orgId, jsonValues.toJSONString());
//    		
//    	}
//    	
//    }
//    
//    
//    /**
//	* 每两个小时执行一次
//    * Scheduled task
//    */
//    @Scheduled(cron = "0 0 */2 * * ?")
//    private void loadLoopMonthMaxData() {
//		LocalDateTime now = LocalDateTime.now();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		String todayDatetime = now.format(formatter);
//		
//		LocalDate searchMonth = LocalDate.parse(todayDatetime,formatter);
//		LocalDate firstDay = searchMonth.with(TemporalAdjusters.firstDayOfMonth()); // 获取当前月的第一天
//		LocalDate lastDay = searchMonth.with(TemporalAdjusters.lastDayOfMonth()); // 获取当前月的最后一天
//		
//		String firstDayStr = firstDay.format(formatter);
//		firstDayStr += " " + "00:00:00";
//		String lastDayStr = lastDay.format(formatter);
//		lastDayStr += " " + "23:59:59";
//		
//		List<MapEntity> lineLossL1List = powerAnalysisService.getLineLossDataEnd(firstDayStr, lastDayStr, "9");
//		for(MapEntity lineLossL1 : lineLossL1List) {
//			String chId = lineLossL1.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL1);
//			redisTempldate.opsForHash().put("LoopMaxData:"+todayDatetime, chId, jsonValue.toJSONString());	
//		}
//		
//		List<MapEntity> lineLossL2List = powerAnalysisService.getLineLossDataEnd(firstDayStr, lastDayStr, "10");
//		for(MapEntity lineLossL2 : lineLossL2List) {
//			String chId = lineLossL2.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL2);
//			redisTempldate.opsForHash().put("LoopMaxData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//		
//		List<MapEntity> lineLossL3List = powerAnalysisService.getLineLossDataEnd(firstDayStr, lastDayStr, "11");
//		for(MapEntity lineLossL3 : lineLossL3List) {
//			String chId = lineLossL3.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL3);
//			redisTempldate.opsForHash().put("LoopMaxData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//		
//		List<MapEntity> lineLossL4List = powerAnalysisService.getLineLossDataEnd(firstDayStr, lastDayStr, "12");
//		for(MapEntity lineLossL4 : lineLossL4List) {
//			String chId = lineLossL4.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL4);
//			redisTempldate.opsForHash().put("LoopMaxData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//		
//		List<MapEntity> lineLossL5List = powerAnalysisService.getLineLossDataEnd(firstDayStr, lastDayStr, "13");
//		for(MapEntity lineLossL5 : lineLossL5List) {
//			String chId = lineLossL5.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL5);
//			redisTempldate.opsForHash().put("LoopMaxData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//    }
//    
//    //每月1号1点
//    @Scheduled(cron="0 0 1 1 * ?")
//    private void loadLoopMonthMinData() {
//		LocalDateTime now = LocalDateTime.now();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		String todayDatetime = now.format(formatter);
//		
//		LocalDate searchMonth = LocalDate.parse(todayDatetime,formatter);
//		LocalDate firstDay = searchMonth.with(TemporalAdjusters.firstDayOfMonth()); // 获取当前月的第一天
//		LocalDate lastDay = searchMonth.with(TemporalAdjusters.lastDayOfMonth()); // 获取当前月的最后一天
//		
//		String firstDayStr = firstDay.format(formatter);
//		firstDayStr += " " + "00:00:00";
//		String lastDayStr = lastDay.format(formatter);
//		lastDayStr += " " + "23:59:59";
//		
//		List<MapEntity> lineLossL1List = powerAnalysisService.getLineLossDataBegin(firstDayStr, lastDayStr, "9");
//		for(MapEntity lineLossL1 : lineLossL1List) {
//			String chId = lineLossL1.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL1);
//			redisTempldate.opsForHash().put("LoopMinData:"+todayDatetime, chId, jsonValue.toJSONString());	
//		}
//		
//		List<MapEntity> lineLossL2List = powerAnalysisService.getLineLossDataBegin(firstDayStr, lastDayStr, "10");
//		for(MapEntity lineLossL2 : lineLossL2List) {
//			String chId = lineLossL2.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL2);
//			redisTempldate.opsForHash().put("LoopMinData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//		
//		List<MapEntity> lineLossL3List = powerAnalysisService.getLineLossDataBegin(firstDayStr, lastDayStr, "11");
//		for(MapEntity lineLossL3 : lineLossL3List) {
//			String chId = lineLossL3.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL3);
//			redisTempldate.opsForHash().put("LoopMinData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//		
//		List<MapEntity> lineLossL4List = powerAnalysisService.getLineLossDataBegin(firstDayStr, lastDayStr, "12");
//		for(MapEntity lineLossL4 : lineLossL4List) {
//			String chId = lineLossL4.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL4);
//			redisTempldate.opsForHash().put("LoopMinData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//		
//		List<MapEntity> lineLossL5List = powerAnalysisService.getLineLossDataBegin(firstDayStr, lastDayStr, "13");
//		for(MapEntity lineLossL5 : lineLossL5List) {
//			String chId = lineLossL5.get("ch_id").toString();
//			JSONObject jsonValue = new JSONObject(lineLossL5);
//			redisTempldate.opsForHash().put("LoopMinData:"+todayDatetime, chId, jsonValue.toJSONString());		
//		}
//		
//		
//    }
//    
//}
