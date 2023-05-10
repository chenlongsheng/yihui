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
import java.util.Set;
import org.apache.poi.hssf.util.Region;
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
import com.jeeplus.modules.starnet.dao.CabinetSystemDao;
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
public class CabinetSystemService {
	@Autowired
	private CabinetSystemDao cabinetSystemDao;

	@Autowired
	private EnergyAnalysisDao energyAnalysisDao;

	public List<MapEntity> tDeviceCabinetList(String orgId) {
		List<MapEntity> tDeviceVoltageList = energyAnalysisDao.tDeviceVoltageList("217");

		List<MapEntity> tDeviceIncomingList = energyAnalysisDao.tDeviceIncomingList(orgId);

		for (MapEntity parentEntity : tDeviceVoltageList) {

			parentEntity.put("entity", tDeviceIncomingList);
		}
		return tDeviceVoltageList;

	}

	public List<MapEntity> selectCabinetData(String loopId, String time, String type) {
		List<MapEntity> selectCabinetData = cabinetSystemDao.selectCabinetData(loopId, time, type);
		return selectCabinetData;
	}
	
	
	
	
	
	
	public List<MapEntity> selectCabinetTempData(String orgId, String time) {
		List<MapEntity> selectCabinetData = cabinetSystemDao.selectCabinetTempData(orgId, time);
		return selectCabinetData;
	}

	public List<MapEntity> selectCableTempData(String orgId, String time) {
		List<MapEntity> selectCabinetData = cabinetSystemDao.selectCableTempData(orgId, time);
		return selectCabinetData;
	}


	// 导出柜内wendu
	public void exportCabinetDataReports(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("1111111");
// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		
		HSSFRow row = sheet.createRow((int) 1);
// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(40);
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
		cell.setCellValue("温度(°C)");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "柜内温度";
		String name = "fiberName";
//		if (type.equals("1")) {
//			fileName = "电缆温度";
//			name = "name";
//		}
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 2);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值"分区名称") + ")统计表
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get(name));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("time"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue(excel.get("value").toString());
			cell.setCellStyle(style1);
		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();

	}

	public void exportCableDataReports(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list) {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("1111111");
// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		
		HSSFRow row = sheet.createRow((int) 1);
// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(40);
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
		cell.setCellValue("温度(°C)");
		cell.setCellStyle(style);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		sheet.setDefaultColumnWidth(30);
		HSSFFont nameRowFont1 = wb.createFont();
		nameRowFont1.setFontName("微软雅黑");
		nameRowFont1.setFontHeightInPoints((short) 12);// 设置字体大小
		style1.setFont(nameRowFont1);
		String fileName = "柜内温度";
		String name = "fiberName";
//		if (type.equals("1")) {
//			fileName = "电缆温度";
//			name = "name";
//		}
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow((int) i + 2);
			Map excel = list.get(i);
			// 第四步，创建单元格，并设置值"分区名称") + ")统计表
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get(name));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("time"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue(excel.get("value").toString());
			cell.setCellStyle(style1);
		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os;
		try {
			os = httpServletResponse.getOutputStream();
			wb.write(os);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<MapEntity> selectCabinetTempChannel(String orgId) {
		return cabinetSystemDao.selectCabinetTempChannel(orgId);
	}

}