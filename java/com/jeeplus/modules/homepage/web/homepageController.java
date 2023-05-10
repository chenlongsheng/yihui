/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.homepage.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.enterprise.service.TPreAlarmSettingsService;
import com.jeeplus.modules.homepage.service.StatisticsService;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.service.OfficeService;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * 机构Controller
 * 
 * @author jeeplus
 * @version 2013-5-15
 */
@Controller
@RequestMapping(value = "homepage/")
public class homepageController extends BaseController {

    @Autowired
    private TOrgService tOrgService;

    @Autowired
    TOperLogService operLogService;

    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    RedisTemplate<String, String> redisTempldate;
    
    
    

    // 配电房获取视频通道
    @ResponseBody
    @RequestMapping(value = "/getOrg")
    public String getOrg(String orgId) {
        TOrg org = tOrgService.get(orgId);
        return ServletUtils.buildRs(true, "配电房信息", org);
    }

    // 配电房获取视频通道
    @ResponseBody
    @RequestMapping(value = "/orgVideoList")
    public String orgVideoList(String orgId) {
        return ServletUtils.buildRs(true, "配电房视频通道", statisticsService.orgList(orgId));
    }

    
    // 加载一次图 999
    @RequestMapping(value = { "getPicturesByOrgId" })
    @ResponseBody
    public String getPicturesByOrgId(String orgId) {

        Map<String, Object> org = operLogService.getOrgById(orgId);
        if (org == null)
            return ServletUtils.buildRs(false, "空值", null);
        String pics = org.get("picUrl").toString();
        return ServletUtils.buildRs(true, "加载一次图成功", pics);
    }

    // 加载现场图片 //平面图
    @RequestMapping(value = { "getLivePicByOrgId" })
    @ResponseBody
    public String getLivePicByOrgId(String orgId) {
        System.out.println(orgId + "=======================");
        TOrg tOrg = tOrgService.get(orgId);
        return ServletUtils.buildRs(true, "", tOrg);
    }
    
    
    /*
     * 用户归属所有供电所和配电房
     */
    @RequestMapping(value = { "getBureauList" })
    @ResponseBody
    public String getBureauList() {
        return ServletUtils.buildRs(true, "用户归属供电所集合", statisticsService.getBureauList());
    }

}
