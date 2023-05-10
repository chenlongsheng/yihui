/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.chaoan.service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.poi.hssf.util.Region;
import org.restlet.engine.local.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.chaoan.dao.EnvironmentalDao;
import com.jeeplus.modules.homepage.dao.OverviewDao;
import com.jeeplus.modules.machine.dao.TDeviceMachineDao;
import com.jeeplus.modules.machine.dao.TDeviceReportDao;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.entity.TDeviceConfig;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.service.TDeviceConfigService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
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
public class EnvironmentalService extends CrudService<TDeviceMachineDao, TDeviceMachine> {
	@Autowired
	private EnvironmentalDao environmentalDao;

	public List<MapEntity> powerCodes(String status) {

		List<MapEntity> powerCodes = environmentalDao.powerCodes("29");
		return powerCodes;
	}

	public List<MapEntity> changeCymbols(String chType) {

		List<MapEntity> changeCymbols = environmentalDao.changeCymbols(chType);
		return changeCymbols;
	}

	public Page<MapEntity> findPage(Page<MapEntity> page, MapEntity entity) {
		try {
			entity.setPage(page);
			page.setList(environmentalDao.getPdfEntranceLogs(entity.get("beginTime").toString(),
					entity.get("endTime").toString(), entity.get("userName").toString(),
					entity.get("employeeNo").toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	
	
	public Page<MapEntity> findPageMessage(Page<MapEntity> page, MapEntity entity) {
		try {
			entity.setPage(page);
			page.setList(environmentalDao.getMessageLogs(entity));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
 
	}
	
	public List<MapEntity> getMessageLogs(MapEntity entity){
		return environmentalDao.getMessageLogs(entity);
	}
	
	
	
	
	
	

	public List<MapEntity> getPdfEntranceLogs(MapEntity entity) {

		List<MapEntity> pdfEntranceLogs = environmentalDao.getPdfEntranceLogs(entity.get("beginTime").toString(),
				entity.get("endTime").toString(), entity.get("userName").toString(),
				entity.get("employeeNo").toString());

		return pdfEntranceLogs;

	}
	


	@Transactional(readOnly = false)
	public void insertPdfEntranceLog(String user_name, String unlock_type, String picture_url, String device_sn,
			String result, String employee_no, String event_time, String channel_no) {

		environmentalDao.insertPdfEntranceLog(user_name, unlock_type, picture_url, device_sn, result, employee_no,
				event_time, channel_no);

	}

	public List<MapEntity> tDeviceTypes(String orgId) {
		List<MapEntity> tDeviceTypes = environmentalDao.tDeviceTypes(orgId);
		for (MapEntity mapEntity : tDeviceTypes) {
			String devType = mapEntity.get("devType").toString();

			mapEntity.put("list", environmentalDao.tDeviceListByType(devType,orgId));
		}

		return tDeviceTypes;
	}

	public List<MapEntity> wenshiList(String beginDate, String endDate, String devId) {
		List<MapEntity> wenshiList = environmentalDao.wenshiList(beginDate, endDate, devId);

		return wenshiList;
	}

	public List<MapEntity> ph2and10(String beginDate, String endDate, String devId) {
		List<MapEntity> ph2and10 = environmentalDao.ph2and10(beginDate, endDate, devId);

		return ph2and10;
	}

	public List<MapEntity> gasConcentration(String beginDate, String endDate, String devId) {
		List<MapEntity> gasConcentration = environmentalDao.gasConcentration(beginDate, endDate, devId);

		return gasConcentration;
	}

	public List<MapEntity> getConditioner(String beginDate, String endDate, String devId) {
		List<MapEntity> getConditioner = environmentalDao.getConditioner(beginDate, endDate, devId);

		return getConditioner;
	}

	public List<MapEntity> IncomingCabinet(String beginDate, String endDate, String devId, String name,
			String chTypes) {
		List<String> chTypeList = new ArrayList<String>();
		String[] split = chTypes.split(",");

		for (int i = 0; i < split.length; i++) {
			chTypeList.add(split[i]);
		}
		List<MapEntity> IncomingCabinet = environmentalDao.IncomingCabinet(beginDate, endDate, name, devId, chTypes,
				chTypeList);
		return IncomingCabinet;
	}

	public List<MapEntity> outgoingCabinet(String beginDate, String endDate, String devId, String chTypes) {
		List<String> chTypeList = new ArrayList<String>();
		String[] split = chTypes.split(",");
		for (int i = 0; i < split.length; i++) {
			chTypeList.add(split[i]);
		}
		List<MapEntity> outgoingCabinet = environmentalDao.outgoingCabinet(beginDate, endDate, devId, chTypes,
				chTypeList);

		return outgoingCabinet;
	}



	public void exportWenshiList(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list, String name) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("统计表");
// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("安装位置");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("采集时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("温度");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("湿度");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = name;
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map excel = list.get(i);

			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("addr"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("time"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("wendu"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue((String) excel.get("shidu"));
			cell.setCellStyle(style1);
		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();

	}

	// PH2.5浓度
	public void exportPH25_10(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("统计表");
// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("安装位置");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("采集时间");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("PM2.5");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("PM10");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "PM2.5报表";
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("addr"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("time"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("PM25"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue((String) excel.get("PM10"));
			cell.setCellStyle(style1);

		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}

	// 氢气浓度
	public void exportGasConcentration(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("统计表");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("安装位置");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("采集时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("lel值");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "氢气报表";
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map excel = list.get(i);

			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("addr"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("time"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("lel值"));
			cell.setCellStyle(style1);

		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();

	}

	// 输入柜和输出柜
	public void exportComingCabinet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list, String chTypes) throws Exception {

		List<MapEntity> changeCymbols = environmentalDao.changeCymbols(chTypes);

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("统计表");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("电柜名称");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("采集时间");
		cell.setCellStyle(style);
		int a = 2;
		for (MapEntity mapEntity : changeCymbols) {
			cell = row.createCell((short) a);
			a++;
			cell.setCellValue(mapEntity.get("name").toString());
			cell.setCellStyle(style);
		}
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "线柜报表";
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("addr"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("time"));
			cell.setCellStyle(style1);
			int b = 2;
			for (MapEntity mapEntity : changeCymbols) {
				cell = row.createCell((short) b);
				b++;
				cell.setCellValue(excel.get(mapEntity.get("chType").toString()).toString());
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

	// 导出门禁日志
	public void exportPdfEntranceLogs(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("统计表");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont = wb.createFont();
		nameRowFont.setFontName("微软雅黑");
		nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
		nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
		style.setFont(nameRowFont);
		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue("编号");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("姓名");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("日期");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("考勤点");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "部门考情表";
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 1);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("employee_no"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("user_name"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue(excel.get("event_time").toString());
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue(excel.get("attendance").toString());
			cell.setCellStyle(style1);

		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}
	
	// 导出门禁日志
		public void exportMessageLogs(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
				List<MapEntity> list) throws Exception {
			
			// 第一步，创建一个webbook，对应一个Excel文件
			HSSFWorkbook wb = new HSSFWorkbook();
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet sheet = wb.createSheet("统计表");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow row = sheet.createRow((int) 0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			sheet.setDefaultColumnWidth(25);
			HSSFFont nameRowFont = wb.createFont();
			nameRowFont.setFontName("微软雅黑");
			nameRowFont.setFontHeightInPoints((short) 12);// 设置字体大小
			nameRowFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
			style.setFont(nameRowFont);
			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("报警类型");
			cell.setCellStyle(style);
			cell = row.createCell((short) 1);
			cell.setCellValue("设备类型");
			cell.setCellStyle(style);

			cell = row.createCell((short) 2);
			cell.setCellValue("安装位置");
			cell.setCellStyle(style);

			cell = row.createCell((short) 3);
			cell.setCellValue("告警数值");
			cell.setCellStyle(style);
			
			cell = row.createCell((short) 4);
			cell.setCellValue("告警等级");
			cell.setCellStyle(style);

			cell = row.createCell((short) 5);
			cell.setCellValue("通知类型");
			cell.setCellStyle(style);

			cell = row.createCell((short) 6);
			cell.setCellValue("短信发送内容");
			cell.setCellStyle(style);			
			
			cell = row.createCell((short) 7);
			cell.setCellValue("短信发送时间");
			cell.setCellStyle(style);

			cell = row.createCell((short) 8);
			cell.setCellValue("短信发送对象");
			cell.setCellStyle(style);


			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			sheet.setDefaultColumnWidth(30);
			HSSFFont nameRowFont1 = wb.createFont();
			nameRowFont1.setFontName("微软雅黑");
			nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
			style1.setFont(nameRowFont1);
			String fileName = "短信消息日志表";
			String alarmName ="" ;
			String messageType ="" ;
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow((int) i + 1);
				
				Map excel = list.get(i);
				if (excel.get("status").equals("1")) {
					alarmName = "数据异常";		
					messageType = "告警";
				}
				else if(excel.get("status").equals("2")) {
					alarmName = "数据异常";
					messageType = "恢复";
				}else if(excel.get("status").equals("3")) {
					alarmName = "设备故障";
					messageType = "上线";
				}
				else if(excel.get("status").equals("0")) {
					alarmName = "设备故障";		
					messageType = "离线";
				}
				// 第四步，创建单元格，并设置值
				cell = row.createCell((short) 0);				
				cell.setCellValue(alarmName);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 1);
				cell.setCellValue((String) excel.get("devName"));
				cell.setCellStyle(style1);
				cell = row.createCell((short) 2);
				cell.setCellValue(excel.get("addr").toString());
				cell.setCellStyle(style1);
				cell = row.createCell((short) 3);
				cell.setCellValue(excel.get("alarm_value").toString());
				cell.setCellStyle(style1);
				
				cell = row.createCell((short) 4);
				cell.setCellValue(excel.get("level").toString());
				cell.setCellStyle(style1);
				
				cell = row.createCell((short) 5);
				cell.setCellValue(messageType);
				cell.setCellStyle(style1);
				cell = row.createCell((short) 6);
				cell.setCellValue((String) excel.get("content"));
				cell.setCellStyle(style1);
				cell = row.createCell((short) 7);
				cell.setCellValue(excel.get("time").toString());
				cell.setCellStyle(style1);
				cell = row.createCell((short) 8);
				cell.setCellValue(excel.get("phones").toString());
				cell.setCellStyle(style1);				
			 

			}
			// 响应到客户端
			ExcelUtil.setResponseHeader(httpServletResponse, fileName);
			OutputStream os = httpServletResponse.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		}
}