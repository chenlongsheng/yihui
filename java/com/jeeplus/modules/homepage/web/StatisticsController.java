package com.jeeplus.modules.homepage.web;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.bim.service.SourceStoreyService;
import com.jeeplus.modules.homepage.dao.StatisticsDao;
import com.jeeplus.modules.homepage.entity.*;
import com.jeeplus.modules.homepage.service.StatisticsService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.sys.utils.UserUtils;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2018-12-20.
 */
@Controller
@RequestMapping(value = "/PDF")
public class StatisticsController {
	@Autowired
	private TOrgService tOrgService;
	@Autowired
	private SourceStoreyService storeyService;
	@Autowired
	private StatisticsService statisticsService;
	@Autowired
	StatisticsDao statisticsDao;

	// 配电房获取视频通道
	@ResponseBody
	@RequestMapping(value = "/orgList")
	public String orgList(String orgId) {
		return ServletUtils.buildRs(true, "配电房树形集合", statisticsService.orgList(orgId));
	}

	@ResponseBody
	@RequestMapping(value = "/getOrgList")
	public String getOrgList() {// 首页所有区域集合
		return ServletUtils.buildRs(true, "首页所有区域集合", statisticsService.getOrgList());
	}

	// 设备启用率
	@ResponseBody
	@RequestMapping(value = "/findDevTypeUse") // 修改999
	public JSONObject findDevTypeUse(Statistics statistics) {
		if (StringUtils.isBlank(statistics.getOrgId())) {
			return ServletUtils.buildJsonRs(false, "地区id不能为空", null);
		}
		if (StringUtils.isBlank(statistics.getDevType())) {
			return ServletUtils.buildJsonRs(false, "设备类型不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", statistics.getOrgId());
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			statistics.setOrgId(o.getId());
		}
		Statistics data = statisticsService.findDevTypeUse(statistics);
//		List<MapEntity> data = statisticsService.findDevTypeUse9(o.getId());
		return ServletUtils.buildJsonRs(true, null, data);
	}

	// 实时报警
	@ResponseBody
	@RequestMapping(value = "/findAlarmType")
	public JSONObject findAlarmType(String orgId) {
		if (StringUtils.isBlank(orgId)) {
			return ServletUtils.buildJsonRs(false, "区域id不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", orgId);
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			orgId = o.getId();
		}
		try {
			List<JSONObject> object = statisticsService.findAlarmType(orgId);
			return ServletUtils.buildJsonRs(true, null, object);
		} catch (Exception e) {
			return ServletUtils.buildJsonRs(false, e.getMessage(), null);
		}
	}

	// 查询某地区安全运行天数、配电房总数、报警配电房数量
	@ResponseBody
	@RequestMapping(value = "/findOrgInfo")
	public JSONObject findOrgInfo(String orgId) {
		if (StringUtils.isBlank(orgId)) {
			return ServletUtils.buildJsonRs(false, "区域id不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", orgId);
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			orgId = o.getId();
		}
		OrgPDFInfo org = statisticsService.findLastAlarm(orgId);
		return ServletUtils.buildJsonRs(true, "", org);
	}

	// 查询某个地区的门禁实时状态
	@ResponseBody
	@RequestMapping(value = "/findAccessOpen")
	public JSONObject findAccessOpen(Statistics statistics) {
		if (StringUtils.isBlank(statistics.getOrgId())) {
			return ServletUtils.buildJsonRs(false, "区域id不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", statistics.getOrgId());
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			statistics.setOrgId(o.getId());
		}
		Statistics data = statisticsService.findAccessOpen(statistics);
		return ServletUtils.buildJsonRs(true, "", data);
	}

	// 门禁首页详情
	@ResponseBody
	@RequestMapping(value = "/doorList")
	public String doorList(String orgId, HttpServletRequest request, HttpServletResponse response) {

	
		MapEntity entity = new MapEntity();
		entity.put("orgId", orgId);
		entity.put("userId", UserUtils.getUser().getId());
		Page<MapEntity> page = statisticsService.findPage(new Page<MapEntity>(request, response), entity);
		return ServletUtils.buildRs(true, "门禁详情", page);
	}

	// 温湿度排行top5
	@ResponseBody
	@RequestMapping(value = "/findHumitureRanking")
	public JSONObject findHumitureRanking(Statistics statistics) {

		if (StringUtils.isBlank(statistics.getOrgId())) {
			return ServletUtils.buildJsonRs(false, "区域id不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", statistics.getOrgId());
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			statistics.setOrgId(o.getId());
		}
		try {
			JSONObject data = statisticsService.findHumitureRanking(statistics);
			return ServletUtils.buildJsonRs(true, "", data);
		} catch (Exception e) {
			return ServletUtils.buildJsonRs(false, e.getMessage(), null);
		}
	}

	// 沙盘模式 报警沙盘
	@ResponseBody
	@RequestMapping(value = "/findWarnSandTable")
	public JSONObject findWarnSandTable(String orgId) {

		if (StringUtils.isBlank(orgId)) {
			return ServletUtils.buildJsonRs(false, "区域id不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", orgId);
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			orgId = o.getId();
		}
		try {
			// List<PDFOrg> data = statisticsService.sandTable(orgId);
			List<MapEntity> data = statisticsService.sandTable(orgId);
			return ServletUtils.buildJsonRs(true, "", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildJsonRs(false, e.getMessage(), null);
		}
	}

	// 沙盘模式 全部
	@ResponseBody
	@RequestMapping(value = "/getSandTable")
	public JSONObject getSandTable(String orgId) {
		if (StringUtils.isBlank(orgId)) {
			return ServletUtils.buildJsonRs(false, "区域id不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", orgId);
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			orgId = o.getId();
		}
		try {
			List<MapEntity> data = statisticsService.sandTable1(orgId);
			return ServletUtils.buildJsonRs(true, "", data);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildJsonRs(false, e.getMessage(), null);
		}
	}

	// 报警趋势
	@ResponseBody
	@RequestMapping(value = "/findAlarmTrend")
	public JSONObject findLastAlarm(String time, int length, String orgId) {

		if (StringUtils.isBlank(orgId)) {
			return ServletUtils.buildJsonRs(false, "区域id不能为空", null);
		}
		TOrg o = storeyService.findPDFOrgByProperty("code", orgId);
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			orgId = o.getId();
		}
		try {
			List<StatisData> data = statisticsService.findAlarmTrend(orgId, time, length);

			return ServletUtils.buildJsonRs(true, "", data);
		} catch (Exception e) {
			return ServletUtils.buildJsonRs(false, e.getMessage(), null);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/getCityByParentId")
	public JSONObject getAllCity(String orgId) {
		TOrg org = new TOrg();
		org.setParentId(Long.valueOf(orgId));
		TOrg o = storeyService.findPDFOrgByProperty("code", orgId);
		if (o == null) {
			return ServletUtils.buildJsonRs(false, "区域信息不存在", null);
		} else {
			orgId = o.getId();
		}
		try {
			List<TOrg> list = tOrgService.findListByParentId(Long.valueOf(orgId));
			return ServletUtils.buildJsonRs(true, "", list);
		} catch (Exception e) {
			return ServletUtils.buildJsonRs(false, e.getMessage(), null);
		}
	}

	// 导入区域
	@ResponseBody
	@RequestMapping(value = "/demo")
	public void demo() {
		ImportExcel ei = null;
		try {
			String url = "D:\\Documents\\WeChat Files\\ct20784\\Files\\目录树.xlsx";
			File file = new File(url);
//            excel.transferTo(file);
			ei = new ImportExcel(file, 1);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 1; i < 5; i++) {
			Row row = ei.getRow(i);
			for (int j = 0; j < 6; j++) {
				Object val = ei.getCellValue(row, j);
				System.out.println(val);
			}
		}
	}

	// 导入区域
	@ResponseBody
	@RequestMapping(value = "/import")
	public void importExcel() {
		ImportExcel ei = null;
		try {
			String url = "D:\\Documents\\Tencent Files\\1015126725\\FileRecv\\工作簿1.xlsx";
			File file = new File(url);
        //   excel.transferTo(file);
			ei = new ImportExcel(file, 1);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> arr = new ArrayList<String>();
		com.jeeplus.modules.bim.entity.PDFOrg org = new com.jeeplus.modules.bim.entity.PDFOrg();
		String shen = "";
		String shenid = "";
		String shi = "";
		String shiId = "";
		String flag = "";
		String parentId = "";
		String parentIds = "";
		String code = "";
		String type = "";
		String name = "";
		for (int i = 1; i < 3221; i++) {
			Row row = ei.getRow(i);
			// 读取项目标题
//            System.out.println(ei.);
			for (int j = 0; j < 6; j++) {
				Object val = ei.getCellValue(row, j);
				if (j == 0 || j == 2 || j == 4) {
					code = "10" + val.toString();
				}
				// 判断是否是新的省
				if (j == 1) {
					if (val.equals(shen)) {
						parentId = shenid;
						parentIds = "1," + shenid;
					} else {
						flag = "1";
						parentId = "1";
						parentIds = "1,";
						shen = val.toString();
						type = "2";
						name = shen;
						break;
					}
					System.out.println(val + ",");
				}
				if (j == 3) {
					if (val.equals(shi) && shi != "") {
						parentId = shiId;
						parentIds = "1," + shenid + "," + shiId + ",";
					} else if (val == "") {
						parentId = shenid;
						parentIds = "1," + shenid + ",";
					} else {
						flag = "2";
						type = "3";
						shi = val.toString();
						name = shi;
						break;
					}
					System.out.println(val + ",");
				}
				if (j == 5) {
					flag = "3";
					type = "4";
					name = val.toString();
					System.out.println(val);
				}
// else{
//                    arr.add(String.valueOf(val));
//                }
//                System.out.print(val+",");
			}
			org.setParentId(Long.valueOf(parentId));
			org.setParentIds(parentIds);
			org.setType(Integer.valueOf(type));
			org.setName(name);
			org.setCode(code);
			org.setId(null);
			storeyService.insertPDFOrg(org);
			if (flag == "1") {
				shenid = org.getId();
			} else if (flag == "2") {
				shiId = org.getId();
			}
			flag = "";
			System.out.println("\n");
		}
	}

}
