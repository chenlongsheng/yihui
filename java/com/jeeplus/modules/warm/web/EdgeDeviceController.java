package com.jeeplus.modules.warm.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.modules.starnet.util.FileCopyUtil;
import com.jeeplus.modules.warm.service.EdgeDeviceService;

@Controller
@RequestMapping("/warm")
public class EdgeDeviceController {

	@Autowired
	EdgeDeviceService edgeDeviceService;
	
	/*
	 * 
	{
	    "machineCode":"test-MachineCode",
	    "devGroup":"default",
	    "name":"dev1",
	    "authCode":"ab2324f12ca2312b213133bfac",
	    "ip":"192.168.100.33",
	    "mac":"00:00:00:00",
	    "remarks":"test",
	    "faceNum":0,
	    "lastOnTime":15328329,
	    "statCode":1,
	    "deviceType":1,
	    "versionCode":114
	}
	 */
	@RequestMapping("/edgeDeviceHeartbeat")
	@ResponseBody
	public JSONObject edgeDeviceHeartbeat(@RequestBody JSONObject jobj) {
		Map<String,Object> channel = edgeDeviceService.getChannelByCameraId(jobj.getString("cameraId"));
		System.out.println(jobj.toJSONString());
		
		JSONObject jsonObject = new JSONObject();
		try {
			Map map = null;
			jsonObject.put("data", map);
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			System.out.println("================");
		}
		return jsonObject;
	}
	
	
	/*
	 * 
	{
          "machineCode": "abcde-fghij-klmno-pqrst-11111", //设备机器码
          "alarmPicData": "base64", //报警图片base64
          "alarmPicName": "20210304145630_455000_0_123450_alarm.jpg", //报警图片名称
          "cameraUrl": "rtsp://admin:admin@192.168.1.1:554/ch1/main/h264", //摄像头对应拉流地址
          "imageHeight": 720, //图片宽度
          "imageWidth": 1280, //图片高度
          "taskId": 0,     //算法ID，定义的算法ID
          "timestamp": 1641519783 ,//时间戳
          "cameraId": "lskasljdlkajldkjalkjdl",//摄像头唯一id
          "cameraName": "摄像头1",//摄像头别名
          "srcPicData": "base64",//报警原图base64          
          "srcPicName": "20210304145630_455000_0_123450_alarm.jpg"//报警原图名称
		}
	 */	
	/*
	 	{
		"imageWidth":1280,
		"machineCode":"ux1xz-07zz0-317zz-032x8-z93z5",
		"operation_type":1,
		"resultData":{"classId":1},
		"score":0.7291957139968872,
		"srcPicName":"20230215175152869_2.jug",
		"cameraId":"",
		"cameraUrl":"rtsp://user:vaKGRClveF7S@192.168.3.40:40554/Streaming/Channels/401",
		"SN":"ux1xz-07zz0-317zz-032x8-z93z5",
		"cameraName":"摄像头报警测试104",
		"taskId":4,
		"timestamp":1676454712,
		"alarmPicData":"base64"
		}
	 */
	@RequestMapping("/edgeDeviceAlarm")
	@ResponseBody
	public JSONObject edgeDeviceAlarm(@RequestBody JSONObject jobj) {
		Map<String,Object> channel = edgeDeviceService.getChannelByCameraId(jobj.getString("cameraId"));
		
		if(channel != null) {
			

			//0安全帽,2反光衣,4行人闯入,7烟火,9抽烟,10打电话,22红色安全帽,23反光衣,
			//24玩手机,25摩托车,26蓝色安全帽,27白色安全帽,28黄色安全帽,29汽车检测,
			//30大巴,31汽车SUV,32货车,33自行车,34卡车,36搅拌车,37自卸卡车,38挖掘机,
			//40人员离岗,41人员聚众,42区域人数,43出入人数,44超员检测,45少员检测,46人形检测,
			//47人员徘徊,48翻墙检测,49区域入侵,50通道占用,51车辆离开
			
			//String taskId = jobj.getString("taskId");
			System.out.println(jobj.toJSONString());
			
			JSONArray alarmList = jobj.getJSONArray("alarmArray");
			for(int i=0;i<alarmList.size();i++) {
				JSONObject alarm = alarmList.getJSONObject(i);
				Map<String,Object> alarmLog = new HashMap<String,Object>();
				alarmLog.put("chId", channel.get("id").toString());
				alarmLog.put("alarmLevel", "4");
				alarmLog.put("aiCameraAlarmType", alarm.getInteger("classid"));	
				
				System.out.println("===================收到摄像头报警类型:"+ alarm.getInteger("classid") +"==================");
				
				edgeDeviceService.insertAlarmLog(alarmLog);
				
				

				
				
				String imageFile = jobj.getString("alarmPicData");
				//alarm_pic_data_path
				String rootPath = ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1)+"../../ai_alarm_pic/";
				//String rootPath = "d://ai_alarm_pic";
				FileCopyUtil.createDirectory(rootPath);
				String filePath = rootPath+ "/" + jobj.getString("srcPicName");
				System.out.println("====================base64 start=====================");
				if(imageFile != null && !imageFile.equals("")) {
					if(imageFile.length() > 200) {
						System.out.println(imageFile.substring(0,100));
					}
				} else {
					imageFile = "";
					System.out.println("imageFile 为空");
				}
				System.out.println("====================base64 end=======================");

				System.out.println("filepath:"+filePath);
				boolean judge = FileCopyUtil.base64ToFile(imageFile, new File(filePath));
				
				
				
				
				
				

				Map<String,Object> aiAlarmLog = new HashMap<String,Object>();
				aiAlarmLog.put("alarmLogId", alarmLog.get("id").toString());
				aiAlarmLog.put("imageWidth",jobj.getString("imageWidth") );
				aiAlarmLog.put("imageHeight", jobj.getString("imageHeight"));
				aiAlarmLog.put("alarmPicName", jobj.getString("imageHeight"));
				
				aiAlarmLog.put("cameraId",jobj.getString("cameraId") );
				aiAlarmLog.put("cameraName",jobj.getString("cameraName") );
				aiAlarmLog.put("cameraUrl",jobj.getString("cameraUrl") );


				aiAlarmLog.put("alarmPicDataPath","/ai_alarm_pic"+"/"+jobj.getString("srcPicName"));
				edgeDeviceService.insertAiAlarmLog(aiAlarmLog);
				
			}
		}

		JSONObject jsonObject = new JSONObject();
		try {
			Map map = null;
			jsonObject.put("data", map);
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			System.out.println("================");
		}
		return jsonObject;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping("/getAiAlarmDetail")
	@ResponseBody
	public JSONObject getAiAlarmDetail(String alarmId) {

		JSONObject jsonObject = new JSONObject();
		try {
			Map<String,Object> map = edgeDeviceService.getAiAlarmDetail(alarmId);
			jsonObject.put("data", map);
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			System.out.println("================");
		}
		return jsonObject;
	}
	
	
	
	
	@RequestMapping("/getAiAlarmByType")
	@ResponseBody
	public JSONObject getAiAlarmByType(int alarmType) {

		JSONObject jsonObject = new JSONObject();
		try {
			List<Map<String,Object>> alarmList = edgeDeviceService.getAiAlarmByType(alarmType);
			jsonObject.put("data", alarmList);
			jsonObject.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("success", false);
			jsonObject.put("msg", e.getMessage());
			System.out.println("================");
		}
		return jsonObject;
	}
	
	
	
	
	
}
