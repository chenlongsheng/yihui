/**
 * 
 */
package com.jeeplus.modules.foshan.utils.led;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URLDecoder;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.modules.foshan.service.FoshanService;

/**
 * @author admin
 *
 */
@Controller
@RequestMapping("/sendled")
public class sendled  extends BaseController{

    public static Logger logger = LoggerFactory.getLogger(sendled.class);

	public static void main(String args[]) {
		String showMessage = "高压危险请勿靠近11111111111111111111111";
		Base64 base64 = new Base64();
		byte[] textByte = null;
		try {
			textByte = showMessage.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.debug("get bytes error!");
			logger.debug(e1.toString());
		}
		// 编码
		String encodedText = base64.encodeToString(textByte);
		System.out.println(encodedText);
		String receive = "{\"area_array\":[{\"textBinary\":1,\"areaType\":0,"
				+ "\"color\":0,\"displayStyle\":2,\"fontBold\":0,\"fontName\":\"宋体\","
				+ "\"fontSize\":12,\"height\":64,\"message\":\"" + encodedText + ""
				+ "\",\"speed\":5,\"stayTime\":200,\"width\":192,\"x\":0,\"y\":0,"
				+ "\"showTime\":"+(showMessage.length()*2)+",\"id\":\"0\",\"runMode\":\"2\",\"immediatePlay\":\"1\"}],\"cmd_type\":\"show\"}";

		sendled.sendPacketToLed(receive);
	}
	
	
	public static void sendPacketToLed(String message) {
		
		logger.debug("sendPacketToLed");
		logger.debug(message);
		
		DatagramSocket ds;
		try {
			ds = new DatagramSocket();
			byte[] buf=message.getBytes("UTF-8");
			DatagramPacket dp=new DatagramPacket(buf, buf.length,InetAddress.getByName("192.168.10.143"),10000);
			ds.send(dp);
			ds.close();
		} catch (SocketException e) {
			e.printStackTrace();
			logger.debug(e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(e.toString());
		}

	}

}
