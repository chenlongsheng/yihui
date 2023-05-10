package com.jeeplus.modules.pdfData.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jeeplus.modules.pdfData.dao.PdfMainpageDao;
import com.jeeplus.modules.settings.dao.TOrgDao;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;

@Service
public class PdfMainpageService {
	
	@Autowired
	PdfMainpageDao pdfMainpageDao;
	
	@Autowired
	TOrgDao orgDao;
	
	public Map<String, Object> getPdfBaseInfo(String pdfId) {
		TOrg org = orgDao.get(pdfId);
		String code = org.getCode();
		int codeLen = code.length();
		String code1 = code.substring(0,4);
		TOrg org1 = orgDao.findUniqueByProperty("code", code1);
		String code2 = "";
		if(codeLen >= 6) {
			code2 = code.substring(0,6);
		}
		TOrg org2 = null;
		if(!code2.equals("")) {
			org2 = orgDao.findUniqueByProperty("code", code2);
		}
		String code3 = "";
		if(codeLen >= 8) {
			code3 = code.substring(0,8);
		}
		TOrg org3 = null;
		if(!code3.equals("")) {
			org3 = orgDao.findUniqueByProperty("code", code3);
		}
		
		StringBuilder pdfAddress = new StringBuilder();
		pdfAddress.append(org1.getName());
		if(org2 != null) {
			pdfAddress.append(org2.getName());
		}
		if(org3 != null) {
			pdfAddress.append(org3.getName());
		}
		
		String pdfName = org.getName();
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("pdfName", pdfName);
		result.put("pdfAddress", pdfAddress);
		
		List<Map<String, Object>> alarmList = pdfMainpageDao.notProcAlarmList(org.getCode());
		result.put("notProcAlarmList", alarmList);

		return result;
	}
	
	public List<Map<String, Object>> getLightData(String code) {
		List<Map<String,Object>> lightDataList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> lightData1 = new HashMap<String,Object>();
		lightData1.put("name","东南角灯控");
		lightData1.put("vltage","12");
		lightData1.put("current","0.5");
		lightData1.put("power","6");
		lightData1.put("energy","21600");
		
		Map<String,Object> lightData2 = new HashMap<String,Object>();
		lightData2.put("name","西南角灯控");
		lightData2.put("vltage","12");
		lightData2.put("current","0.5");
		lightData2.put("power","6");
		lightData2.put("energy","21600");
		
		Map<String,Object> lightData3 = new HashMap<String,Object>();
		lightData3.put("name","东北角灯控");
		lightData3.put("vltage","12");
		lightData3.put("current","0.5");
		lightData3.put("power","6");
		lightData3.put("energy","21600");
		
		Map<String,Object> lightData4 = new HashMap<String,Object>();
		lightData4.put("name","西北角灯控");
		lightData4.put("vltage","12");
		lightData4.put("current","0.5");
		lightData4.put("power","6");
		lightData4.put("energy","21600");
		
		lightDataList.add(lightData1);
		lightDataList.add(lightData2);
		lightDataList.add(lightData3);
		lightDataList.add(lightData4);
		
		return lightDataList;
		//return pdfMainpageDao.getLightData(pdfId);
	}
	
	public List<Map<String, Object>> getHumTempData(String code) {
		List<Map<String,Object>> humTempDataList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> deviceList = pdfMainpageDao.getLoraDeviceByTypeAndCode(169,code);
		for(int i=0;i<deviceList.size();i++) {
			Map<String,Object> map = deviceList.get(i);
			String deviceId = map.get("id").toString();
			List<Map<String,Object>> channellist = pdfMainpageDao.getLoraChannelDataByDeviceId(deviceId);
			map.put("channellist", channellist);
			humTempDataList.add(map);
		}
		return humTempDataList;
		/*
		List<Map<String,Object>> humDataList = pdfMainpageDao.getHumData(code);
		List<Map<String,Object>> tempDataList = pdfMainpageDao.getTempData(code);
		for(int i=0;i<humDataList.size();i++) {
			Map<String,Object> map = humDataList.get(i);
			if(i < tempDataList.size()) {
				Map<String,Object> tempItem = tempDataList.get(i);
				map.put("temp", tempItem);
			}
		}
		return humDataList;
		*/
	}
	
	public List<Map<String, Object>> getWaterData(String code) {
		List<Map<String,Object>> waterDataList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> deviceList = pdfMainpageDao.getLoraDeviceByTypeAndCode(168,code);
		for(int i=0;i<deviceList.size();i++) {
			Map<String,Object> map = deviceList.get(i);
			String deviceId = map.get("id").toString();
			List<Map<String,Object>> channellist = pdfMainpageDao.getLoraChannelDataByDeviceId(deviceId);
			map.put("channellist", channellist);
			waterDataList.add(map);
		}
		return waterDataList;
		/*
		List<Map<String,Object>> waterDataList = pdfMainpageDao.getWaterData(code);
		//水浸深度
		List<Map<String,Object>> waterDeepList = pdfMainpageDao.getWaterDeepData(code);
		for(int i=0;i<waterDataList.size();i++) {
			Map<String,Object> map = waterDataList.get(i);
			if(i < waterDeepList.size()) {
				Map<String,Object> waterDeepItem = waterDeepList.get(i);
				map.put("waterDeepItem", waterDeepItem);
			}
		}
		//水浸绳子
		List<Map<String,Object>> waterStringList = pdfMainpageDao.getWaterStringData(code);
		for(int i=0;i<waterDataList.size();i++) {
			Map<String,Object> map = waterDataList.get(i);
			if(i < waterStringList.size() ) {
				Map<String,Object> waterStringItem = waterStringList.get(i);
				map.put("waterStringItem", waterStringItem);
			}
		}
		return waterDataList;
		*/
	}
	
	public List<Map<String, Object>> getDoorData(String code) {
		List<Map<String,Object>> doorDataList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> deviceList = pdfMainpageDao.getLoraDeviceByTypeAndCode(162,code);
		for(int i=0;i<deviceList.size();i++) {
			Map<String,Object> map = deviceList.get(i);
			String deviceId = map.get("id").toString();
			List<Map<String,Object>> channellist = pdfMainpageDao.getLoraChannelDataByDeviceId(deviceId);
			map.put("channellist", channellist);
			doorDataList.add(map);
		}
		return doorDataList;
		/*
		List<Map<String,Object>> doorDataList = pdfMainpageDao.getDoorData(code);
		//开门超时
		List<Map<String,Object>> doorOpenOverTimeList = pdfMainpageDao.getDoorOpenOverTimeData(code);
		for(int i=0;i<doorDataList.size();i++) {
			Map<String,Object> map = doorDataList.get(i);
			if(i < doorOpenOverTimeList.size()) {
				Map<String,Object> doorOpenOverTimeItem = doorOpenOverTimeList.get(i);
				map.put("doorOpenOverTimeItem", doorOpenOverTimeItem);
			}
		}
		//开门时长
		List<Map<String,Object>> doorOpenTimeList = pdfMainpageDao.getDoorOpenTimeData(code);
		for(int i=0;i<doorDataList.size();i++) {
			Map<String,Object> map = doorDataList.get(i);
			if(i < doorOpenTimeList.size()) {
				Map<String,Object> doorOpenTimeItem = doorOpenTimeList.get(i);
				map.put("doorOpenTimeItem", doorOpenTimeItem);
			}
		}
		return doorDataList;
		*/
	}

	public List<Map<String, Object>> getSmokeData(String code) {
		return pdfMainpageDao.getSmokeData(code);
	}

	public List<Map<String, Object>> getHighVoltageData(String pdfId) {
		List<Map<String,Object>> highVoltageDataList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> highVoltageData1 = new HashMap<String,Object>();
		highVoltageData1.put("name","高压1");
		highVoltageData1.put("L1","2.0");
		highVoltageData1.put("L2","5.0");
		highVoltageData1.put("L3","12.0");
		highVoltageData1.put("YGGL","10.0");
		highVoltageData1.put("SZGL","12.0");
		highVoltageData1.put("DDL","10.0");
		highVoltageData1.put("GL","过流");
		highVoltageData1.put("DL","短路");
		highVoltageData1.put("DTZ","短跳闸");
		highVoltageData1.put("KG","关");
		
		Map<String,Object> highVoltageData2 = new HashMap<String,Object>();
		highVoltageData2.put("name","高压2");
		highVoltageData2.put("L1","2.0");
		highVoltageData2.put("L2","5.0");
		highVoltageData2.put("L3","12.0");
		highVoltageData2.put("YGGL","10.0");
		highVoltageData2.put("SZGL","12.0");
		highVoltageData2.put("DDL","10.0");
		highVoltageData2.put("GL","过流");
		highVoltageData2.put("DL","短路");
		highVoltageData2.put("DTZ","短跳闸");
		highVoltageData2.put("KG","关");
		
		Map<String,Object> highVoltageData3 = new HashMap<String,Object>();
		highVoltageData3.put("name","高压3");
		highVoltageData3.put("L1","2.0");
		highVoltageData3.put("L2","5.0");
		highVoltageData3.put("L3","12.0");
		highVoltageData3.put("YGGL","10.0");
		highVoltageData3.put("SZGL","12.0");
		highVoltageData3.put("DDL","10.0");
		highVoltageData3.put("GL","过流");
		highVoltageData3.put("DL","短路");
		highVoltageData3.put("DTZ","短跳闸");
		highVoltageData3.put("KG","关");
		
		Map<String,Object> highVoltageData4 = new HashMap<String,Object>();
		highVoltageData4.put("name","高压4");
		highVoltageData4.put("L1","2.0");
		highVoltageData4.put("L2","5.0");
		highVoltageData4.put("L3","12.0");
		highVoltageData4.put("YGGL","10.0");
		highVoltageData4.put("SZGL","12.0");
		highVoltageData4.put("DDL","10.0");
		highVoltageData4.put("GL","过流");
		highVoltageData4.put("DL","短路");
		highVoltageData4.put("DTZ","短跳闸");
		highVoltageData4.put("KG","关");
		
		highVoltageDataList.add(highVoltageData1);
		highVoltageDataList.add(highVoltageData2);
		highVoltageDataList.add(highVoltageData3);
		highVoltageDataList.add(highVoltageData4);
		
		return highVoltageDataList;		
		//return pdfMainpageDao.getHighVoltageData(pdfId);
	}

	public List<Map<String, Object>> getLowVltageData(String boxId) {
		List<Map<String,Object>> lowVoltageDataList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> lowVoltageData1 = new HashMap<String,Object>();
		lowVoltageData1.put("name","低压1");
		lowVoltageData1.put("L1","2.0");
		lowVoltageData1.put("L2","5.0");
		lowVoltageData1.put("L3","12.0");
		lowVoltageData1.put("YGGL","10.0");
		lowVoltageData1.put("SZGL","12.0");
		lowVoltageData1.put("DDL","10.0");
		lowVoltageData1.put("GL","过流");
		lowVoltageData1.put("DL","短路");
		lowVoltageData1.put("DTZ","短跳闸");
		lowVoltageData1.put("KG","关");
		
		Map<String,Object> lowVoltageData2 = new HashMap<String,Object>();
		lowVoltageData2.put("name","低压2");
		lowVoltageData2.put("L1","2.0");
		lowVoltageData2.put("L2","5.0");
		lowVoltageData2.put("L3","12.0");
		lowVoltageData2.put("YGGL","10.0");
		lowVoltageData2.put("SZGL","12.0");
		lowVoltageData2.put("DDL","10.0");
		lowVoltageData2.put("GL","过流");
		lowVoltageData2.put("DL","短路");
		lowVoltageData2.put("DTZ","短跳闸");
		lowVoltageData2.put("KG","关");
		
		Map<String,Object> lowVoltageData3 = new HashMap<String,Object>();
		lowVoltageData3.put("name","低压3");
		lowVoltageData3.put("L1","2.0");
		lowVoltageData3.put("L2","5.0");
		lowVoltageData3.put("L3","12.0");
		lowVoltageData3.put("YGGL","10.0");
		lowVoltageData3.put("SZGL","12.0");
		lowVoltageData3.put("DDL","10.0");
		lowVoltageData3.put("GL","过流");
		lowVoltageData3.put("DL","短路");
		lowVoltageData3.put("DTZ","短跳闸");
		lowVoltageData3.put("KG","关");
		
		
		lowVoltageDataList.add(lowVoltageData1);
		lowVoltageDataList.add(lowVoltageData2);
		lowVoltageDataList.add(lowVoltageData3);
		
		return lowVoltageDataList;
		//return pdfMainpageDao.getLowVltageData(boxId);
	}
	
	public List<Map<String, Object>> getSplitSegmentData(String boxId) {
		
		List<Map<String,Object>> splitSegmentDataList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> splitSegmentData1 = new HashMap<String,Object>();
		splitSegmentData1.put("name","分段1");
		splitSegmentData1.put("L1","2.0");
		splitSegmentData1.put("L2","5.0");
		splitSegmentData1.put("L3","12.0");
		splitSegmentData1.put("YGGL","10.0");
		splitSegmentData1.put("SZGL","12.0");
		splitSegmentData1.put("DDL","10.0");
		splitSegmentData1.put("GL","过流");
		splitSegmentData1.put("DL","短路");
		splitSegmentData1.put("DTZ","短跳闸");
		splitSegmentData1.put("KG","关");
		
		Map<String,Object> splitSegmentData2 = new HashMap<String,Object>();
		splitSegmentData2.put("name","分段2");
		splitSegmentData2.put("L1","2.0");
		splitSegmentData2.put("L2","5.0");
		splitSegmentData2.put("L3","12.0");
		splitSegmentData2.put("YGGL","10.0");
		splitSegmentData2.put("SZGL","12.0");
		splitSegmentData2.put("DDL","10.0");
		splitSegmentData2.put("GL","过流");
		splitSegmentData2.put("DL","短路");
		splitSegmentData2.put("DTZ","短跳闸");
		splitSegmentData2.put("KG","关");
		
		Map<String,Object> splitSegmentData3 = new HashMap<String,Object>();
		splitSegmentData3.put("name","分段3");
		splitSegmentData3.put("L1","2.0");
		splitSegmentData3.put("L2","5.0");
		splitSegmentData3.put("L3","12.0");
		splitSegmentData3.put("YGGL","10.0");
		splitSegmentData3.put("SZGL","12.0");
		splitSegmentData3.put("DDL","10.0");
		splitSegmentData3.put("GL","过流");
		splitSegmentData3.put("DL","短路");
		splitSegmentData3.put("DTZ","短跳闸");
		splitSegmentData3.put("KG","关");
		
		
		splitSegmentDataList.add(splitSegmentData1);
		splitSegmentDataList.add(splitSegmentData2);
		splitSegmentDataList.add(splitSegmentData3);
		
		return splitSegmentDataList;
		
		//return pdfMainpageDao.getSplitSegmentData(boxId);
	}
	
	public List<Map<String, Object>> getReturnPathData(String boxId) {
		
		List<Map<String,Object>> returnPathDataList = new ArrayList<Map<String,Object>>();
		
		Map<String,Object> returnPathData1 = new HashMap<String,Object>();
		returnPathData1.put("name","回路1");
		returnPathData1.put("L1","2.0");
		returnPathData1.put("L2","5.0");
		returnPathData1.put("L3","12.0");
		returnPathData1.put("YGGL","10.0");
		returnPathData1.put("SZGL","12.0");
		returnPathData1.put("DDL","10.0");
		returnPathData1.put("GL","过流");
		returnPathData1.put("DL","短路");
		returnPathData1.put("DTZ","短跳闸");
		returnPathData1.put("KG","关");
		
		Map<String,Object> returnPathData2 = new HashMap<String,Object>();
		returnPathData2.put("name","回路2");
		returnPathData2.put("L1","2.0");
		returnPathData2.put("L2","5.0");
		returnPathData2.put("L3","12.0");
		returnPathData2.put("YGGL","10.0");
		returnPathData2.put("SZGL","12.0");
		returnPathData2.put("DDL","10.0");
		returnPathData2.put("GL","过流");
		returnPathData2.put("DL","短路");
		returnPathData2.put("DTZ","短跳闸");
		returnPathData2.put("KG","关");
		
		Map<String,Object> returnPathData3 = new HashMap<String,Object>();
		returnPathData3.put("name","回路3");
		returnPathData3.put("L1","2.0");
		returnPathData3.put("L2","5.0");
		returnPathData3.put("L3","12.0");
		returnPathData3.put("YGGL","10.0");
		returnPathData3.put("SZGL","12.0");
		returnPathData3.put("DDL","10.0");
		returnPathData3.put("GL","过流");
		returnPathData3.put("DL","短路");
		returnPathData3.put("DTZ","短跳闸");
		returnPathData3.put("KG","关");
		
		
		returnPathDataList.add(returnPathData1);
		returnPathDataList.add(returnPathData2);
		returnPathDataList.add(returnPathData3);
		
		return returnPathDataList;		
		
		//return pdfMainpageDao.getReturnPathData(boxId);
	}
	
	private List<Map<String, Object>> getEleBoxList(String pdfId) {
		return pdfMainpageDao.getEleBoxList(pdfId);
	}
	
	public Map<String, Object> getEleBoxData(String pdfId) {
		Map<String,Object> result = new HashMap<String,Object>();
		//List<Map<String,Object>> boxList = this.getEleBoxList(pdfId);
		
		List<Map<String,Object>> boxList = new ArrayList<Map<String,Object>>();
		Map<String,Object> box = new HashMap<String,Object>();
		box.put("name", "1号电力柜");
		box.put("boxid", "1");
		boxList.add(box);
		
		for(Map<String,Object> map : boxList) {
			String boxId = map.get("boxid").toString();
			//低压数据
			map.put("lowVoltageData", this.getLowVltageData(boxId));
			//分段数据
			map.put("splitSegmentData", this.getSplitSegmentData(boxId));
			//回路数据
			map.put("returnPathData", this.getReturnPathData(boxId));
		}
		result.put("boxList", boxList);
		return result;
	}
	
	public Map<String, Object> getMaintainanceData() {
		return null;
	}
	
	public Map<String, Object> getArrangementData() {
		return null;
	}
	
}
