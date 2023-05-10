package com.jeeplus.modules.warm.web;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.jeeplus.modules.warm.service.PdfOrderRecorderService;
import com.jeeplus.modules.warm.service.PdfOrderService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ZZUSER on 2018/12/7.
 */
@Controller
@RequestMapping("/warmTemplate")
public class PdfOrderRecorderController extends BaseController {

	@Autowired
	PdfOrderRecorderService pdfOrderRecorderService;

	// 添加模板
	@RequestMapping("/saveTemplateDetail")
	@ResponseBody
	public String saveTemplateDetail(String id, String templateDetail) {
//		String list = templateDetail.replace("&quot;", """);
		System.out.println("-----空的-----"+templateDetail);
		if(templateDetail==null) {
			templateDetail="";
		}	
		try {
			if (StringUtils.isNotBlank(id)) {
				pdfOrderRecorderService.updateTemplate(templateDetail.toString(), id);// xiugai
				UserUtils.saveLog("一个通知模板被修改", "修改");
			} else {
				pdfOrderRecorderService.insertTemplateDetail(UserUtils.getUser().getId(), templateDetail);
				UserUtils.saveLog("新建一个通知模板", "新增");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", "");
		}
		return ServletUtils.buildRs(true, "成功", "");

	}

	// 获取集合
	@RequestMapping("/getTemplateList")
	@ResponseBody
	public String getTemplateList( String name,String beginDate,String endDate,HttpServletRequest request, HttpServletResponse response) {
		MapEntity entity = new MapEntity();
		entity.put("name", name);
		if(beginDate != null && !beginDate.equals("")) {
			beginDate = beginDate+" 00:00:00";
		}
		if(endDate != null && !endDate.equals("")) {
			endDate = endDate+" 23:59:59";
		}
		entity.put("beginDate", beginDate);
		entity.put("endDate", endDate);
		List<MapEntity> list = pdfOrderRecorderService.findPage(entity);
		return ServletUtils.buildRs(true, "成功",list);
	}
	
	@RequestMapping("/mobanList")
	@ResponseBody
	public String mobanList( String name,String beginDate,String endDate,HttpServletRequest request, HttpServletResponse response) {
		
		return ServletUtils.buildRs(true, "成功",pdfOrderRecorderService.mobanList(null));		
	}
	
	@RequestMapping("/deleteTemplate")
	@ResponseBody
	public String deleteTemplate(String id) {
		try {
			pdfOrderRecorderService.deleteTemplate(id);
			UserUtils.saveLog("删除一个通知模板", "删除");
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", "");
		}
		return ServletUtils.buildRs(true, "成功", "");

	}

}
