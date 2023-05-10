/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.foshan.web.FoShanController;
import com.jeeplus.modules.homepage.dao.OverviewDao;

import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.qxz.excel.ExcelUtil;
import com.jeeplus.modules.settings.dao.TChannelDao;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.service.TDeviceConfigService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.starnet.dao.EnergyAnalysisDao;
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
public class EnergyAnalysisService {

    public static Logger logger = LoggerFactory.getLogger(EnergyAnalysisService.class);


    @Autowired
    RedisTemplate<String, String> redisTempldate;

    @Autowired
    private EnergyAnalysisDao energyAnalysisDao;

    @Autowired
    ElectricityUnitService electricityUnitService;


    @Autowired
    DataService dataService;

    @Autowired
    ElecHistoryDataService elecHistoryDataService;


    public void loadDayDataByDate(String loadDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate loadDay = LocalDate.parse(loadDate, formatter);
        //获取回路所关联的 有功电能 通道
        List<MapEntity> loopElecChannelList = dataService.getLoopElecChannelList();
        //查询 增量表该回路最后一条数据 ,历史数据表该通道的最后一条数据, 并将该数据id,缓存11分钟.
        for (MapEntity elecChannel : loopElecChannelList) {
            String chId = elecChannel.get("id").toString();
            String orgId = elecChannel.get("orgId").toString();
            String chType = elecChannel.get("chType").toString();

            Double sum = elecHistoryDataService.getSumValueByTimeBetween(orgId, loadDay.format(formatter) + " 00:00:00", loadDay.format(formatter) + " 23:59:59");
            if (sum == null)
                sum = 0.0;
            //LoopDataDay
            redisTempldate.opsForHash().put("LoopDataDay:" + loadDay.format(formatter), orgId, String.valueOf(sum));

            JSONObject jsonValue = new JSONObject();
            for (int i = 0; i < 24; i++) {
                String hour = "";
                if (i < 10) {
                    hour = "0" + i;
                } else {
                    hour = String.valueOf(i);
                }
                Double sum2 = elecHistoryDataService.getSumValueByTimeBetween(orgId, loadDay.format(formatter) + " " + hour + ":00:00", loadDay.format(formatter) + " " + hour + ":59:59");
                if (sum2 == null)
                    sum2 = 0.0;
                jsonValue.put(hour, sum2);
            }
            //LoopDataHour
            redisTempldate.opsForHash().put("LoopDataHour:" + loadDay.format(formatter), orgId, jsonValue.toJSONString());
        }

    }


    public void loadMonthDataByMonth(String loadMonth) {
        logger.debug("loadMonthDataByMonth");
        logger.debug("loadMonth:" + loadMonth);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDate loadMonthDate = LocalDate.parse(loadMonth + "-01", formatter);
        LocalDate firstDay = loadMonthDate.with(TemporalAdjusters.firstDayOfMonth()); // 获取月的第一天
        LocalDate lastDay = loadMonthDate.with(TemporalAdjusters.lastDayOfMonth()); // 获取月的最后一天

        //获取回路所关联的 有功电能 通道
        List<MapEntity> loopElecChannelList = dataService.getLoopElecChannelList();
        //查询 增量表该回路最后一条数据 ,历史数据表该通道的最后一条数据, 并将该数据id,缓存11分钟.
        for (MapEntity elecChannel : loopElecChannelList) {
            String chId = elecChannel.get("id").toString();
            String orgId = elecChannel.get("orgId").toString();
            String chType = elecChannel.get("chType").toString();

            logger.debug("chId:" + chId);
            logger.debug("orgId:" + orgId);
            logger.debug("chType:" + chType);

            Double sum = elecHistoryDataService.getSumValueByTimeBetween(orgId, firstDay.format(formatter) + " 00:00:00", lastDay.format(formatter) + " 23:59:59");
            if (sum == null)
                sum = 0.0;
            //LoopDataMonth
            redisTempldate.opsForHash().put("LoopDataMonth:" + loadMonthDate.format(formatter2), orgId, String.valueOf(sum));
        }

    }


    public void loadDayDataByLoopIdAndDate(String loadDate, String loopId) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate loadDay = LocalDate.parse(loadDate, formatter);

        Double sum = elecHistoryDataService.getSumValueByTimeBetween(loopId, loadDay.format(formatter) + " 00:00:00", loadDay.format(formatter) + " 23:59:59");
        if (sum == null)
            sum = 0.0;
        //LoopDataDay
        redisTempldate.opsForHash().put("LoopDataDay:" + loadDay.format(formatter), loopId, String.valueOf(sum));

        JSONObject jsonValue = new JSONObject();
        for (int i = 0; i < 24; i++) {
            String hour = "";
            if (i < 10) {
                hour = "0" + i;
            } else {
                hour = String.valueOf(i);
            }
            Double sum2 = elecHistoryDataService.getSumValueByTimeBetween(loopId, loadDay.format(formatter) + " " + hour + ":00:00", loadDay.format(formatter) + " " + hour + ":59:59");
            if (sum2 == null)
                sum2 = 0.0;
            jsonValue.put(hour, sum2);
        }
        //LoopDataHour
        redisTempldate.opsForHash().put("LoopDataHour:" + loadDay.format(formatter), loopId, jsonValue.toJSONString());

    }

    public void loadMonthDataByLoopIdAndMonth(String loadMonth, String loopId) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM");

        LocalDate loadMonthDate = LocalDate.parse(loadMonth + "-01", formatter);
        LocalDate firstDay = loadMonthDate.with(TemporalAdjusters.firstDayOfMonth()); // 获取月的第一天
        LocalDate lastDay = loadMonthDate.with(TemporalAdjusters.lastDayOfMonth()); // 获取月的最后一天


        Double sum = elecHistoryDataService.getSumValueByTimeBetween(loopId, firstDay.format(formatter) + " 00:00:00", lastDay.format(formatter) + " 23:59:59");
        if (sum == null)
            sum = 0.0;
        //LoopDataMonth
        redisTempldate.opsForHash().put("LoopDataMonth:" + loadMonthDate.format(formatter2), loopId, String.valueOf(sum));


    }


    public List<Map<String, Object>> getHistoryListByTypeAndTime(final String time, String type, final String loop) {
        List<Map<String, Object>> historyDataList = new ArrayList<>();


        //小时
        if (type.equals("0")) {
            Object hourDataListObj = redisTempldate.opsForHash().get("LoopDataHour:" + time, loop);
            if (hourDataListObj == null) {
                //尝试加载缓存
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadDayDataByLoopIdAndDate(time, loop);
                    }
                }).start();
            } else {
                String hourDataJson = hourDataListObj.toString();
                JSONObject hourDataJobj = JSONObject.parseObject(hourDataJson);
                for (int i = 0; i < 24; i++) {
                    String hour = "";
                    if (i < 10) {
                        hour = "0" + i;
                    } else {
                        hour = String.valueOf(i);
                    }
                    double value = hourDataJobj.getDouble(hour);
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("total", value);
                    data.put("showTime", time + " " + hour);
                    historyDataList.add(data);
                }
            }
        }
        //天
        else if (type.equals("1")) {
            final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate firstDayOfMonth = LocalDate.parse(time + "-01", formatterDay);
            LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            LocalDate tempDate = firstDayOfMonth;
            while (!tempDate.isAfter(lastDayOfMonth)) {
                final String timeDay = tempDate.format(formatterDay);

                Object dayDataObj = redisTempldate.opsForHash().get("LoopDataDay:" + timeDay, loop);
                if (dayDataObj == null) {
                    //尝试加载缓存
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadDayDataByLoopIdAndDate(timeDay, loop);
                        }
                    }).start();
                } else {
                    String dayDataJson = dayDataObj.toString();
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("total", Double.parseDouble(dayDataJson));
                    data.put("showTime", timeDay);
                    historyDataList.add(data);
                }

                tempDate = tempDate.plusDays(1);
            }
        }
        //月
        else if (type.equals("2")) {
            final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            final DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM");

            LocalDate yearLocalDate = LocalDate.parse(time + "-01-01", formatterDay);
            LocalDate firstDayOfYear = yearLocalDate.with(TemporalAdjusters.firstDayOfYear());
            LocalDate lastDayOfYear = yearLocalDate.with(TemporalAdjusters.lastDayOfYear());

            LocalDate tempMonth = firstDayOfYear;
            while (!tempMonth.isAfter(lastDayOfYear)) {
                final String timeMonth = tempMonth.format(formatterMonth);

                Object monthDataObj = redisTempldate.opsForHash().get("LoopDataMonth:" + timeMonth, loop);
                if (monthDataObj == null) {
                    //尝试加载缓存
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            loadMonthDataByLoopIdAndMonth(timeMonth, loop);
                        }
                    }).start();
                } else {
                    String monthDataJson = monthDataObj.toString();
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("total", Double.parseDouble(monthDataJson));
                    data.put("showTime", timeMonth);
                    historyDataList.add(data);
                }

                tempMonth = tempMonth.plusMonths(1);
            }
        }

        return historyDataList;
    }


    public List<MapEntity> tDeviceVoltageList(String orgId) {
        List<MapEntity> tDeviceVoltageList = energyAnalysisDao.tDeviceVoltageList("218");

        List<MapEntity> tDeviceIncomingList = energyAnalysisDao.tDeviceIncomingList(orgId);

        for (MapEntity parentEntity : tDeviceVoltageList) {

            parentEntity.put("entity", tDeviceIncomingList);
        }
        return tDeviceVoltageList;

    }

    public List<MapEntity> historyListByHour(String devId, String time, String pids, String type) {
        List<MapEntity> historyListByHour = null;

        historyListByHour = energyAnalysisDao.historyListByHour(devId, time, pids, type);

        for (int i = 0; i < historyListByHour.size(); i++) {
            BigDecimal b = null;
            if (i == 0) {
                b = new BigDecimal(Double.valueOf(historyListByHour.get(i).get("mValue").toString())
                        - Double.valueOf(historyListByHour.get(i).get("minValue").toString()));
            } else {
                b = new BigDecimal(Double.valueOf(historyListByHour.get(i).get("mValue").toString())
                        - Double.valueOf(historyListByHour.get(i - 1).get("mValue").toString()));
            }
            double value = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            historyListByHour.get(i).put("value", value);
            // ------------------

            if (type.equals("0")) {
                String price = energyAnalysisDao.getPrice(historyListByHour.get(i).get("time").toString(), value + "");
                historyListByHour.get(i).put("price", price);
            }
        }
        return historyListByHour;
    }


    public List<MapEntity> historyListBymoney(String devId, String time, String pids, String type) {
        List<MapEntity> historyListByHour = null;

        historyListByHour = energyAnalysisDao.historyListBymoney(devId, time, pids, type);

        for (int i = 0; i < historyListByHour.size(); i++) {
            BigDecimal b = null;
            if (i == 0) {
                b = new BigDecimal(Double.valueOf(historyListByHour.get(i).get("mValue").toString())
                        - Double.valueOf(historyListByHour.get(i).get("minValue").toString()));
            } else {
                b = new BigDecimal(Double.valueOf(historyListByHour.get(i).get("mValue").toString())
                        - Double.valueOf(historyListByHour.get(i - 1).get("mValue").toString()));
            }

            double price = Double.valueOf(historyListByHour.get(i).get("avgPrice").toString());
            double value = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            b = new BigDecimal(price * value);
            value = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            historyListByHour.get(i).put("value", value);
        }
        return historyListByHour;
    }

    public String getChIds(String devIds) {

        String chIds = "";
        try {
            String[] devs = devIds.split(",");
            for (int i = 0; i < devs.length; i++) {
                String des = devs[i];
                String[] ds = des.split("-");
                String devId = ds[0];
                String pId = ds[1];

                chIds += energyAnalysisDao.getChId(devId, pId);
                if (i != (devs.length - 1)) {
                    chIds += ",";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("chIds=====:  " + chIds);
        return chIds;
    }


    @Transactional(readOnly = false)
    public String insertStarEnergy(String chargeJson) {

        JSONArray ja = JSONArray.parseArray(chargeJson);
        for (int i = 0; i < ja.size(); i++) {

            MapEntity entity = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);

            energyAnalysisDao.insertStarEnergy(entity.get("startTime").toString(), entity.get("endTime").toString(),
                    entity.get("state").toString(), entity.get("price").toString());
        }
        return "";
    }


    public void exportChannelRportList(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
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


    public List<Map<String, Object>> historyListByLoopId(String orgId, String type, String time) {
        return energyAnalysisDao.historyListByLoopId(orgId, type, time);
    }

    public List<Map<String, Object>> historyListByUnitId(String unitId, String type, String time) {
        return energyAnalysisDao.historyListByUnitId(unitId, type, time);
    }

	/*
    public MapEntity historyTrendByTime(String devId, String pids) throws Exception {

        Date day = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(time.format(day));
        //String ti = "2021-06-25";
        String ti = time.format(day);

        List<String> tiPrefix = energyAnalysisDao.historyTrendByTiPrefix(devId, ti, pids, "0");
        List<String> tiPrefix1 = energyAnalysisDao.historyTrendByTiPrefix(devId, ti, pids, "1");
        List<String> tiPrefix2 = energyAnalysisDao.historyTrendByTiPrefix(devId, ti, pids, "2");

        MapEntity entity = new MapEntity();
        entity.put("dayData", tiPrefix);
        entity.put("monthData", tiPrefix1);
        entity.put("yearData", tiPrefix2);

        return entity;
    }

    public MapEntity historyTrendBymoney(String devId, String pids) throws Exception {

        Date day = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(time.format(day));
        String ti = "2021-06-25";

        List<String> tiPrefix = energyAnalysisDao.historyTrendBymoney(devId, ti, pids, "0");
        List<String> tiPrefix1 = energyAnalysisDao.historyTrendBymoney(devId, ti, pids, "1");
        List<String> tiPrefix2 = energyAnalysisDao.historyTrendBymoney(devId, ti, pids, "2");

        MapEntity entity = new MapEntity();
        entity.put("dayData", tiPrefix);
        entity.put("monthData", tiPrefix1);
        entity.put("yearData", tiPrefix2);

        return entity;
    }
    */

    //回路  今时,今日,今月
    public MapEntity dayMonthYear(String orgId) {
        int day = 1;
        int month = 2;
        int year = 3;

        String dayMap = energyAnalysisDao.dayMonthYear(orgId, day);
        String monthMap = energyAnalysisDao.dayMonthYear(orgId, month);
        String yearMap = energyAnalysisDao.dayMonthYear(orgId, year);

        MapEntity result = new MapEntity();
        result.put("dayMap", dayMap);
        result.put("monthMap", monthMap);
        result.put("yearMap", yearMap);
        return result;
    }


    public MapEntity preDayMonthYear(String orgId) {
        int day = 1;
        int month = 2;
        int year = 3;

        String dayMap = energyAnalysisDao.preDayMonthYear(orgId, day);
        String monthMap = energyAnalysisDao.preDayMonthYear(orgId, month);
        String yearMap = energyAnalysisDao.preDayMonthYear(orgId, year);

        MapEntity result = new MapEntity();
        result.put("dayMap", dayMap);
        result.put("monthMap", monthMap);
        result.put("yearMap", yearMap);
        return result;
    }


    //能效数据 -> 对比分析
	/*
    public List<List<MapEntity>> historyPicsByHour(String devIds, String time, String type) throws Exception {
        String chIds = getChIds(devIds);
        List<MapEntity> historyListByHour = energyAnalysisDao.historyPics(chIds, time, type);
        Set<String> ids = new HashSet<String>();
        for (MapEntity mapEntity : historyListByHour) {
            ids.add(mapEntity.get("id").toString());
        }
        List<List<MapEntity>> list = new ArrayList<List<MapEntity>>();
        for (String id : ids) {
            List<MapEntity> idList = new ArrayList<MapEntity>();
            for (int i = 0; i < historyListByHour.size(); i++) {
                BigDecimal b = null;
                if (i == 0) {
                    b = new BigDecimal(Double.valueOf(historyListByHour.get(i).get("mValue").toString())
                            - Double.valueOf(historyListByHour.get(i).get("minValue").toString()));
                } else {
                    b = new BigDecimal(Double.valueOf(historyListByHour.get(i).get("mValue").toString())
                            - Double.valueOf(historyListByHour.get(i - 1).get("mValue").toString()));
                }
                double value = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                historyListByHour.get(i).put("value", value);

                if (id.equals(historyListByHour.get(i).get("id").toString())) {
                    idList.add(historyListByHour.get(i));
                }
            }
            list.add(idList);
        }
        return list;
    }
    */

    //能效数据 -> 对比分析
    public List<MapEntity> historyPicsByLoopId(String orgId, String time, String type) {
        List<MapEntity> historyPics = energyAnalysisDao.historyPicsByLoopId(orgId, time, type);
        return historyPics;
    }

    public List<MapEntity> historyPicsByUnitId(String unitId, String time, String type) {
        List<MapEntity> historyPics = energyAnalysisDao.historyPicsByUnitId(unitId, time, type);
        return historyPics;
    }

    public List<MapEntity> getOrgListByOrgId(String orgId) {
        return energyAnalysisDao.getOrgListByOrgId(orgId);
    }

    public String getSumEnergyByLoopIdAndTime(String loopId, String startTime, String endTime) {
        return energyAnalysisDao.getSumEnergyByLoopIdAndTime(loopId, startTime, endTime);
    }

    public MapEntity dayMonthYearByLoopId(String loopId) {
        int day = 0;
        int month = 1;
        int year = 2;

        //String dayMap = energyAnalysisDao.dayMonthYearByLoopId(loopId,day);
        //String monthMap = energyAnalysisDao.dayMonthYearByLoopId(loopId,month);
        //String yearMap = energyAnalysisDao.dayMonthYearByLoopId(loopId,year);


        // 获取天
        final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dayStr = LocalDate.now().format(formatterDay);
        logger.debug("dayMonthYearByLoopId1   time:" + dayStr + "   type:" + 0 + "   loopId:" + loopId);
        logger.debug("21 getHistoryListByTypeAndTime point:" + loopId);
        List<Map<String, Object>> dayList = getHistoryListByTypeAndTime(dayStr, "0", loopId);
        Double sumDay = 0.0;
        //合并
        for (Map<String, Object> map : dayList) {
            Object objTotal = map.get("total");
            if (objTotal != null) {
                sumDay += Double.parseDouble(objTotal.toString());
            }
        }
        String dayMap = String.valueOf(sumDay);


        //获取月
        final DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM");
        String monthStr = LocalDate.now().format(formatterMonth);
        logger.debug("dayMonthYearByLoopId2   time:" + monthStr + "   type:" + 1 + "   loopId:" + loopId);
        logger.debug("22 getHistoryListByTypeAndTime point:" + loopId);
        List<Map<String, Object>> monthList = getHistoryListByTypeAndTime(monthStr, "1", loopId);
        Double sumMonth = 0.0;
        //合并
        for (Map<String, Object> map : monthList) {
            Object objTotal = map.get("total");
            if (objTotal != null) {
                sumMonth += Double.parseDouble(objTotal.toString());
            }
        }
        String monthMap = String.valueOf(sumMonth);


        //获取年
        final DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
        String yearStr = LocalDate.now().format(formatterYear);
        logger.debug("dayMonthYearByLoopId3   time:" + yearStr + "   type:" + 2 + "   loopId:" + loopId);
        logger.debug("23 getHistoryListByTypeAndTime point:" + loopId);
        List<Map<String, Object>> yearList = getHistoryListByTypeAndTime(yearStr, "2", loopId);
        Double sumYear = 0.0;
        //合并
        for (Map<String, Object> map : yearList) {
            Object objTotal = map.get("total");
            if (objTotal != null) {
                sumYear += Double.parseDouble(objTotal.toString());
            }
        }
        String yearMap = String.valueOf(sumYear);


        MapEntity result = new MapEntity();
        result.put("dayMap", dayMap);
        result.put("monthMap", monthMap);
        result.put("yearMap", yearMap);
        return result;
    }

    public MapEntity dayMonthYearByUnitId(String unitId) {
        int day = 0;
        int month = 1;
        int year = 2;
		/*
		String dayMap = energyAnalysisDao.dayMonthYearByUnitId(unitId,day);
		String monthMap = energyAnalysisDao.dayMonthYearByUnitId(unitId,month);
		String yearMap = energyAnalysisDao.dayMonthYearByUnitId(unitId,year);
		*/
        Double dayMap = 0.0;
        Double monthMap = 0.0;
        Double yearMap = 0.0;

        List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitId);
        for (final MapEntity unitItem : unitMapList) {

            String unitLoopId = unitItem.get("loopOrgId").toString();
            String proportion = unitItem.get("proportion").toString();
            if (proportion == null) {
                proportion = "0.0";
            }

            // 获取天
            final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dayStr = LocalDate.now().format(formatterDay);
            logger.debug("dayMonthYearByUnitId1   time:" + dayStr + "   type:" + 0 + "   loopId:" + unitLoopId);
            logger.debug("24 getHistoryListByTypeAndTime point:" + unitLoopId);
            List<Map<String, Object>> dayList = getHistoryListByTypeAndTime(dayStr, "0", unitLoopId);
            Double sumDay = 0.0;
            //合并
            for (Map<String, Object> map : dayList) {
                Object objTotal = map.get("total");
                if (objTotal != null) {
                    sumDay += Double.parseDouble(objTotal.toString());
                }
            }

            List<List<Map<String, Object>>> subLoopdataList = new ArrayList<>();
            String sonLoopIds = "";
            if (unitItem.get("sonLoopIds") != null) {
                sonLoopIds = unitItem.get("sonLoopIds").toString();
            }
            String loopArr[] = sonLoopIds.split(",");
            for (final String loop : loopArr) {
                if (!loop.equals("")) {
                    logger.debug("dayMonthYearByUnitId2   time:" + dayStr + "   type:" + 0 + "   loopId:" + loop);
                    logger.debug("25 getHistoryListByTypeAndTime point:" + loop);
                    subLoopdataList.add(getHistoryListByTypeAndTime(dayStr, "0", loop));
                }
            }
            Double sunLoopSumDay = 0.0;
            //归并计算,子回路的使用电量
            for (List<Map<String, Object>> tempLoopDataList : subLoopdataList) {
                for (Map<String, Object> loopData : tempLoopDataList) {
                    String showTime1 = loopData.get("showTime").toString();
                    Double total1 = Double.parseDouble(loopData.get("total").toString());
                    sunLoopSumDay += total1;
                }
            }

            dayMap += (sumDay - sunLoopSumDay) * Double.parseDouble(proportion);


            //获取月
            final DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM");
            String monthStr = LocalDate.now().format(formatterMonth);
            logger.debug("dayMonthYearByUnitId3   time:" + monthStr + "   type:" + 1 + "   loopId:" + unitLoopId);
            logger.debug("26 getHistoryListByTypeAndTime point:" + unitLoopId);
            List<Map<String, Object>> monthList = getHistoryListByTypeAndTime(monthStr, "1", unitLoopId);
            Double sumMonth = 0.0;
            //合并
            for (Map<String, Object> map : monthList) {
                Object objTotal = map.get("total");
                if (objTotal != null) {
                    sumMonth += Double.parseDouble(objTotal.toString());
                }
            }

            subLoopdataList.clear();
            for (final String loop : loopArr) {
                if (!loop.equals("")) {
                    logger.debug("27 getHistoryListByTypeAndTime point:" + loop);
                    subLoopdataList.add(getHistoryListByTypeAndTime(monthStr, "0", loop));
                }
            }
            Double sunLoopSumMonth = 0.0;
            //归并计算,子回路的使用电量
            for (List<Map<String, Object>> tempLoopDataList : subLoopdataList) {
                for (Map<String, Object> loopData : tempLoopDataList) {
                    String showTime1 = loopData.get("showTime").toString();
                    Double total1 = Double.parseDouble(loopData.get("total").toString());
                    sunLoopSumMonth += total1;
                }
            }

            monthMap += (sumMonth - sunLoopSumMonth) * Double.parseDouble(proportion);


            //获取年
            final DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
            String yearStr = LocalDate.now().format(formatterYear);
            logger.debug("dayMonthYearByUnitId4   time:" + yearStr + "   type:" + 2 + "   loopId:" + unitLoopId);
            logger.debug("28 getHistoryListByTypeAndTime point:" + unitLoopId);
            List<Map<String, Object>> yearList = getHistoryListByTypeAndTime(yearStr, "2", unitLoopId);
            Double sumYear = 0.0;
            //合并
            for (Map<String, Object> map : yearList) {
                Object objTotal = map.get("total");
                if (objTotal != null) {
                    sumYear += Double.parseDouble(objTotal.toString());
                }
            }

            subLoopdataList.clear();
            for (final String loop : loopArr) {
                if (!loop.equals("")) {
                    logger.debug("dayMonthYearByUnitId5   time:" + yearStr + "   type:" + 2 + "   loopId:" + loop);
                    logger.debug("29 getHistoryListByTypeAndTime point:" + loop);
                    subLoopdataList.add(getHistoryListByTypeAndTime(yearStr, "2", loop));
                }

            }
            Double sunLoopSumYear = 0.0;
            //归并计算,子回路的使用电量
            for (List<Map<String, Object>> tempLoopDataList : subLoopdataList) {
                for (Map<String, Object> loopData : tempLoopDataList) {
                    String showTime1 = loopData.get("showTime").toString();
                    Double total1 = Double.parseDouble(loopData.get("total").toString());
                    sunLoopSumYear += total1;
                }
            }

            yearMap += (sumYear - sunLoopSumYear) * Double.parseDouble(proportion);


        }


        MapEntity result = new MapEntity();
        result.put("dayMap", dayMap);
        result.put("monthMap", monthMap);
        result.put("yearMap", yearMap);
        return result;
    }

    public MapEntity preDayMonthYearByLoopId(String loopId) {
        int day = 0;
        int month = 1;
        int year = 2;

//		String dayMap = energyAnalysisDao.preDayMonthYearByLoopId(loopId,day);
//		String monthMap = energyAnalysisDao.preDayMonthYearByLoopId(loopId,month);
//		String yearMap = energyAnalysisDao.preDayMonthYearByLoopId(loopId,year);


        // 获取天
        final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dayStr = LocalDate.now().minusDays(1).format(formatterDay);
        logger.debug("preDayMonthYearByLoopId1   time:" + dayStr + "   type:" + 0 + "   loopId:" + loopId);
        logger.debug("30 getHistoryListByTypeAndTime point:" + loopId);
        List<Map<String, Object>> dayList = getHistoryListByTypeAndTime(dayStr, "0", loopId);
        Double sumDay = 0.0;
        //合并
        for (Map<String, Object> map : dayList) {
            Object objTotal = map.get("total");
            if (objTotal != null) {
                sumDay += Double.parseDouble(objTotal.toString());
            }
        }
        String dayMap = String.valueOf(sumDay);


        //获取月
        final DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM");
        String monthStr = LocalDate.now().minusMonths(1).format(formatterMonth);
        logger.debug("preDayMonthYearByLoopId2   time:" + monthStr + "   type:" + 1 + "   loopId:" + loopId);
        logger.debug("31 getHistoryListByTypeAndTime point:" + loopId);
        List<Map<String, Object>> monthList = getHistoryListByTypeAndTime(monthStr, "1", loopId);
        Double sumMonth = 0.0;
        //合并
        for (Map<String, Object> map : monthList) {
            Object objTotal = map.get("total");
            if (objTotal != null) {
                sumMonth += Double.parseDouble(objTotal.toString());
            }
        }
        String monthMap = String.valueOf(sumMonth);


        //获取年
        final DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
        String yearStr = LocalDate.now().minusYears(1).format(formatterYear);
        logger.debug("preDayMonthYearByLoopId3   time:" + yearStr + "   type:" + 2 + "   loopId:" + loopId);
        logger.debug("32 getHistoryListByTypeAndTime point:" + loopId);
        List<Map<String, Object>> yearList = getHistoryListByTypeAndTime(yearStr, "2", loopId);
        Double sumYear = 0.0;
        //合并
        for (Map<String, Object> map : yearList) {
            Object objTotal = map.get("total");
            if (objTotal != null) {
                sumYear += Double.parseDouble(objTotal.toString());
            }
        }
        String yearMap = String.valueOf(sumYear);


        MapEntity result = new MapEntity();
        result.put("dayMap", dayMap);
        result.put("monthMap", monthMap);
        result.put("yearMap", yearMap);
        return result;
    }

    public MapEntity preDayMonthYearByUnitId(String unitId) {
        int day = 0;
        int month = 1;
        int year = 2;

//		String dayMap = energyAnalysisDao.preDayMonthYearByUnitId(unitId,day);
//		String monthMap = energyAnalysisDao.preDayMonthYearByUnitId(unitId,month);
//		String yearMap = energyAnalysisDao.preDayMonthYearByUnitId(unitId,year);

        Double dayMap = 0.0;
        Double monthMap = 0.0;
        Double yearMap = 0.0;

        List<MapEntity> unitMapList = electricityUnitService.getUnitLoopList(unitId);
        for (final MapEntity unitItem : unitMapList) {


            String unitLoopId = unitItem.get("loopOrgId").toString();
            String proportion = unitItem.get("proportion").toString();
            if (proportion == null) {
                proportion = "0.0";
            }


            // 获取天
            final DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dayStr = LocalDate.now().minusDays(1).format(formatterDay);
            logger.debug("preDayMonthYearByUnitId1   time:" + dayStr + "   type:" + 0 + "   loopId:" + unitLoopId);
            logger.debug("33 getHistoryListByTypeAndTime point:" + unitLoopId);
            List<Map<String, Object>> dayList = getHistoryListByTypeAndTime(dayStr, "0", unitLoopId);
            Double sumDay = 0.0;
            //合并
            for (Map<String, Object> map : dayList) {
                Object objTotal = map.get("total");
                if (objTotal != null) {
                    sumDay += Double.parseDouble(objTotal.toString());
                }
            }

            List<List<Map<String, Object>>> subLoopdataList = new ArrayList<>();
            String sonLoopIds = "";
            if (unitItem.get("sonLoopIds") != null) {
                sonLoopIds = unitItem.get("sonLoopIds").toString();
            }
            String loopArr[] = sonLoopIds.split(",");
            for (final String loop : loopArr) {
                if (!loop.equals("")) {
                    logger.debug("preDayMonthYearByUnitId2   time:" + dayStr + "   type:" + 0 + "   loopId:" + loop);
                    logger.debug("34 getHistoryListByTypeAndTime point:" + loop);
                    subLoopdataList.add(getHistoryListByTypeAndTime(dayStr, "0", loop));
                }
            }
            Double sunLoopSumDay = 0.0;
            //归并计算,子回路的使用电量
            for (List<Map<String, Object>> tempLoopDataList : subLoopdataList) {
                for (Map<String, Object> loopData : tempLoopDataList) {
                    String showTime1 = loopData.get("showTime").toString();
                    Double total1 = Double.parseDouble(loopData.get("total").toString());
                    sunLoopSumDay += total1;
                }
            }

            dayMap += (sumDay - sunLoopSumDay) * Double.parseDouble(proportion);


            //获取月
            final DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("yyyy-MM");
            String monthStr = LocalDate.now().minusMonths(1).format(formatterMonth);
            logger.debug("preDayMonthYearByUnitId3   time:" + monthStr + "   type:" + 1 + "   loopId:" + unitLoopId);
            logger.debug("35 getHistoryListByTypeAndTime point:" + unitLoopId);
            List<Map<String, Object>> monthList = getHistoryListByTypeAndTime(monthStr, "1", unitLoopId);
            Double sumMonth = 0.0;
            //合并
            for (Map<String, Object> map : monthList) {
                Object objTotal = map.get("total");
                if (objTotal != null) {
                    sumMonth += Double.parseDouble(objTotal.toString());
                }
            }

            subLoopdataList.clear();
            for (final String loop : loopArr) {
                if (!loop.equals("")) {
                    logger.debug("preDayMonthYearByUnitId4   time:" + monthStr + "   type:" + 1 + "   loopId:" + loop);
                    logger.debug("36 getHistoryListByTypeAndTime point:" + loop);
                    subLoopdataList.add(getHistoryListByTypeAndTime(monthStr, "1", loop));
                }
            }
            Double sunLoopSumMonth = 0.0;
            //归并计算,子回路的使用电量
            for (List<Map<String, Object>> tempLoopDataList : subLoopdataList) {
                for (Map<String, Object> loopData : tempLoopDataList) {
                    String showTime1 = loopData.get("showTime").toString();
                    Double total1 = Double.parseDouble(loopData.get("total").toString());
                    sunLoopSumMonth += total1;
                }
            }

            monthMap += (sumMonth - sunLoopSumMonth) * Double.parseDouble(proportion);


            //获取年
            final DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy");
            String yearStr = LocalDate.now().minusYears(1).format(formatterYear);
            logger.debug("preDayMonthYearByUnitId5   time:" + yearStr + "   type:" + 2 + "   loopId:" + unitLoopId);
            logger.debug("37 getHistoryListByTypeAndTime point:" + unitLoopId);
            List<Map<String, Object>> yearList = getHistoryListByTypeAndTime(yearStr, "2", unitLoopId);
            Double sumYear = 0.0;
            //合并
            for (Map<String, Object> map : yearList) {
                Object objTotal = map.get("total");
                if (objTotal != null) {
                    sumYear += Double.parseDouble(objTotal.toString());
                }
            }

            subLoopdataList.clear();
            for (final String loop : loopArr) {
                if (!loop.equals("")) {
                    logger.debug("preDayMonthYearByUnitId6   time:" + yearStr + "   type:" + 2 + "   loopId:" + loop);
                    logger.debug("38 getHistoryListByTypeAndTime point:" + loop);
                    subLoopdataList.add(getHistoryListByTypeAndTime(yearStr, "2", loop));
                }
            }
            Double sunLoopSumYear = 0.0;
            //归并计算,子回路的使用电量
            for (List<Map<String, Object>> tempLoopDataList : subLoopdataList) {
                for (Map<String, Object> loopData : tempLoopDataList) {
                    String showTime1 = loopData.get("showTime").toString();
                    Double total1 = Double.parseDouble(loopData.get("total").toString());
                    sunLoopSumYear += total1;
                }
            }

            yearMap += (sumYear - sunLoopSumYear) * Double.parseDouble(proportion);


        }


        MapEntity result = new MapEntity();
        result.put("dayMap", dayMap);
        result.put("monthMap", monthMap);
        result.put("yearMap", yearMap);
        return result;
    }


}