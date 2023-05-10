/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.starnet.service;

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
import com.jeeplus.modules.starnet.dao.EnergyAnalysisDao;
import com.jeeplus.modules.starnet.dao.LargeScreenPageDao;
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
public class LargeScreenPageService {

	@Autowired
	private LargeScreenPageDao largeScreenPageDao;

	public List<MapEntity> getChNames() {

		return largeScreenPageDao.getChNames();

	}

	public List<MapEntity> getHistoryByChId(String chId, String time) {

		return largeScreenPageDao.getHistoryByChId(chId, time);

	}

	public List<MapEntity> getOrgNums() {

		return largeScreenPageDao.getOrgNums();

	}

}