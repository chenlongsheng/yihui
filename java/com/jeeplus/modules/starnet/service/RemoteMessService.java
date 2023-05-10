/**
 * 
 */
package com.jeeplus.modules.starnet.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.mq.rabbitmq.RabbitMQConnection;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.sys.utils.UserUtils;
import com.rabbitmq.client.Channel;

/**
 * @author admin
 *
 */
@Service
@Transactional(readOnly = true)
public class RemoteMessService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RabbitMQConnection rabbitMQProducer;

    public String getVoiceDatas(MapEntity entity, String alarmResume) throws UnsupportedEncodingException {

        String message = entity.get("orgName").toString() + entity.get("addr") + entity.get("devName").toString()
                + alarmResume;

        JSONObject data = new JSONObject();
        // string
        data.put("command", 14);
        data.put("PlayTTS", message);
        System.out.println(data.toString());
        Base64 base64 = new Base64();
        byte[] textByte = data.toString().getBytes("UTF-8");
        // 编码
        String encodedText = base64.encodeToString(textByte);
        return encodedText;
    }

    public String getTextDatas(String message,String phone, String alarmResume) throws UnsupportedEncodingException {

    
        JSONObject data = new JSONObject();
        // string
        data.put("command", 12);
        data.put("Tel", phone);
        data.put("Sms", message);

        System.out.println(data.toString());

        Base64 base64 = new Base64();
        byte[] textByte = data.toString().getBytes("UTF-8");
        // 编码
        String encodedText = base64.encodeToString(textByte);
        return encodedText;
    }

    public String remoteControl(String sn, String data) {

        // channelId 通道id
        // opr 0是关 ,1是开

        String message = "{\"channels\": {\"-1\": {\"cmd\": \"trans_serialport\",\"data\": \"" + data + "\"}}}";
        System.out.println(sn + "======" + message);
        Channel rmqChannel = null;

        try {
            // 创建一个通道
            rmqChannel = rabbitMQProducer.createChannel();

            // 发送消息到队列中
            rmqChannel.basicPublish("org.10", "dev.command.sn." + sn, null, message.getBytes("UTF-8"));
            logger.debug("/**============================================================**/");
            logger.debug("Producer Send +'" + message + "'  to " + "dev.command.sn." + sn);
            logger.debug("/**============================================================**/");
            // 关闭通道和连接
            rmqChannel.close();
            System.out.println("发送成功!");
            return ServletUtils.buildRs(true, "成功", "");
        } catch (Exception e) {
            try {
                rmqChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (java.util.concurrent.TimeoutException e1) {
                e1.printStackTrace();
            }
            return ServletUtils.buildRs(false, "失败", "");
        }
    }
}
