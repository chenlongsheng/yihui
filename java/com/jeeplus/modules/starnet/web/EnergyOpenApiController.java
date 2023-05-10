package com.jeeplus.modules.starnet.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.starnet.service.ElectricityUnitService;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.starnet.service.EnergyOpenApiService;
import com.jeeplus.modules.starnet.service.PowerAnalysisService;

@Controller
@RequestMapping(value = "openApi")
public class EnergyOpenApiController {
	
	public static Logger logger = LoggerFactory.getLogger(EnergyOpenApiController.class);
	
	@Autowired
	EnergyOpenApiService energyOpenApiService;
	
	@Autowired
	TOrgService tOrgService;
	
	@Autowired
	private PowerAnalysisService powerAnalysisService;
	
	
    @Autowired
    ElectricityUnitService electricityUnitService;
    
    @Autowired
    EnergyAnalysisService energyAnalysisService;
	
	
    
	//园区总能耗数值（日数值）
    @RequestMapping(value = { "gardenEnergyConsumption" })
    @ResponseBody
    public String gardenTotalEnergyConsumption(String gardenId,String day) {
    	String totalEnergyConsumption = energyOpenApiService.todayTotalEnergyConsumption(gardenId,day);
    	TOrg org = tOrgService.get(gardenId);
    	Map<String,Object> data = new HashMap<String,Object>();
    	data.put("gardenName", org.getName());
    	data.put("totalEnergy", totalEnergyConsumption);
    	return ServletUtils.buildRs(true, "园区总能耗数值（日数值）", data);
    }
	
	//园区总电费数值（能耗乘单价计算）（日数据）
    @RequestMapping(value = { "gardenEnergyConsumptionTotalMoney" }) 
    @ResponseBody
    public String todayGardenEnergyConsumptionTotalMoney(String gardenId,String day) {
    	String totalEnergyConsumption = energyOpenApiService.todayTotalEnergyConsumption(gardenId,day);
    	TOrg org = tOrgService.get(gardenId);
    	Map<String,Object> data = new HashMap<String,Object>();
    	data.put("gardenName", org.getName());
    	//点费单价一元
    	int price = 1;
    	data.put("totalMoney", totalEnergyConsumption );
    	return ServletUtils.buildRs(true, "园区总电费数值（能耗乘单价计算）（日数据）", data);
    }
	
	//四个配电房各自能耗数值（日数值）
    @RequestMapping(value = { "pdfEnergyConsumption" }) 
    @ResponseBody
    public String todayPdfEnergyConsumption(String pdfId,String day) {
    	String totalEnergyConsumption = energyOpenApiService.todayTotalEnergyConsumption(pdfId,day);
    	TOrg org = tOrgService.get(pdfId);
    	Map<String,Object> data = new HashMap<String,Object>();
    	data.put("pdfName", org.getName());
    	data.put("totalEnergy", totalEnergyConsumption);
    	return ServletUtils.buildRs(true, "四个配电房各自能耗数值（日数值）", data);
    }
	
	//四个配电房各自电费数据（能耗乘单价计算）（日数据）
    @RequestMapping(value = { "pdfEnergyConsumptionTotalMoney" }) 
    @ResponseBody
    public String todayPdfEnergyConsumptionTotalMoney(String pdfId,String day) {
    	String totalEnergyConsumption = energyOpenApiService.todayTotalEnergyConsumption(pdfId,day);
    	TOrg org = tOrgService.get(pdfId);
    	Map<String,Object> data = new HashMap<String,Object>();
    	data.put("pdfName", org.getName());
    	//点费单价一元
    	int price = 1;
    	data.put("totalMoney", totalEnergyConsumption );
    	return ServletUtils.buildRs(true, "四个配电房各自电费数据（能耗乘单价计算）（日数据）", data);
    }
	
	//配电房中各条回路能耗数据（日数据）
    @RequestMapping(value = { "loopEnergyConsumption" }) 
    @ResponseBody
    public String todayLoopEnergyConsumption(String pdfId,String day) {
    	List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
    	List<Map<String,Object>> loopList = energyOpenApiService.getLoopByPdfId(pdfId);
    	for(Map<String,Object> loop: loopList) {
        	String totalEnergyConsumption = energyOpenApiService.todayTotalEnergyConsumption(loop.get("id").toString(),day);
        	TOrg org = tOrgService.get(loop.get("id").toString());
        	Map<String,Object> data = new HashMap<String,Object>();
        	data.put("pdfName", org.getName());
        	data.put("totalEnergy", totalEnergyConsumption);
        	datas.add(data);
    	}
    	return ServletUtils.buildRs(true, "配电房中各条回路能耗数据（日数据）", datas);
    }
	
	//园区中各个用电单位能耗数据（日数据）
    @RequestMapping(value = { "unitEnergyConsumption" }) 
    @ResponseBody
    public String todayUnitEnergyConsumption(String unitId,String day) {
    	
    	MapEntity unitMap = powerAnalysisService.getStarElectricityUnit(unitId);
    	Map<String,Object> data = new HashMap<String,Object>();
    	String totalConsumption = energyOpenApiService.todayTotalEnergyConsumptionByUnitId(unitId,day);
    	data.put("unitName", unitMap.get("name").toString());
    	data.put("totalEnergy", totalConsumption);
    	
    	return ServletUtils.buildRs(true, "园区中各个用电单位能耗数据（日数据）", data);
    }
    
	//园区中多个用电单位能耗数据（日数据）
    @RequestMapping(value = { "todayUnitsEnergyConsumption" }) 
    @ResponseBody
    public String todayUnitsEnergyConsumption(String unitIds,String day) {
    	/*
    	String[] unitIdArr = unitIds.split(",");
    	List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
    	
    	for(String unitId : unitIdArr) {
        	Map<String,Object> data = new HashMap<String,Object>();
        	MapEntity unitMap = powerAnalysisService.getStarElectricityUnit(unitId);
        	String totalConsumption = energyOpenApiService.todayTotalEnergyConsumptionByUnitId(unitId,day);
        	data.put("unitId", unitMap.get("id").toString());
        	data.put("unitName", unitMap.get("name").toString());
        	data.put("totalEnergy", totalConsumption);
        	datas.add(data);
    	}
    	return ServletUtils.buildRs(true, "园区中各个用电单位能耗数据（日数据）", datas);
    	*/
    	String beginTime = day + " 00:00:00";
    	String endTime = day + " 23:59:59";
		List<MapEntity> resultData = new ArrayList<MapEntity>();
		String unitIdsArr[] = unitIds.split(",");
		for(String unitid : unitIdsArr) {
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
		return ServletUtils.buildRs(true, "园区中各个用电单位能耗数据（日数据）", resultData);
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
	
    
    
	
	//配电房中报警数据（日数据）
    @RequestMapping(value = { "pdfAlarmList" }) 
    @ResponseBody
    public String todayPdfAlarmList(String pdfId,String day) {
    	List<Map<String,Object>> alarmList = energyOpenApiService.getAlarmList(pdfId);
    	return ServletUtils.buildRs(true, "配电房中报警数据（日数据）", alarmList);
    }
	
	//配电房报警类型数据（累计总数据）
    @RequestMapping(value = { "pdfAlarmTypeList" }) 
    @ResponseBody
    public String pdfAlarmTypeList(String pdfId) {
    	List<Map<String,Object>> alarmList = energyOpenApiService.getAlarmList(pdfId);
    	return ServletUtils.buildRs(true, "配电房报警类型数据（累计总数据）", alarmList);
    }

	//配电房中各个回路电压电流数据（实时数据）
    @RequestMapping(value = { "getPdfLoopVoltageAndElectricity" }) 
    @ResponseBody
    public String getPdfLoopVoltageAndElectricity(String pdfId) {
    	List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
    	List<Map<String,Object>> loopList = energyOpenApiService.getLoopByPdfId(pdfId);
    	for(Map<String,Object> loop: loopList) {
    		List<Map<String, Object>> voltages = energyOpenApiService.getLoopVoltage(loop.get("id").toString());
    		List<Map<String, Object>> electricitys = energyOpenApiService.getLoopElectricity(loop.get("id").toString());
    		
    		TOrg org = tOrgService.get(loop.get("id").toString());
        	Map<String,Object> data = new HashMap<String,Object>();
        	data.put("loopName", org.getName());
        	data.put("voltage", voltages);
        	data.put("electricity", electricitys);
        	datas.add(data);
    	}
    	return ServletUtils.buildRs(true, "配电房中各个回路电压电流数据（实时数据）", datas);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
	
	//实时数据（单条发送）
    @RequestMapping(value = { "getRealData" }) 
    @ResponseBody
    public String getRealData(String loopId) {
    	return ServletUtils.buildRs(true, "能耗历史数据", "");
    }
	
	//各个用电单位能耗数据（线损能耗月报）（月数据）
    @RequestMapping(value = { "monthUnitLineLoss" }) 
    @ResponseBody
    public String monthUnitLineLoss(String unitId) {
    	return ServletUtils.buildRs(true, "各个用电单位能耗数据（线损能耗月报）（月数据）", "");
    }
    
    
    
    
	//1、电表设备台账（包含：设备地址码，设备状态，回路名）
    @RequestMapping(value = { "deviceStateList" }) 
    @ResponseBody
    public String deviceStateList() {
    	List<Map<String,Object>> deviceStateList = energyOpenApiService.deviceStateList();
    	return ServletUtils.buildRs(true, "电表设备台账（包含：设备地址码，设备状态，回路名）", deviceStateList);
    }
    
    
}
