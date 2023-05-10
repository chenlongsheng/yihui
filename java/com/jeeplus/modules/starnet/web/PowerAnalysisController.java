/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.web.BaseController;

import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.starnet.entity.ConsumptionWithLoss;
import com.jeeplus.modules.starnet.entity.ElecUnitStrategy;
import com.jeeplus.modules.starnet.service.ConsumptionWithLossService;
import com.jeeplus.modules.starnet.service.ElecUnitStrategyService;
import com.jeeplus.modules.starnet.service.ElectricityUnitService;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.starnet.service.EnergyOpenApiService;
import com.jeeplus.modules.starnet.service.LoadUnitAdjustProportionService;
import com.jeeplus.modules.starnet.service.MonthTotalService;
import com.jeeplus.modules.starnet.service.PowerAnalysisService;
import com.jeeplus.modules.starnet.service.PowerDataService;
import com.jeeplus.modules.starnet.util.FileCopyUtil;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;


/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */

//用电分析
@Controller
@RequestMapping(value = "analysisDatas")
public class PowerAnalysisController extends BaseController {

	@Autowired
	RedisTemplate<String, String> redisTempldate;
	
	@Autowired
	PowerAnalysisService powerAnalysisService;
	
	@Autowired
	EnergyAnalysisService energyAnalysisService;
	
	@Autowired
	TOrgService tOrgService;
	
	@Autowired
	EnergyOpenApiService energyOpenApiService;  
	
	@Autowired
	ElecUnitStrategyService elecUnitStrategyService;
	
	@Autowired
	ConsumptionWithLossService consumptionWithLossService;


    @Autowired
    ElectricityUnitService electricityUnitService;
    
    @Autowired
    MonthTotalService monthTotalService;
	
	@RequestMapping("/analysisReport")
	@ResponseBody
	public String analysisReport(String loopId, String unitId, String time, String begin, String end) {
		final DateTimeFormatter formatterDayHourMin = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
		List<MapEntity> resultData = new ArrayList<MapEntity>();
    	if(loopId != null && !loopId.equals("")) {
    		String loopIds[] = loopId.split(",");
    		for(String loop: loopIds) {
    			if(!loop.equals("")) {
	    			TOrg org = tOrgService.get(loop);
	    			MapEntity data = new MapEntity();
	    			//List<MapEntity> showTimeList = powerAnalysisService.analysisReportByLoopId(loop, time, begin, end);
	    			logger.debug("1 getHistoryListByTypeAndTime point:"+loop);
	    			List<Map<String,Object>> showTimeListTemp = energyAnalysisService.getHistoryListByTypeAndTime(time,"0",loop);
	    			List<Map<String,Object>> showTimeList = new ArrayList<>();
					int iBegin = Integer.parseInt(begin);
					int iEnd = Integer.parseInt(end);
	    			for(Map<String,Object> showTime:showTimeListTemp) {
	    				LocalDateTime timeLocalDateTime = LocalDateTime.parse(showTime.get("showTime").toString(), formatterDayHourMin);
	    				int iHour = timeLocalDateTime.getHour();
	    				if(iHour >= iBegin && iHour <= iEnd) {
	    					showTimeList.add(showTime);
	    				}
	    			}
	    			data.put(org.getId(), showTimeList);
	    			resultData.add(data);
    			}
    		}
    		
    	} else if(unitId != null && !unitId.equals("")) {
    		String unitIds[] = unitId.split(",");
    		for(String unitid : unitIds) {
    			MapEntity unitMap = powerAnalysisService.getStarElectricityUnit(unitid);
    			MapEntity data = new MapEntity();
    			//List<MapEntity> showTimeList = powerAnalysisService.analysisReportByUnitId(unitid, time, begin, end);
    			
    			List<Map<String,Object>> showTimeList = new ArrayList<>();
    			List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitid);
    			List<List<Map<String,Object>>> loopDataListList = new ArrayList<>(); 
        		for(final MapEntity unitItem : unitMapList) {
        			String unitLoopId = unitItem.get("loopOrgId").toString();
        			
        			logger.debug("analysisReport");
        			logger.debug("unitLoopId:"+unitLoopId);
        			
        			
        			String notDeduction = "0";
        			Object notDeductionObj = unitItem.get("notDeduction");
        			if(notDeductionObj != null) {
        				notDeduction = notDeductionObj.toString();
        			}
        			String proportion = "0.0";
        			Object proportionObj = unitItem.get("proportion");
        			if(proportionObj != null) {
        				proportion = proportionObj.toString();
        			}
        			
        			
        			logger.debug("2 getHistoryListByTypeAndTime point:"+unitLoopId);
        			List<Map<String,Object>> loopDataList = energyAnalysisService.getHistoryListByTypeAndTime(time,"0",unitLoopId);
        			
        			
            		List<List<Map<String,Object>>> subLoopdataList = new ArrayList<>();
        			String sonLoopIds = "";
        			if(unitMap.get("sonLoopIds") != null) {
        				sonLoopIds = unitMap.get("sonLoopIds").toString();
        			}
        			
        			logger.debug("sonLoopIds:"+sonLoopIds);
        			
            		String loopArr[] = sonLoopIds.split(",");
            		for(final String loop : loopArr) {
            			if(!loop.equals("")) {
            				logger.debug("3 getHistoryListByTypeAndTime point:"+loop);
            				subLoopdataList.add(energyAnalysisService.getHistoryListByTypeAndTime(time,"0",loop));
            			}
            		}

            		logger.debug("subLoopdataList:"+subLoopdataList);
        		
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
        					String showTime1 = loopData.get("showTime").toString();
        					Double total1 = Double.parseDouble(loopData.get("total").toString());
        					Object valueObj = subLoopDataListSum.get(showTime1);
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
        		
        		
				int iBegin = Integer.parseInt(begin);
				int iEnd = Integer.parseInt(end);
        		loopDataListSum.forEach((k, v) -> {
        			Map<String,Object> map = new HashMap<>();
        			map.put("showTime", k);
        			map.put("total", v);
        			
        			LocalDateTime timeLocalDateTime = LocalDateTime.parse(k, formatterDayHourMin);
    				int iHour = timeLocalDateTime.getHour();
    				if(iHour >= iBegin && iHour <= iEnd) {
    					showTimeList.add(map);
    				}
                });
        		
        		
    			data.put(unitMap.get("name").toString(), showTimeList);
    			resultData.add(data);
    		}
    	}
    	
		return ServletUtils.buildRs(true, "用电报表",resultData);
	}


	/* 
	 * 导出用电报表
	 */
	@RequestMapping("/exportAnalysisReports")
	@ResponseBody
	public String exportAnalysisReports(@RequestBody String uploadData) throws FileNotFoundException, IOException {
		JSONArray dataList = JSONArray.parseArray(uploadData);
		
		
		

		logger.debug("===============exportExcel================");
        List<String> headerList = Lists.newArrayList();
        headerList.add("名称");
        headerList.add("00时");
        headerList.add("01时");
        headerList.add("02时");
        headerList.add("03时");
        headerList.add("04时");
        headerList.add("05时");
        headerList.add("06时");
        headerList.add("07时");
        headerList.add("08时");
        headerList.add("09时");
        headerList.add("10时");
        headerList.add("11时");
        headerList.add("12时");
        headerList.add("13时");
        headerList.add("14时");
        headerList.add("15时");
        headerList.add("16时");
        headerList.add("17时");
        headerList.add("18时");
        headerList.add("19时");
        headerList.add("20时");
        headerList.add("21时");
        headerList.add("22时");
        headerList.add("23时");
        headerList.add("合计");
        

        //计算完整路径
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date =  LocalDate.now().format(formatter);
        String filePrefix = "/";
        String rootPath = filePrefix + ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1)+"../..";
        //String rootPath = "d:\\";
        logger.debug("create excel path:" + rootPath+"/export_excel/"+date);

        boolean isCreteFileFolder = FileCopyUtil.createDirectory(rootPath+"/export_excel/"+date);
        if(isCreteFileFolder) {
            logger.debug("创建成功");
        } else {
            logger.debug("创建失败");
        }

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH_mm_ss");

        String excelRelativePath = "/export_excel/"+ date + "/用电报表_" + date + "_" + LocalTime.now().format(formatter2)+".xlsx";
        String filePath = rootPath + excelRelativePath;

        logger.debug("文件全称");
        logger.debug(filePath);

        ExportExcel ee = null;
        try {
            ee = new ExportExcel("", headerList);
        }catch (Exception e) {
            logger.debug(e.toString());
        }
        //ExportExcel ee = new ExportExcel("", headerList);
        logger.debug("===============ExportExcel init ok================");
        for (int i = 0; i < dataList.size() - 1; i++) {
        	JSONObject data = dataList.getJSONObject(i);
            Row row = ee.addRow();
            for (int j = 0; j < 25; j++) {
            	String key = "";
            	if(j == 0) {
            		key = "name";
            	} else {
                	if((j-1)<10) {
                		key = "0" + (j-1);
                	} else {
                		key = String.valueOf((j-1));
                	}
            	}
            	ee.addCell(row, j, data.getString(key));	
            }
            
            ee.addCell(row, 25, data.getString("total"));
        }
        int size = dataList.size();
        JSONObject data = dataList.getJSONObject(size-1);
        Row rowTotal = ee.addRow();
        //写入总计行
        for(int j = 0; j < 25; j++) {
        	String key = "";
        	if(j == 0) {
        		key = "name";
        	} else {
            	if((j-1)<10) {
            		key = "0" + (j-1);
            	} else {
            		key = String.valueOf((j-1));
            	}
        	}
        	ee.addCell(rowTotal, j, data.getString(key));
        }
        
        ee.addCell(rowTotal, 25, data.getString("total"));
        
        ee.writeFile(filePath);
        ee.dispose();
        logger.debug("Export success.");
        
    	
    	return ServletUtils.buildRs(true, "用电报表导出",excelRelativePath);
	}
	
	
	
	/*
	@RequestMapping("/exportAnalysisReports")
	@ResponseBody
	public String exportAnalysisReports(@RequestBody String uploadData) throws FileNotFoundException, IOException {
		final DateTimeFormatter formatterDayHourMin = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
		JSONObject param = JSONObject.parseObject(uploadData);
		JSONArray loopIds = param.getJSONArray("loopIds");
		String unitId = param.getString("unitId");
		String time = param.getString("time");
		String begin = param.getString("begin");
		String end = param.getString("end");
		
		List<MapEntity> resultData = new ArrayList<MapEntity>();
    	if(loopIds != null) {
    		for(int i=0;i< loopIds.size(); i++) {
    			JSONObject loop = loopIds.getJSONObject(i);
    			String loopId = loop.getString("ids");
    			String name = loop.getString("name");
    			MapEntity data = new MapEntity();
    			//List<MapEntity> showTimeList = powerAnalysisService.analysisReportByLoopId(loopId, time, begin, end);
    			
    			logger.debug("4 getHistoryListByTypeAndTime point:"+loopId);
    			List<Map<String,Object>> showTimeListTemp = energyAnalysisService.getHistoryListByTypeAndTime(time,"0",loopId);
    			List<Map<String,Object>> showTimeList = new ArrayList<>();
				int iBegin = Integer.parseInt(begin);
				int iEnd = Integer.parseInt(end);
    			for(Map<String,Object> showTime:showTimeListTemp) {
    				LocalDateTime timeLocalDateTime = LocalDateTime.parse(showTime.get("showTime").toString(), formatterDayHourMin);
    				int iHour = timeLocalDateTime.getHour();
    				if(iHour >= iBegin && iHour <= iEnd) {
    					showTimeList.add(showTime);
    				}
    			}
    			
    			
    			
    			
    			data.put(name, showTimeList);
    			resultData.add(data);
    		}
    		
    		
    	} else if(unitId != null && !unitId.equals("")) {
    		String unitIds[] = unitId.split(",");
    		for(String unitid : unitIds) {
    			MapEntity unitMap = powerAnalysisService.getStarElectricityUnit(unitid);
    			MapEntity data = new MapEntity();
    			
    			//List<MapEntity> showTimeList = powerAnalysisService.analysisReportByUnitId(unitid, time, begin, end);

    			
    			
    			List<Map<String,Object>> showTimeList = new ArrayList<>();
    			List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitid);
    			List<List<Map<String,Object>>> loopDataListList = new ArrayList<>(); 
        		for(final MapEntity unitItem : unitMapList) {
        			String unitLoopId = unitItem.get("loopOrgId").toString();
        			String notDeduction = unitItem.get("notDeduction").toString();
        			String proportion = unitItem.get("proportion").toString();
        			if(proportion == null) {
        				proportion = "0.0";
        			}
        			logger.debug("5 getHistoryListByTypeAndTime point:"+unitLoopId);
        			List<Map<String,Object>> loopDataList = energyAnalysisService.getHistoryListByTypeAndTime(time,"0",unitLoopId);
        			
        			
            		List<List<Map<String,Object>>> subLoopdataList = new ArrayList<>();
        			String sonLoopIds = "";
        			if(unitMap.get("sonLoopIds") != null) {
        				sonLoopIds = unitMap.get("sonLoopIds").toString();
        			}
            		String loopArr[] = sonLoopIds.split(",");
            		for(final String loop : loopArr) {
            			if(!loop.equals(""))
            				logger.debug("6 getHistoryListByTypeAndTime point:"+loop);
            				subLoopdataList.add(energyAnalysisService.getHistoryListByTypeAndTime(time,"0",loop));
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
        		
        		
				int iBegin = Integer.parseInt(begin);
				int iEnd = Integer.parseInt(end);
        		loopDataListSum.forEach((k, v) -> {
        			Map<String,Object> map = new HashMap<>();
        			map.put("showTime", k);
        			map.put("total", v);
        			LocalDateTime timeLocalDateTime = LocalDateTime.parse(k, formatterDayHourMin);
    				int iHour = timeLocalDateTime.getHour();
    				if(iHour >= iBegin && iHour <= iEnd) {
    					showTimeList.add(map);
    				}
                });
        		
    			
        		
    			Double totalSum = 0.0;
    			for(Map<String,Object> showTime:showTimeList) {
    				totalSum += Double.parseDouble(showTime.get("total").toString());
    			}
        		
    			
    			
    			data.put(unitMap.get("name").toString(), totalSum);
    			resultData.add(data);
    		}
    	}

		logger.debug("===============exportExcel================");
        List<String> headerList = Lists.newArrayList();
        headerList.add("名称");
        headerList.add("00时");
        headerList.add("01时");
        headerList.add("02时");
        headerList.add("03时");
        headerList.add("04时");
        headerList.add("05时");
        headerList.add("06时");
        headerList.add("07时");
        headerList.add("08时");
        headerList.add("09时");
        headerList.add("10时");
        headerList.add("11时");
        headerList.add("12时");
        headerList.add("13时");
        headerList.add("14时");
        headerList.add("15时");
        headerList.add("16时");
        headerList.add("17时");
        headerList.add("18时");
        headerList.add("19时");
        headerList.add("20时");
        headerList.add("21时");
        headerList.add("22时");
        headerList.add("23时");
        headerList.add("合计");
        
        List<List<String>> dataList = Lists.newArrayList();
        
        for (int i = 0; i < resultData.size(); i++) {
            MapEntity data = resultData.get(i);
            List<String> dataRowList = Lists.newArrayList();
            
            Set<Entry<String,Object>> es = data.entrySet();
            Iterator<Map.Entry<String, Object>> it = es.iterator();
            while(it.hasNext()) {
            	Map.Entry<String, Object> e1 = it.next();
            	String key = e1.getKey();
                dataRowList.add(key);
            	Object valueObj = e1.getValue();
            	List<Object> showTimeList = (List<Object>)valueObj;
            	JSONArray values = new JSONArray(showTimeList);
            	for(int j = 0; j < values.size(); j++) {
            		JSONObject jobj = values.getJSONObject(j);
                    dataRowList.add(  String.valueOf(jobj.getDoubleValue("total"))  );
                    //dataRowList.add(  String.valueOf(jobj.getString("showTime"))    );
            	}
            }
            dataList.add(dataRowList);
            
        }

        //计算完整路径
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date =  LocalDate.now().format(formatter);
        String filePrefix = "/";
        String rootPath = filePrefix + ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1)+"../..";
        //String rootPath = "d:\\";
        logger.debug("create excel path:" + rootPath+"/export_excel/"+date);

        boolean isCreteFileFolder = FileCopyUtil.createDirectory(rootPath+"/export_excel/"+date);
        if(isCreteFileFolder) {
            logger.debug("创建成功");
        } else {
            logger.debug("创建失败");
        }

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH_mm_ss");

        String excelRelativePath = "/export_excel/"+ date + "/用电报表_" + date + "_" + LocalTime.now().format(formatter2)+".xlsx";
        String filePath = rootPath + excelRelativePath;

        logger.debug("文件全称");
        logger.debug(filePath);

        ExportExcel ee = null;
        try {
            ee = new ExportExcel("", headerList);
        }catch (Exception e) {
            logger.debug(e.toString());
        }
        //ExportExcel ee = new ExportExcel("", headerList);
        logger.debug("===============ExportExcel init ok================");
        for (int i = 0; i < dataList.size(); i++) {
            Row row = ee.addRow();
            double totalValue = 0.0;
            for (int j = 0; j < dataList.get(i).size(); j++) {
            	String cellValue = dataList.get(i).get(j);
            	if(cellValue != null) {
            		if(j!=0) {
            			totalValue += Double.parseDouble(dataList.get(i).get(j));
            		}
            		ee.addCell(row, j, dataList.get(i).get(j));
            	}
            	else
            		ee.addCell(row, j, "0");
            }
            
            ee.addCell(row, 25, totalValue);
        }
        
        double allTotal = 0.0;
        Row rowTotal = ee.addRow();
        //写入总计行
        for(int j = 0; j < 25; j++) {
        	if(j == 0)
        		ee.addCell(rowTotal, j, "合计");
        	else {
        		double totalValue = 0.0;
        		for (int i = 0; i < dataList.size(); i++) {
        			List<String> data = dataList.get(i);
                	String cellValue = null;
                	if(data != null && data.size() > j) {
                		cellValue = data.get(j);
                	}
                		
                	if(cellValue != null) {
                		totalValue += Double.parseDouble(dataList.get(i).get(j));
                	}
                }
        		allTotal += totalValue;
        		ee.addCell(rowTotal, j, totalValue);
        	}
        }
        
        ee.addCell(rowTotal, 25, allTotal);
        
        ee.writeFile(filePath);
        ee.dispose();
        logger.debug("Export success.");
        
    	
    	return ServletUtils.buildRs(true, "用电报表导出",excelRelativePath);
	}
	 
	 */
	
	
	public List<Map<String,Object>> loadShowTimeDataByTimeRange(String beginTime,String endTime,String loopId) {
		logger.debug("loadShowTimeDataByTimeRange    beginTime:"+beginTime+"    endTime:"+endTime+"    loopId:"+loopId);
		
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
			logger.debug("7 getHistoryListByTypeAndTime point:"+loopId);
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
				logger.debug("8 getHistoryListByTypeAndTime point:"+loopId);
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
					logger.debug("9 getHistoryListByTypeAndTime point:"+loopId);
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
					logger.debug("10 getHistoryListByTypeAndTime point:"+loopId);
        			List<Map<String,Object>> showTimeListTemp = energyAnalysisService.getHistoryListByTypeAndTime(nextDay.format(formatterDay),"0",loopId);
        			showTimeList.addAll(showTimeListTemp);
				}
				nextDay = nextDay.plusDays(1);
			}
			
		}
		
		return showTimeList;
		
	}
	
	
	/*
	 * 用电报表
	 */
	@RequestMapping("/electricReport")
	@ResponseBody
	public String electricReport(String loopId, String unitId, String beginTime, String endTime){
		List<MapEntity> resultData = new ArrayList<MapEntity>();
    	if(loopId != null && !loopId.equals("")) {
    		String loopIds[] = loopId.split(",");
    		for(String loop: loopIds) {
    			if(!loop.equals("")) {
	    			TOrg org = tOrgService.get(loop);
	    			MapEntity data = new MapEntity();
	    			List<Map<String,Object>> showTimeList = loadShowTimeDataByTimeRange(beginTime,endTime,loop);
	    			Double totalSum = 0.0;
	    			for(Map<String,Object> showTime:showTimeList) {
	    				
	    				String st = showTime.get("showTime").toString();
	    				String total = showTime.get("total").toString();
	    				logger.debug("showTime:"+st);
	    				logger.debug("total:"+total);
	    						
	    				totalSum += Double.parseDouble(showTime.get("total").toString());
	    			}
	    			Map<String,Object> total = new HashMap<>();
	    			total.put("total", totalSum);
	    			data.put(org.getId(),total);
	    			
	    			//data.put(org.getId(),powerAnalysisService.electricReportByLoopId(loop, beginTime, endTime ) );
	    			resultData.add(data);
    			}
    		}
    		
    	} else if(unitId != null && !unitId.equals("")) {
    		
    		String unitIds[] = unitId.split(",");
    		for(String unitid : unitIds) {
    			MapEntity unitMap = powerAnalysisService.getStarElectricityUnit(unitid);
	    		MapEntity data = new MapEntity();

	    		Double unitTotalSum = 0.0;
    			List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitid);
        		for(final MapEntity unitItem : unitMapList) {

        			String unitLoopId = unitItem.get("loopOrgId").toString();
        			String notDeduction = unitItem.get("notDeduction").toString();
        			String proportion = unitItem.get("proportion").toString();
        			if(proportion == null) {
        				proportion = "0.0";
        			}
        			
        			List<Map<String,Object>> showTimeList = loadShowTimeDataByTimeRange(beginTime,endTime,unitLoopId);
        			Double loopTotalSum = 0.0;
        			for(Map<String,Object> showTime:showTimeList) {
        				loopTotalSum += Double.parseDouble(showTime.get("total").toString());
        			}
        			
            		List<Map<String,Object>> subLoopdataList = new ArrayList<>();
        			String sonLoopIds = "";
        			if(unitItem.get("sonLoopIds") != null) {
        				sonLoopIds = unitItem.get("sonLoopIds").toString();
        			}
            		String loopArr[] = sonLoopIds.split(",");
            		for(final String loop : loopArr) {
            			if(!loop.equals("")) {
            				List<Map<String,Object>> showTimeListTemp = loadShowTimeDataByTimeRange(beginTime,endTime,unitLoopId);
                			subLoopdataList.addAll(showTimeListTemp);	
            			}
            		}

        			Double totalSubLoopSum = 0.0;
        			for(Map<String,Object> showTime:subLoopdataList) {
        				totalSubLoopSum += Double.parseDouble(showTime.get("total").toString());
        			}
        			
        			if(notDeduction.equals("1")) {
        				loopTotalSum = loopTotalSum - totalSubLoopSum;
        			}
        			
        			loopTotalSum = loopTotalSum * Double.parseDouble(proportion);
        			
        			unitTotalSum += loopTotalSum;
        		}
        		
    			Map<String,Object> total = new HashMap<>();
    			total.put("total", unitTotalSum);
    			data.put(unitMap.get("name").toString(),total);
	    		
    			//data.put(unitMap.get("name").toString(),powerAnalysisService.electricReportByUnitId(unitid, beginTime, endTime));
	    		resultData.add(data);
    		}
    	}
    	
		return ServletUtils.buildRs(true, "电能报表",resultData);
	}
	
	/*
	 * 导出电能报表
	 */
	@RequestMapping("/exportelEctricReport")
	@ResponseBody
	public String exportelEctricReport(@RequestBody String uploadData) throws FileNotFoundException, IOException {
		JSONArray dataList = JSONArray.parseArray(uploadData);

		logger.debug("===============exportExcel================");
        List<String> headerList = Lists.newArrayList();
        headerList.add("名称");
        headerList.add("电能值");
        

        //计算完整路径
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date =  LocalDate.now().format(formatter);
        String filePrefix = "/";
        String rootPath = filePrefix + ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1)+"../..";
        //String rootPath = "d:\\";
        logger.debug("create excel path:" + rootPath+"/export_excel/"+date);

        boolean isCreteFileFolder = FileCopyUtil.createDirectory(rootPath+"/export_excel/"+date);
        if(isCreteFileFolder) {
            logger.debug("创建成功");
        } else {
            logger.debug("创建失败");
        }

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH_mm_ss");

        String excelRelativePath = "/export_excel/"+ date + "/电能报表_" + date + "_" + LocalTime.now().format(formatter2)+".xlsx";
        String filePath = rootPath + excelRelativePath;

        logger.debug("文件全称");
        logger.debug(filePath);

        ExportExcel ee = null;
        try {
            ee = new ExportExcel("", headerList);
        }catch (Exception e) {
            logger.debug(e.toString());
        }
        //ExportExcel ee = new ExportExcel("", headerList);
        logger.debug("===============ExportExcel init ok================");

        for (int i = 0; i < dataList.size(); i++) {
        	JSONObject data = dataList.getJSONObject(i);
        	String name = data.getString("name");
        	String total = data.getString("total");
            Row row = ee.addRow();
			ee.addCell(row, 0, name);
			ee.addCell(row, 1, total);            
        }
        
        ee.writeFile(filePath);
        ee.dispose();
        logger.debug("Export success.");
        
		return ServletUtils.buildRs(true, "电能报表",excelRelativePath);
	}
	
	
	/*
	@RequestMapping("/exportelEctricReport")
	@ResponseBody
	public String exportelEctricReport(@RequestBody String uploadData) throws FileNotFoundException, IOException {
		JSONObject param = JSONObject.parseObject(uploadData);
		JSONArray loopIds = param.getJSONArray("loopIds");
		String unitId = param.getString("unitId");
		String beginTime = param.getString("beginTime");
		String endTime = param.getString("endTime");
		
		List<MapEntity> resultData = new ArrayList<MapEntity>();
    	if(loopIds != null) {
    	
    		for(int i=0;i< loopIds.size(); i++) {
    			JSONObject loop = loopIds.getJSONObject(i);
    			String loopId = loop.getString("ids");
    			String name = loop.getString("name");
    			MapEntity data = new MapEntity();
    			
    			
				List<Map<String,Object>> showTimeList = loadShowTimeDataByTimeRange(beginTime,endTime,loopId);
    			Double totalSum = 0.0;
    			for(Map<String,Object> showTime:showTimeList) {
    				totalSum += Double.parseDouble(showTime.get("total").toString());
    			}
    			
    			Map<String,Object> total = new HashMap<>();
    			total.put("total", totalSum);
    			data.put(name,total);
    			
    			//data.put(name,powerAnalysisService.electricReportByLoopId(loopId, beginTime, endTime ) );
    			resultData.add(data);
    		}
    		
    	} else if(unitId != null && !unitId.equals("")) {
    		String unitIds[] = unitId.split(",");
    		for(String unitid : unitIds) {
    			MapEntity unitMap = powerAnalysisService.getStarElectricityUnit(unitid);
	    		MapEntity data = new MapEntity();
	    		
	    		Double unitTotalSum = 0.0;
    			List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitId);
        		for(final MapEntity unitItem : unitMapList) {

        			String unitLoopId = unitItem.get("loopOrgId").toString();
        			String notDeduction = unitMap.get("notDeduction").toString();
        			String proportion = unitItem.get("proportion").toString();
        			if(proportion == null) {
        				proportion = "0.0";
        			}
        			
        			List<Map<String,Object>> showTimeList = loadShowTimeDataByTimeRange(beginTime,endTime,unitLoopId);
        			Double loopTotalSum = 0.0;
        			for(Map<String,Object> showTime:showTimeList) {
        				loopTotalSum += Double.parseDouble(showTime.get("total").toString());
        			}
        			
            		List<Map<String,Object>> subLoopdataList = new ArrayList<>();
        			String sonLoopIds = "";
        			if(unitItem.get("sonLoopIds") != null) {
        				sonLoopIds = unitItem.get("sonLoopIds").toString();
        			}
            		String loopArr[] = sonLoopIds.split(",");
            		for(final String loop : loopArr) {
            			if(!loop.equals("")) {
            				List<Map<String,Object>> showTimeListTemp = loadShowTimeDataByTimeRange(beginTime,endTime,unitLoopId);
                			subLoopdataList.addAll(showTimeListTemp);	
            			}
            		}

        			Double totalSubLoopSum = 0.0;
        			for(Map<String,Object> showTime:subLoopdataList) {
        				totalSubLoopSum += Double.parseDouble(showTime.get("total").toString());
        			}
        			
        			if(notDeduction.equals("1")) {
        				loopTotalSum = loopTotalSum - totalSubLoopSum;
        			}
        			
        			loopTotalSum = loopTotalSum * Double.parseDouble(proportion);
        			
        			unitTotalSum += loopTotalSum;
        		}
        		
    			Map<String,Object> total = new HashMap<>();
    			total.put("total", unitTotalSum);
    			data.put(unitMap.get("name").toString(),total);
	    		
	    		
	    		data.put(unitMap.get("name").toString(),powerAnalysisService.electricReportByUnitId(unitid, beginTime, endTime)  );
	    		resultData.add(data);
    		}
    	}

		logger.debug("===============exportExcel================");
        List<String> headerList = Lists.newArrayList();
        headerList.add("名称");
        headerList.add("电能值");
        
        List<List<String>> dataList = Lists.newArrayList();
        
        for (int i = 0; i < resultData.size(); i++) {
            MapEntity data = resultData.get(i);
            List<String> dataRowList = Lists.newArrayList();
            
            Set<Entry<String,Object>> es = data.entrySet();
            Iterator<Map.Entry<String, Object>> it = es.iterator();
            while(it.hasNext()) {
            	Map.Entry<String, Object> e1 = it.next();
            	String key = e1.getKey();
                dataRowList.add(key);
            	Object valueObj = e1.getValue();
            	JSONObject value = (JSONObject) JSONObject.toJSON(valueObj);
            	if(value != null)  {
            		dataRowList.add(value.getString("total"));	
            	} else {
            		dataRowList.add("0");
            	}
            	
            }
            dataList.add(dataRowList);
            
        }

        //计算完整路径
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String date =  LocalDate.now().format(formatter);
        String filePrefix = "/";
        String rootPath = filePrefix + ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1)+"../..";
        //String rootPath = "d:\\";
        logger.debug("create excel path:" + rootPath+"/export_excel/"+date);

        boolean isCreteFileFolder = FileCopyUtil.createDirectory(rootPath+"/export_excel/"+date);
        if(isCreteFileFolder) {
            logger.debug("创建成功");
        } else {
            logger.debug("创建失败");
        }

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH_mm_ss");

        String excelRelativePath = "/export_excel/"+ date + "/电能报表_" + date + "_" + LocalTime.now().format(formatter2)+".xlsx";
        String filePath = rootPath + excelRelativePath;

        logger.debug("文件全称");
        logger.debug(filePath);

        ExportExcel ee = null;
        try {
            ee = new ExportExcel("", headerList);
        }catch (Exception e) {
            logger.debug(e.toString());
        }
        //ExportExcel ee = new ExportExcel("", headerList);
        logger.debug("===============ExportExcel init ok================");
        double totalValue = 0.0;
        for (int i = 0; i < dataList.size(); i++) {
            Row row = ee.addRow();
            for (int j = 0; j < dataList.get(i).size(); j++) {
            	String cellValue = dataList.get(i).get(j);
            	if(cellValue != null) {
            		if(j!=0) {
            			totalValue += Double.parseDouble(dataList.get(i).get(j));
            			ee.addCell(row, j, String.format("%.2f",Double.parseDouble( dataList.get(i).get(j) )));	
            		} else {
            			ee.addCell(row, j, dataList.get(i).get(j));	
            		}
            	}
            	else
            		ee.addCell(row, j, "0");
            }
        }
        
        ee.writeFile(filePath);
        ee.dispose();
        logger.debug("Export success.");
            	
    	    	
    	
		return ServletUtils.buildRs(true, "电能报表",excelRelativePath);
	}
	
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//线损报表
	@RequestMapping("/getBreauOrgId")
	@ResponseBody
	public String getBreauOrgId(String beginTime, String endTime) {

		return ServletUtils.buildRs(true, "线损报表", powerAnalysisService.getBreauOrgId(beginTime,endTime));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//==========================下面的暂时不做=============================//
	
	
	//分时段用电
	@RequestMapping("/daypartData")
	@ResponseBody
	public String daypartData(String devIds, String beginTime, String endTime) {

		return ServletUtils.buildRs(true, "分时段分析", powerAnalysisService.daypartData(devIds, beginTime, endTime));
	}
	
	
	
	@RequestMapping("/selectCharges")
	@ResponseBody
	public String selectCharges() {

		return ServletUtils.buildRs(true, "查询费率", powerAnalysisService.selectCharges());
	}

	/*
	 * 付费率设置
	 */
	@RequestMapping(value = { "insertStarEnergy" })
	@ResponseBody
	public String insertStarEnergy(String jsonChanger) {

//		jsonChanger = "[{'startTime':'00:00:00','endTime':'05:30:00','state':'0','price':'0.85'},{'startTime':'05:30:00','endTime':'15:30:00','state':'0','price':'0.55'},{'startTime':'15:30:00','endTime':'23:59:59','state':'0','price':'0.66'}]";
		System.out.println("jsonChanger+=======" + jsonChanger);
		jsonChanger = jsonChanger.replace("&quot;", "'");
		JSONArray ja = JSONArray.parseArray(jsonChanger);
		try {
			powerAnalysisService.modifyCharges(ja);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "付费率设置", "");
		}
		return ServletUtils.buildRs(true, "付费率设置", "");
	}
	

	
	/*
	 * 
	 * 
	 * 
	 */
	@RequestMapping("/exportLineLossData")
	@ResponseBody
	public void exportLineLossData(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,String month) throws Exception {

		Double totalValue = monthTotalService.getMonthTotalValue(month);
		
		boolean loadFlag = false;
		String monthFirstDay = null;
		String monthLastDay = null;
		if(month != null) {
			monthFirstDay = month + "-01";
			
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate ldMFD = LocalDate.parse(monthFirstDay, fmt);
			LocalDate ldMLD = ldMFD.with(TemporalAdjusters.lastDayOfMonth());
			LocalDate today = LocalDate.now();
			if(ldMLD.isAfter(today)) {
				monthLastDay = today.format(fmt);
			} else {
				monthLastDay = ldMLD.format(fmt);
			}
			
			/*
			MapEntity loopValueMap = new MapEntity();
			List<MapEntity> sumValueList = powerAnalysisService.getLoopMonthConsumption(month);
			for(MapEntity sumValue : sumValueList) {
				Double value = Double.parseDouble(sumValue.get("sumValue").toString());
				String orgId = sumValue.get("orgId").toString();
				loopValueMap.put(orgId, value);
			}
			*/
			
			int type = 9;
			int level = 6;
			
			List<List<MapEntity>> channelLevelList = new ArrayList<>(); 
			//数据加载
			for(int i=0; i<level; i++) {
				//7是进线柜, 9+是回路
				if( i == 0)
					type = 7;
				else
					type = 8;
				
				List<MapEntity> loopChannelList = powerAnalysisService.getLoopChannel(String.valueOf(type+i));
				
				for(MapEntity loopChannel : loopChannelList) {
					
					String chId = loopChannel.get("chId").toString();
					String orgId = loopChannel.get("orgId").toString();
					
					Object maxDataObj = redisTempldate.opsForHash().get("LoopMaxData:"+monthLastDay, chId);
					if(maxDataObj == null && loadFlag == false) {
						final String finalMonthLastDay = monthLastDay;
						new Thread(new Runnable() {
							public void run() {
								loadLoopMonthMaxData(finalMonthLastDay);
							}
						}).start();
					} else {
						loadFlag = true;
					}
					JSONObject maxDataJSON = null;
					if(maxDataObj != null) {
						maxDataJSON = JSONObject.parseObject(maxDataObj.toString());
 					} else {
						maxDataJSON = new JSONObject();						
						maxDataJSON.put("history_value", 0);
						maxDataJSON.put("id", 0);
						maxDataJSON.put("ch_id", chId);
					}
					
					Object minDataObj = redisTempldate.opsForHash().get("LoopMinData:"+monthFirstDay, chId);
					if(minDataObj == null && loadFlag == false) {
						final String finalMonthLastDay = monthLastDay;
						new Thread(new Runnable() {
							public void run() {
								loadLoopMonthMinData(finalMonthLastDay);
							}
						}).start();
					} else {
						loadFlag = true;
					}
					
					JSONObject minDataJSON = null;
					if(minDataObj != null) {
						minDataJSON = JSONObject.parseObject(minDataObj.toString());
					} else {
						minDataJSON = new JSONObject();
						minDataJSON.put("history_value", 0);
						minDataJSON.put("id", 0);
						minDataJSON.put("ch_id", chId);
					}
					
					loopChannel.put("startValue", minDataJSON.get("history_value"));
					loopChannel.put("endValue", maxDataJSON.get("history_value"));
					
					double startValue = Double.parseDouble(minDataJSON.get("history_value").toString());
					double endValue = Double.parseDouble(maxDataJSON.get("history_value").toString());
					loopChannel.put("currentLoopEnergyConsumption", endValue - startValue);
					loadFlag = true;
					
					//Object valueObj = loopValueMap.get(orgId);
					Object valueObj = redisTempldate.opsForHash().get("LoopDataMonth:"+month, orgId);
					if(valueObj == null)
					{
						loopChannel.put("currentLoopEnergyConsumption", 0.0);
					}
					else 
					{
						loopChannel.put("currentLoopEnergyConsumption", valueObj);
					}
				}
				channelLevelList.add(loopChannelList);
				
			}

			List<MapEntity> dataTree = new ArrayList<>();
			if(channelLevelList.size() > 0) {
				dataTree = channelLevelList.get(0);	
			}
			List<MapEntity> tempList = new ArrayList<>();
			if(channelLevelList.size() > 0) {
				tempList = channelLevelList.get(0);	
			}
			//数据归位
			for(int i=0;i<channelLevelList.size();i++) {
				List<MapEntity> list = channelLevelList.get(i);
				if(i != 0) {

					for(MapEntity channel : list) {
						
						String orgCode = channel.get("parentIds").toString();
						for(MapEntity temp : tempList) {
							String tempCode = temp.get("parentIds").toString();
							if(orgCode.indexOf(tempCode) != -1) {
								Object children = temp.get("children");
								if(children != null) {
									List<MapEntity> childrenList = (List<MapEntity>) children;
									childrenList.add(channel);
									temp.put("children", childrenList);
								} else {
									List<MapEntity> childrenList = new ArrayList<>();
									childrenList.add(channel);
									temp.put("children", childrenList);
								}
							}
						}
						
					}
					
					tempList = list;
				}
			}
			
			
			Double totalLoopEnergyConsumption = 0.0;
			//获取一级总用电量
			for(MapEntity data : dataTree) {
				totalLoopEnergyConsumption += Double.parseDouble(data.get("currentLoopEnergyConsumption").toString());
			}
			
			//数据计算
			for(MapEntity data : dataTree) {
				data.put("childLoopEnergyConsumptionPercentage", 100);
				
				/*
				Object AdjustMapObj = redisTempldate.opsForHash().get("AdjustMap", month);
				if(AdjustMapObj != null) {
					JSONArray AdjustMapJSONArray = JSONArray.parseArray(AdjustMapObj.toString());
					AdjustMapthreadLocal.set(AdjustMapJSONArray);
				}
				*/
				data.put("adjustedConsumption", Double.parseDouble(data.get("currentLoopEnergyConsumption").toString()));
				Double currentLoopEnergyConsumption = Double.parseDouble(data.get("currentLoopEnergyConsumption").toString());
				Double diffPercentage = totalValue / totalLoopEnergyConsumption;
				data.put("adjustedConsumption2",  currentLoopEnergyConsumption * diffPercentage );
				data.put("diffPercentage", diffPercentage);
				fillLoopEnergyConsumptionData(data);
			}
			
			
			
			
			/* 线损回路层插入 start */
			String dataTreeStr = JSON.toJSONString(dataTree);
			JSONArray dataTree2 = JSON.parseArray(dataTreeStr);
			
			List<MapEntity> outLoopList = powerAnalysisService.getOutLoopList();
			
			for(int k=0;k<dataTree2.size();k++) {
				JSONObject temp = dataTree2.getJSONObject(k);
				temp.put("children", null);
			}
			for(int k=0;k<dataTree2.size();k++) {
				JSONObject temp = dataTree2.getJSONObject(k);
				String orgId = temp.get("orgId").toString();
				for(MapEntity outLoop : outLoopList) {
					String parentId = outLoop.get("parentId").toString();
					if(parentId.equals(orgId)) {
						JSONArray children = temp.getJSONArray("children");
						if(children != null) {
							JSONObject outLoopJOBJ = JSON.parseObject(JSON.toJSONString(outLoop));
							children.add(outLoopJOBJ);
							temp.put("children", children);
						} else {
							children = new JSONArray();
							JSONObject outLoopJOBJ = JSON.parseObject(JSON.toJSONString(outLoop));
							children.add(outLoopJOBJ);
							temp.put("children", children);
						}
					}
				}
			}
			
			//for(MapEntity temp : dataTree) {
			for(int l=0;l<dataTree.size();l++) {
				MapEntity temp = dataTree.get(l);
				JSONObject temp2 = dataTree2.getJSONObject(l);
				Object children = temp.get("children");
				List<MapEntity> childrenList = (List<MapEntity>) children;
				JSONArray childrenList2 = temp2.getJSONArray("children");
				
				int clsize2 = childrenList2.size();
				for(int i=0;i<clsize2;i++) {
					JSONObject c2 = childrenList2.getJSONObject(i);
					String orgId = c2.getString("orgId");
					
					int clsize = childrenList.size();
					for(int j=0;j<clsize;j++) {
						MapEntity c = childrenList.get(j);
						String parentId = c.get("parentId").toString();
						if(parentId.equals(orgId)) {
							JSONArray c2Children = c2.getJSONArray("children");
							if(c2Children != null) {
								String cStr = JSON.toJSONString(c);
								JSONObject cDeepClone = JSON.parseObject(cStr);
								c2Children.add(cDeepClone);
								c2.put("children", c2Children);
							} else {
								c2Children = new JSONArray();
								String cStr = JSON.toJSONString(c);
								JSONObject cDeepClone = JSON.parseObject(cStr);
								c2Children.add(cDeepClone);
								c2.put("children", c2Children);
							}
						}
					}
					
				}
			}
			
			/* 线损回路层插入 end */
			
			
			List<MapEntity> pdfList = powerAnalysisService.getPdfList();
			for(MapEntity pdf : pdfList) {
				
				String orgCode = pdf.get("parentIds").toString();
				pdf.put("childLoopEnergyConsumptionPercentage", 100);
				
				for(int k=0;k<dataTree2.size();k++) {
					JSONObject temp = dataTree2.getJSONObject(k);
					String tempCode = temp.getString("parentIds");
					if(tempCode.indexOf(orgCode) != -1) {
						Object children = pdf.get("children");
						if(children != null) {
							JSONArray childrenList = (JSONArray) children;
							childrenList.add(temp);
							pdf.put("children", childrenList);
						} else {
							JSONArray childrenList = new JSONArray();
							childrenList.add(temp);
							pdf.put("children", childrenList);
						}
					}
				}
			}
			
			
			
			powerAnalysisService.exportLineLossData(httpServletRequest,httpServletResponse,pdfList,month);
		}
	}



	
	
	/*
	 * 导出线损,用电单位excel
	 */
	@RequestMapping("/exportBreauOrgId")
	@ResponseBody
	public void exportBreauOrgId(String month,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
		
		
		Map<String,JSONObject> unitTotalEnergyConsumption = new HashMap<>();
		if(month != null) {

			List<MapEntity> unitLoopList = powerAnalysisService.getUnitList();
			for(MapEntity unitLoop : unitLoopList) {
				String unitId = unitLoop.get("id").toString();
				String unitName = unitLoop.get("name").toString();
				String orderNo = unitLoop.get("order_no").toString();
				String orgId =  "";
				if(unitLoop.get("loop_org_id") != null) {
					orgId = unitLoop.get("loop_org_id").toString();
				}

				String sonLoopIds = "";
				double proportion = 1.0;
				MapEntity unitLoopOrg = loadUnitAdjustProportionService.getUnitLoopOrg(unitId,orgId);
				if(unitLoopOrg != null) {
					proportion = Double.parseDouble(unitLoopOrg.get("proportion").toString());
					sonLoopIds = unitLoopOrg.get("son_loop_ids").toString();
				}
				
				//获取子回路
				Double sonLoopConsumptionSum = 0.0;
				Double sonLoopConsumptionAdjustSum = 0.0;
				String[] loopArr = sonLoopIds.split(",");
				for(String loopId : loopArr) {
					if(!loopId.equals("")) {
						ConsumptionWithLoss consumptionWithLoss = new ConsumptionWithLoss();
						consumptionWithLoss.setLoopOrgId(loopId);
						consumptionWithLoss.setMonth(month);
						consumptionWithLoss = consumptionWithLossService.getLossDataByLoopIdAndMonth(consumptionWithLoss);
						sonLoopConsumptionSum += consumptionWithLoss.getConsumption();
						sonLoopConsumptionAdjustSum += consumptionWithLoss.getConsumptionWithLoss();
					}
				}
				
				
				ConsumptionWithLoss consumptionWithLoss = new ConsumptionWithLoss();
				consumptionWithLoss.setLoopOrgId(orgId);
				consumptionWithLoss.setMonth(month);
				consumptionWithLoss = consumptionWithLossService.getLossDataByLoopIdAndMonth(consumptionWithLoss);
				
				JSONObject unitJSON = new JSONObject();
				unitJSON.put("unitId", unitId);
				unitJSON.put("unitName", unitName);
				unitJSON.put("orderNo", orderNo);
				if(consumptionWithLoss != null) {
					unitJSON.put("consumption", (consumptionWithLoss.getConsumption() - sonLoopConsumptionSum) * proportion  );
					unitJSON.put("adjustedConsumption", (consumptionWithLoss.getConsumptionWithLoss() - sonLoopConsumptionAdjustSum) * proportion   );
				} else {
					unitJSON.put("consumption", 0.0);
					unitJSON.put("adjustedConsumption", 0.0);
				}
				
				unitTotalEnergyConsumption.put(unitId, unitJSON);
			}
			
			
			Set<String> keySet = unitTotalEnergyConsumption.keySet();
			for(Iterator<String> keyItr = keySet.iterator();keyItr.hasNext();) {
				String unitId = keyItr.next();
				JSONObject unitJSON = unitTotalEnergyConsumption.get(unitId);
				
				ElecUnitStrategy eus = new ElecUnitStrategy();
				eus.setUnitId(Long.parseLong(unitId));
				eus.setMonth(month);
				eus = elecUnitStrategyService.get(eus);
				if(eus != null) {
					unitJSON.put("adjust", eus.getRealValue());
					unitJSON.put("totalConsumption", unitJSON.getDouble("adjustedConsumption") + eus.getRealValue());
				}
				else { 
					unitJSON.put("adjust", 0);
					unitJSON.put("totalConsumption", unitJSON.getDouble("adjustedConsumption"));
				}
				
			}
			
			
			List<JSONObject> values = new ArrayList<>();
			Set<String> keySet2 = unitTotalEnergyConsumption.keySet();
			for(Iterator<String> keyItr = keySet2.iterator();keyItr.hasNext();) {
				String unitId = keyItr.next();
				JSONObject unitJSON = unitTotalEnergyConsumption.get(unitId);
				values.add(unitJSON);
			}
			
			
			  final Comparator<JSONObject> order =new Comparator<JSONObject>() {
			        public int compare(JSONObject n1, JSONObject n2) {
			        	int orderNo1 = n1.getInteger("orderNo");
			        	int orderNo2 = n2.getInteger("orderNo");
			        	return orderNo1 - orderNo2;
			        }
			    };
			Collections.sort(values, order);
			
			powerAnalysisService.exportBreauOrgId(httpServletRequest, httpServletResponse,values);
			
		}
	}




	// 导出分时段数据
	@RequestMapping("/exportDaypartDataRport")
	@ResponseBody
	public void exportDaypartDataRport(String devIds, String beginTime, String endTime,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
		/*
		powerAnalysisService.exportDaypartDataRport(httpServletRequest, httpServletResponse,
				powerAnalysisService.daypartData(devIds, beginTime, endTime));
				*/
	}
	
	
	
	/*
	 * 获取月总电能
	 */
	@RequestMapping("/getMonthTotal")
	@ResponseBody
	public String getMonthTotal(String month) {
		Double totalValue = monthTotalService.getMonthTotalValue(month);
		return ServletUtils.buildRs(true, "获取成功", totalValue);
	}
	
	/*
	 * 设置月总电能
	 */
	@RequestMapping("/setMonthTotal")
	@ResponseBody
	public String setMonthTotal(String month,String value) {
		monthTotalService.setMonthTotalValue(month, value);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000);
					startLoadAdjustedConsumption(month);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return ServletUtils.buildRs(true, "设置成功!", null);
	}
	
	/*
	 * 线损能耗月报
	 */
	//线损报表
	@RequestMapping("/getLineLossData")
	@ResponseBody
	public String getLineLossData(String month) {
		
		Double totalValue = monthTotalService.getMonthTotalValue(month);
		
		boolean loadFlag = false;
		String monthFirstDay = null;
		String monthLastDay = null;
		if(month != null) {
			monthFirstDay = month + "-01";
			
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate ldMFD = LocalDate.parse(monthFirstDay, fmt);
			LocalDate ldMLD = ldMFD.with(TemporalAdjusters.lastDayOfMonth());
			LocalDate today = LocalDate.now();
			if(ldMLD.isAfter(today)) {
				monthLastDay = today.format(fmt);
			} else {
				monthLastDay = ldMLD.format(fmt);
			}
			
			/*
			MapEntity loopValueMap = new MapEntity();
			List<MapEntity> sumValueList = powerAnalysisService.getLoopMonthConsumption(month);
			for(MapEntity sumValue : sumValueList) {
				Double value = Double.parseDouble(sumValue.get("sumValue").toString());
				String orgId = sumValue.get("orgId").toString();
				loopValueMap.put(orgId, value);
			}
			*/
			
			int type = 9;
			int level = 6;
			
			List<List<MapEntity>> channelLevelList = new ArrayList<>(); 
			//数据加载
			for(int i=0; i<level; i++) {
				//7是进线柜, 9+是回路
				if( i == 0)
					type = 7;
				else
					type = 8;
				
				List<MapEntity> loopChannelList = powerAnalysisService.getLoopChannel(String.valueOf(type+i));
				
				for(MapEntity loopChannel : loopChannelList) {
					
					String chId = loopChannel.get("chId").toString();
					String orgId = loopChannel.get("orgId").toString();
					
					Object maxDataObj = redisTempldate.opsForHash().get("LoopMaxData:"+monthLastDay, chId);
					if(maxDataObj == null && loadFlag == false) {
						final String finalMonthLastDay = monthLastDay;
						new Thread(new Runnable() {
							public void run() {
								loadLoopMonthMaxData(finalMonthLastDay);
							}
						}).start();
					} else {
						loadFlag = true;
					}
					JSONObject maxDataJSON = null;
					if(maxDataObj != null) {
						maxDataJSON = JSONObject.parseObject(maxDataObj.toString());
 					} else {
						maxDataJSON = new JSONObject();						
						maxDataJSON.put("history_value", 0);
						maxDataJSON.put("id", 0);
						maxDataJSON.put("ch_id", chId);
					}
					
					Object minDataObj = redisTempldate.opsForHash().get("LoopMinData:"+monthFirstDay, chId);
					if(minDataObj == null && loadFlag == false) {
						final String finalMonthFirstDay = monthFirstDay;
						new Thread(new Runnable() {
							public void run() {
								loadLoopMonthMinData(finalMonthFirstDay);
							}
						}).start();
					} else {
						loadFlag = true;
					}
					
					JSONObject minDataJSON = null;
					if(minDataObj != null) {
						minDataJSON = JSONObject.parseObject(minDataObj.toString());
					} else {
						minDataJSON = new JSONObject();
						minDataJSON.put("history_value", 0);
						minDataJSON.put("id", 0);
						minDataJSON.put("ch_id", chId);
					}
					
					loopChannel.put("startValue", minDataJSON.get("history_value"));
					loopChannel.put("endValue", maxDataJSON.get("history_value"));
					
					double startValue = Double.parseDouble(minDataJSON.get("history_value").toString());
					double endValue = Double.parseDouble(maxDataJSON.get("history_value").toString());
					loopChannel.put("currentLoopEnergyConsumption", endValue - startValue);
					loadFlag = true;
					
					//Object valueObj = loopValueMap.get(orgId);
					Object valueObj = redisTempldate.opsForHash().get("LoopDataMonth:"+month, orgId);
					if(valueObj == null)
					{
						loopChannel.put("currentLoopEnergyConsumption", 0.0);
					}
					else 
					{
						loopChannel.put("currentLoopEnergyConsumption", valueObj);
					}
				}
				channelLevelList.add(loopChannelList);
				
			}

			List<MapEntity> dataTree = new ArrayList<>();
			if(channelLevelList.size() > 0) {
				dataTree = channelLevelList.get(0);
			}
			List<MapEntity> tempList = new ArrayList<>();
			if(channelLevelList.size() > 0) {
				tempList = channelLevelList.get(0);	
			}
			//数据归位
			for(int i=0;i<channelLevelList.size();i++) {
				List<MapEntity> list = channelLevelList.get(i);
				if(i != 0) {

					for(MapEntity channel : list) {
						
						String orgCode = channel.get("parentIds").toString();
						for(MapEntity temp : tempList) {
							String tempCode = temp.get("parentIds").toString();
							if(orgCode.indexOf(tempCode) != -1) {
								Object children = temp.get("children");
								if(children != null) {
									List<MapEntity> childrenList = (List<MapEntity>) children;
									childrenList.add(channel);
									temp.put("children", childrenList);
								} else {
									List<MapEntity> childrenList = new ArrayList<>();
									childrenList.add(channel);
									temp.put("children", childrenList);
								}
							}
						}
						
					}
					
					tempList = list;
				}
			}
			
			Double totalLoopEnergyConsumption = 0.0;
			//获取一级总用电量
			for(MapEntity data : dataTree) {
				totalLoopEnergyConsumption += Double.parseDouble(data.get("currentLoopEnergyConsumption").toString());
			}
			
			//数据计算
			for(MapEntity data : dataTree) {
				data.put("childLoopEnergyConsumptionPercentage", 100);
				/*
				Object AdjustMapObj = redisTempldate.opsForHash().get("AdjustMap", month);
				if(AdjustMapObj != null) {
					JSONArray AdjustMapJSONArray = JSONArray.parseArray(AdjustMapObj.toString());
					AdjustMapthreadLocal.set(AdjustMapJSONArray);
				}
				*/
				data.put("adjustedConsumption", Double.parseDouble(data.get("currentLoopEnergyConsumption").toString()));
				Double currentLoopEnergyConsumption = Double.parseDouble(data.get("currentLoopEnergyConsumption").toString());
				Double diffPercentage = totalValue / totalLoopEnergyConsumption;
				data.put("adjustedConsumption2",  currentLoopEnergyConsumption * diffPercentage );
				data.put("diffPercentage", diffPercentage);
				fillLoopEnergyConsumptionData(data);
			}
			
			logger.debug("dataTree JSON 00:" + JSON.toJSONString(dataTree));
			
			/* 线损回路层插入 start */
			String dataTreeStr = JSON.toJSONString(dataTree);
			JSONArray dataTree2 = JSON.parseArray(dataTreeStr);
			
			List<MapEntity> outLoopList = powerAnalysisService.getOutLoopList();
			
			for(int k=0;k<dataTree2.size();k++) {
				JSONObject temp = dataTree2.getJSONObject(k);
				temp.put("children", null);
			}
			for(int k=0;k<dataTree2.size();k++) {
				JSONObject temp = dataTree2.getJSONObject(k);
				String orgId = temp.get("orgId").toString();
				for(MapEntity outLoop : outLoopList) {
					String parentId = outLoop.get("parentId").toString();
					if(parentId.equals(orgId)) {
						JSONArray children = temp.getJSONArray("children");
						if(children != null) {
							JSONObject outLoopJOBJ = JSON.parseObject(JSON.toJSONString(outLoop));
							children.add(outLoopJOBJ);
							temp.put("children", children);
						} else {
							children = new JSONArray();
							JSONObject outLoopJOBJ = JSON.parseObject(JSON.toJSONString(outLoop));
							children.add(outLoopJOBJ);
							temp.put("children", children);
						}
					}
				}
			}
			
			logger.debug("dataTree JSON 011:" + JSON.toJSONString(dataTree));
			logger.debug("dataTree JSON 012:" + JSON.toJSONString(dataTree2));
			
			//for(MapEntity temp : dataTree) {
			for(int l=0;l<dataTree.size();l++) {
				MapEntity temp = dataTree.get(l);
				JSONObject temp2 = dataTree2.getJSONObject(l);
				Object children = temp.get("children");
				List<MapEntity> childrenList = (List<MapEntity>) children;
				JSONArray childrenList2 = temp2.getJSONArray("children");
				
				int clsize2 = childrenList2.size();
				for(int i=0;i<clsize2;i++) {
					JSONObject c2 = childrenList2.getJSONObject(i);
					String orgId = c2.getString("orgId");
					
					int clsize = childrenList.size();
					for(int j=0;j<clsize;j++) {
						MapEntity c = childrenList.get(j);
						String parentId = c.get("parentId").toString();
						if(parentId.equals(orgId)) {
							JSONArray c2Children = c2.getJSONArray("children");
							if(c2Children != null) {
								String cStr = JSON.toJSONString(c);
								JSONObject cDeepClone = JSON.parseObject(cStr);
								c2Children.add(cDeepClone);
								c2.put("children", c2Children);
							} else {
								c2Children = new JSONArray();
								String cStr = JSON.toJSONString(c);
								JSONObject cDeepClone = JSON.parseObject(cStr);
								c2Children.add(cDeepClone);
								c2.put("children", c2Children);
							}
						}
					}
					
				}
			}
			
			logger.debug("dataTree JSON 021:" + JSON.toJSONString(dataTree));
			logger.debug("dataTree JSON 022:" + JSON.toJSONString(dataTree2));
			/* 线损回路层插入 end */
			
			
			
			List<MapEntity> pdfList = powerAnalysisService.getPdfList();
			for(MapEntity pdf : pdfList) {
				
				String orgCode = pdf.get("parentIds").toString();
				pdf.put("childLoopEnergyConsumptionPercentage", 100);
				
				for(int k=0;k<dataTree2.size();k++) {
					JSONObject temp = dataTree2.getJSONObject(k);
					String tempCode = temp.getString("parentIds");
					if(tempCode.indexOf(orgCode) != -1) {
						Object children = pdf.get("children");
						if(children != null) {
							JSONArray childrenList = (JSONArray) children;
							childrenList.add(temp);
							pdf.put("children", childrenList);
						} else {
							JSONArray childrenList = new JSONArray();
							childrenList.add(temp);
							pdf.put("children", childrenList);
						}
					}
				}
			}
			
			logger.debug("pdfList JSON:" + JSON.toJSONString(pdfList));
			
			return ServletUtils.buildRs(true, "线损报表", pdfList);
		}
		return ServletUtils.buildRs(true, "线损报表,月份参数为空", null);
	}
	
	public void fillLoopEnergyConsumptionData(MapEntity node) {
		
		double currentLoopEnergyConsumption = Double.parseDouble(node.get("currentLoopEnergyConsumption").toString());
		
		/*
		//计算当前回路整后用电量=========================
		String orgId = node.get("orgId").toString();
		JSONArray adjustMapJSONArray = AdjustMapthreadLocal.get();
		if(adjustMapJSONArray != null) {
			Object unitObjects = redisTempldate.opsForHash().get("AdjustLoopMap", orgId);	
			if(unitObjects != null) {
				JSONArray unitArray = JSONArray.parseArray(unitObjects.toString());
				double totalAdjust = 0.0;
				for(int i=0; i<unitArray.size(); i++) {
					JSONObject unit = unitArray.getJSONObject(i);
					logger.debug(unit.toJSONString());
					for(int j=0;j<adjustMapJSONArray.size();j++) {
						JSONObject adjustValue = adjustMapJSONArray.getJSONObject(j);
						int unitId = adjustValue.getInteger("unit_id");
						if(unitId == unit.getIntValue("unit_id")) {
							totalAdjust += (adjustValue.getDoubleValue("real_value") * unit.getDoubleValue("proportion"));
						}
					}
				}
				node.put("adjustedConsumption", currentLoopEnergyConsumption + totalAdjust);
			} else {
				node.put("adjustedConsumption", currentLoopEnergyConsumption);
			}
		} else {
			node.put("adjustedConsumption", currentLoopEnergyConsumption);
		}
		//============================================
		*/

		
		Object children = node.get("children");
		List<MapEntity> childrenList = (List<MapEntity>) children;
		if(children != null && childrenList.size() > 0) {
			
			logger.debug("fillLoopEnergyConsumptionData children > 0");

			double childSum = 0;
			for(MapEntity childrenNode : childrenList) {
				childrenNode.put("parentLoopEnergyConsumption", currentLoopEnergyConsumption);
				double childLoopEnergyConsumption = Double.parseDouble(childrenNode.get("currentLoopEnergyConsumption").toString());
				childSum += childLoopEnergyConsumption;
				//childrenNode.put("percentageOfParent", (childLoopEnergyConsumption / currentLoopEnergyConsumption) * 100 );
			}
			//下级支路能耗
			node.put("childLoopEnergyConsumption", childSum);
			//当前支路与下级差值
			node.put("energyConsumptionDiffWithChild", currentLoopEnergyConsumption - childSum);
			//损耗占比百分比
			if(currentLoopEnergyConsumption != 0) {
				node.put("lossPercentage",  ((currentLoopEnergyConsumption - childSum) / currentLoopEnergyConsumption) * 100 );
			} else {
				node.put("lossPercentage",  "");
			}


			
			for(MapEntity childrenNode : childrenList) {
				
				double childLoopEnergyConsumption = Double.parseDouble(childrenNode.get("currentLoopEnergyConsumption").toString());
				
				//下级能耗占百分比
				if(childLoopEnergyConsumption != 0 && currentLoopEnergyConsumption != 0) {
					childrenNode.put("childLoopEnergyConsumptionPercentage", (childLoopEnergyConsumption / childSum) * 100 );
				} else {
					childrenNode.put("childLoopEnergyConsumptionPercentage", 0);
				}
				
				
				//损耗分摊
				if(currentLoopEnergyConsumption != 0) {
					logger.debug("orgName:"+childrenNode.get("orgName") );
					double loss = (currentLoopEnergyConsumption - childSum) * (childLoopEnergyConsumption/childSum);
					
					logger.debug("loss =  (currentLoopEnergyConsumption - childSum) * (childLoopEnergyConsumption/currentLoopEnergyConsumption)");
					logger.debug("("+currentLoopEnergyConsumption+" - " + childSum+ ") * (" + childLoopEnergyConsumption + "/" + childSum + ") = " + String.valueOf( (currentLoopEnergyConsumption - childSum) * (childLoopEnergyConsumption/childSum) ) );
					
					childrenNode.put("lossAllocation",  loss);
					if(loss > 0) {
						childrenNode.put("adjustedConsumption", childLoopEnergyConsumption + loss);
					} else {
						childrenNode.put("adjustedConsumption", childLoopEnergyConsumption);
					}
				} else {
					childrenNode.put("lossAllocation", "");
					childrenNode.put("adjustedConsumption", childLoopEnergyConsumption);
				}		
				
				//差异值与整后用电量2
				double adjustedConsumption2 = Double.parseDouble(node.get("adjustedConsumption2").toString());
				Double diffPercentage = adjustedConsumption2 / childSum;
				childrenNode.put("adjustedConsumption2",  childLoopEnergyConsumption * diffPercentage );
				childrenNode.put("diffPercentage", diffPercentage);
				
				fillLoopEnergyConsumptionData(childrenNode);
			}
			
		} else {
			
			logger.debug("fillLoopEnergyConsumptionData children = 0");
			
		}
	}
	
	public double getChildrenLoopEnergyConsumption(MapEntity node) {
		return 0;
	}
	
	private void loadLoopMonthMaxData(String month) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate searchMonth = LocalDate.parse(month,formatter);
		LocalDate firstDay = searchMonth.with(TemporalAdjusters.firstDayOfMonth()); // 获取当前月的第一天
		LocalDate lastDay = searchMonth.with(TemporalAdjusters.lastDayOfMonth()); // 获取当前月的最后一天
		LocalDate today = LocalDate.now();
		if(lastDay.isAfter(today))
			lastDay = today;
		
		String firstDayStr = firstDay.format(formatter);
		firstDayStr += " " + "00:00:00";
		String lastDayStr = lastDay.format(formatter);
		lastDayStr += " " + "23:59:59";
		
		List<MapEntity> lineLossL0List = powerAnalysisService.getLineLossMaxData(firstDayStr, lastDayStr, "7");
		for(MapEntity lineLossL0 : lineLossL0List) {
			String chId = lineLossL0.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL0);
			redisTempldate.opsForHash().put("LoopMaxData:"+month, chId, jsonValue.toJSONString());	
		}
		
		List<MapEntity> lineLossL1List = powerAnalysisService.getLineLossMaxData(firstDayStr, lastDayStr, "9");
		for(MapEntity lineLossL1 : lineLossL1List) {
			String chId = lineLossL1.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL1);
			redisTempldate.opsForHash().put("LoopMaxData:"+month, chId, jsonValue.toJSONString());	
		}
		
		List<MapEntity> lineLossL2List = powerAnalysisService.getLineLossMaxData(firstDayStr, lastDayStr, "10");
		for(MapEntity lineLossL2 : lineLossL2List) {
			String chId = lineLossL2.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL2);
			redisTempldate.opsForHash().put("LoopMaxData:"+month, chId, jsonValue.toJSONString());		
		}
		
		List<MapEntity> lineLossL3List = powerAnalysisService.getLineLossMaxData(firstDayStr, lastDayStr, "11");
		for(MapEntity lineLossL3 : lineLossL3List) {
			String chId = lineLossL3.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL3);
			redisTempldate.opsForHash().put("LoopMaxData:"+month, chId, jsonValue.toJSONString());		
		}
		
		List<MapEntity> lineLossL4List = powerAnalysisService.getLineLossMaxData(firstDayStr, lastDayStr, "12");
		for(MapEntity lineLossL4 : lineLossL4List) {
			String chId = lineLossL4.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL4);
			redisTempldate.opsForHash().put("LoopMaxData:"+month, chId, jsonValue.toJSONString());		
		}
		
		List<MapEntity> lineLossL5List = powerAnalysisService.getLineLossMaxData(firstDayStr, lastDayStr, "13");
		for(MapEntity lineLossL5 : lineLossL5List) {
			String chId = lineLossL5.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL5);
			redisTempldate.opsForHash().put("LoopMaxData:"+month, chId, jsonValue.toJSONString());		
		}
    }
    
    private void loadLoopMonthMinData(String month) {
		
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	LocalDate searchMonth = LocalDate.parse(month,formatter);
		LocalDate firstDay = searchMonth.with(TemporalAdjusters.firstDayOfMonth()); // 获取当月的第一天
		LocalDate lastDay = searchMonth.with(TemporalAdjusters.lastDayOfMonth()); // 获取当月的最后一天
		
		String firstDayStr = firstDay.format(formatter);
		firstDayStr += " " + "00:00:00";
		String lastDayStr = lastDay.format(formatter);
		lastDayStr += " " + "23:59:59";
		
		List<MapEntity> lineLossL0List = powerAnalysisService.getLineLossMinData(firstDayStr, lastDayStr, "7");
		for(MapEntity lineLossL0 : lineLossL0List) {
			String chId = lineLossL0.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL0);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());	
		}
		
		List<MapEntity> lineLossL1List = powerAnalysisService.getLineLossMinData(firstDayStr, lastDayStr, "9");
		for(MapEntity lineLossL1 : lineLossL1List) {
			String chId = lineLossL1.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL1);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());	
		}
		
		List<MapEntity> lineLossL2List = powerAnalysisService.getLineLossMinData(firstDayStr, lastDayStr, "10");
		for(MapEntity lineLossL2 : lineLossL2List) {
			String chId = lineLossL2.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL2);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
		List<MapEntity> lineLossL3List = powerAnalysisService.getLineLossMinData(firstDayStr, lastDayStr, "11");
		for(MapEntity lineLossL3 : lineLossL3List) {
			String chId = lineLossL3.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL3);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
		List<MapEntity> lineLossL4List = powerAnalysisService.getLineLossMinData(firstDayStr, lastDayStr, "12");
		for(MapEntity lineLossL4 : lineLossL4List) {
			String chId = lineLossL4.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL4);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
		List<MapEntity> lineLossL5List = powerAnalysisService.getLineLossMinData(firstDayStr, lastDayStr, "13");
		for(MapEntity lineLossL5 : lineLossL5List) {
			String chId = lineLossL5.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL5);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
		
		
		

		String preMonthTodayDatetime = searchMonth.minusMonths(1).format(formatter);
		LocalDate searchMonth2 = LocalDate.parse(preMonthTodayDatetime,formatter);
		LocalDate preMonthFirstDay = searchMonth2.with(TemporalAdjusters.firstDayOfMonth()); // 获取上月的第一天
		LocalDate preMonthLastDay = searchMonth2.with(TemporalAdjusters.lastDayOfMonth()); // 获取上月的最后一天
		

		String preMonthFirstDayStr = preMonthFirstDay.format(formatter);
		preMonthFirstDayStr += " " + "00:00:00";
		String preMonthLastDayStr = preMonthLastDay.format(formatter);
		preMonthLastDayStr += " " + "23:59:59";
		
		lineLossL0List = powerAnalysisService.getLineLossMaxData(preMonthFirstDayStr, preMonthLastDayStr, "7");
		for(MapEntity lineLossL0 : lineLossL0List) {
			String chId = lineLossL0.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL0);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());	
		}
		
		lineLossL1List = powerAnalysisService.getLineLossMaxData(preMonthFirstDayStr, preMonthLastDayStr, "9");
		for(MapEntity lineLossL1 : lineLossL1List) {
			String chId = lineLossL1.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL1);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());	
		}
		
		lineLossL2List = powerAnalysisService.getLineLossMaxData(preMonthFirstDayStr, preMonthLastDayStr, "10");
		for(MapEntity lineLossL2 : lineLossL2List) {
			String chId = lineLossL2.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL2);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
		lineLossL3List = powerAnalysisService.getLineLossMaxData(preMonthFirstDayStr, preMonthLastDayStr, "11");
		for(MapEntity lineLossL3 : lineLossL3List) {
			String chId = lineLossL3.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL3);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
		lineLossL4List = powerAnalysisService.getLineLossMaxData(preMonthFirstDayStr, preMonthLastDayStr, "12");
		for(MapEntity lineLossL4 : lineLossL4List) {
			String chId = lineLossL4.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL4);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
		lineLossL5List = powerAnalysisService.getLineLossMaxData(firstDayStr, lastDayStr, "13");
		for(MapEntity lineLossL5 : lineLossL5List) {
			String chId = lineLossL5.get("ch_id").toString();
			JSONObject jsonValue = new JSONObject(lineLossL5);
			redisTempldate.opsForHash().put("LoopMinData:"+firstDay, chId, jsonValue.toJSONString());		
		}
		
    	
    	
    	
    	
    	
    	
	}
	
    
	/*
	 * 线损能耗月报
	 * 用电单位查看
	 */
	@RequestMapping("/getLineLossDataByUnit")
	@ResponseBody
	public String getLineLossDataByUnit(String month) {
		Map<String,JSONObject> unitTotalEnergyConsumption = new HashMap<>();
		if(month != null) {
			
			List<MapEntity> unitLoopList = powerAnalysisService.getUnitList();
			for(MapEntity unitLoop : unitLoopList) {
				
				String unitId = unitLoop.get("id").toString();
				String unitName = unitLoop.get("name").toString();
				String orderNo = unitLoop.get("order_no").toString();
				String orgId =  "";
				if(unitLoop.get("loop_org_id") != null) {
					orgId = unitLoop.get("loop_org_id").toString();
				}

				String sonLoopIds = "";
				double proportion = 1.0;
				MapEntity unitLoopOrg = loadUnitAdjustProportionService.getUnitLoopOrg(unitId,orgId);
				if(unitLoopOrg != null) {
					proportion = Double.parseDouble(unitLoopOrg.get("proportion").toString());
					sonLoopIds = unitLoopOrg.get("son_loop_ids").toString();
				}
				
				//获取子回路
				Double sonLoopConsumptionSum = 0.0;
				Double sonLoopConsumptionAdjustSum = 0.0;
				String[] loopArr = sonLoopIds.split(",");
				for(String loopId : loopArr) {
					if(!loopId.equals("")) {
						ConsumptionWithLoss consumptionWithLoss = new ConsumptionWithLoss();
						consumptionWithLoss.setLoopOrgId(loopId);
						consumptionWithLoss.setMonth(month);
						consumptionWithLoss = consumptionWithLossService.getLossDataByLoopIdAndMonth(consumptionWithLoss);
						sonLoopConsumptionSum += consumptionWithLoss.getConsumption();
						sonLoopConsumptionAdjustSum += consumptionWithLoss.getConsumptionWithLoss();
					}
				}
				
				
				ConsumptionWithLoss consumptionWithLoss = new ConsumptionWithLoss();
				consumptionWithLoss.setLoopOrgId(orgId);
				consumptionWithLoss.setMonth(month);
				consumptionWithLoss = consumptionWithLossService.getLossDataByLoopIdAndMonth(consumptionWithLoss);
				
				JSONObject unitJSON = new JSONObject();
				unitJSON.put("unitId", unitId);
				unitJSON.put("unitName", unitName);
				unitJSON.put("orderNo", orderNo);
				if(consumptionWithLoss != null) {
					unitJSON.put("consumption", (consumptionWithLoss.getConsumption() - sonLoopConsumptionSum) * proportion  );
					unitJSON.put("adjustedConsumption", (consumptionWithLoss.getConsumptionWithLoss() - sonLoopConsumptionAdjustSum) * proportion   );
				} else {
					unitJSON.put("consumption", 0.0);
					unitJSON.put("adjustedConsumption", 0.0);
				}
				
				unitTotalEnergyConsumption.put(unitId, unitJSON);
			}
			
			
			Set<String> keySet = unitTotalEnergyConsumption.keySet();
			for(Iterator<String> keyItr = keySet.iterator();keyItr.hasNext();) {
				String unitId = keyItr.next();
				JSONObject unitJSON = unitTotalEnergyConsumption.get(unitId);
				
				ElecUnitStrategy eus = new ElecUnitStrategy();
				eus.setUnitId(Long.parseLong(unitId));
				eus.setMonth(month);
				eus = elecUnitStrategyService.get(eus);
				if(eus != null) {
					unitJSON.put("adjust", eus.getRealValue());
					unitJSON.put("totalConsumption", unitJSON.getDouble("adjustedConsumption") + eus.getRealValue());
				}
				else { 
					unitJSON.put("adjust", 0);
					unitJSON.put("totalConsumption", unitJSON.getDouble("adjustedConsumption"));
				}
				
			}
			
			
			List<JSONObject> values = new ArrayList<>();
			Set<String> keySet2 = unitTotalEnergyConsumption.keySet();
			for(Iterator<String> keyItr = keySet2.iterator();keyItr.hasNext();) {
				String unitId = keyItr.next();
				JSONObject unitJSON = unitTotalEnergyConsumption.get(unitId);
				values.add(unitJSON);
			}
			
			
			  final Comparator<JSONObject> order =new Comparator<JSONObject>() {
			        public int compare(JSONObject n1, JSONObject n2) {
			        	int orderNo1 = n1.getInteger("orderNo");
			        	int orderNo2 = n2.getInteger("orderNo");
			        	return orderNo1 - orderNo2;
			        }
			    };
			Collections.sort(values, order);
			
			return ServletUtils.buildRs(true, "线损报表,用电单位查看", values);
		} else {
			return ServletUtils.buildRs(true, "线损报表,用电单位查看,month参数错误", null);	
		}
		
	}

	
	
	
	
	@Autowired
	LoadUnitAdjustProportionService loadUnitAdjustProportionService;
	@RequestMapping("/startLoadAdjustedConsumption")
	@ResponseBody
    public String startLoadAdjustedConsumption(String month) {
		Double totalValue = monthTotalService.getMonthTotalValue(month);
		
		boolean loadFlag = false;
		String monthFirstDay = null;
		String monthLastDay = null;
		if(month != null) {
			monthFirstDay = month + "-01";
			
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate ldMFD = LocalDate.parse(monthFirstDay, fmt);
			LocalDate ldMLD = ldMFD.with(TemporalAdjusters.lastDayOfMonth());
			LocalDate today = LocalDate.now();
			if(ldMLD.isAfter(today)) {
				monthLastDay = today.format(fmt);
			} else {
				monthLastDay = ldMLD.format(fmt);
			}
			
			/*
			MapEntity loopValueMap = new MapEntity();
			List<MapEntity> sumValueList = powerAnalysisService.getLoopMonthConsumption(month);
			for(MapEntity sumValue : sumValueList) {
				Double value = Double.parseDouble(sumValue.get("sumValue").toString());
				String orgId = sumValue.get("orgId").toString();
				loopValueMap.put(orgId, value);
			}
			*/
			
			int type = 9;
			int level = 6;
			
			List<List<MapEntity>> channelLevelList = new ArrayList<>(); 
			//数据加载
			for(int i=0; i<level; i++) {
				//7是进线柜, 9+是回路
				if( i == 0)
					type = 7;
				else
					type = 8;
				
				List<MapEntity> loopChannelList = powerAnalysisService.getLoopChannel(String.valueOf(type+i));
				
				for(MapEntity loopChannel : loopChannelList) {
					
					String chId = loopChannel.get("chId").toString();
					String orgId = loopChannel.get("orgId").toString();
					
					Object maxDataObj = redisTempldate.opsForHash().get("LoopMaxData:"+monthLastDay, chId);
					if(maxDataObj == null && loadFlag == false) {
						final String finalMonthLastDay = monthLastDay;
						new Thread(new Runnable() {
							public void run() {
								loadLoopMonthMaxData(finalMonthLastDay);
							}
						}).start();
					} else {
						loadFlag = true;
					}
					JSONObject maxDataJSON = null;
					if(maxDataObj != null) {
						maxDataJSON = JSONObject.parseObject(maxDataObj.toString());
 					} else {
						maxDataJSON = new JSONObject();						
						maxDataJSON.put("history_value", 0);
						maxDataJSON.put("id", 0);
						maxDataJSON.put("ch_id", chId);
					}
					
					Object minDataObj = redisTempldate.opsForHash().get("LoopMinData:"+monthFirstDay, chId);
					if(minDataObj == null && loadFlag == false) {
						final String finalMonthFirstDay = monthFirstDay;
						new Thread(new Runnable() {
							public void run() {
								loadLoopMonthMinData(finalMonthFirstDay);
							}
						}).start();
					} else {
						loadFlag = true;
					}
					
					JSONObject minDataJSON = null;
					if(minDataObj != null) {
						minDataJSON = JSONObject.parseObject(minDataObj.toString());
					} else {
						minDataJSON = new JSONObject();
						minDataJSON.put("history_value", 0);
						minDataJSON.put("id", 0);
						minDataJSON.put("ch_id", chId);
					}
					
					loopChannel.put("startValue", minDataJSON.get("history_value"));
					loopChannel.put("endValue", maxDataJSON.get("history_value"));
					
					double startValue = Double.parseDouble(minDataJSON.get("history_value").toString());
					double endValue = Double.parseDouble(maxDataJSON.get("history_value").toString());
					loopChannel.put("currentLoopEnergyConsumption", endValue - startValue);
					loadFlag = true;
					
					//Object valueObj = loopValueMap.get(orgId);
					Object valueObj = redisTempldate.opsForHash().get("LoopDataMonth:"+month, orgId);
					if(valueObj == null)
					{
						loopChannel.put("currentLoopEnergyConsumption", 0.0);
					}
					else 
					{
						loopChannel.put("currentLoopEnergyConsumption", valueObj);
					}
				}
				channelLevelList.add(loopChannelList);
				
			}

			List<MapEntity> dataTree = new ArrayList<>();
			if(channelLevelList.size() > 0) {
				dataTree = channelLevelList.get(0);
			}
			List<MapEntity> tempList = new ArrayList<>();
			if(channelLevelList.size() > 0) {
				tempList = channelLevelList.get(0);	
			}
			//数据归位
			for(int i=0;i<channelLevelList.size();i++) {
				List<MapEntity> list = channelLevelList.get(i);
				if(i != 0) {

					for(MapEntity channel : list) {
						
						String orgCode = channel.get("parentIds").toString();
						for(MapEntity temp : tempList) {
							String tempCode = temp.get("parentIds").toString();
							if(orgCode.indexOf(tempCode) != -1) {
								Object children = temp.get("children");
								if(children != null) {
									List<MapEntity> childrenList = (List<MapEntity>) children;
									childrenList.add(channel);
									temp.put("children", childrenList);
								} else {
									List<MapEntity> childrenList = new ArrayList<>();
									childrenList.add(channel);
									temp.put("children", childrenList);
								}
							}
						}
						
					}
					
					tempList = list;
				}
			}
			
			Double totalLoopEnergyConsumption = 0.0;
			//获取一级总用电量
			for(MapEntity data : dataTree) {
				totalLoopEnergyConsumption += Double.parseDouble(data.get("currentLoopEnergyConsumption").toString());
			}
			
			//数据计算
			for(MapEntity data : dataTree) {
				data.put("childLoopEnergyConsumptionPercentage", 100);
				/*
				Object AdjustMapObj = redisTempldate.opsForHash().get("AdjustMap", month);
				if(AdjustMapObj != null) {
					JSONArray AdjustMapJSONArray = JSONArray.parseArray(AdjustMapObj.toString());
					AdjustMapthreadLocal.set(AdjustMapJSONArray);
				}
				*/
				data.put("adjustedConsumption", Double.parseDouble(data.get("currentLoopEnergyConsumption").toString()));
				Double currentLoopEnergyConsumption = Double.parseDouble(data.get("currentLoopEnergyConsumption").toString());
				Double diffPercentage = totalValue / totalLoopEnergyConsumption;
				data.put("adjustedConsumption2",  currentLoopEnergyConsumption * diffPercentage );
				data.put("diffPercentage", diffPercentage);
				
				consumptionWithLossService.updateMonthLossData(data.get("orgId").toString(),month,currentLoopEnergyConsumption, currentLoopEnergyConsumption * diffPercentage);
				
				fillLoopEnergyConsumptionData2(data,month);
			}
			
			logger.debug("dataTree JSON 00:" + JSON.toJSONString(dataTree));
		}
		return ServletUtils.buildRs(true, "ok", "");	
    }
    
    public void fillLoopEnergyConsumptionData2(MapEntity node,String month) {

		double currentLoopEnergyConsumption = Double.parseDouble(node.get("currentLoopEnergyConsumption").toString());
		
		Object children = node.get("children");
		List<MapEntity> childrenList = (List<MapEntity>) children;
		if(children != null && childrenList.size() > 0) {
			
			logger.debug("fillLoopEnergyConsumptionData children > 0");

			double childSum = 0;
			for(MapEntity childrenNode : childrenList) {
				childrenNode.put("parentLoopEnergyConsumption", currentLoopEnergyConsumption);
				double childLoopEnergyConsumption = Double.parseDouble(childrenNode.get("currentLoopEnergyConsumption").toString());
				childSum += childLoopEnergyConsumption;
				//childrenNode.put("percentageOfParent", (childLoopEnergyConsumption / currentLoopEnergyConsumption) * 100 );
			}
			//下级支路能耗
			node.put("childLoopEnergyConsumption", childSum);
			//当前支路与下级差值
			node.put("energyConsumptionDiffWithChild", currentLoopEnergyConsumption - childSum);
			//损耗占比百分比
			if(currentLoopEnergyConsumption != 0) {
				node.put("lossPercentage",  ((currentLoopEnergyConsumption - childSum) / currentLoopEnergyConsumption) * 100 );
			} else {
				node.put("lossPercentage",  "");
			}


			
			for(MapEntity childrenNode : childrenList) {
				
				double childLoopEnergyConsumption = Double.parseDouble(childrenNode.get("currentLoopEnergyConsumption").toString());
				
				//下级能耗占百分比
				if(childLoopEnergyConsumption != 0 && currentLoopEnergyConsumption != 0) {
					childrenNode.put("childLoopEnergyConsumptionPercentage", (childLoopEnergyConsumption / childSum) * 100 );
				} else {
					childrenNode.put("childLoopEnergyConsumptionPercentage", 0);
				}
				
				
				//损耗分摊
				if(currentLoopEnergyConsumption != 0) {
					logger.debug("orgName:"+childrenNode.get("orgName") );
					double loss = (currentLoopEnergyConsumption - childSum) * (childLoopEnergyConsumption/childSum);
					
					logger.debug("loss =  (currentLoopEnergyConsumption - childSum) * (childLoopEnergyConsumption/currentLoopEnergyConsumption)");
					logger.debug("("+currentLoopEnergyConsumption+" - " + childSum+ ") * (" + childLoopEnergyConsumption + "/" + childSum + ") = " + String.valueOf( (currentLoopEnergyConsumption - childSum) * (childLoopEnergyConsumption/childSum) ) );
					
					childrenNode.put("lossAllocation",  loss);
					if(loss > 0) {
						childrenNode.put("adjustedConsumption", childLoopEnergyConsumption + loss);
					} else {
						childrenNode.put("adjustedConsumption", childLoopEnergyConsumption);
					}
				} else {
					childrenNode.put("lossAllocation", "");
					childrenNode.put("adjustedConsumption", childLoopEnergyConsumption);
				}		
				
				//差异值与整后用电量2
				double adjustedConsumption2 = Double.parseDouble(node.get("adjustedConsumption2").toString());
				Double diffPercentage = adjustedConsumption2 / childSum;
				childrenNode.put("adjustedConsumption2",  childLoopEnergyConsumption * diffPercentage );
				childrenNode.put("diffPercentage", diffPercentage);
				
				consumptionWithLossService.updateMonthLossData(childrenNode.get("orgId").toString(),month,childLoopEnergyConsumption, childLoopEnergyConsumption * diffPercentage);
				
				fillLoopEnergyConsumptionData2(childrenNode,month);
			}
			
		} else {
			
			logger.debug("fillLoopEnergyConsumptionData children = 0");
			
		}
	}
    
}