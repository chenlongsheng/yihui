/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.machine.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.homepage.dao.OverviewDao;
import com.jeeplus.modules.machine.dao.TDeviceMachineDao;
import com.jeeplus.modules.machine.dao.TDeviceReportDao;
import com.jeeplus.modules.machine.entity.TDeviceMachine;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
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
public class TDeviceRportService extends CrudService<TDeviceMachineDao, TDeviceMachine> {
	@Autowired
	private TDeviceReportDao tDeviceReportDao;

	public List<MapEntity> tDeviceRportList(String orgId, String name) {
		List<String> numberList = tDeviceReportDao.numberList();
		List<MapEntity> tDeviceRportList = tDeviceReportDao.tDeviceRportList(name);
		for (MapEntity mapEntity : tDeviceRportList) {
			mapEntity.put("numberList", numberList);
		}
		return tDeviceRportList;

	}

	public List<MapEntity> tChannelRportList(String devId, String str, String time) {

		return tDeviceReportDao.tChannelRportList(devId, str, time);
	}

	public List<List<MapEntity>> tChannelRportList1(String devId, String s, String time) {

		List<MapEntity> tChannelRportList = tDeviceReportDao.tChannelRportList(devId, "01", time);
		Set<String> set = new HashSet<String>();
		for (MapEntity enti : tChannelRportList) {
			set.add(enti.get("time").toString());
		}
		//
		List<List<MapEntity>> list = new ArrayList<List<MapEntity>>();
		for (String str : set) {
			ArrayList<MapEntity> aary = new ArrayList<MapEntity>();
			for (MapEntity user : tChannelRportList) {
				if (str.equals(user.get("time").toString())) {
					aary.add(user);
				}
			}
			list.add(aary);
		}

		return list;
	}

	public void exportChannelRportList(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			List<MapEntity> list) throws Exception {

		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("光纤分段统计表");
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
		cell.setCellValue("分区名称");
		cell.setCellStyle(style);
		cell = row.createCell((short) 1);
		cell.setCellValue("采集时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("最高温度");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("平均温度");
		cell.setCellStyle(style);
		cell = row.createCell((short) 4);
		cell.setCellValue("最低温度");
		cell.setCellStyle(style);
		cell = row.createCell((short) 5);
		cell.setCellValue("最低温度位置");
		cell.setCellStyle(style);
		cell = row.createCell((short) 6);
		cell.setCellValue("最高温度位置");
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
			fileName = "光纤" + (String) excel.get("ip") + "(" + (String) excel.get("分区名称") + ")统计表";
			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("分区名称"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("采集时间"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("最高温度"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue((String) excel.get("平均温度"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 4);
			cell.setCellValue((String) excel.get("最低温度"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 5);
			cell.setCellValue((String) excel.get("最低温度位置"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 6);
			cell.setCellValue((String) excel.get("最高温度位置"));
			cell.setCellStyle(style1);

		}
		// 响应到客户端
		ExcelUtil.setResponseHeader(httpServletResponse, fileName);
		OutputStream os = httpServletResponse.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();

	}

	public void exportChannelRportList1(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
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
		sheet.setDefaultColumnWidth(30);
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

		HSSFCell cell = row.createCell((short) 1);
		cell.setCellValue("采集时间");
		cell.setCellStyle(style);
		cell = row.createCell((short) 2);
		cell.setCellValue("最高温度");
		cell.setCellStyle(style);
		cell = row.createCell((short) 3);
		cell.setCellValue("平均温度");
		cell.setCellStyle(style);
		cell = row.createCell((short) 4);
		cell.setCellValue("最低温度");
		cell.setCellStyle(style);
		cell = row.createCell((short) 5);
		cell.setCellValue("最低温度位置");
		cell.setCellStyle(style);
		cell = row.createCell((short) 6);
		cell.setCellValue("最高温度位置");
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
			row = sheet.createRow((int) i + 2);
			Map excel = list.get(i);
			fileName = "光纤" + (String) excel.get("ip") + "(" + (String) excel.get("分区名称") + ")统计表";
			// 第四步，创建单元格，并设置值
			cell = row.createCell((short) 0);
			cell.setCellValue((String) excel.get("分区名称"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 1);
			cell.setCellValue((String) excel.get("采集时间"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 2);
			cell.setCellValue((String) excel.get("最高温度"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 3);
			cell.setCellValue((String) excel.get("平均温度"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 4);
			cell.setCellValue((String) excel.get("最低温度"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 5);
			cell.setCellValue((String) excel.get("最低温度位置"));
			cell.setCellStyle(style1);
			cell = row.createCell((short) 6);
			cell.setCellValue((String) excel.get("最高温度位置"));
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