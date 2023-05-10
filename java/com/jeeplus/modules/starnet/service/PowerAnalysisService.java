/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xslf.usermodel.VerticalAlignment;
import org.restlet.engine.local.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.support.json.JSONParser;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.OverviewDao;
import com.jeeplus.modules.maintenance.entity.PdfMaintenanceDetail;
import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.service.TDeviceConfigService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.starnet.dao.EnergyAnalysisDao;
import com.jeeplus.modules.starnet.dao.PowerAnalysisDao;
import com.jeeplus.modules.starnet.dao.PowerDataDao;
import com.jeeplus.modules.starnet.web.EnergyAnalysisController;
import com.jeeplus.modules.sys.dao.AreaDao;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.dao.PdfOrderDao;

/**
 * 数据配置Service
 * 
 * @author long
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class PowerAnalysisService {
	
	public static Logger logger = LoggerFactory.getLogger(PowerAnalysisService.class);
	
	@Autowired
	private PowerAnalysisDao powerAnalysisDao;

	@Autowired
	private EnergyAnalysisService energyAnalysisService;




	public List<MapEntity> selectCharges() {
		return powerAnalysisDao.selectCharges();

	}



	public List<MapEntity> getBreauOrgId(String beginTime, String endTime) {

		List<MapEntity> breauOrgId = powerAnalysisDao.getBreauOrgId(beginTime, endTime);

	    return breauOrgId;

	}

//分时段分析
	public List<MapEntity> daypartData(String devIds, String beginTime, String endTime) {
		String chIds = energyAnalysisService.getChIds(devIds);

		List<MapEntity> analysisReports = powerAnalysisDao.daypartData(chIds, beginTime, endTime);
		// for (MapEntity mapEntity : analysisReports) {
		// Double allvalue = Double.parseDouble(mapEntity.get("value0").toString())
		// + Double.parseDouble(mapEntity.get("value1").toString())
		// + Double.parseDouble(mapEntity.get("value2").toString());
		// Double allamount = Double.parseDouble(mapEntity.get("amount0").toString())
		// + Double.parseDouble(mapEntity.get("amount1").toString())
		// + Double.parseDouble(mapEntity.get("amount2").toString());
		// mapEntity.put("allvalue", allvalue);
		// mapEntity.put("allamount", allamount);
		// }
		return analysisReports;

	}

	@Transactional(readOnly = false)
	public void modifyCharges(JSONArray ja) {

		powerAnalysisDao.deleteChanger();
		for (int i = 0; i < ja.size(); i++) {
			MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
			powerAnalysisDao.modifyCharges(entity.get("startTime").toString(), entity.get("endTime").toString(),
					entity.get("state").toString(), entity.get("price").toString());
		}

	}

	// 导出用电报表
	public void exportAnalysisReports(String devIds, String time, Integer begin, Integer end,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		//List<MapEntity> list = this.analysisReports(devIds, time, begin, end);
		List<MapEntity> list = new ArrayList<>();
		
		
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("111");
// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(15);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);

		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("回路名称");
		cell.setCellStyle(style);
		int n = 1;
		for (int i = begin; i <= end; i++) {

			cell = row.createCell((short) n);
			n++;
			cell.setCellValue((i < 10 ? "0" : "") + i + "时");
			cell.setCellStyle(style);
		}

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(15);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "";
		fileName = "用电报表";
		for (int i = 0; i < list.size(); i++) {
			int m = 1;
			row = sheet.createRow((int) i + 1);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值"分区名称") + ")统计表
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("回路名称"));
			cell.setCellStyle(style1);

			for (int j = begin; j <= end; j++) {

				cell = row.createCell((short) m);
				m++;
				cell.setCellValue((String) excel.get((j < 10 ? "0" : "") + j));
				cell.setCellStyle(style1);

			}
		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();

	}

	/*
	// 导出电能报表
	public void exportelEctricReport(String devIds, String beginTime, String endTime,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

		List<MapEntity> list = this.electricReport(devIds, beginTime, endTime);

		//第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		//第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("111");
		//第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		//第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);

		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("回路名称");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("起始数据");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("截止数据");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("差距");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "";
		for (int i = 0; i < list.size(); i++) {

			row = sheet.createRow((int) i + 1);
			Map excel = list.get(i);
			fileName = "电能报表";
			// 第四步，创建单元格，并设置值"分区名称") + ")统计表
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("name"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("beginValue").toString());
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("endValue").toString());
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue((String) excel.get("diffValue").toString());
			cell.setCellStyle(style1);

		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}
	*/

	/*
	public void exportDaypartDataRport(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("光纤分段统计表");
// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row0 = sheet.createRow((int) 0);

		HSSFRow row = sheet.createRow((int) 1);
// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(20);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);

		Region region1 = new Region(0, (short) 1, 0, (short) 3);
		// 参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
		sheet.addMergedRegion(region1);
		Region region2 = new Region(0, (short) 4, 0, (short) 6);
		sheet.addMergedRegion(region2);
		Region region3 = new Region(0, (short) 0, 1, (short) 0);
		sheet.addMergedRegion(region3);

		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		HSSFCell cell0 = row0.createCell((short) 0);
		cell0.setCellValue("分区名称");
		cell0.setCellStyle(style);

		cell0 = row0.createCell((short) 1);
		cell0.setCellValue("峰");
		cell0.setCellStyle(style);
		cell0 = row0.createCell((short) 4);
		cell0.setCellValue("平");
		cell0.setCellStyle(style);
		cell0 = row0.createCell((short) 8);
		cell0.setCellValue("谷");
		cell0.setCellStyle(style);

		cell0 = row0.createCell((short) 11);
		cell0.setCellValue("合计");
		cell0.setCellStyle(style);

		HSSFCell cell = row.createCell((short) 1);
		cell.setCellValue("电量");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("单价");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("金额");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("电量");
		cell.setCellStyle(style);
		cell = row.createCell((short) 5);
		cell.setCellValue("单价");
		cell.setCellStyle(style);
		cell = row.createCell((short) 6);
		cell.setCellValue("金额");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("电量");
		cell.setCellStyle(style);
		cell = row.createCell((short) 8);
		cell.setCellValue("单价");
		cell.setCellStyle(style);
		cell = row.createCell((short) 9);
		cell.setCellValue("金额");
		cell.setCellStyle(style);

		cell = row.createCell((short) 10);
		cell.setCellValue("电量(KW.H)");
		cell.setCellStyle(style);
		cell = row.createCell((short) 11);
		cell.setCellValue("金额(元)");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(20);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "分时段用电";
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 2);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("name"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("value0"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("price0"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue((String) excel.get("amount0"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 4);
			cell.setCellValue((String) excel.get("value1"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 5);
			cell.setCellValue((String) excel.get("price1"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 6);
			cell.setCellValue((String) excel.get("amount1"));
			cell.setCellStyle(style1);

			cell = row.createCell((short) 7);
			cell.setCellValue((String) excel.get("value2"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 8);
			cell.setCellValue((String) excel.get("price2"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 9);
			cell.setCellValue((String) excel.get("amount2"));
			cell.setCellStyle(style1);

			cell = row.createCell((short) 10);

			cell.setCellValue(excel.get("allvalue").toString());
			cell.setCellStyle(style1);
			cell = row.createCell((short) 11);
			cell.setCellValue(excel.get("allamount").toString());
			cell.setCellStyle(style1);
		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();

	}
	*/
	
	static int rowNum = 0;
	// 导出线能损耗报表
	public void exportLineLossData(HttpServletRequest httpServletRequest, 
								   HttpServletResponse httpServletResponse,
								   List<MapEntity> list,String month) throws Exception {
		//第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		//第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("线损能耗");
		//第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		//第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(20);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);

		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("柜号");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 1);
		cell.setCellValue("回路名称");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 2);
		cell.setCellValue("层级");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 3);
		cell.setCellValue("起始数值(kw·h)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 4);
		cell.setCellValue("截止数值(kw·h)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 5);
		cell.setCellValue("当前支路能耗(kw·h)");
		cell.setCellStyle(style);
		
		
		
		cell = row.createCell((short) 6);
		cell.setCellValue("差异值占比");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 7);
		cell.setCellValue("整后用电量");
		cell.setCellStyle(style);
		

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 创建一个居中格式
		style2.setFont(nameRowFont1);
		
		String fileName = "线能损耗回路查看报表"+month;

		//1.开始行 2.结束行  3.开始列  4.结束列
		//CellRangeAddress region = new CellRangeAddress(1,2,0,0);
		
		rowNum = 1;
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(rowNum++);
			Map excel = list.get(i);

			// 第四步，创建单元格，并设置值"分区名称") + ")统计表
			cell = row.createCell((short) 1);
			Object orgNameObj =  excel.get("orgName");
			if(orgNameObj != null) {
				cell.setCellValue(orgNameObj.toString());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style2);
			
			
			cell = row.createCell((short) 2);
			cell.setCellValue("");
			cell.setCellStyle(style1);
			
			
			cell = row.createCell((short) 3);
			Object startValueObj =  excel.get("startValue");
			if(startValueObj != null) {
				cell.setCellValue(startValueObj.toString());
			} else {
				cell.setCellValue("");
			}			
			cell.setCellStyle(style1);
			
			
			cell = row.createCell((short) 4);
			Object endValueObj =  excel.get("endValue");
			if(endValueObj != null) {
				cell.setCellValue(endValueObj.toString());
			} else {
				cell.setCellValue("");
			}		
			cell.setCellStyle(style1);
			
			
			cell = row.createCell((short) 5);
			Object currentLoopEnergyConsumptionObj =  excel.get("currentLoopEnergyConsumption");
			if(currentLoopEnergyConsumptionObj != null) {
				cell.setCellValue(String.format("%.2f",Double.parseDouble(currentLoopEnergyConsumptionObj.toString())));
			} else {
				cell.setCellValue("");
			}		
			cell.setCellStyle(style1);
			
			
			
			
			cell = row.createCell((short) 6);
			Object diffPercentage =  excel.get("diffPercentage");
			if(diffPercentage != null) {
				cell.setCellValue(String.format("%.2f",Double.parseDouble(diffPercentage.toString())));
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style1);
			
			
			cell = row.createCell((short) 7);
			Object adjustedConsumption2 =  excel.get("adjustedConsumption2");
			if(adjustedConsumption2 != null) {
				cell.setCellValue(String.format("%.2f",Double.parseDouble(adjustedConsumption2.toString())));
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style1);
			
			fillChildLoopExcelData(wb,sheet,style1,style2,(JSONArray)excel.get("children"));
		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}
	
	public void fillChildLoopExcelData(HSSFWorkbook wb,HSSFSheet sheet,HSSFCellStyle style1,HSSFCellStyle style2,JSONArray children) {
		if(children != null) {
			HSSFRow row = null;
			HSSFCell cell = null;
			for (int i = 0; i < children.size(); i++) {
				row = sheet.createRow(rowNum);
				JSONObject excel = children.getJSONObject(i);
				String type = excel.getString("type");
				if(type.equals("7")) {
					CellRangeAddress region = new CellRangeAddress(rowNum,rowNum,0,1);
					sheet.addMergedRegion(region);
				}
				//总表节点合并
				int startRowNum = rowNum;
				
				if(!type.equals("8")) {
					rowNum++;
				}

				String marginStr = "";
				for(int k=0;k<(Integer.parseInt(type)-9);k++) {
					marginStr += "    ";
				}

				// 第四步，创建单元格，并设置值"分区名称") + ")统计表
				HSSFCellStyle nameStype = null;
				String nameMarginStr = "";
				if(type.equals("7")) {
					cell = row.createCell((short)0);
					nameMarginStr = "";
					nameStype = style1;
				} else {
					cell = row.createCell((short)1);
					nameMarginStr = marginStr;
					nameStype = style2;
				}
				
				
				String orgNameObj =  excel.getString("orgName");
				if(orgNameObj != null) {
					cell.setCellValue(nameMarginStr+orgNameObj.toString());
				} else {
					cell.setCellValue("");
				}
				cell.setCellStyle(nameStype);
				
				
				
				
				
				if(type.equals("7")) {
					cell = row.createCell((short)2);
					cell.setCellValue("1级");
					cell.setCellStyle(style1);
				} else {
					
					if(!type.equals("8")) {
						int level = Integer.parseInt(type) - 7;
						cell = row.createCell((short)2);
						cell.setCellValue(level + "级");
						cell.setCellStyle(style1);
					}

				}
				
				
				
				
				
				
				cell = row.createCell((short)3);
				String startValueObj =  excel.getString("startValue");
				if(startValueObj != null) {
					cell.setCellValue(startValueObj.toString());
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				cell = row.createCell((short)4);
				String endValueObj =  excel.getString("endValue");
				if(endValueObj != null) {
					cell.setCellValue(endValueObj.toString());
				} else {
					cell.setCellValue("");
				}		
				cell.setCellStyle(style1);
				
				
				cell = row.createCell((short)5);
				String currentLoopEnergyConsumptionObj =  excel.getString("currentLoopEnergyConsumption");
				if(currentLoopEnergyConsumptionObj != null) {
					cell.setCellValue(String.format("%.2f",Double.parseDouble(currentLoopEnergyConsumptionObj.toString())));
				} else {
					cell.setCellValue("");
				}		
				cell.setCellStyle(style1);
				
				
				
				
				cell = row.createCell((short)6);
				String diffPercentage =  excel.getString("diffPercentage");
				if(diffPercentage != null) {
					cell.setCellValue(String.format("%.2f",Double.parseDouble(diffPercentage.toString())));
				} else {
					cell.setCellValue("");
				}
				cell.setCellStyle(style1);
				
				
				cell = row.createCell((short)7);
				String adjustedConsumption2 =  excel.getString("adjustedConsumption2");
				if(adjustedConsumption2 != null) {
					cell.setCellValue(String.format("%.2f",Double.parseDouble(adjustedConsumption2.toString())));
				} else {
					cell.setCellValue("");
				}
				cell.setCellStyle(style1);
				

				
				fillChildLoopExcelData(wb,sheet,style1,style2,excel.getJSONArray("children"));
				
				if(type.equals("8")) {
					CellRangeAddress region = null;
					if(startRowNum < rowNum) {
						region = new CellRangeAddress(startRowNum,rowNum-1,0,0);
					} else {
						region = new CellRangeAddress(startRowNum,rowNum,0,0);
					}
					sheet.addMergedRegion(region);
					//tempRow
					cell = row.createCell((short)0);
					String orgNameObj2 =  excel.getString("orgName");
					if(orgNameObj2 != null) {
						String fillName = orgNameObj2.toString().replaceAll("出线柜", "");
						fillName = fillName.replaceAll("（", "");
						fillName = fillName.replaceAll("）", "");
						fillName = fillName.replaceAll("[(]", "");
						fillName = fillName.replaceAll("[)]", "");
						cell.setCellValue(fillName);
					} else {
						cell.setCellValue("");
					}
					
					HSSFFont nameRowFont1 = wb.createFont();
					nameRowFont1.setFontName("微软雅黑");
					nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
					HSSFCellStyle nameStyle = wb.createCellStyle();
					
					nameStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					nameStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
					
					nameStyle.setFont(nameRowFont1);
					cell.setCellStyle(nameStyle);
					
				}
				
				
				
			}
		}
	}


	//导出线能损耗报表
	public void exportBreauOrgId(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,Collection<JSONObject> list) throws Exception {

		//第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		//第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("111");
		//第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		//第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);

		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("用电单位");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("表计用电量");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("整后用电量");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 3);
		cell.setCellValue("固定调整电量");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 4);
		cell.setCellValue("汇总用电量");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "公司查看报表";
		
		int i = 0;
		for(JSONObject excel : list) {
			
			//[{"adjustedConsumption":0.0,"unitName":"福建星网天和智能科技有限公司","adjust":0,"unitId":"22","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网智慧科技有限公司","adjust":0,"unitId":"23","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网视易信息系统有限公司","adjust":0,"unitId":"24","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"睿云联（厦门）网络通讯技术有限公司","adjust":0,"unitId":"25","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网视易信息系统有限公司","adjust":0,"unitId":"26","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建升腾资讯有限公司","adjust":0,"unitId":"27","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"财务部","adjust":0,"unitId":"28","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":3565.6384072427622,"unitName":"高管办公室待定","adjust":0,"unitId":"29","consumption":2354.352,"totalConsumption":3565.6384072427622}, {"adjustedConsumption":0.0,"unitName":"运作管理审查部","adjust":0,"unitId":"10","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网创智科技有限公司","adjust":0,"unitId":"11","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"销售物流部","adjust":0,"unitId":"12","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"体系管理中心","adjust":0,"unitId":"13","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"工业设计部","adjust":0,"unitId":"14","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建凯米网络科技有限公司","adjust":0,"unitId":"15","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网天和智能科技有限公司","adjust":0,"unitId":"16","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"睿云联（厦门）网络通讯技术有限公司","adjust":0,"unitId":"17","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网视易信息系统有限公司","adjust":0,"unitId":"18","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建凯米网络科技有限公司","adjust":0,"unitId":"19","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"前台","adjust":0,"unitId":"3","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"信息中心","adjust":0,"unitId":"4","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"办公室","adjust":0,"unitId":"5","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"财务部","adjust":0,"unitId":"6","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"采购部品部","adjust":0,"unitId":"7","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":18843.86764477129,"unitName":"自动开发部","adjust":0,"unitId":"8","consumption":18770.5,"totalConsumption":18843.86764477129}, {"adjustedConsumption":0.0,"unitName":"品质部","adjust":0,"unitId":"9","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网元智科技有限公司","adjust":0,"unitId":"20","consumption":0.0,"totalConsumption":0.0}, {"adjustedConsumption":0.0,"unitName":"福建星网信通软件有限公司","adjust":0,"unitId":"21","consumption":0.0,"totalConsumption":0.0}]
			
			row = sheet.createRow((int) i + 1);
			//第四步，创建单元格，并设置值"分区名称") + ")统计表
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("unitName"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue( String.format("%.2f",Double.parseDouble( excel.get("consumption").toString() )) );
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue( String.format("%.2f",Double.parseDouble( excel.get("adjustedConsumption").toString() )) );
			cell.setCellStyle(style1);
			
			cell = row.createCell((short) 3);
			cell.setCellValue( String.format("%.2f",Double.parseDouble( excel.get("adjust").toString() )) );
			cell.setCellStyle(style1);
			
			cell = row.createCell((short) 4);
			cell.setCellValue( String.format("%.2f",Double.parseDouble( excel.get("totalConsumption").toString() )) );
			cell.setCellStyle(style1);
			
			i++;
		}
		
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}

	
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
	/*
	public List<MapEntity> analysisReports(String loopId, String unitId, String time, Integer begin, Integer end) {

		String beginTime = time + " " + begin + ":00:00";
		String endTime = time + " " + end + ":59:59";

		String chIds = energyAnalysisService.getChIds(devIds);
		List<String> numList = new ArrayList<String>();
		for (int i = begin; i <= end; i++) {
			numList.add((i < 10 ? "0" : "") + i);
		}

		List<MapEntity> analysisReports = powerAnalysisDao.analysisReports(chIds, beginTime, endTime, numList);

//				Set<String> sets = new HashSet<String>();
//				for (MapEntity mapEntity : analysisReports) {
//					sets.add(mapEntity.get("name").toString());
//				}
//
//				List<List<MapEntity>> list = new ArrayList<List<MapEntity>>();
//				for (String name : sets) {
//
//					List<MapEntity> idList = new ArrayList<MapEntity>();
//					MapEntity en = new MapEntity();
//					for (int i = 0; i < analysisReports.size(); i++) {
//						if (name.equals(analysisReports.get(i).get("name"))) {
//							BigDecimal b = null;
//							if (i == 0) {
//								b = new BigDecimal(Double.valueOf(analysisReports.get(i).get("mvalue").toString())
//										- Double.valueOf(analysisReports.get(i).get("minvalue").toString()));
//							} else {
//								b = new BigDecimal(Double.valueOf(analysisReports.get(i).get("mvalue").toString())
//										- Double.valueOf(analysisReports.get(i - 1).get("mvalue").toString()));
//							}
//							double value = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//
//							analysisReports.get(i).put("value", value);
//							idList.add(analysisReports.get(i));
//
//						}
//
//					}
//
//					list.add(idList);
//				}
		return analysisReports;
	}
	*/


	public List<MapEntity> analysisReportByLoopId(String loopId, String time, String begin, String end) {
		String startTime = time + " " + begin + ":00:00";
		String endTime = time + " " + end + ":59:59";
		List<MapEntity> showTimeList = powerAnalysisDao.analysisReportByLoopId(loopId,startTime,endTime); 
		return showTimeList;
	}
	
	public List<MapEntity> analysisReportByUnitId(String unitId, String time, String begin, String end) {
		String startTime = time + " " + begin + ":00:00";
		String endTime = time + " " + end + ":59:59";
		return powerAnalysisDao.analysisReportByUnitId(unitId,startTime,endTime);
	}
	/*
	public List<MapEntity> electricReport(String devIds, String beginTime, String endTime) {

		String chIds = energyAnalysisService.getChIds(devIds);
		List<MapEntity> analysisReports = powerAnalysisDao.electricReport(chIds, beginTime, endTime);

		return analysisReports;

	}
	 */
	
	public MapEntity electricReportByLoopId(String loopId, String startTime, String endTime) {
		MapEntity analysisReports = powerAnalysisDao.electricReportByLoopId(loopId, startTime, endTime);
		return analysisReports;
	}
	
	public MapEntity electricReportByUnitId(String unitid, String startTime, String endTime) {
		MapEntity analysisReports = powerAnalysisDao.electricReportByUnitId(unitid, startTime, endTime);
		return analysisReports;
	}

	public MapEntity getStarElectricityUnit(String unitid) {
		MapEntity starElectricityUnit = powerAnalysisDao.getStarElectricityUnit(unitid);
		return starElectricityUnit;
	}
	
	public List<MapEntity> getLineLossData(String beginTime, String endTime,String orgType) {
		return powerAnalysisDao.getLineLossData(beginTime,endTime,orgType);
	}

	public List<MapEntity> getLineLossMaxData(String beginTime, String endTime,String orgType) {
		
		List<Map<String,String>> chIdHistoryTimeList = powerAnalysisDao.getLineLossMaxDataChIdAndHistoryTime(beginTime,endTime,orgType);
		
		//List<Map<String,String>> dataIds = powerAnalysisDao.getLineLossDataBeginId(firstDayStr,lastDayStr,orgType);
		List<MapEntity> datas = new ArrayList<>();
		for(Map<String,String> data : chIdHistoryTimeList) {
			logger.debug("function getLineLossMaxData");
			logger.debug("beginTime:" + beginTime + ",endTime:" + endTime + ",orgType:" + orgType);
			logger.debug(data.toString());
			
			String chId = String.valueOf(data.get("chId"));
			String historyTime = String.valueOf(data.get("historyTime"));
			/*
			thdf.id,
			thdf.ch_id chId,
			thdf.history_value historyValue,
			thdf.history_time historyTime
			 */
			List<MapEntity> historyData = powerAnalysisDao.getHistoryData(chId,historyTime);
			logger.debug(historyData.toString());
			datas.addAll(historyData);

		}
		return datas;
	}
	

	public List<MapEntity> getLineLossMinData(String beginTime, String endTime, String orgType) {
		List<Map<String,String>> chIdHistoryTimeList = powerAnalysisDao.getLineLossMinDataChIdAndHistoryTime(beginTime,endTime,orgType);
		
		//List<Map<String,String>> dataIds = powerAnalysisDao.getLineLossDataBeginId(firstDayStr,lastDayStr,orgType);
		List<MapEntity> datas = new ArrayList<>();
		for(Map<String,String> data : chIdHistoryTimeList) {
			logger.debug("function getLineLossMinData");
			logger.debug("beginTime:" + beginTime + ",endTime:" + endTime + ",orgType:" + orgType);
			logger.debug(data.toString());
			
			String chId = String.valueOf(data.get("chId"));
			String historyTime = String.valueOf(data.get("historyTime"));
			/*
			thdf.id,
			thdf.ch_id chId,
			thdf.history_value historyValue,
			thdf.history_time historyTime
			 */
			List<MapEntity> historyData = powerAnalysisDao.getHistoryData(chId,historyTime);
			logger.debug(historyData.toString());
			datas.addAll(historyData);

		}
		return datas;
	}

	public List<MapEntity> getLoopChannel(String orgType) {
		return powerAnalysisDao.getLoopChannel(orgType);
	}


	public List<MapEntity> getUnitLoopList() {
		return powerAnalysisDao.getUnitLoopList();
	}

	
	public List<MapEntity> getUnitList() {
		return powerAnalysisDao.getUnitList();
	}

	public List<MapEntity> getPdfList() {
		return powerAnalysisDao.getPdfList();
	}



	public List<MapEntity> getLoopMonthConsumption(String month) {
		List<MapEntity> list = powerAnalysisDao.getLoopMonthConsumption(month);
		return list;
	}



	public List<MapEntity> getOutLoopList() {
		List<MapEntity> list = powerAnalysisDao.getOutLoopList();
		return list;
	}








	
}
