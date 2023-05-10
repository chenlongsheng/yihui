package com.jeeplus.modules.enterprise.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.poi.hpsf.Thumbnail;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.utils.Encodes;
import com.jeeplus.common.utils.FileUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.enterprise.entity.TPreAlarmSettings;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.enterprise.service.TPreAlarmSettingsService;

import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TOrgService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;
 

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

@Controller
@RequestMapping(value = "enterprise/")
public class EntpManagerController {
	@Autowired
	private AreaService areaService;
	
	@Autowired
	private TOrgService tOrgService;
	
	@Autowired
	private TChannelService channelService;
	
	@Autowired
	private TPreAlarmSettingsService tPreAlarmSettingsService;
	
	@Autowired
	TOperLogService operLogService;

    //加载一次图 999
	@RequestMapping(value = { "getPicturesByOrgId" })
	@ResponseBody
	public String getPicturesByOrgId(String orgId) {
		
		Map<String,Object> org = operLogService.getOrgById(orgId);
		if(org==null)
			return ServletUtils.buildRs(false, "空值",null);
		
		String pics =  org.get("picUrl").toString();
		return ServletUtils.buildRs(true, "加载一次图成功",pics);
	}
	
	//加载现场图片   999
	@RequestMapping(value = { "getLivePicByOrgId" })
	@ResponseBody
	public String getLivePicByOrgId(String orgId) {
		System.out.println(orgId+"=======================");
		TOrg tOrg = tOrgService.get(orgId);		
		return ServletUtils.buildRs(true, "",tOrg);
	}
	
	
	//上传现场图片   999
	@RequestMapping(value = {"uploadLivePic"})
	@ResponseBody
	public String imageUpload( HttpServletRequest request, HttpServletResponse response,@RequestParam("files")MultipartFile[] files,@RequestParam("orgId")String orgId) throws IllegalStateException, IOException {
		 String path = request.getSession().getServletContext().getRealPath("");
			String realPath = Global.USERFILES_BASE_URL+"live_pic/" ;
			ArrayList<String> imgUrlAry = new ArrayList<String>();
			if(files!=null) {
				for (int i = 0; i < files.length; i++) {
					FileUtils.createDirectory(Global.getUserfilesBaseDir()+realPath);
					String imgUrl = realPath + new Date().getTime()+files[i].getOriginalFilename(); 
					
					System.out.println(imgUrl);
//					Thumbnails.of(files[i].getInputStream()).scale(0.3f).outputQuality(0.25f).toFile(new File(Global.getUserfilesBaseDir() +imgUrl));;
	            	imgUrlAry.add(imgUrl);
	            	System.out.println(imgUrl);
	            	File file = new File(path+imgUrl);
	            	System.out.println("哈哈"+path+imgUrl);
	            	files[i].transferTo(file);
				}
			}
		 TOrg tOrg = tOrgService.get(orgId);
		 JSONArray parseArray=null;
		 if(tOrg.getImage()!=null&&!tOrg.getImage().trim().equals("")) {
			  parseArray = JSON.parseArray(tOrg.getImage());
		 }else {
			  parseArray = new JSONArray();
		 }
		 parseArray.addAll(imgUrlAry);
			tOrg.setIsNewRecord(false);
			tOrg.setImage(JSON.toJSONString(parseArray));
			System.out.println(tOrg.getId()+"---------"+tOrg);			
			tOrgService.save(tOrg);
			System.out.println();
		return ServletUtils.buildRs(true, "图片上传成功!",JSON.toJSONString(parseArray));
	}
	
	
	//************************梁勃下*************************	
	
}
