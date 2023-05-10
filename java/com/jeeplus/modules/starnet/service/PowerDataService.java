/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.restlet.engine.local.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.jeeplus.modules.starnet.dao.PowerDataDao;
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
public class PowerDataService {
	@Autowired
	private PowerDataDao powerDataDao;

	public List<MapEntity> powerDataList(String devId, String time, String ids, String type) {

		List<MapEntity> powerDataList = powerDataDao.powerDataList(devId, time, ids, type);
		for (int i = 0; i < powerDataList.size(); i++) {

			BigDecimal b = null;
//			if (i == 0) {
				b = new BigDecimal(Double.valueOf(powerDataList.get(i).get("mValue").toString())
						- Double.valueOf(powerDataList.get(i).get("minValue").toString()));
//			} else {
//				b = new BigDecimal(Double.valueOf(powerDataList.get(i).get("mValue").toString())
//						- Double.valueOf(powerDataList.get(i - 1).get("mValue").toString()));
//			}
			double value = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			powerDataList.get(i).put("187", value);
		}
		return powerDataList;

	}


	public List<MapEntity> powerCodes() {

		return powerDataDao.powerCodes();
	}

	@Transactional(readOnly = false)
	public String updateThresholdNum(String message) {
		try {
			JSONArray ja = JSONArray.parseArray(message);
			for (int i = 0; i < ja.size(); i++) {
				MapEntity detail = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);

				powerDataDao.updateThresholdNum(detail.get("id").toString(), detail.get("num").toString());

			}

		} catch (Exception e) {
			e.printStackTrace();
			return "false";
		}

		return "true";
	}

	// 导出日原始数据
	public void exportChannelRportList(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,List<Map<String,Object>> list,String time) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		//第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("日原始数据");
		//第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 1);
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
		cell.setCellValue("采集时间");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 2);
		cell.setCellValue("吸收有功电能");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 3);
		cell.setCellValue("A相电压(V)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 4);
		cell.setCellValue("B相电压(V)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 5);
		cell.setCellValue("C相电压(V)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 6);
		cell.setCellValue("A电流(A)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 7);
		cell.setCellValue("B电流(A)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 8);
		cell.setCellValue("C电流(A)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 9);
		cell.setCellValue("A相总功率(W)");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 10);
		cell.setCellValue("B相总功率(W)");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 11);
		cell.setCellValue("C相总功率(W)");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 12);
		cell.setCellValue("总功率(W)");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 13);
		cell.setCellValue("AB电压(V)");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 14);
		cell.setCellValue("BC电压(V)");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 15);
		cell.setCellValue("CA电压(V)");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 16);
		cell.setCellValue("功率因数");
		cell.setCellStyle(style);
		
		
		cell = row.createCell((short) 17);
		cell.setCellValue("电源频率(Hz)");
		cell.setCellStyle(style);
 
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String 	fileName = "日原始数据_"+time;
		int i=0;
		for (Map<String,Object> map: list ) {
			
			System.out.print(map);
			List<MapEntity> datalist = (List<MapEntity>)map.get("datalist");
			for(MapEntity data:datalist) {
				row = sheet.createRow((int) i + 2);
				
				String loopName = data.get("name").toString();
				String historyTime = data.get("historyTime").toString();
				/*
				{382=0.0, 174=0.0, 163=0.0, 383=0.0, 153=100.0, 164=0.0, 384=0.0, 165=0.0, 385=0.0, 166=0.0, 167=0.0, 168=0.0, 105=0.0, 106=0.0, 403=0.0, 
				name=出线回路（4BB-6WP 备用）, 107=0.0, 404=0.0, historyTime=2022-06-13 00:00:03}, 
				 */
				
				//吸收有功电能
				String loopE = "0";
				if(data.get("403") != null) {
					loopE = data.get("403").toString();
				}
				
				//A B C电压
				String AV = "0";
				if(data.get("105") != null) {
					AV = data.get("105").toString();
				}
				String BV = "0";
				if(data.get("106") != null) {
					BV = data.get("106").toString();
				}				
				String CV = "0";
				if(data.get("107") != null) {
					CV = data.get("107").toString();
				}	
				//A B C电流
				String AA = "0";
				if(data.get("166") != null) {
					AA = data.get("166").toString();
				}					
				String BA = "0";
				if(data.get("167") != null) {
					BA = data.get("167").toString();
				}	
				String CA = "0";
				if(data.get("168") != null) {
					CA = data.get("168").toString();
				}	
				//A相B相C相功率  总功率
				String AW = "0";
				if(data.get("382") != null) {
					AW = data.get("382").toString();
				}					
				String BW = "0";
				if(data.get("383") != null) {
					BW = data.get("383").toString();
				}									
				String CW = "0";
				if(data.get("384") != null) {
					CW = data.get("384").toString();
				}							
				String W = "0";
				if(data.get("385") != null) {
					W = data.get("385").toString();
				}			
				//AB  BC CA电压
				String ABV = "0";
				if(data.get("163") != null) {
					ABV = data.get("163").toString();
				}			
				String BCV = "0";
				if(data.get("164") != null) {
					BCV = data.get("164").toString();
				}						
				String CAV = "0";
				if(data.get("165") != null) {
					CAV = data.get("165").toString();
				}	
				//功率因数
				String powerFactor = "0";
				if(data.get("153") != null) {
					powerFactor = data.get("153").toString();
				}	
				//电源频率
				String powerHz = "0";
				if(data.get("174") != null) {
					powerHz = data.get("174").toString();
				}	
				
				
				

				// 第四步，创建单元格，并设置值"分区名称") + ")统计表
				cell = row.createCell((short) 0);
				cell.setCellValue(loopName);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 1);
				cell.setCellValue(historyTime);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 2);
				cell.setCellValue(loopE);
				cell.setCellStyle(style1);
				
				
				
				
				cell = row.createCell((short) 3);
				cell.setCellValue(AV);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 4);
				cell.setCellValue(BV);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 5);
				cell.setCellValue(CV);
				cell.setCellStyle(style1);
				
				
				
				
				
				
				cell = row.createCell((short) 6);
				cell.setCellValue(AA);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 7);
				cell.setCellValue(BA);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 8);
				cell.setCellValue(CA);
				cell.setCellStyle(style1);
				
				
				
				
				
				cell = row.createCell((short) 9);
				cell.setCellValue(AW);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 10);
				cell.setCellValue(BW);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 11);
				cell.setCellValue(CW);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 12);
				cell.setCellValue(W);
				cell.setCellStyle(style1);
				
				
				
				
				
				
				
				cell = row.createCell((short) 13);
				cell.setCellValue(ABV);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 14);
				cell.setCellValue(BCV);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 15);
				cell.setCellValue(CAV);
				cell.setCellStyle(style1);
				
				
				
				cell = row.createCell((short) 16);
				cell.setCellValue(powerFactor);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 17);
				cell.setCellValue(powerHz);
				cell.setCellStyle(style1);
				
				
				
				 
				i++;
			}
		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();

	}

	// 导出逐日极值
	public void exportextremalDatas(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, List<MapEntity> list) throws Exception {
		/*
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("光纤分段统计表");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row2 = sheet.createRow((int) 0);
		HSSFRow row0 = sheet.createRow((int) 1);
		HSSFRow row = sheet.createRow((int) 2);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(18);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);

		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		HSSFCell cell0 = null;

		HSSFCell cell2 = null;
		String[] split = value.split(",");
		for (int i = 0; i < split.length; i++) {
			cell2 = row2.createCell((short) 4 + i * 5);
			cell2.setCellValue(split[i]);
			cell2.setCellStyle(style);

			cell0 = row0.createCell((short) 2 + i * 5);
			cell0.setCellValue("最大值");
			cell0.setCellStyle(style);
			cell0 = row0.createCell((short) 4 + i * 5);
			cell0.setCellValue("最小值");
			cell0.setCellStyle(style);

		}

		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中

		Region region1 = new Region(0, (short) 1, 0, (short) 3);
		// 参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号

		Region region2 = new Region(1, (short) 2, 1, (short) 3);
		// 参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号

		Region region3 = new Region(1, (short) 4, 1, (short) 5);
		// 参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号

		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("回路名称");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("日期");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("数值");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("发生时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 4);
		cell.setCellValue("数值");
		cell.setCellStyle(style);
		cell = row.createCell((short) 5);
		cell.setCellValue("发生时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 6);
		cell.setCellValue("平均值");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("数值");
		cell.setCellStyle(style);
		cell = row.createCell((short) 8);
		cell.setCellValue("发生时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 9);
		cell.setCellValue("数值");
		cell.setCellStyle(style);
		cell = row.createCell((short) 10);
		cell.setCellValue("发生时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 11);
		cell.setCellValue("平均值");
		cell.setCellStyle(style);

		cell = row.createCell((short) 12);
		cell.setCellValue("数值");
		cell.setCellStyle(style);
		cell = row.createCell((short) 13);
		cell.setCellValue("发生时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 14);
		cell.setCellValue("数值");
		cell.setCellStyle(style);
		cell = row.createCell((short) 15);
		cell.setCellValue("发生时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 16);
		cell.setCellValue("平均值");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(18);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "";
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 3);
			Map excel = list.get(i);
			fileName = "逐日极值统计表";
			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("回路名称"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("采集时间"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("maxvalue1"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue((String) excel.get("maxtime1"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 4);
			cell.setCellValue((String) excel.get("minvalue1"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 5);
			cell.setCellValue((String) excel.get("mintime1"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 6);
			cell.setCellValue((String) excel.get("avgvalue1"));
			cell.setCellStyle(style1);

			cell = row.createCell((short) 7);
			cell.setCellValue((String) excel.get("maxvalue2"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 8);
			cell.setCellValue((String) excel.get("maxtime2"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 9);
			cell.setCellValue((String) excel.get("minvalue2"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 10);
			cell.setCellValue((String) excel.get("mintime2"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 11);
			cell.setCellValue((String) excel.get("avgvalue2"));
			cell.setCellStyle(style1);

			cell = row.createCell((short) 12);
			cell.setCellValue((String) excel.get("maxvalue3"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 13);
			cell.setCellValue((String) excel.get("maxtime3"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 14);
			cell.setCellValue((String) excel.get("minvalue3"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 15);
			cell.setCellValue((String) excel.get("mintime3"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 16);
			cell.setCellValue((String) excel.get("avgvalue3"));
			cell.setCellStyle(style1);

		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
		*/
	}
	
	
	
	
	
	public List<MapEntity> getDayDataList(String orgId, String time) {
		return powerDataDao.getDayDataList(orgId,time);
	}
	
	public List<MapEntity> extremalDatas(String orgId, String startTime, String endTime,String chType) {
		return powerDataDao.extremalDatas(orgId,startTime,endTime,chType);
	}


	
	static int rowNum = 0;
	public void exportDateMetryValue(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list,String startDate,String endDate) throws IOException {

		//第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		//第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("日抄表数据");
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
		cell.setCellValue("起始用电量");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 3);
		cell.setCellValue("结束用电量");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("用电量");
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
		
		
		
		String fileName = "日抄表数据_" + startDate + "_" + endDate;
		
		rowNum = 1;
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(rowNum++);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值"分区名称") + ")统计表
			cell = row.createCell((short) 0);
			Object orgNameObj =  excel.get("orgName");
			if(orgNameObj != null) {
				cell.setCellValue(orgNameObj.toString());
			} else {
				cell.setCellValue("");
			}
			cell.setCellStyle(style2);
			
			
			cell = row.createCell((short) 1);
			Object startValueObj =  excel.get("startValue");
			if(startValueObj != null) {
				cell.setCellValue(startValueObj.toString());
			} else {
				cell.setCellValue("");
			}			
			cell.setCellStyle(style1);
			
			
			cell = row.createCell((short) 2);
			Object endValueObj =  excel.get("endValue");
			if(endValueObj != null) {
				cell.setCellValue(endValueObj.toString());
			} else {
				cell.setCellValue("");
			}			
			cell.setCellStyle(style1);
			
			
			cell = row.createCell((short) 3);
			if(startValueObj != null && endValueObj != null) {
				double value = Double.parseDouble(endValueObj.toString()) - Double.parseDouble(startValueObj.toString());
				cell.setCellValue(value);
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
				
				
				

				cell = row.createCell((short)2);
				String startValueObj =  excel.getString("startValue");
				if(startValueObj != null) {
					cell.setCellValue(startValueObj.toString());
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				cell = row.createCell((short)3);
				String endValueObj =  excel.getString("endValue");
				if(endValueObj != null) {
					cell.setCellValue(endValueObj.toString());
				} else {
					cell.setCellValue("");
				}		
				cell.setCellStyle(style1);
				
				
				
				cell = row.createCell((short)4);
				if(startValueObj != null && endValueObj != null) {
					double value = Double.parseDouble(endValueObj.toString()) - Double.parseDouble(startValueObj.toString());
					cell.setCellValue(value);
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
	
	
	
	
	
	
	
	

	
	int rowNum2 = 0;
	public void exportDayExtremalDatas(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list, String date) throws IOException {

		//第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		//第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("日极值数据");
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
		
		//柜号
		CellRangeAddress region = new CellRangeAddress(0,2,0,0);
		sheet.addMergedRegion(region);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("柜号");
		cell.setCellStyle(style);
		
		//回路名称
		CellRangeAddress region2 = new CellRangeAddress(0,2,1,1);
		sheet.addMergedRegion(region2);
		cell = row.createCell((short) 1);
		cell.setCellValue("回路名称");
		cell.setCellStyle(style);
		
		//A相电流
		CellRangeAddress region3 = new CellRangeAddress(0,0,2,5);
		sheet.addMergedRegion(region3);
		cell = row.createCell((short) 2);
		cell.setCellValue("A相电流");
		cell.setCellStyle(style);

		//B相电流
		CellRangeAddress region4 = new CellRangeAddress(0,0,6,9);
		sheet.addMergedRegion(region4);
		cell = row.createCell((short) 6);
		cell.setCellValue("B相电流");
		cell.setCellStyle(style);
		
		//C相电流
		CellRangeAddress region5 = new CellRangeAddress(0,0,10,13);
		sheet.addMergedRegion(region5);
		cell = row.createCell((short) 10);
		cell.setCellValue("C相电流");
		cell.setCellStyle(style);
		
		//A相电压
		CellRangeAddress region6 = new CellRangeAddress(0,0,14,17);
		sheet.addMergedRegion(region6);
		cell = row.createCell((short) 14);
		cell.setCellValue("A相电压");
		cell.setCellStyle(style);

		//B相电压
		CellRangeAddress region7 = new CellRangeAddress(0,0,18,21);
		sheet.addMergedRegion(region7);
		cell = row.createCell((short) 18);
		cell.setCellValue("B相电压");
		cell.setCellStyle(style);
		
		//C相电压
		CellRangeAddress region8 = new CellRangeAddress(0,0,22,25);
		sheet.addMergedRegion(region8);
		cell = row.createCell((short) 22);
		cell.setCellValue("C相电压");
		cell.setCellStyle(style);
		
		//功率因素
		CellRangeAddress region9 = new CellRangeAddress(0,0,26,29);
		sheet.addMergedRegion(region9);
		cell = row.createCell((short) 26);
		cell.setCellValue("功率因素");
		cell.setCellStyle(style);
		
		
		//A相总功率
		CellRangeAddress region109 = new CellRangeAddress(0,0,30,33);
		sheet.addMergedRegion(region109);
		cell = row.createCell((short) 30);
		cell.setCellValue("A相总功率");
		cell.setCellStyle(style);
		
		//B相总功率
		CellRangeAddress region110 = new CellRangeAddress(0,0,34,37);
		sheet.addMergedRegion(region110);
		cell = row.createCell((short) 34);
		cell.setCellValue("B相总功率");
		cell.setCellStyle(style);
		
		
		//C相总功率
		CellRangeAddress region111 = new CellRangeAddress(0,0,38,41);
		sheet.addMergedRegion(region111);
		cell = row.createCell((short) 38);
		cell.setCellValue("C相总功率");
		cell.setCellStyle(style);
		
		//总功率
		CellRangeAddress region112 = new CellRangeAddress(0,0,42,45);
		sheet.addMergedRegion(region112);
		cell = row.createCell((short) 42);
		cell.setCellValue("总功率");
		cell.setCellStyle(style);
		
		
		
		
		
		HSSFRow row1 = sheet.createRow((int) 1);
		//A相电流 最大值 最小值
		CellRangeAddress region10 = new CellRangeAddress(1,1,2,3);
		sheet.addMergedRegion(region10);
		CellRangeAddress region11 = new CellRangeAddress(1,1,4,5);
		sheet.addMergedRegion(region11);
		cell = row1.createCell((short) 2);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 4);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		//B相电流 最大值 最小值
		CellRangeAddress region12 = new CellRangeAddress(1,1,6,7);
		sheet.addMergedRegion(region12);
		CellRangeAddress region13 = new CellRangeAddress(1,1,8,9);
		sheet.addMergedRegion(region13);
		cell = row1.createCell((short) 6);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 8);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		//C相电流 最大值 最小值
		CellRangeAddress region14 = new CellRangeAddress(1,1,10,11);
		sheet.addMergedRegion(region14);
		CellRangeAddress region15 = new CellRangeAddress(1,1,12,13);
		sheet.addMergedRegion(region15);
		cell = row1.createCell((short) 10);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 12);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		//A相电压 最大值 最小值
		CellRangeAddress region16 = new CellRangeAddress(1,1,14,15);
		sheet.addMergedRegion(region16);
		CellRangeAddress region17 = new CellRangeAddress(1,1,16,17);
		sheet.addMergedRegion(region17);
		cell = row1.createCell((short) 14);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 16);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);

		//B相电压 最大值 最小值
		CellRangeAddress region18 = new CellRangeAddress(1,1,18,19);
		sheet.addMergedRegion(region18);
		CellRangeAddress region19 = new CellRangeAddress(1,1,20,21);
		sheet.addMergedRegion(region19);
		cell = row1.createCell((short) 18);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 20);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		//C相电压 最大值 最小值
		CellRangeAddress region20 = new CellRangeAddress(1,1,22,23);
		sheet.addMergedRegion(region20);
		CellRangeAddress region21 = new CellRangeAddress(1,1,24,25);
		sheet.addMergedRegion(region21);
		cell = row1.createCell((short) 22);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 24);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		//功率因素 最大值 最小值
		CellRangeAddress region22 = new CellRangeAddress(1,1,26,27);
		sheet.addMergedRegion(region22);
		CellRangeAddress region23 = new CellRangeAddress(1,1,28,29);
		sheet.addMergedRegion(region23);
		cell = row1.createCell((short) 26);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 28);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		
		//A相总功率 最大值 最小值
		CellRangeAddress region222 = new CellRangeAddress(1,1,26,27);
		sheet.addMergedRegion(region222);
		CellRangeAddress region223 = new CellRangeAddress(1,1,28,29);
		sheet.addMergedRegion(region223);
		cell = row1.createCell((short) 30);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 32);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		
		//B相总功率 最大值 最小值
		CellRangeAddress region224 = new CellRangeAddress(1,1,30,31);
		sheet.addMergedRegion(region224);
		CellRangeAddress region225 = new CellRangeAddress(1,1,32,33);
		sheet.addMergedRegion(region225);
		cell = row1.createCell((short) 34);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 36);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		//C相总功率 最大值 最小值
		CellRangeAddress region226 = new CellRangeAddress(1,1,34,35);
		sheet.addMergedRegion(region226);
		CellRangeAddress region227 = new CellRangeAddress(1,1,36,37);
		sheet.addMergedRegion(region227);
		cell = row1.createCell((short) 38);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 40);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		//总功率 最大值 最小值
		CellRangeAddress region228 = new CellRangeAddress(1,1,38,39);
		sheet.addMergedRegion(region228);
		CellRangeAddress region229 = new CellRangeAddress(1,1,40,41);
		sheet.addMergedRegion(region229);
		cell = row1.createCell((short) 42);
		cell.setCellValue("最大值");
		cell.setCellStyle(style);
		cell = row1.createCell((short) 44);
		cell.setCellValue("最小值");
		cell.setCellStyle(style);
		
		
		
		
		
		
		
		
		
		
		
		
		
		HSSFRow row2 = sheet.createRow((int) 2);
		for(int i=1;i<=22;i++) {
			int cellIndex = i*2;
			cell = row2.createCell((short) cellIndex);
			cell.setCellValue("数值");
			cell.setCellStyle(style);
			cell = row2.createCell((short) cellIndex+1);
			cell.setCellValue("时间");
			cell.setCellStyle(style);
		}
		

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
		
		String fileName = "日极值数据_" + date;
		
		
		rowNum2 = 3;
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(rowNum2++);
			
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
			
			fillChildLoopExcelExtremalData(wb,sheet,style1,style2,(JSONArray)excel.get("children"));
		}
		
		
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}

	


	public void fillChildLoopExcelExtremalData(HSSFWorkbook wb,HSSFSheet sheet,HSSFCellStyle style1,HSSFCellStyle style2,JSONArray children) {
		if(children != null) {
			HSSFRow row = null;
			HSSFCell cell = null;
			for (int i = 0; i < children.size(); i++) {
				
				row = sheet.createRow(rowNum2);
				JSONObject excel = children.getJSONObject(i);
				String type = excel.getString("type");
				if(type.equals("7")) {
					CellRangeAddress region = new CellRangeAddress(rowNum2,rowNum2,0,1);
					sheet.addMergedRegion(region);
				}
				//总表节点合并
				int startRowNum = rowNum2;
				
				if(!type.equals("8")) {
					rowNum2++;
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
				

				JSONObject extremalData = excel.getJSONObject("ExtremalData");
				if(extremalData == null) extremalData = new JSONObject();
				JSONObject max_a_curValue =  extremalData.getJSONObject("max_a_curValue");
				cell = row.createCell((short)2);
				if(max_a_curValue != null) {
					cell.setCellValue(max_a_curValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)3);
				if(max_a_curValue != null) {
					cell.setCellValue(max_a_curValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_a_curValue =  extremalData.getJSONObject("min_a_curValue");
				cell = row.createCell((short)4);
				if(min_a_curValue != null) {
					cell.setCellValue(min_a_curValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)5);
				if(min_a_curValue != null) {
					cell.setCellValue(min_a_curValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				
				JSONObject max_b_curValue =  extremalData.getJSONObject("max_b_curValue");
				cell = row.createCell((short)6);
				if(max_b_curValue != null) {
					cell.setCellValue(max_b_curValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)7);
				if(max_b_curValue != null) {
					cell.setCellValue(max_b_curValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_b_curValue =  extremalData.getJSONObject("min_b_curValue");
				cell = row.createCell((short)8);
				if(min_b_curValue != null) {
					cell.setCellValue(min_b_curValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)9);
				if(min_b_curValue != null) {
					cell.setCellValue(min_b_curValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				JSONObject max_c_curValue =  extremalData.getJSONObject("max_c_curValue");
				cell = row.createCell((short)10);
				if(max_c_curValue != null) {
					cell.setCellValue(max_c_curValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)11);
				if(max_c_curValue != null) {
					cell.setCellValue(max_c_curValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_c_curValue =  extremalData.getJSONObject("min_c_curValue");
				cell = row.createCell((short)12);
				if(min_b_curValue != null) {
					cell.setCellValue(min_b_curValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)13);
				if(min_c_curValue != null) {
					cell.setCellValue(min_c_curValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				JSONObject max_a_volValue =  extremalData.getJSONObject("max_a_volValue");
				cell = row.createCell((short)14);
				if(max_a_volValue != null) {
					cell.setCellValue(max_a_volValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)15);
				if(max_a_volValue != null) {
					cell.setCellValue(max_a_volValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_a_volValue =  extremalData.getJSONObject("min_a_volValue");
				cell = row.createCell((short)16);
				if(min_a_volValue != null) {
					cell.setCellValue(min_a_volValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)17);
				if(min_a_volValue != null) {
					cell.setCellValue(min_a_volValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				JSONObject max_b_volValue =  extremalData.getJSONObject("max_b_volValue");
				cell = row.createCell((short)18);
				if(max_b_volValue != null) {
					cell.setCellValue(max_b_volValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)19);
				if(max_b_volValue != null) {
					cell.setCellValue(max_b_volValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_b_volValue =  extremalData.getJSONObject("min_b_volValue");
				cell = row.createCell((short)20);
				if(min_b_volValue != null) {
					cell.setCellValue(min_b_volValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)21);
				if(min_b_volValue != null) {
					cell.setCellValue(min_b_volValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				
				

				
				JSONObject max_c_volValue =  extremalData.getJSONObject("max_c_volValue");
				cell = row.createCell((short)22);
				if(max_c_volValue != null) {
					cell.setCellValue(max_c_volValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)23);
				if(max_c_volValue != null) {
					cell.setCellValue(max_c_volValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_c_volValue =  extremalData.getJSONObject("min_c_volValue");
				cell = row.createCell((short)24);
				if(min_c_volValue != null) {
					cell.setCellValue(min_c_volValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)25);
				if(min_c_volValue != null) {
					cell.setCellValue(min_c_volValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				

				JSONObject max_powerFactorValue =  extremalData.getJSONObject("max_powerFactorValue");
				cell = row.createCell((short)26);
				if(max_powerFactorValue != null) {
					cell.setCellValue(max_powerFactorValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)27);
				if(max_powerFactorValue != null) {
					cell.setCellValue(max_powerFactorValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_powerFactorValue =  extremalData.getJSONObject("min_powerFactorValue");
				cell = row.createCell((short)28);
				if(min_powerFactorValue != null) {
					cell.setCellValue(min_powerFactorValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)29);
				if(min_powerFactorValue != null) {
					cell.setCellValue(min_powerFactorValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				


				JSONObject max_a_totalPowerValue =  extremalData.getJSONObject("max_a_totalPowerValue");
				cell = row.createCell((short)30);
				if(max_a_totalPowerValue != null) {
					cell.setCellValue(max_a_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)31);
				if(max_powerFactorValue != null) {
					cell.setCellValue(max_a_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_a_totalPowerValue =  extremalData.getJSONObject("min_a_totalPowerValue");
				cell = row.createCell((short)32);
				if(min_powerFactorValue != null) {
					cell.setCellValue(min_a_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)33);
				if(min_powerFactorValue != null) {
					cell.setCellValue(min_a_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				

				JSONObject max_b_totalPowerValue =  extremalData.getJSONObject("max_b_totalPowerValue");
				cell = row.createCell((short)34);
				if(max_b_totalPowerValue != null) {
					cell.setCellValue(max_b_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)35);
				if(max_b_totalPowerValue != null) {
					cell.setCellValue(max_b_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				JSONObject min_b_totalPowerValue =  extremalData.getJSONObject("min_b_totalPowerValue");
				cell = row.createCell((short)36);
				if(min_b_totalPowerValue != null) {
					cell.setCellValue(min_b_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)37);
				if(min_b_totalPowerValue != null) {
					cell.setCellValue(min_b_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				

				

				JSONObject max_c_totalPowerValue =  extremalData.getJSONObject("max_c_totalPowerValue");
				cell = row.createCell((short)38);
				if(max_c_totalPowerValue != null) {
					cell.setCellValue(max_c_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)39);
				if(max_c_totalPowerValue != null) {
					cell.setCellValue(max_c_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				JSONObject min_c_totalPowerValue =  extremalData.getJSONObject("min_c_totalPowerValue");
				cell = row.createCell((short)40);
				if(min_c_totalPowerValue != null) {
					cell.setCellValue(min_c_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)41);
				if(min_c_totalPowerValue != null) {
					cell.setCellValue(min_c_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				
				
				

				JSONObject max_totalPowerValue =  extremalData.getJSONObject("max_totalPowerValue");
				cell = row.createCell((short)42);
				if(max_totalPowerValue != null) {
					cell.setCellValue(max_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)43);
				if(max_totalPowerValue != null) {
					cell.setCellValue(max_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				JSONObject min_totalPowerValue =  extremalData.getJSONObject("min_totalPowerValue");
				cell = row.createCell((short)44);
				if(min_totalPowerValue != null) {
					cell.setCellValue(min_totalPowerValue.getDouble("historyValue"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				cell = row.createCell((short)45);
				if(min_totalPowerValue != null) {
					cell.setCellValue(min_totalPowerValue.getString("upload_time"));
				} else {
					cell.setCellValue("");
				}			
				cell.setCellStyle(style1);
				
				
				
				
				
				fillChildLoopExcelExtremalData(wb,sheet,style1,style2,excel.getJSONArray("children"));
				
				

				if(type.equals("8")) {
					CellRangeAddress region = null;
					if(startRowNum < rowNum2) {
						region = new CellRangeAddress(startRowNum,rowNum2-1,0,0);
					} else {
						region = new CellRangeAddress(startRowNum,rowNum2,0,0);
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
	
	
	
}