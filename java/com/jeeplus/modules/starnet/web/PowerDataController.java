/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.web;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;

import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.starnet.service.DataService;
import com.jeeplus.modules.starnet.service.EnergyAnalysisService;
import com.jeeplus.modules.starnet.service.EnergyOpenApiService;
import com.jeeplus.modules.starnet.service.PowerAnalysisService;
import com.jeeplus.modules.starnet.service.PowerDataService;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.entity.PdfOrder;

/**
 * 数据配置Controller
 * 
 * @author long
 * @version 2018-07-24
 */
//电力数据
@Controller
@RequestMapping(value = "starData")
public class PowerDataController extends BaseController {

	@Autowired
	private PowerDataService powerDataService;
	
	@Autowired
	RedisTemplate<String, String> redisTempldate;
	
	@Autowired
	EnergyOpenApiService energyOpenApiService;
	
	@Autowired
	private PowerAnalysisService powerAnalysisService;
	
	
	//日原始数据
	@RequestMapping("/powerDataList")
	@ResponseBody
	public String powerDataList(String orgId,String time) {
		List<Map<String,Object>> loopList = energyOpenApiService.getLoopByOrgId(orgId);
		for(Map<String,Object> loop : loopList ) {
			List<MapEntity> datalist = powerDataService.getDayDataList(loop.get("id").toString(),time);
			loop.put("datalist", datalist);
		}

		return ServletUtils.buildRs(true, "日原始数据", loopList);
	}
	
	//导出日原始数据
	@RequestMapping("/exportpowerDataList")
	@ResponseBody
	public void exportpowerDataList(String orgId, String time,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
		
		List<Map<String,Object>> loopList = energyOpenApiService.getLoopByOrgId(orgId);
		for(Map<String,Object> loop : loopList ) {
			List<MapEntity> datalist = powerDataService.getDayDataList(loop.get("id").toString(),time);
			loop.put("datalist", datalist);
		}
		
		powerDataService.exportChannelRportList(httpServletRequest,httpServletResponse,loopList,time);
		
	}	
	
	
	//逐日极值数据
	@RequestMapping("/extremalDatas")
	@ResponseBody
	public String extremalDatas(String orgId,String startTime,String endTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startTimeLD = LocalDate.parse(startTime,formatter);
		LocalDate endTimeLD = LocalDate.parse(endTime,formatter);
		LocalDate tmpLD = startTimeLD;
		
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		while( tmpLD.isBefore(endTimeLD) || tmpLD.isEqual(endTimeLD) )
		{
			Object strData = redisTempldate.opsForHash().get("ExtremalData:"+tmpLD.format(formatter), orgId);
			JSONObject data = null;
			if(strData == null) {
				data = loadExtremalData(tmpLD.format(formatter),orgId);
			} else {
				data = JSONObject.parseObject(strData.toString());
			}
			dataList.add(data);
			tmpLD = tmpLD.plusDays(1);
		}
		
		return ServletUtils.buildRs(true, "逐日极值数据",dataList);
	}

	
	//导出逐日极值数据
	@RequestMapping("/exportextremalDatas")
	@ResponseBody
	public void exportextremalDatas(String orgId,String startTime,String endTime,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startTimeLD = LocalDate.parse(startTime,formatter);
		LocalDate endTimeLD = LocalDate.parse(endTime,formatter);
		LocalDate tmpLD = startTimeLD;
		
		List<JSONObject> dataList = new ArrayList<JSONObject>();
		while( tmpLD.isBefore(endTimeLD) )
		{
			JSONObject data = JSONObject.parseObject(redisTempldate.opsForHash().get(tmpLD.format(formatter), orgId).toString());
			if(data == null) {
				//将数据从 数据库加载到 redis
				data = loadExtremalData(tmpLD.format(formatter),orgId);
			}
			dataList.add(data);
			tmpLD = tmpLD.plusDays(1);
		}
		
		//powerDataService.exportextremalDatas(httpServletRequest, httpServletResponse,dataList);

	}
	
	
	
	
	
	//日极值数据
	@RequestMapping("/dayExtremalDatas")
	@ResponseBody
	public String dayExtremalDatas(String date) {
		if(date != null) {

			int type = 9;
			int level = 6;
			
			List<List<MapEntity>> channelLevelList = new ArrayList<>(); 
			//数据加载
			for(int i=0; i<level; i++) {
				//7是进线柜, 9+是回路
				if( i == 0)
					type = 7;
				else
					//出线柜这一级被跳过了
					type = 8;
				
				List<MapEntity> loopChannelList = powerAnalysisService.getLoopChannel(String.valueOf(type+i));
				
				for(MapEntity loopChannel : loopChannelList) {
					String chId = loopChannel.get("chId").toString();
					String orgId = loopChannel.get("orgId").toString();
					
					Object strData = redisTempldate.opsForHash().get("ExtremalData:"+date, orgId);
					JSONObject data = new JSONObject();
					if(strData == null) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								loadExtremalData(date,orgId);		
							}
						}).start();
					} else {
						data = JSONObject.parseObject(strData.toString());
					}
					loopChannel.put("ExtremalData", data);
					//填充数据
					
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
			
			return ServletUtils.buildRs(true, "日极值数据", pdfList);
		} else {
			return ServletUtils.buildRs(true, "日期为空", "");
		}
		
	}
	
	
	
	
	
	//导出日极值数据
	@RequestMapping("/exportDayExtremalDatas")
	@ResponseBody
	public void exportDayExtremalDatas(String date,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		int type = 9;
		int level = 6;
		
		List<List<MapEntity>> channelLevelList = new ArrayList<>(); 
		//数据加载
		for(int i=0; i<level; i++) {
			//7是进线柜, 9+是回路
			if( i == 0)
				type = 7;
			else
				//出线柜这一级被跳过了
				type = 8;
			
			List<MapEntity> loopChannelList = powerAnalysisService.getLoopChannel(String.valueOf(type+i));
			
			for(MapEntity loopChannel : loopChannelList) {
				String chId = loopChannel.get("chId").toString();
				String orgId = loopChannel.get("orgId").toString();
				Object strData = redisTempldate.opsForHash().get("ExtremalData:"+date, orgId);
				JSONObject data = new JSONObject();
				if(strData == null) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							loadExtremalData(date,orgId);		
						}
					}).start();
				} else {
					data = JSONObject.parseObject(strData.toString());
				}
				
				loopChannel.put("ExtremalData", data);
				
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
		
		powerDataService.exportDayExtremalDatas(httpServletRequest,httpServletResponse,pdfList,date);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Autowired
	DataService dataService;
	
	public JSONObject loadExtremalData(String date,String orgId) {

    		//获取 A,B,C相 电压通道
    		MapEntity phaseAVolChannel = dataService.getPhaseAVolChannelByOrgId(orgId);
    		MapEntity phaseBVolChannel = dataService.getPhaseBVolChannelByOrgId(orgId);
    		MapEntity phaseCVolChannel = dataService.getPhaseCVolChannelByOrgId(orgId);
    		
    		//根据当天日期, 获取A,B,C相 电压通道 的极值
    		MapEntity aMaxVolValue = null;
    		if(phaseAVolChannel != null) {
    			aMaxVolValue = dataService.getChannelMaxValueInADay(phaseAVolChannel.get("id").toString(),date);
    		}

    		MapEntity aMinVolValue = null;
    		if(phaseAVolChannel != null) {
    			aMinVolValue = dataService.getChannelMinValueInADay(phaseAVolChannel.get("id").toString(),date);
    		}
    		
    		MapEntity aAVGVolValue = null;
    		if(phaseAVolChannel != null) {
    			aAVGVolValue = dataService.getChannelAVGValueInADay(phaseAVolChannel.get("id").toString(),date);
    		}
    		
    		
    		MapEntity bMaxVolValue = null;
    		if(phaseBVolChannel != null) {
    			bMaxVolValue = dataService.getChannelMaxValueInADay(phaseBVolChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bMinVolValue = null;
    		if(phaseBVolChannel != null) {
    			bMinVolValue = dataService.getChannelMinValueInADay(phaseBVolChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bAVGVolValue = null;
    		if(phaseBVolChannel != null) {
    			bAVGVolValue = dataService.getChannelAVGValueInADay(phaseBVolChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cMaxVolValue = null;
    		if(phaseCVolChannel != null) {
    			cMaxVolValue = dataService.getChannelMaxValueInADay(phaseCVolChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cMinVolValue = null;
    		if(phaseCVolChannel != null) {
    			cMinVolValue = dataService.getChannelMinValueInADay(phaseCVolChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cAVGVolValue = null;
    		if(phaseCVolChannel != null) {
    			cAVGVolValue = dataService.getChannelAVGValueInADay(phaseCVolChannel.get("id").toString(),date);
    		}
    		
    		//获取A,B,C相 电流通道
    		MapEntity phaseACurChannel = dataService.getPhaseACurChannelByOrgId(orgId);
    		MapEntity phaseBCurChannel = dataService.getPhaseBCurChannelByOrgId(orgId);
    		MapEntity phaseCCurChannel = dataService.getPhaseCCurChannelByOrgId(orgId);
    		
    		//根据当天日期, 获取A,B,C相 电流通道 的极值
    		MapEntity aMaxCurValue = null;
    		if(phaseACurChannel != null) {
    			aMaxCurValue = dataService.getChannelMaxValueInADay(phaseACurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity aMinCurValue = null; 
    		if(phaseACurChannel != null) {
    			aMinCurValue = dataService.getChannelMinValueInADay(phaseACurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity aAVGCurValue = null; 
    		if(phaseACurChannel != null) {
    			aAVGCurValue = dataService.getChannelAVGValueInADay(phaseACurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bMaxCurValue = null;
    		if(phaseBCurChannel != null) {
    			bMaxCurValue = dataService.getChannelMaxValueInADay(phaseBCurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bMinCurValue = null;
    		if(phaseBCurChannel != null) {
    			bMinCurValue = dataService.getChannelMinValueInADay(phaseBCurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bAVGCurValue = null; 
    		if(phaseBCurChannel != null) {
    			bAVGCurValue = dataService.getChannelAVGValueInADay(phaseBCurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cMaxCurValue = null;
    		if(phaseCCurChannel != null) {
    			cMaxCurValue = dataService.getChannelMaxValueInADay(phaseCCurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cMinCurValue = null;
    		if(phaseCCurChannel != null) {
    			cMinCurValue = dataService.getChannelMinValueInADay(phaseCCurChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cAVGCurValue = null;
    		if(phaseCCurChannel != null) {
    			cAVGCurValue = dataService.getChannelAVGValueInADay(phaseCCurChannel.get("id").toString(),date);
    		}
    		
    		
    		

    		//获取A B C相总功率
    		MapEntity phaseATotalPowerChannel = dataService.getPhaseATotalPowerChannelByOrgId(orgId);
    		MapEntity phaseBTotalPowerChannel = dataService.getPhaseBTotalPowerChannelByOrgId(orgId);
    		MapEntity phaseCTotalPowerChannel = dataService.getPhaseCTotalPowerChannelByOrgId(orgId);
    		//根据当天日期, 获取A,B,C相 总功率通道 的极值
    		MapEntity aMaxTotalPowerValue = null;
    		if(phaseATotalPowerChannel != null) {
    			aMaxTotalPowerValue = dataService.getChannelMaxValueInADay(phaseATotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity aMinTotalPowerValue = null; 
    		if(phaseATotalPowerChannel != null) {
    			aMinTotalPowerValue = dataService.getChannelMinValueInADay(phaseATotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity aAVGTotalPowerValue = null; 
    		if(phaseATotalPowerChannel != null) {
    			aAVGTotalPowerValue = dataService.getChannelAVGValueInADay(phaseATotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bMaxTotalPowerValue = null;
    		if(phaseBTotalPowerChannel != null) {
    			bMaxTotalPowerValue = dataService.getChannelMaxValueInADay(phaseBTotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bMinTotalPowerValue = null;
    		if(phaseBTotalPowerChannel != null) {
    			bMinTotalPowerValue = dataService.getChannelMinValueInADay(phaseBTotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity bAVGTotalPowerValue = null; 
    		if(phaseBTotalPowerChannel != null) {
    			bAVGTotalPowerValue = dataService.getChannelAVGValueInADay(phaseBTotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cMaxTotalPowerValue = null;
    		if(phaseCTotalPowerChannel != null) {
    			cMaxTotalPowerValue = dataService.getChannelMaxValueInADay(phaseCTotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cMinTotalPowerValue = null;
    		if(phaseCTotalPowerChannel != null) {
    			cMinTotalPowerValue = dataService.getChannelMinValueInADay(phaseCTotalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity cAVGTotalPowerValue = null;
    		if(phaseCTotalPowerChannel != null) {
    			cAVGTotalPowerValue = dataService.getChannelAVGValueInADay(phaseCTotalPowerChannel.get("id").toString(),date);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		//获取正向电能通道
    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
    		
    		//根据当天日期, 获取 正向电能通道 的极值
    		MapEntity maxEnergyValue = null;
    		if(phaseEnergyChannel != null) {
    			maxEnergyValue = dataService.getChannelMaxValueInADay(phaseEnergyChannel.get("id").toString(),date);
    		}
    		
    		MapEntity minEnergyValue = null;
    		if(phaseEnergyChannel != null) {
    			minEnergyValue = dataService.getChannelMinValueInADay(phaseEnergyChannel.get("id").toString(),date);
    		}
    		
    		MapEntity avgEnergyValue = null;
    		if(phaseEnergyChannel != null) {
    			avgEnergyValue = dataService.getChannelAVGValueInADay(phaseEnergyChannel.get("id").toString(),date);
    		}
    		
    		//获取有功功率通道
//    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
    		//获取视在功率通道
//    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
    		//获取无功功率通道
//    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
    		
    		
    		//获取功率因素通道
    		MapEntity powerFactorChannel = dataService.getPowerFactorChannelByOrgId(orgId);
    		
    		
    		//获取线电压通道
//    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
    		//负载率
//    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
    		//频率
//    		MapEntity phaseEnergyChannel = dataService.getEnergyChannelByOrgId(orgId);
    		
    		
    		//根据当天日期, 获取 功率因素通道的极值
    		MapEntity maxPowerFactorValue = null;
    		if(powerFactorChannel != null) {
    			maxPowerFactorValue = dataService.getChannelMaxValueInADay(powerFactorChannel.get("id").toString(),date);
    		}
    		
    		MapEntity minPowerFactorValue = null;
    		if(powerFactorChannel != null) {
    			minPowerFactorValue = dataService.getChannelMinValueInADay(powerFactorChannel.get("id").toString(),date);
    		}
    		
    		MapEntity avgPowerFactorValue = null;
    		if(powerFactorChannel != null) {
    			avgPowerFactorValue = dataService.getChannelAVGValueInADay(powerFactorChannel.get("id").toString(),date);
    		}
    		
    		
    		

    		//获取总功率通道
    		MapEntity totalPowerChannel = dataService.getTotalPowerChannelByOrgId(orgId);
    		
    		//根据当天日期, 获取 功率因素通道的极值
    		MapEntity maxTotalPowerValue = null;
    		if(totalPowerChannel != null) {
    			maxTotalPowerValue = dataService.getChannelMaxValueInADay(totalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity minTotalPowerValue = null;
    		if(totalPowerChannel != null) {
    			minTotalPowerValue = dataService.getChannelMinValueInADay(totalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity avgTotalPowerValue = null;
    		if(totalPowerChannel != null) {
    			avgTotalPowerValue = dataService.getChannelAVGValueInADay(totalPowerChannel.get("id").toString(),date);
    		}
    		
    		MapEntity values = new MapEntity();
    		values.put("max_a_volValue", aMaxVolValue);
    		values.put("min_a_volValue", aMinVolValue);
    		values.put("avg_a_volValue", aAVGVolValue);
    		
    		values.put("max_b_volValue", bMaxVolValue);
    		values.put("min_b_volValue", bMinVolValue);
    		values.put("avg_b_volValue", bAVGVolValue);
    		
    		values.put("max_c_volValue", cMaxVolValue);
    		values.put("min_c_volValue", cMinVolValue);
    		values.put("avg_c_volValue", cAVGVolValue);

    		values.put("max_a_curValue", aMaxCurValue);
    		values.put("min_a_curValue", aMinCurValue);
    		values.put("avg_a_curValue", aAVGCurValue);
    		
    		values.put("max_b_curValue", bMaxCurValue);
    		values.put("min_b_curValue", bMinCurValue);
    		values.put("avg_b_curValue", bAVGCurValue);
    		
    		values.put("max_c_curValue", cMaxCurValue);
    		values.put("min_c_curValue", cMinCurValue);
    		values.put("avg_c_curValue", cAVGCurValue);
    		
    		values.put("max_a_totalPowerValue", aMaxTotalPowerValue);
    		values.put("min_a_totalPowerValue", aMinTotalPowerValue);
    		values.put("avg_a_totalPowerValue", aAVGTotalPowerValue);
    		
    		values.put("max_b_totalPowerValue", bMaxTotalPowerValue);
    		values.put("min_b_totalPowerValue", bMinTotalPowerValue);
    		values.put("avg_b_totalPowerValue", bAVGTotalPowerValue);
    		
    		values.put("max_c_totalPowerValue", cMaxTotalPowerValue);
    		values.put("min_c_totalPowerValue", cMinTotalPowerValue);
    		values.put("avg_c_totalPowerValue", cAVGTotalPowerValue);
    		
    		values.put("max_energyValue", maxEnergyValue);
    		values.put("min_energyValue", minEnergyValue);
    		values.put("avg_energyValue", avgEnergyValue);
    		
    		values.put("max_powerFactorValue", maxPowerFactorValue);
    		values.put("min_powerFactorValue", minPowerFactorValue);
    		values.put("avg_powerFactorValue", avgPowerFactorValue);
    		
    		values.put("max_totalPowerValue", maxTotalPowerValue);
    		values.put("min_totalPowerValue", minTotalPowerValue);
    		values.put("avg_totalPowerValue", avgTotalPowerValue);
    		
    		JSONObject jsonValues = new JSONObject(values);
    		redisTempldate.opsForHash().put("ExtremalData:"+date, orgId, jsonValues.toJSONString());
    		return jsonValues;
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping("/powerCodes")
	@ResponseBody
	public String powerCodes() {

		return ServletUtils.buildRs(true, "通道类型数据", powerDataService.powerCodes());
	}
	
	@RequestMapping(value = { "updateThresholdNum" }) 
	@ResponseBody
	public String updateThresholdNum(String message) {
		System.out.println(message);

		message = message.replace("&quot;", "'");	 
		System.out.println(message);	

		return ServletUtils.buildRs(true, "修改阈值数据", powerDataService.updateThresholdNum(message));
	}
	
	
	
	
	
	//日抄表数据
	@RequestMapping(value = { "dateMetryValue" }) 
	@ResponseBody
	public String dateMetryValue(final String startDate,final String endDate) {
		if(startDate != null && endDate != null) {
			
			MapEntity metryStartValueMap = new MapEntity();
			MapEntity metryEndValueMap = new MapEntity();
			List<MapEntity> metryStartValueList = dataService.getMetryVlaueOfDate(startDate);
			List<MapEntity> metryEndValueList = dataService.getMetryVlaueOfDate(endDate);
//			if(metryValueList.size() == 0) {
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//				    	//获取回路所关联的 有功电能 通道
//				    	List<MapEntity> loopElecChannelList = dataService.getLoopElecChannelList();
//				    	//查询 增量表该回路最后一条数据 ,历史数据表该通道的最后一条数据, 并将该数据id,缓存11分钟.
//				    	for(MapEntity elecChannel : loopElecChannelList) {
//				    		String chId = elecChannel.get("id").toString();
//				    		String orgId = elecChannel.get("orgId").toString();
//				    		String chType = elecChannel.get("chType").toString();
//				    		
//				    		Double value = dataService.getFirstMeterDataOfDay(chId,date);
//				    		if(value == null) {
//				    			value = 0.0;
//				    		}
//				    		logger.debug("value:"+value);
//				    		
//				    		dataService.addMeterData(orgId,chId,date,value);
//				    	}
//					}
//				}).start();
//
//			}
			
			for(MapEntity metryValue : metryStartValueList) {
				Double value = Double.parseDouble(metryValue.get("value").toString());
				String orgId = metryValue.get("orgId").toString();
				metryStartValueMap.put(orgId, value);
			}
			
			for(MapEntity metryValue : metryEndValueList) {
				Double value = Double.parseDouble(metryValue.get("value").toString());
				String orgId = metryValue.get("orgId").toString();
				metryEndValueMap.put(orgId, value);
			}
			
			int type = 9;
			int level = 6;
			
			List<List<MapEntity>> channelLevelList = new ArrayList<>(); 
			//数据加载
			for(int i=0; i<level; i++) {
				//7是进线柜, 9+是回路
				if( i == 0)
					type = 7;
				else
					//出线柜这一级被跳过了
					type = 8;
				
				List<MapEntity> loopChannelList = powerAnalysisService.getLoopChannel(String.valueOf(type+i));
				
				for(MapEntity loopChannel : loopChannelList) {
					String chId = loopChannel.get("chId").toString();
					String orgId = loopChannel.get("orgId").toString();
					
					Object startValueObj = metryStartValueMap.get(orgId);
					if(startValueObj == null)
						loopChannel.put("startValue", 0.0);
					else 
						loopChannel.put("startValue", startValueObj);
					
					Object endValueObj = metryEndValueMap.get(orgId);
					if(endValueObj == null)
						loopChannel.put("endValue", 0.0);
					else 
						loopChannel.put("endValue", endValueObj);
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
			
			return ServletUtils.buildRs(true, "日抄表数据", pdfList);
		} else {
			return ServletUtils.buildRs(true, "日期为空", "");
		}
		
		
	}
	
	
	
	
	

	
	//导出日抄表数据
	@RequestMapping(value = { "exportDateMetryValue" }) 
	@ResponseBody
	public void exportDateMetryValue(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,final String startDate,final String endDate) throws IOException {
		if(startDate != null && endDate != null) {

			MapEntity metryStartValueMap = new MapEntity();
			MapEntity metryEndValueMap = new MapEntity();
			List<MapEntity> metryStartValueList = dataService.getMetryVlaueOfDate(startDate);
			List<MapEntity> metryEndValueList = dataService.getMetryVlaueOfDate(endDate);

			for(MapEntity metryValue : metryStartValueList) {
				Double value = Double.parseDouble(metryValue.get("value").toString());
				String orgId = metryValue.get("orgId").toString();
				metryStartValueMap.put(orgId, value);
			}
			
			for(MapEntity metryValue : metryEndValueList) {
				Double value = Double.parseDouble(metryValue.get("value").toString());
				String orgId = metryValue.get("orgId").toString();
				metryEndValueMap.put(orgId, value);
			}			
			
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
					
					Object startValueObj = metryStartValueMap.get(orgId);
					if(startValueObj == null)
						loopChannel.put("startValue", 0.0);
					else 
						loopChannel.put("startValue", startValueObj);
					
					Object endValueObj = metryEndValueMap.get(orgId);
					if(endValueObj == null)
						loopChannel.put("endValue", 0.0);
					else 
						loopChannel.put("endValue", endValueObj);
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
			
			powerDataService.exportDateMetryValue(httpServletRequest,httpServletResponse,pdfList,startDate,endDate);
			
		}
		
	}
	
	
	
	
	
	
	
}