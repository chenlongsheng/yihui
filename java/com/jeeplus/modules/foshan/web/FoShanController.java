package com.jeeplus.modules.foshan.web;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.mq.mqtt.MqttProducer;
import com.jeeplus.modules.foshan.service.FoshanService;
import com.jeeplus.modules.foshan.utils.led.sendled;

@Controller
@RequestMapping(value = "FoShanController")
public class FoShanController {

	public static Logger logger = LoggerFactory.getLogger(FoShanController.class);
	@Autowired
	FoshanService foshanService;

	@Autowired
	MqttProducer mqttProducer;

	public static String deviceId = "02c00181af1ac210";
	
	
	@RequestMapping(value = { "gethsitorylists" })
    @ResponseBody
    public String gethsitorylists() {
       
	    
	    foshanService.gethsitorylists();
	    
	    
	    return null;
	    
	    
	    
	}
	

	@RequestMapping(value = { "test" })
	@ResponseBody
	public String test() {
		foshanService.test();
		return "";
	}

	class PhoneCallThread extends Thread {
		String shortMessage = "";

		public PhoneCallThread(String shortMessage) {
			this.shortMessage = shortMessage;
		}

		public String getShortMessage() {
			return shortMessage;
		}

		public void setShortMessage(String shortMessage) {
			this.shortMessage = shortMessage;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mqttProducer.sendMessage(deviceId, shortMessage);
		}
	}

	@RequestMapping(value = { "send" })
	@ResponseBody
	public String send(@RequestBody String uploadData) {
		logger.debug(uploadData);
		JSONObject jobj = JSONObject.parseObject(uploadData);
		String type = jobj.getString("type");

		JSONArray phones = jobj.getJSONArray("phone");

		String text = jobj.getString("text");

		for (int i = 0; i < phones.size(); i++) {
			String phone = phones.get(i).toString();
			String phoneCall = "{" + "\"command\":11," + "\"Tel\":\"" + phone + "\"," + "\"PlayTTS\":\"" + text + "\","
					+ "\"ShdnSec\":" + text.length() + "}";

			String shortMessage = "{" + "\"command\":12," + "\"Tel\":\"" + phone + "\"," + "\"Sms\":\"" + text + "\""
					+ "}";

			if (type.equals("SMS")) {
				mqttProducer.sendMessage(deviceId, shortMessage);
			} else if (type.equals("call")) {
				mqttProducer.sendMessage(deviceId, phoneCall);
			} else if (type.equals("all")) {
				mqttProducer.sendMessage(deviceId, shortMessage);

				PhoneCallThread phoneCallThread = new PhoneCallThread(phoneCall);
				phoneCallThread.start();
				mqttProducer.sendMessage(deviceId, phoneCall);
			}
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		JSONObject retObj = new JSONObject();
		retObj.put("code", 0);

		return retObj.toJSONString();
	}

	@RequestMapping(value = { "update" })
	@ResponseBody
	public String update(String devId, String content) {

		String update = "{" + "\"command\":13," + "\"Url\":\"http://speaker-app.cdsoft.cn\"" + "}";
		mqttProducer.sendMessage(devId, update);
		return "ok";
	}

	@RequestMapping(value = { "setVolume" })
	@ResponseBody
	public String setVolume(String devId, String content) {

		String update = "{" + "\"command\":16," + "\"Volume\":90" + "}";
		mqttProducer.sendMessage(devId, update);
		return "ok";
	}

	@RequestMapping(value = { "sendTTS" })
	@ResponseBody
	public String sendTTS(String devId, String content) {

		String update = "{" + "\"command\":14," + "\"PlayTTS\":\"森林防火十不准\"" + "}";
		mqttProducer.sendMessage(devId, update);
		return "ok";
	}

	@RequestMapping(value = { "sendMP3" })
	@ResponseBody
	public String sendMP3(String devId, String content) {

		String update = "{" + "\"command\":15,"
				+ "\"PlayUrl\":\"http://121.227.152.212:8082/upload/20200805/1596590158434.mp3\"," + "\"ShdnSec\":10"
				+ "}";
		mqttProducer.sendMessage(devId, update);
		return "ok";
	}

}
