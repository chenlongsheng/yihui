/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.settings.service.TDeviceService;

/**
 * 数据配置Controller
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "settings/tDeviceDetail")
public class TDeviceDetailController extends BaseController {

	@Autowired
	private TDeviceDetailService tDeviceDetailService;
	
	@ModelAttribute("tDeviceDetail")
	public TDeviceDetail get(@RequestParam(required=false) String id) {
		TDeviceDetail entity = null;
		if (StringUtils.isNotBlank(id+"")){
			entity = tDeviceDetailService.get(id);
		}
		if (entity == null){
			entity = new TDeviceDetail();
		}
		return entity;
	}
	
	/**
	 * 数据配置列表页面
	 */
	@RequiresPermissions("settings:tDeviceDetail:list")
	@RequestMapping(value = {"list", ""})
	public String list(@ModelAttribute("tDeviceDetail")TDeviceDetail tDeviceDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TDeviceDetail> page = tDeviceDetailService.findPage(new Page<TDeviceDetail>(request, response), tDeviceDetail); 
		model.addAttribute("page", page);
		return "modules/settings/tDeviceDetailList";
	}

	/**
	 * 查看，增加，编辑数据配置表单页面
	 */
	@RequiresPermissions(value={"settings:tDeviceDetail:view","settings:tDeviceDetail:add","settings:tDeviceDetail:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(@ModelAttribute("tDeviceDetail")TDeviceDetail tDeviceDetail, Model model) {
		model.addAttribute("tDeviceDetail", tDeviceDetail);
		return "modules/settings/tDeviceDetailForm";
	}

	/**
	 * 保存数据配置
	 */
	@RequiresPermissions(value={"settings:tDevice:add","settings:tDevice:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public String save(TDeviceDetail tDeviceDetail, Model model, RedirectAttributes redirectAttributes) throws Exception{
		
		System.out.println(tDeviceDetail.toString());
		if (!beanValidator(model, tDeviceDetail)){
			return form(tDeviceDetail, model);
		}
		if(!tDeviceDetail.getIsNewRecord()){//编辑表单保存
			TDeviceDetail t = tDeviceDetailService.get(tDeviceDetail.getId());//从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(tDeviceDetail, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
			tDeviceDetailService.save(t);//保存
		}else{//新增表单保存
			tDeviceDetailService.save(tDeviceDetail);//保存
		}
		addMessage(redirectAttributes, "保存数据配置成功");
		return "redirect:"+Global.getAdminPath()+"/settings/tDeviceDetail/?repage";
	}
	
	/**
	 * 删除数据配置
	 */
	@RequiresPermissions("settings:tDeviceDetail:del")
	@RequestMapping(value = "delete")
	public String delete(TDeviceDetail tDeviceDetail, RedirectAttributes redirectAttributes) {
		tDeviceDetailService.delete(tDeviceDetail);
		addMessage(redirectAttributes, "删除数据配置成功");
		return "redirect:"+Global.getAdminPath()+"/settings/tDeviceDetail/?repage";
	}
	
	/**
	 * 批量删除数据配置
	 */
	@RequiresPermissions("settings:tDeviceDetail:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			tDeviceDetailService.delete(tDeviceDetailService.get(id));
		}
		addMessage(redirectAttributes, "删除数据配置成功");
		return "redirect:"+Global.getAdminPath()+"/settings/tDeviceDetail/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("settings:tDeviceDetail:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(TDeviceDetail tDeviceDetail, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "数据配置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TDeviceDetail> page = tDeviceDetailService.findPage(new Page<TDeviceDetail>(request, response, -1), tDeviceDetail);
    		new ExportExcel("数据配置", TDeviceDetail.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出数据配置记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/settings/tDeviceDetail/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("settings:tDeviceDetail:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TDeviceDetail> list = ei.getDataList(TDeviceDetail.class);
			for (TDeviceDetail tDeviceDetail : list){
				try{
					tDeviceDetailService.save(tDeviceDetail);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条数据配置记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条数据配置记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入数据配置失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/settings/tDeviceDetail/?repage";
    }
	
	/**
	 * 下载导入数据配置数据模板
	 */
	@RequiresPermissions("settings:tDeviceDetail:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "数据配置数据导入模板.xlsx";
    		List<TDeviceDetail> list = Lists.newArrayList(); 
    		new ExportExcel("数据配置数据", TDeviceDetail.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/settings/tDeviceDetail/?repage";
    }
	
	@RequestMapping(value = { "tCatgutList" })
	@ResponseBody
	public String tCatgutList(String orgId) {
				
		return ServletUtils.buildRs(true, "地埋线设备", tDeviceDetailService.tCatgutList(orgId));
	}

}