/**
 * 
 */
package com.jeeplus.modules.warm.util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Autowired;

import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author admin
 *
 */
public class SendMessage {


	private static String host = "smtp.163.com";
	private static String port = "25";
	private static String userName = "cdsoft_cdkj@163.com";
	private static String password = "cdsoft123";

	public static void sendTextMail(String email, String mobanMessage) {

		Properties pro = System.getProperties();
		pro.put("mail.smtp.host", host);
		pro.put("mail.smtp.port", port);
		pro.put("mail.smtp.auth", "true");

		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(pro, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 设置邮件消息的发送者
			mailMessage.setFrom(new InternetAddress(userName));
			// 创建邮件的接收者地址，并设置到邮件消息中
			mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
			// 设置邮件消息的主题
			mailMessage.setSubject("cdsoft");
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			mailMessage.setText(mobanMessage);
			// 发送邮件

			Transport.send(mailMessage);
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("=====");
	}
	
	public static String sendMobileMessage(String phone, String mobanMessage) {
		MapEntity entity = new MapEntity();		 
		
	    System.out.println(phone+"-----------phone");        
		Random random = new Random();
		Long now = new Date().getTime() / 1000;
	
//		logger.debug("now:" + now);
//		logger.debug("messageCode" + messageCode);
//		logger.debug("phone" + phone);
		entity.put("messageCode",mobanMessage );
		// 创建StringBuffer对象用来操作字符串
		StringBuffer sb = new StringBuffer(
				"http://cdsoft.cn/aliyun-java-sdk/sendCdsoftVerifyCode/?templdateCcode=SMS_169898632");
		// 向StringBuffer追加用户名
		sb.append("&phone=" + phone);// 在此申请企信通uid，并进行配置用户名
		// 向StringBuffer追加密码（密码采用MD5 32位 小写）
		sb.append("&code=" + mobanMessage);
		// 创建url对象
		try {
			URL url = new URL(sb.toString());
			// 打开url连接
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 设置url请求方式 ‘get’ 或者 ‘post’
			connection.setRequestMethod("POST");
			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			// 返回发送结果
			String inputline = in.readLine();
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return ServletUtils.buildRs(true, "已发送验证码至手机:" + phone, entity);
	}
	
	
	public static void main(String[] args) {
//		sendMobileMessage("18259005635", "789546465456");	
		sendTextMail("125951761@qq.com", "113");
	}
	
	
	

	public static void sendSMS(String mobile, String mobanMessage) {

		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://gbk.api.smschinese.cn");
		post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
		NameValuePair[] data = { new NameValuePair("Uid", "陈龙生924"), new NameValuePair("Key", "d41d8cd98f00b204e980"),
				new NameValuePair("smsMob", mobile), new NameValuePair("smsText", mobanMessage) };
		post.setRequestBody(data);
		try {
			client.executeMethod(post);
			Header[] headers = post.getResponseHeaders();
			int statusCode = post.getStatusCode();
			System.out.println("statusCode:" + statusCode);
			for (Header h : headers) {
				System.out.println(h.toString());
			}
			String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
			System.out.println(result); // 打印返回消息状态
		} catch (Exception e) {
		
		}
		post.releaseConnection();
	}

}
