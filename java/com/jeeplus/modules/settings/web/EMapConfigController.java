package com.jeeplus.modules.settings.web;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.IdGenSnowFlake;
import com.jeeplus.common.utils.MyBeanUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.enterprise.service.TOperLogService;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.service.EMapConfigService;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TDeviceService;
import com.jeeplus.modules.sys.entity.Area;
import com.jeeplus.modules.sys.service.AreaService;
import com.jeeplus.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "settings/map")
public class EMapConfigController {

	@Autowired
	private EMapConfigService eMapConfigService;
	@Autowired
	private TDeviceService tDeviceService;
	@Autowired
	private TChannelService tChannelService;
	@Autowired
	private AreaService areaService;
	@Autowired
	TOperLogService operLogService;
	@Autowired
	TCodeController tCodeController;

	// 获取区域id==TChannel
	@RequestMapping(value = { "getChannelListByOrgId" })
	@ResponseBody
	public String getChannelListByOrgId(HttpServletRequest request, HttpServletResponse response, String orgId) {

		return ServletUtils.buildRs(true, "", eMapConfigService.getChannelListByOrgId(orgId));
	}

	// 获取区域id==AudTDevice
	@RequestMapping(value = { "getDeviceListByOrgId" })
	@ResponseBody
	public String getDeviceListByOrgId(HttpServletRequest request, HttpServletResponse response, String orgId) {
		System.out.println("orgId======" + orgId);
		return ServletUtils.buildRs(true, "", eMapConfigService.getDeviceListByOrgId(orgId));
	}

	// 保存坐标==TChannel
	@RequestMapping(value = { "saveChannelCoordsXCoordsY" }, method = RequestMethod.POST)
	@ResponseBody
	public String saveChannelCoordsXCoordsY(String channellist) throws Exception {

		JSONArray ja = JSONArray.parseArray(channellist);
		for (int i = 0; i < ja.size(); i++) {
			TChannel tChannel = JSONObject.parseObject(ja.get(i).toString(), TChannel.class);
			TChannel t = tChannelService.get(tChannel.getId());// 从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(tChannel, t);
			tChannelService.save(t);
		}
		return ServletUtils.buildRs(true, "保存坐标成功!", channellist);
	}

	// 保存坐标==AudTDevice
	@RequestMapping(value = { "saveDeviceCoordsXCoordsY" }, method = RequestMethod.POST)
	@ResponseBody
	public String saveDeviceCoordsXCoordsY(String devicelist) throws Exception {
		System.out.println("nihao");
		System.out.println(devicelist);
		JSONArray ja = JSONArray.parseArray(devicelist);
		for (int i = 0; i < ja.size(); i++) {
			TDevice tDevice = JSONObject.parseObject(ja.get(i).toString(), TDevice.class);
			TDevice t = tDeviceService.get(tDevice.getId());// 从数据库取出记录的值
			MyBeanUtils.copyBeanNotNull2Bean(tDevice, t);
			tDeviceService.save(t);
		}
		return ServletUtils.buildRs(true, "保存坐标成功!", devicelist);
	}

	// 上传图片 一次图
	@RequestMapping(value = { "upLoadPic" })
	@ResponseBody
	public String upLoadPic(String orgId, HttpServletRequest request, MultipartFile imgFile,
			HttpServletResponse response) throws IllegalStateException, IOException {

		// 获取文件原始名称
		String originalFilename = imgFile.getOriginalFilename();
		// 上传图片
		if (imgFile != null && originalFilename != null && originalFilename.length() > 0) {
			// 存储图片的物理路径
			String pic_path = request.getSession().getServletContext().getRealPath("");
			// String pic_path = fileRoot;
			// 新的图片名称
			String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
			File file = new File(pic_path + "/static_modules/emap_upload/");
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			System.out.println(pic_path + "/static_modules/emap_upload/");
			// 新图片
			File newFile = new File(pic_path + "/static_modules/emap_upload/" + newFileName);
			// 将内存中的数据写入磁盘
			imgFile.transferTo(newFile);
			Map map = new HashMap();
			map.put("url", "/static_modules/emap_upload/" + newFileName);
			JSONArray array = new JSONArray();
			array.add(map);
			try {
				areaService.updateUrlImage(array.toString(), originalFilename, orgId);
				UserUtils.saveLog(areaService.get(orgId).getName() + "上传了一张一次图：" + originalFilename, "新增");
				return ServletUtils.buildRs(true, "成功", "");
			} catch (Exception e) {
				return ServletUtils.buildRs(false, "失败", "");
			}
		} else {
			return ServletUtils.buildRs(false, "失败", "");
		}
	}

	// 上传平面图999
	@RequestMapping(value = { "savePic" })
	@ResponseBody
	public String savePic(String orgId, HttpServletRequest request, MultipartFile imgFile, HttpServletResponse response)
			throws IllegalStateException, IOException {

		MapEntity entity = new MapEntity();
		// 获取文件原始名称
		String originalFilename = imgFile.getOriginalFilename();
		// 上传图片
		if (imgFile != null && originalFilename != null && originalFilename.length() > 0) {
			// 存储图片的物理路径
			String pic_path = request.getSession().getServletContext().getRealPath("");
			// String pic_path = fileRoot;
			// 新的图片名称
			String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
			File file = new File(pic_path + "/static_modules/emap_upload/");
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			System.out.println(pic_path + "/static_modules/emap_upload/");
			// 新图片
			File newFile = new File(pic_path + "/static_modules/emap_upload/" + newFileName);
			// 将内存中的数据写入磁盘
			imgFile.transferTo(newFile);
			Area area = new Area();
			area.setId(orgId);
			area.setImage(newFileName);
			entity.put("id", IdGenSnowFlake.uuid().toString());
			entity.put("orgId", orgId);
			entity.put("name", originalFilename.substring(0, originalFilename.lastIndexOf(".")));
			entity.put("image", "/static_modules/emap_upload/" + newFileName);
			try {
				areaService.saveImage(entity);

				UserUtils.saveLog(areaService.get(orgId).getName() + "上传了一张平面图：" + originalFilename, "新增");
				return ServletUtils.buildRs(true, "获取成功", "");
			} catch (Exception e) {
				return ServletUtils.buildRs(false, "获取失败", "");
			}
		} else {
			return ServletUtils.buildRs(false, "请上传图片", "");
		}
	}

	// 更新新图片
	@RequestMapping(value = { "updateImageById" }) // 删除平面图
	@ResponseBody
	public String updateImageById(MultipartFile imgFile, HttpServletRequest request, String id, String imageUrl)
			throws IllegalStateException, IOException {

		// 获取文件原始名称
		String originalFilename = imgFile.getOriginalFilename();

		// 上传图片
		if (imgFile != null && originalFilename != null && originalFilename.length() > 0) {
			try {
				// 存储图片的物理路径
				String pic_path = request.getSession().getServletContext().getRealPath("");
				// String pic_path = fileRoot;
				// 新的图片名称
				String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
				File file = new File(pic_path + "/static_modules/emap_upload/");
				// 如果文件夹不存在则创建
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
				}
				System.out.println(pic_path + "/static_modules/emap_upload/");
				// 新图片
				File newFile = new File(pic_path + "/static_modules/emap_upload/" + newFileName);
				// 将内存中的数据写入磁盘
				imgFile.transferTo(newFile);

				File fi = new File(pic_path + imageUrl);
				if (fi != null) {
					fi.delete();
				}
				eMapConfigService.updateImageById("/static_modules/emap_upload/" + newFileName, id);
			} catch (Exception e) {
				e.printStackTrace();
				return ServletUtils.buildRs(false, "修改图片失败", null);
			}
		}
		return ServletUtils.buildRs(true, "修改图片成功", null);
	}

	@RequestMapping(value = { "deleteImage" }) // 删除平面图
	@ResponseBody
	public String deleteImage(String id) {
		try {
			MapEntity imageDev = eMapConfigService.getImageDev(id);
			String orgId = (String) imageDev.get("orgId");
			areaService.deleteImage(id); // 删除图片和小图标
			UserUtils.saveLog(areaService.get(orgId).getName() + "删除了一张平面图：" + (String) imageDev.get("name"), "删除");
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "删除失败", null);
		}
		return ServletUtils.buildRs(true, "删除成功", null);
	}

	@RequestMapping(value = { "updateOnceImage" }) // 删除一次图
	@ResponseBody
	public String updateOnceImage(String orgId, String onceImageName) {

		Area area = areaService.get(orgId);
		try {
			if (StringUtils.isBlank(onceImageName)) {
				areaService.updateUrlImage("", "", orgId);// 删除
				UserUtils.saveLog(
						areaService.get(orgId).getName() + "删除了一张一次图：" + areaService.get(orgId).getImageName(), "删除");
			} else {
				areaService.updateUrlImage(area.getPicUrl(), onceImageName, orgId);// 修改一次图名
			}
		} catch (Exception e) {
			return ServletUtils.buildRs(false, "删除失败", null);
		}
		return ServletUtils.buildRs(true, "删除成功", null);
	}

	@RequestMapping(value = { "updateImageName" }) // 修改平面图 999
	@ResponseBody
	public String updateImageName(String name, String id) {
		try {
			areaService.updateImageName(name, id);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
		return ServletUtils.buildRs(true, "成功", null);
	}

	// 下载电子配置 平面图999
	@RequestMapping(value = { "getPics" }, method = RequestMethod.POST)
	@ResponseBody
	public String getpics(String id, HttpServletRequest request, HttpServletResponse response) throws IOException {

//		String image = "static_modules/emap_upload/" + area.getImage();
		List<MapEntity> image = areaService.getImage(id);
		return ServletUtils.buildRs(true, "获取成功", image);
	}

	// 获取图片和一次图 999
	@RequestMapping(value = { "findImageList" })
	@ResponseBody
	public String findImageList(String orgId) {
		MapEntity entity = new MapEntity();
		entity.put("imageList", areaService.findImageList(orgId));
		MapEntity onceImage = areaService.getOnceImage(orgId);
		entity.put("onceImage", onceImage);
		return ServletUtils.buildRs(true, "成功", entity);
	}

	// 下载电子配置图片 一次图
	@RequestMapping(value = { "getPic" }, method = RequestMethod.POST)
	@ResponseBody
	public String getpic(String areaId, HttpServletRequest request, HttpServletResponse response) throws IOException {

		Area area = areaService.get(areaId);
		String image = "/static_modules/emap_upload/" + area.getImage();
		System.out.println(image);
		String path = request.getSession().getServletContext().getRealPath("") + image;
		File file = new File(path);
		System.out.println(file.length());
		if (StringUtils.isBlank(area.getImage()) || file.length() == 0) {
			return "false";
		}
		return image;
	}

	// 设备的类型的
	@RequestMapping(value = { "eMapList" }, method = RequestMethod.POST)
	@ResponseBody
	public JSONObject eMapList(String orgId) {
		JSONObject json = new JSONObject();
		List<MapEntity> list = eMapConfigService.eMapSelect(orgId);
		json.put("list", list);
		return json;
	}

	// 通道的类型的
	@RequestMapping(value = { "eMapChannelList" }, method = RequestMethod.POST)
	@ResponseBody
	public JSONObject eMapChannelList(String orgId) {
		System.out.println(orgId + "-------通道t_code列表");
		JSONObject json = new JSONObject();
		List<MapEntity> list = eMapConfigService.eMapChannelSelect(orgId);
		json.put("list", list);
		return json;
	}

	// 通道的类型的 9999
	@RequestMapping(value = { "getCodeList" })
	@ResponseBody
	public String getCodeList(String orgId) {
		return ServletUtils.buildRs(true, "成功", eMapConfigService.getCodeList(orgId));
	}

	// 获取设备通道下拉框选项
	@RequestMapping(value = { "devChList" })
	@ResponseBody
	public String devChList(String devTypeId, String orgId, Integer typeId) {
		return ServletUtils.buildRs(true, "成功", eMapConfigService.devChList(devTypeId, orgId, typeId));
	}

	// 新增保存一个小图标
	@RequestMapping(value = { "updateDevCoords" })
	@ResponseBody
	public String updateDevCoords(String id, String pdfImageId, String coordsX, String coordsY, String devId,
			Integer typeId) {
		// id是修改时候
		try {
			id = eMapConfigService.updateDevCoords(id, pdfImageId, coordsX, coordsY, devId, typeId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
		return ServletUtils.buildRs(true, "成功返回小图标:ID", id);
	}

	// 删除图片中设备
	@RequestMapping(value = { "deleteImageDevId" })
	@ResponseBody
	public String deleteImageDevId(String devId, Integer typeId) {
		try {
			eMapConfigService.deleteImageDevId(devId, typeId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
		return ServletUtils.buildRs(true, "成功", null);
	}

	// 删除暖通图片中设备
	@RequestMapping(value = { "deleteHvacDevId" })
	@ResponseBody
	public String deleteHvacDevId(String devId, Integer typeId) {
		try {
			eMapConfigService.deleteHvacDevId(devId, typeId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
		return ServletUtils.buildRs(true, "成功", null);
	}

	// 获取图片下配电房小图片集合
	@RequestMapping(value = { "selectImageDevList" })
	@ResponseBody
	public String selectImageDevList(String pdfImageId, String orgId) {
		return ServletUtils.buildRs(true, "成功", eMapConfigService.selectImageDevList(pdfImageId, orgId));
	}

	// 保存所有小图标
	@RequestMapping(value = { "saveDevPicture" })
	@ResponseBody
	public String saveDevPicture(String devList, String pdfImageId, String containerWidth, String containerHeight) {

		if (devList != null) {
			String list = devList.replace("&quot;", "'");// 替换json的乱码
			System.out.println(devList);
			JSONArray ja = JSONArray.parseArray(list);
			try {
				eMapConfigService.saveDevPicture(ja, pdfImageId, containerWidth, containerHeight);
				MapEntity imageDev = eMapConfigService.getImageDev(pdfImageId);
				String orgId = (String) imageDev.get("orgId");
				UserUtils.saveLog(areaService.get(orgId).getName() + "进行了平面图配置", "修改");
			} catch (Exception e) {
				e.printStackTrace();
				return ServletUtils.buildRs(false, "失败", null);
			}
		}
		return ServletUtils.buildRs(true, "成功", null);
	}

	/*
	 * 暖通新加的
	 */

	// 暖通主机的类型的 9999
	@RequestMapping(value = { "getHavcTypeList" })
	@ResponseBody
	public String getHavcTypeList() {
		return ServletUtils.buildRs(true, "成功", eMapConfigService.getHavcTypeList());
	}

	@RequestMapping(value = { "gethvacListByOrgId" }) // 暖通被绑定设备类型 加 下拉框
	@ResponseBody
	public String gethvacListByOrgId(String orgId) {
		return ServletUtils.buildRs(true, "成功", eMapConfigService.gethvacListByOrgId(orgId));
	}

	// 新增保存一个暖通主机小图标
	@RequestMapping(value = { "updateHvacDevCoords" })
	@ResponseBody
	public String updateHvacDevCoords(String id, String hvacDevId, String coordsX, String coordsY, String devId,
			Integer typeId) {
		// id是修改时候
		try {
			id = eMapConfigService.updateHvacDevCoords(id, hvacDevId, coordsX, coordsY, devId, typeId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
		return ServletUtils.buildRs(true, "成功返回小图标:ID", id);
	}

	@RequestMapping(value = { "insertDevice" }) // 添加主机设备
	@ResponseBody
	public String insertDevice(String pdfImageId, String name, String devType, String typeId, String orgId,
			String coordsX, String coordsY) { // 添加主机设备
		try {
			String id = eMapConfigService.insertDevice(pdfImageId, name, orgId, devType, typeId, coordsX, coordsY);
			return ServletUtils.buildRs(true, "成功返回主机设备:ID", id);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "失败", null);
		}
	}

	@RequestMapping(value = { "selectHvacDevList" }) // 主机设备绑定的所有设备
	@ResponseBody
	public String selectHvacDevList(String hvacDevId, String orgId) {
		return ServletUtils.buildRs(true, "暖通主机绑定的所有小图标", eMapConfigService.selectHvacDevList(hvacDevId, orgId));

	}

	@RequestMapping(value = { "modifyHvaceNameById" }) // 修改设备主机名
	@ResponseBody
	public String modifyHvaceNameById(String hvacDevId, String name, HttpServletRequest request) {

		try {

			eMapConfigService.modifyHvaceById(hvacDevId, name, null);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "修改失败", "");
		}
		return ServletUtils.buildRs(true, "修改成功", "");
	}

	@RequestMapping(value = { "modifyHvaceImageById" }) // 修改设备主机设备图片
	@ResponseBody
	public String modifyHvaceImageById(String hvacDevId,
			@RequestParam(value = "hvacImage", required = false) MultipartFile hvacImage, HttpServletRequest request) {

		try {
			String devImageName = null;
			if (hvacImage != null) {
				devImageName = tCodeController.newFile(hvacImage, Long.parseLong("1"), "12", request);
			}
			eMapConfigService.modifyHvaceById(hvacDevId, null, devImageName);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "修改失败", "");
		}
		return ServletUtils.buildRs(true, "修改成功", "");
	}

	// 保存所有小图标
	@RequestMapping(value = { "saveHvacDevPicture" })
	@ResponseBody
	public String saveHvacDevPicture(String devList, String hvacDevId, String containerWidth, String containerHeight) {
		if (devList != null) {
			System.out.println(devList+"---------前");
			String list = devList.replace("&quot;", "'");// 替换json的乱码
		
			JSONArray ja = JSONArray.parseArray(list);
			try {
				eMapConfigService.saveHvacDevPicture(ja, hvacDevId, containerWidth, containerHeight);
			} catch (Exception e) {
				e.printStackTrace();
				return ServletUtils.buildRs(false, "失败", null);
			}
		}
		return ServletUtils.buildRs(true, "成功", null);
	}

	// 删除按钮------整体删除暖通主机
	@RequestMapping(value = { "delHvacDevs" })
	@ResponseBody
	public String delHvacDevs(String hvacDevId) {
		try {
			eMapConfigService.delHvacDevs(hvacDevId);
		} catch (Exception e) {
			e.printStackTrace();
			return ServletUtils.buildRs(false, "删除失败", null);
		}
		return ServletUtils.buildRs(true, "删除成功", null);
	}
	
	
	@RequestMapping(value = { "getHvacDevList" }) // 主机设备绑定的所有设备
	@ResponseBody
	public String getHvacDevList(String hvacDevId, String orgId) {
		return ServletUtils.buildRs(true, "首页中暖通主机绑定小图标", eMapConfigService.getHvacDevList(hvacDevId, orgId));

	}

	
	

}
