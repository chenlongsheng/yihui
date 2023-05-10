package com.jeeplus.common.mq.mqtt;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public class MqttProducer {

	public Logger logger = LoggerFactory.getLogger(MqttProducer.class);

	@Resource
	private MqttPahoMessageHandler mqttHandler;

	private static String getLocalMac(InetAddress ia) throws SocketException {
		// TODO Auto-generated method stub
		// 获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		System.out.println("mac数组长度：" + mac.length);
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// 字节转换为整数
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			System.out.println("每8位:" + str);
			if (str.length() == 1) {
				sb.append("0" + str);
			} else {
				sb.append(str);
			}
		}
		System.out.println("本机MAC地址:" + sb.toString().toUpperCase());
		return sb.toString();
	}

	public String sendMessage(String devId, String message) {
		
		String mac = "";
		InetAddress ia;
//		try {
//			ia = InetAddress.getLocalHost();
//			System.out.println(ia);
//			mac = getLocalMac(ia);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}

		Base64 base64 = new Base64();
		byte[] textByte = null;
		try {
			textByte = message.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			logger.debug("get bytes error!");
			logger.debug(e1.toString());
		}
		// 编码
		String encodedText = base64.encodeToString(textByte);

		String json4T = "{" + "\"cmd_type\": 20," + "\"pro\": \"LEDPro\"," + "\"time_stamp\": "
				+ (new Date().getTime() / 1000) + "," + "\"dev_id\": \"" + devId + "\"," + // 设备ID
				// "\"mac_addr\": \""+mac.replaceAll("-", "")+"\"," +
				"\"mac_addr\":\"aaaabbbbcccc\"," + "\"msg_buff\": \"" + encodedText + "\"," + "\"version\": 10" + "}";
//		logger.debug("send message:" + json4T + ", to dev:" + devId);
		// 构建消息
		Message<String> messages = MessageBuilder.withPayload(json4T)
				.setHeader(MqttHeaders.TOPIC, "dev/sub/data/" + devId).build();
		// 发送消息
		mqttHandler.handleMessage(messages);
	 
		//logger.debug("message_content:" + message);
		return "";
	}

}
