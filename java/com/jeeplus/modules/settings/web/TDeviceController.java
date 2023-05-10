/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.settings.web;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.persistence.Page;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.OrgUtil;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.maintenance.entity.PdfUserOrg;
import com.jeeplus.modules.settings.dao.TDeviceDao;
import com.jeeplus.modules.settings.entity.TAlarmPolicy;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TCode;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceConfig;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TDeviceConfigService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.settings.service.TDeviceService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.entity.Dict;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 设备管理Controller
 *
 * @author long
 * @version 2018-07-24
 */
@Controller
@RequestMapping(value = "settings/tDevice")
public class TDeviceController extends BaseController {

    @Autowired
    private TDeviceService tDeviceService;
    @Autowired
    private TDeviceDetailService tDeviceDetailService;
    @Autowired
    private TDeviceConfigService tDeviceConfigService;
    @Autowired
    private TChannelService tChannelService;
    @Autowired
    private AreaService areaService;
    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    // 设备条数 首页部分
    @RequestMapping(value = {"allView"})
    @ResponseBody
    public String allView(String orgId) {
        return ServletUtils.buildRs(true, "数据总览", tDeviceService.allView(orgId));
    }

    @RequestMapping(value = {"alarmLogList"})
    @ResponseBody
    public String alarmLogList(String orgId) {
        return ServletUtils.buildRs(true, "数据总览", tDeviceService.allView(orgId));
    }

    // 津泰配电房视频列表
    @RequestMapping(value = {"videolist"})
    @ResponseBody
    public String videolist(String orgId) {
        return ServletUtils.buildRs(true, "视频列表", tDeviceService.videolist(orgId));
    }

    // 当天历史记录
    @RequestMapping(value = {"getTodayDatasByPdfId"})
    @ResponseBody
    public String getTodayDatasByPdfId(String orgId) {

//		return ServletUtils.buildRs(true, "当天历史数据", tDeviceService.getTodayDatasByPdfId(orgId));

        return ServletUtils.buildRs(true, "当天历史数据", "");
    }

    // 獲取网关实时状态
    @RequestMapping(value = {"macStateList"})
    @ResponseBody
    public String macStateList(String orgId) {
        return ServletUtils.buildRs(true, "网关实时状态", tDeviceService.macStateList(orgId));
    }

    @RequestMapping(value = "getChannelAndLoopBydevId")
    @ResponseBody
    public String getChannelAndLoopBydevId(String devId) {

        return ServletUtils.buildRs(true, "获取回路关联", tDeviceService.getChannelByDevId(devId));

    }

    // 新网关识别设备
    @RequestMapping(value = {"GatewayList"})
    @ResponseBody
    public String GatewayList(String orgId) {
        return ServletUtils.buildRs(true, "网关实时状态", tDeviceService.GatewayList(orgId));
    }

    // nvr集合设备--------------------------------------------
    @RequestMapping(value = {"getNVRList"})
    @ResponseBody
    public String getNVRList(String orgId) {
        return ServletUtils.buildRs(true, "获取NVR集合", tDeviceService.getNVRList(orgId));
    }

    // 删除nvr集合设备---------------------------------------
    @RequestMapping(value = {"deleteNvrByDevId"})
    @ResponseBody
    public String deleteNvrByDevId(String devId) {
        try {
            tDeviceService.deleteNvrByDevId(devId);
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "nvr删除失败", null);
        }
        return ServletUtils.buildRs(true, "nvr删除成功", null);
    }

    @RequestMapping(value = {"updateOrgBydevId"})
    @ResponseBody
    public String updateOrgBydevId(String devId) {
        try {
            tDeviceService.updateOrgBydevId(devId);
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "网关删除失败", null);
        }
        return ServletUtils.buildRs(true, "网关删除成功", null);
    }

    // ----------------------------------
    // 新写设备管理--获取设备查询
    @RequestMapping(value = {"deviceList"})
    @ResponseBody
    public String deviceList(String orgId) {
        MapEntity entity = new MapEntity();
        List<MapEntity> list = null;
        try {
            list = tDeviceService.deviceList(orgId);
            List<MapEntity> smokeList = tDeviceService.getSmokeList(orgId);
            List<MapEntity> selectNVRList = tDeviceService.selectNVRList(orgId);
            entity.put("list", list);
            entity.put("smokeList", smokeList);
            entity.put("selectNVRList", selectNVRList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServletUtils.buildRs(true, "设备管理", entity);
    }

    // 新写设备管理--修改
    @RequestMapping(value = {"modifyDevice"})
    @ResponseBody
    public String list(String id, String addr) {
        try {
            tDeviceService.midifyDevice(id, addr);
            MapEntity entity = tDeviceService.getOrgName(id);
            UserUtils.saveLog((String) entity.get("orgName") + "的一个" + (String) entity.get("devTypeName") + "修改了安装位置",
                    "修改");
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "修改失败", null);
        }
        return ServletUtils.buildRs(true, "修改成功", null);
    }

    // 删除设备
    @RequestMapping(value = "deleteDevice")
    @ResponseBody
    public String deleteDevice(TDevice tDevice) {
        try {
            MapEntity entity = tDeviceService.getOrgName(tDevice.getId());
            tDeviceService.delete(tDevice);
            UserUtils.saveLog((String) entity.get("orgName") + "配电房删除了一个" + (String) entity.get("devTypeName"), "删除");
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "删除失败", null);
        }
        return ServletUtils.buildRs(true, "删除成功", null);
    }

    // 添加中获取绑定设备信息
    @RequestMapping(value = "addSelectDevice")
    @ResponseBody
    public String addSelectDevice(String mac) {
        List<MapEntity> deviceByMac = null;
        MapEntity entity = new MapEntity();

        try {
            deviceByMac = tDeviceService.deviceByMac(mac);
            if (deviceByMac.size() == 0) {
                entity.put("check", "-1");
                return ServletUtils.buildRs(false, "查无网关", entity);
            }
            for (MapEntity mapEntity : deviceByMac) {
                String orgId = (Long) mapEntity.get("orgId") + "";
                String orgName = (String) mapEntity.get("orgName");
                String codeName = (String) mapEntity.get("codeName");
                if (!orgId.equals("100000")) {
                    entity.put("check", "0");
                    entity.put("orgId", orgId);
                    entity.put("orgName", orgName);
                    entity.put("codeName", codeName);
                    return ServletUtils.buildRs(false, "已绑定配电房", entity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity.put("check", "-2");
            return ServletUtils.buildRs(false, "查询报错失敗", entity);
        }
        return ServletUtils.buildRs(true, "查询成功", deviceByMac);
    }

    /**
     * 回调修改电
     *
     * @param devId
     * @return
     */
    @RequestMapping(value = "getElectricList")
    @ResponseBody
    public String getElectricList(String devId) {
        return ServletUtils.buildRs(true, "查詢成功", tDeviceService.getElectricList(devId));
    }

    // 修改所有設備addr
    @RequestMapping(value = {"addModifyDevice"})
    @ResponseBody
    public String addModifyDevice(String deviceList, String channelList, String NVRList) {
        JSONArray ja = null;
        JSONArray jaCh = null;
        JSONArray jaNVR = null;

        logger.debug("11111进入添加配电房前========:  " + deviceList);
        if (StringUtils.isNotBlank(deviceList)) {
            String list = deviceList.replace("&quot;", "'");
            ja = JSONArray.parseArray(list);
            logger.debug("11111进入添加配电房后========deviceList:  " + ja);
        }
        if (StringUtils.isNotBlank(channelList)) {
            String chList = channelList.replace("&quot;", "'");
            System.out.println(chList);
            jaCh = JSONArray.parseArray(chList);
        }
        if (StringUtils.isNotBlank(NVRList)) {
            String chList = NVRList.replace("&quot;", "'");
            System.out.println(chList);
            jaNVR = JSONArray.parseArray(chList);
        }
        try {
            String orgId = tDeviceService.addModifyDevice(ja, jaCh, jaNVR);
            UserUtils.saveLog(areaService.get(orgId).getName() + "添加了新网关", "新增");

        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("11111保存失败======= ");
            return ServletUtils.buildRs(false, "修改失敗", null);
        }
        return ServletUtils.buildRs(true, "修改成功", null);
    }

    // 獲取配電房信息
    @RequestMapping(value = {"getUserDetail"})
    @ResponseBody
    public String getUserDetail(String orgId) {
        return ServletUtils.buildRs(true, "配电房地址电话", tDeviceService.getUserDetail(orgId));
    }

    // --------------------------------------------------------------
    @ModelAttribute("tDevice")
    public TDevice get(@RequestParam(required = false) String id) {
        TDevice entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = tDeviceService.get(id);
        }
        if (entity == null) {
            entity = new TDevice();
        }
        return entity;
    }

    @RequestMapping(value = {"index"})
    public String index(TDevice tDevice, Model model) {

        return "modules/settings/tDeviceIndex";
    }

    /**
     * 设备管理列表页面
     */
//	@RequiresPermissions("settings:tDevice:index")
    @RequestMapping(value = {"list", ""})
    @ResponseBody
    public String list(String name, String codeName, String notUse, String projectName, String orgTreeId,
                       String signLogo, HttpServletRequest request, HttpServletResponse response, Model model) {


        if (signLogo == null) {
            if (projectName == null || projectName == "") {
                projectName = "停车场";
            }
        }
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        System.out.println(pageNo + "---pageNo");
        String orgId = OrgUtil.getOrgId();

        if (signLogo == null) {
            request.getSession().removeAttribute("deviceName");
            request.getSession().removeAttribute("deviceCodeName");
            request.getSession().removeAttribute("deviceNotUse");
            request.getSession().removeAttribute("deviceProjectName");
            request.getSession().removeAttribute("deviceOrgTreeId");
            request.getSession().removeAttribute("pageNo");
            request.getSession().removeAttribute("pageSize");
        }
        if (name != null || codeName != null || notUse != null || orgTreeId != null) {
            request.getSession().setAttribute("pageNo", pageNo);
            request.getSession().setAttribute("pageSize", pageSize);
            request.getSession().setAttribute("deviceName", name);
            request.getSession().setAttribute("deviceCodeName", codeName);
            request.getSession().setAttribute("deviceNotUse", notUse);
            request.getSession().setAttribute("deviceOrgTreeId", orgTreeId);
        }
        if (signLogo == null) {
            request.getSession().setAttribute("deviceProjectName", projectName);
        }
        if (signLogo != null) {
            name = (String) request.getSession().getAttribute("deviceName");
            codeName = (String) request.getSession().getAttribute("deviceCodeName");
            notUse = (String) request.getSession().getAttribute("deviceNotUse");
            projectName = (String) request.getSession().getAttribute("deviceProjectName");
            orgTreeId = (String) request.getSession().getAttribute("deviceOrgTreeId");
            System.out.println(projectName + "------signLogo != null");
        }
        if (orgTreeId != null) {
            orgId = orgTreeId;
        }
        List<TDeviceConfig> configList = tDeviceConfigService.configList("停车场");
        model.addAttribute("configList", configList);
        String nameConfig = nameConfig(configList);
        MapEntity entity = new MapEntity();
        entity.put("projectName", projectName);
        entity.put("name", name);
        entity.put("codeName", codeName);
        entity.put("notUse", notUse);
        entity.put("orgId", orgId);
        entity.put("nameConfig", nameConfig);
        List<MapEntity> codeList = tDeviceService.codeList();
        model.addAttribute("codeList", codeList);
        Page<MapEntity> page = tDeviceService.findPage(new Page<MapEntity>(request, response), entity);
        List<MapEntity> pageList = page.getList();

        page.setList(pageList);
        List<String> configNames = tDeviceConfigService.configName();// 项目名
        model.addAttribute("configNames", configNames);
        model.addAttribute("page", page);
        model.addAttribute("name", name);
        model.addAttribute("codeName", codeName);
        model.addAttribute("notUse", notUse);
        model.addAttribute("projectName", projectName);
        model.addAttribute("orgTreeId", orgId);
        request.getSession().setAttribute("deviceOrgTreeId", configNames);
        return "modules/settings/tDeviceList";
    }

    /**
     * 查看，增加，编辑设备管理表单页面
     */
//	@RequiresPermissions(value = { "settings:tDevice:view", "settings:tDevice:edit" }, logical = Logical.OR)
    @RequestMapping(value = "form")
    public String form(@RequestParam(required = false) String id, Model model, HttpServletRequest request) {
        String projectName = (String) request.getSession().getAttribute("deviceProjectName");
        TDevice tDevice = tDeviceService.get(id);
        TDeviceDetail tDeviceDetail = tDeviceDetailService.get(id);
        List<MapEntity> codeList = tDeviceService.codeList();
        List<TDeviceConfig> deviceFromList = tDeviceConfigService.deviceFromList(projectName);
        model.addAttribute("deviceFromList", deviceFromList);
        String nameConfig = nameConfig(deviceFromList);
        if (nameConfig != "") {
            System.out.println("进来");
            MapEntity entity = tDeviceService.getDeviceFrom(id, nameConfig);
            model.addAttribute("entity", entity);
        }
        model.addAttribute("codeList", codeList);
        model.addAttribute("tDevice", tDevice);
        model.addAttribute("tDeviceDetail", tDeviceDetail);
        return "modules/settings/tDeviceForm";
    }

    // 添加的from
    // @RequiresPermissions(value = { "settings:tDevice:add" }, logical =
    // Logical.OR)
    @RequestMapping(value = "formAdd")
    public String formAdd(@RequestParam(required = false) String id, Model model, HttpServletRequest request) {
        String projectName = (String) request.getSession().getAttribute("deviceProjectName");

        TDevice tDevice = tDeviceService.get(id);
        TDeviceDetail tDeviceDetail = tDeviceDetailService.get(id);
        List<MapEntity> codeList = tDeviceService.codeList();
        List<TDeviceConfig> deviceFromList = tDeviceConfigService.deviceFromList(projectName);
        model.addAttribute("deviceFromList", deviceFromList);
        String nameConfig = nameConfig(deviceFromList);
        if (nameConfig != "") {
            System.out.println("进来");
            MapEntity entity = tDeviceService.getDeviceFrom(id, nameConfig);
            model.addAttribute("entity", entity);
        }
        if (tDevice == null) {
            tDevice = new TDevice();
            String orgId = UserUtils.getUser().getArea().getId();
            String orgTreeId = (String) request.getSession().getAttribute("deviceOrgTreeId");
            if (orgTreeId != null) {
                orgId = orgTreeId;
            }
            tDevice.setOrgId(orgId);
            tDevice.setArea(areaService.get(orgId));
        }
        model.addAttribute("codeList", codeList);
        model.addAttribute("tDevice", tDevice);
        model.addAttribute("tDeviceDetail", tDeviceDetail);
        return "modules/settings/tDeviceFormAdd";
    }

    /**
     * 保存设备管理
     */
    @RequiresPermissions(value = {"settings:tDevice:add", "settings:tDevice:edit"}, logical = Logical.OR)
    @RequestMapping(value = "save")
    public String save(TDevice tDevice, TDeviceDetail tDeviceDetail, String org, String channelUse, Model model,
                       HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        System.out.println(channelUse);
        System.out.println(org);
        System.out.println(tDevice.getOrgId());
        System.out.println(tDevice.getId());
        int key = -1;
        TChannel tChannel = new TChannel();
        if (!beanValidator(model, tDevice)) {
            return form(tDevice.getId(), model, request);
        }
        if (!tDevice.getIsNewRecord()) {// 编辑表单保存
            System.out.println("更新界面");
            TDevice t = tDeviceService.get(tDevice.getId());// 从数据库取出记录的值
            TDeviceDetail td = tDeviceDetailService.get(tDeviceDetail.getId());
            MyBeanUtils.copyBeanNotNull2Bean(tDevice, t);// 将编辑表单中的非NULL值覆盖数据库记录中的值
            MyBeanUtils.copyBeanNotNull2Bean(tDeviceDetail, td);
            tDeviceService.saveDevice(t, td);// 保存
        } else {// 新增表单保存
            String id = tDeviceService.saveDevice(tDevice, tDeviceDetail);// 保存
            tDevice.setId(id);
        }
        if (channelUse.equals("1")) {
            key = tChannelService.updateOrg(tDevice.getOrgId(), tDevice.getId());
            addMessage(redirectAttributes, "同步设备下通道" + key + "条");
        }
        addMessage(redirectAttributes, "保存设备管理成功");
        if (key >= 0) {
            addMessage(redirectAttributes, "保存设备管理成功,通道同步" + key + "条");
        }
        String pageNo = (String) request.getSession().getAttribute("pageNo");
        String pageSize = (String) request.getSession().getAttribute("pageSize");
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/list/?repage&signLogo=0&pageNo=" + pageNo
                + "&pageSize=" + pageSize;
    }

    @RequestMapping(value = "saveUse")
    public String saveUse(TDevice tDevice, Model model, HttpServletRequest request,
                          RedirectAttributes redirectAttributes) throws Exception {

        int t = tDeviceService.saveUse(tDevice);// 从数据库取出记录的值
        String pageNo = (String) request.getSession().getAttribute("pageNo");
        String pageSize = (String) request.getSession().getAttribute("pageSize");
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/list/?repage&signLogo=0&pageNo=" + pageNo
                + "&pageSize=" + pageSize;
    }

    /**
     * 删除设备管理
     */
    @RequiresPermissions("settings:tDevice:del")
    @RequestMapping(value = "delete")
    public String delete(TDevice tDevice, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        tDeviceService.delete(tDevice);
        addMessage(redirectAttributes, "删除设备管理成功");
        String pageNo = (String) request.getSession().getAttribute("pageNo");
        String pageSize = (String) request.getSession().getAttribute("pageSize");
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/list/?repage&signLogo=0&pageNo=" + pageNo
                + "&pageSize=" + pageSize;
    }

    /**
     * 批量删除设备管理
     */
    @RequiresPermissions("settings:tDevice:del")
    @RequestMapping(value = "deleteAll")
    public String deleteAll(String ids, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String idArray[] = ids.split(",");
        for (String id : idArray) {
            tDeviceService.delete(tDeviceService.get(id));
        }
        addMessage(redirectAttributes, "删除设备管理成功");
        String pageNo = (String) request.getSession().getAttribute("pageNo");
        String pageSize = (String) request.getSession().getAttribute("pageSize");
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/list/?repage&signLogo=0&pageNo=" + pageNo
                + "&pageSize=" + pageSize;
    }

    /**
     * 导出excel文件
     */
    @RequiresPermissions("settings:tDevice:export")
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public String exportFile(TDevice tDevice, HttpServletRequest request, HttpServletResponse response,
                             RedirectAttributes redirectAttributes) {
        try {
            String fileName = "设备管理" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            Page<TDevice> page = tDeviceService.findPage(new Page<TDevice>(request, response, -1), tDevice);
            new ExportExcel("设备管理", TDevice.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出设备管理记录失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/?repage";
    }

    /**
     * 导入Excel数据
     */
    @RequiresPermissions("settings:tDevice:import")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<TDevice> list = ei.getDataList(TDevice.class);
            for (TDevice tDevice : list) {
                try {
                    tDeviceService.save(tDevice);
                    successNum++;
                } catch (ConstraintViolationException ex) {
                    failureNum++;
                } catch (Exception ex) {
                    failureNum++;
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "，失败 " + failureNum + " 条设备管理记录。");
            }
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条设备管理记录" + failureMsg);
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入设备管理失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/?repage";
    }

    /**
     * 下载导入设备管理数据模板
     */
    @RequiresPermissions("settings:tDevice:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "设备管理数据导入模板.xlsx";
            List<TDevice> list = Lists.newArrayList();
            new ExportExcel("设备管理数据", TDevice.class, 1).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/?repage";
    }

    // 方法
    public String nameConfig(List<TDeviceConfig> deviceFromList) {
        String nameConfig = "";
        for (int i = 0; i < deviceFromList.size(); i++) {
            if (i != 0) {
                nameConfig += ",";
            }
            nameConfig += deviceFromList.get(i).getPrefix() + deviceFromList.get(i).getRowName() + " as "
                    + deviceFromList.get(i).getModelName();
        }
        return nameConfig;
    }

    // 上传图片
    @RequestMapping(value = {"upLoadPic"})
    @ResponseBody
    public JSONObject upLoadPic(String id, String status, HttpServletRequest request, MultipartFile imgFile,
                                HttpServletResponse response) throws Exception {
        System.out.println("上传设备图标");
        JSONObject json = new JSONObject();
        // 获取文件原始名称
        String originalFilename = imgFile.getOriginalFilename();
        // 上传图片
        if (imgFile != null && originalFilename != null && originalFilename.length() > 0) {
            // 存储图片的物理路径
            String pic_path = request.getSession().getServletContext().getRealPath("/");
            String path = "static_modules/device/";

            System.out.println(pic_path + path);
            File file = new File(pic_path + path);
            // 如果文件夹不存在则创建
            if (!file.exists() && !file.isDirectory()) {
                System.out.println("//不存在");
                file.mkdir();
            } else {
                System.out.println("//目录存在");
            }

            String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            // 新图片
            System.out.println(pic_path + path + newFileName);
            File newFile = new File(pic_path + path + newFileName);
            // 将内存中的数据写入磁盘
            imgFile.transferTo(newFile);

            try {
                tDeviceService.updatePic(id, path + newFileName);// 保存
                json.put("suc", "成功上传");
            } catch (Exception e) {
                json.put("suc", "成功失败");
            }
        }
        return json;
    }

    // 下载tcode小图标
    @RequestMapping(value = {"getPic"}, method = RequestMethod.POST)
    @ResponseBody
    public String getpic(String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(id + "getPic");
        TDevice tDevice = tDeviceService.get(id);
        String image = tDevice.getPicturePath();
        String pic_path = request.getSession().getServletContext().getRealPath("/") + image;
        File file = new File(pic_path);
        if (StringUtils.isBlank(tDevice.getPicturePath())) {
            return "false";
        }
        return image;
    }

    // 小图片回调
    @RequestMapping(value = "codeRedirect")
    public String codeRedirect(HttpServletRequest request) throws Exception {
        String pageNo = (String) request.getSession().getAttribute("pageNo");
        String pageSize = (String) request.getSession().getAttribute("pageSize");
        return "redirect:" + Global.getAdminPath() + "/settings/tDevice/?repage&signLogo=0&pageNo=" + pageNo
                + "&pageSize=" + pageSize;
    }

    @RequestMapping(value = "eMapForm")
    public String form(String orgId, String coldId, String typeId, String coordX, String coordY,
                       HttpServletRequest request, Model model) {
        System.out.println(orgId + "----哈哈哈");
        System.out.println(coldId);
        // System.out.println(typeId);
        String[] str = coldId.split(",");
        List<MapEntity> deviceList = tDeviceService.devicePic(orgId, str[1]);

        model.addAttribute("length", deviceList.size());
        model.addAttribute("deviceList", deviceList);
        request.getSession().setAttribute("coordX", coordX);
        request.getSession().setAttribute("coordY", coordY);
        System.out.println(coordX);
        System.out.println(coordY);
        return "modules/settings/eMapForm";
    }

    @RequestMapping(value = "eMapSave")
    @ResponseBody
    public String eMapSave(String id, Model model, HttpServletRequest request) {

        String coordX = (String) request.getSession().getAttribute("coordX");
        String coordY = (String) request.getSession().getAttribute("coordY");
        System.out.println(coordX);
        System.out.println(coordY);
        int key = tDeviceService.updateCoords(id, coordX, coordY);
        return "1";
    }

    @RequestMapping(value = "getDevTypeList")
    @ResponseBody
    public String getDevTypeList(String orgId) {

        return ServletUtils.buildRs(true, "获取设备类型数量报警", tDeviceService.getDevTypeList(orgId));

    }

    /**
     * 修改电器addr
     *
     * @param devId
     * @param electricList
     * @return
     */
    @RequestMapping(value = "updateChannelBychId")
    @ResponseBody
    public String updateChannelBychId(String devId, String electricList) {
        JSONArray ja = null;
        if (electricList != null) {
            String list = electricList.replace("&quot;", "'");// 替换json的乱码
            ja = JSONArray.parseArray(list);
        }
        try {
            tDeviceService.updateChannelBychId(devId, ja);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "修改失败", "");
        }
        return ServletUtils.buildRs(true, "修改成功", "");
    }

    @RequestMapping(value = "getOrgListById")
    @ResponseBody
    public String getOrgListById(String orgId) {

        return ServletUtils.buildRs(true, "", tDeviceService.getOrgListById(orgId));
    }

    @RequestMapping(value = "deleteChanByChId")
    @ResponseBody
    public String deleteChanByChId(String chId) {
        try {
            tDeviceService.deleteChanByChId(chId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "删除失败", null);
        }
        return ServletUtils.buildRs(true, "删除成功", null);
    }

    @RequestMapping(value = "updateVideoBychId")
    @ResponseBody
    public String updateVideoBychId(String chId) {
        try {

            tDeviceService.deleteVideoBychId(chId);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "删除失败", "");

        }

        return ServletUtils.buildRs(true, "删除成功", "");
    }

    @RequestMapping(value = {"modifyVideo"})
    @ResponseBody
    public String modifyVideo(String NVRList) {

        JSONArray jaNVR = null;
        if (StringUtils.isNotBlank(NVRList)) {
            String chList = NVRList.replace("&quot;", "'");
            System.out.println(chList);
            jaNVR = JSONArray.parseArray(chList);
            System.out.println(jaNVR);
        }
        try {

            tDeviceService.modifyVideoOrderNo(jaNVR);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "修改失败", null);
        }

        return ServletUtils.buildRs(true, "修改成功", null);
    }

    @RequestMapping(value = "getNVRFrom")
    @ResponseBody
    public String getNVRFrom(String chId) {

        return ServletUtils.buildRs(true, "查询成功", tDeviceService.getNVRFrom(""));
    }

    @RequestMapping(value = "modifyVideoBychId")
    @ResponseBody
    public String modifyVideoBychId(String channelType, String chId, String addr, String remarks, String chName) {
        try {
            tDeviceService.updateVideo(channelType, chId, addr, remarks, chName);
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, " 添加失败", null);
        }
        return ServletUtils.buildRs(true, " 添加成功", null);
    }

    //获取地锁详情
    @RequestMapping(value = "selectLockList")
    @ResponseBody
    public String selectLockList(String orgId) {

        return ServletUtils.buildRs(true, "获取地锁详情", tDeviceService.selectLockList(orgId));
    }


    //获取地锁详情
    @RequestMapping(value = "selectLockList1")
    @ResponseBody
    public String selectLockList1(String orgId) {

        return ServletUtils.buildRs(true, "获取地锁详情", tDeviceService.selectLockList1(orgId));
    }

    @RequestMapping(value = "getLoopByOrgId")
    @ResponseBody
    public String getLoopByOrgId(String orgId) {

        return ServletUtils.buildRs(true, "回路关联下拉框", tDeviceService.getLoopByOrgId(orgId));

    }

    @RequestMapping(value = {"updateOrgIdByChId"})
    @ResponseBody
    public String updateOrgIdByChId(String loopId, String chIds) {

        try {
            tDeviceService.updateOrgIdByChId(chIds, loopId);
            return ServletUtils.buildRs(true, "修改成功", "");
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "修改失败", "");
        }

    }


}