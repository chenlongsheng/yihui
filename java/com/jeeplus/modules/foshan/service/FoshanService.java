package com.jeeplus.modules.foshan.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.mq.mqtt.MqttProducer;
import com.jeeplus.modules.foshan.dao.FoshanDao;
import com.jeeplus.modules.foshan.utils.led.sendled;

@Service
public class FoshanService {

    // 佛山
    // public static String deviceId = "02c00081d1c519d6";
    // 佛山电话号码
    // public static String phone ="13528960300";

    // 潮州
    public static String deviceId = "02c00181af1ac210";
    // 潮州电话号码
    public static String phone = "18060723009";

    public void test() {
        AlarmThread at = new AlarmThread("person", "2021-06-24 10:00:00", "001");
        at.start();
    }

    public static Logger logger = LoggerFactory.getLogger(FoshanService.class);

    @Autowired
    MqttProducer mqttProducer;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    FoshanDao foshanDao;

    public void gethsitorylists() {
        
        
        String s   = "5";

        foshanDao.gethsitorylists( s);
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

    class AlarmThread extends Thread {

        String alarmType;
        String alarmTime;
        String videoCode;

        public AlarmThread(String alarmType, String alarmTime, String videoCode) {
            this.alarmTime = alarmTime;
            this.alarmType = alarmType;
            this.videoCode = videoCode;
        }

        @Override
        public void run() {
            logger.debug("proccess alarm message: alarmTime " + alarmTime + ",alarmType " + alarmType + ",videoCode "
                    + videoCode);
            String showMessage = "";
            if (alarmType.equals("person")) {
                showMessage = "高压危险无关人员请勿靠近.";
            } else if (alarmType.indexOf("f") != -1) {
                showMessage = "火警报警请注意.";
            }

            /*
             * BASE64Encoder encoder = new BASE64Encoder(); String encodeBase64 =
             * encoder.encode(showMessage.getBytes());
             * 
             * String receive = "{" + "  \"area_array\": [" + "    {" +
             * "      \"areaType\": 0," + "      \"color\": 0," +
             * "      \"displayStyle\": 1," + "      \"fontBold\": 0," +
             * "      \"fontName\": \"宋体\"," + "      \"fontSize\": 16," +
             * "      \"height\": 64," +
             * //"      \"message\": \"5Z2a5a6I5a6J5YWo57qi57q/77yM5o6o6L+b5a6J5YWo5Y+R5bGV44CC56uv5Y2I5pyf6Ze05aSp5rCU6aKE5oql77ya56uv5Y2I5YGH5pyf5oiR5Yy65Y2I5ZCO5aSa6Zi16Zuo5oiW6Zu36Zi16Zuo44CC5YW35L2T6aKE5oql5aaC5LiL77yaMTLml6XlpJrkupHvvIzmnInpmLXpm6jmiJbpm7fpmLXpm6jvvIzlgY/kuJzpo44y772eM+e6p++8jOawlOa4qTI1772eMzHihIPvvIznqbrmsJTotKjph4/nrYnnuqfkvJjvvZ7oia/vvJsxM++9njE25pel5aSa5LqR77yM5pyJ6Zi16Zuo5oiW6Zu36Zi16Zuo77ybMTfml6XlpJrkupHvvJsxOOaXpeWkmuS6ke+8jOaciemYtembqOaIlumbt+mYtembqOOAgum+meaWh+WMuuawlOixoeWPsDIwMjHlubQ25pyIMTLml6UwN+aXtuWPkeW4g+OAgg==\","
             * + "      \"message\": \""+encodeBase64+"\"," + "      \"speed\": 30," +
             * "      \"stayTime\": 100," + "      \"textBinary\": 0," +
             * "      \"width\": 96," + "      \"x\": 0," + "      \"y\": 0" + "    }" +
             * "  ]," + "  \"cmd_type\": \"show\"" + "}";
             */

//			BASE64Encoder encoder = new BASE64Encoder();
//			String encodeBase64 = encoder.encode(showMessage.getBytes());

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
                    + "\"color\":0,\"displayStyle\":5,\"fontBold\":0,\"fontName\":\"宋体\","
                    + "\"fontSize\":20,\"height\":80,\"message\":\"" + encodedText + ""
                    + "\",\"speed\":5,\"stayTime\":200,\"width\":560,\"x\":0,\"y\":0,"
                    + "\"showTime\":60,\"id\":\"0\",\"runMode\":\"2\",\"immediatePlay\":\"1\"}],\"cmd_type\":\"show\"}";

            try {
                sendled.sendPacketToLed(receive);
            } catch (Exception e) {
                logger.debug("led 发送异常");
                logger.debug(e.toString());
                e.printStackTrace();
            }

            String phoneCall = "{" + "\"command\":11," + "\"Tel\":\"" + phone + "\"," + "\"PlayTTS\":\"" + showMessage
                    + "\"," + "\"ShdnSec\":" + showMessage.length() + "}";

            String shortMessage = "{" + "\"command\":12," + "\"Tel\":\"" + phone + "\","
                    + "\"Sms\":\"佛山顺德容桂隧道容马线入口检测发现人员进入\"" + "}";

            String tts = "{" + "\"command\":14," + "\"PlayTTS\":\"" + showMessage + "\"" + "}";

            long preSendCurrent = 0;
            long sleepTime = 0;
            long ss = 0;
            String redisValue = redisTemplate.opsForValue().get("camera_sms_alarm:" + phone);
            String ttsFilterValue = redisTemplate.opsForValue().get("camera_tts_alarm");
            if (redisValue == null) {
                logger.debug("发送短信报警信息");
                redisTemplate.opsForValue().set("camera_sms_alarm:" + phone, "1", 60 * 5, TimeUnit.SECONDS);
                mqttProducer.sendMessage(deviceId, shortMessage);

//    			mqttProducer.sendMessage(deviceId,tts);
//  		   	  	PhoneCallThread phoneCallThread = new PhoneCallThread(phoneCall);
//  		   	  	phoneCallThread.start();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (ttsFilterValue == null) {
                logger.debug("发送tts报警信息");
                redisTemplate.opsForValue().set("camera_tts_alarm", "1", 5, TimeUnit.SECONDS);
                mqttProducer.sendMessage(deviceId, tts);
            }

        }
    }

    public void queryCameraAlarm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            CookieStore cookieStore = new BasicCookieStore();
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setCookieStore(cookieStore);
            String url = "http://172.16.0.2/index1/" + today;
            // HttpGet httpget = new
            // HttpGet("http://192.168.1.110:8181/AIServer/getAlarm?sDate="+today);
            HttpGet httpget = new HttpGet(url);
            // HttpGet httpget = new HttpGet("http://172.16.0.2/index1/"+20210602);
            logger.debug("request url:" + url);
            CloseableHttpResponse response = httpclient.execute(httpget, localContext);
            try {
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String str = "";
                String retStr = "";
                while ((str = br.readLine()) != null) {
                    retStr += str;
                }
                // logger.debug(retStr);
                JSONObject retObj = JSONObject.parseObject(retStr);
                int count = retObj.getInteger("count");
                if (count > 0) {
                    JSONArray alarmList = retObj.getJSONArray("alarm_list");
                    for (int i = 0; i < alarmList.size(); i++) {
                        JSONObject alarm = alarmList.getJSONObject(i);
                        String id = alarm.getString("id");
                        String redisValue = redisTemplate.opsForValue().get("camera_alarm:alarm_id" + id);
                        if (redisValue == null) {
                            logger.debug("报警id:" + id + ",报警未处理");
                            redisTemplate.opsForValue().set("camera_alarm:alarm_id" + id, "1", 3600 * 36,
                                    TimeUnit.SECONDS);
                            String alarmTime = alarm.getString("alarm_time");
                            String videoCode = alarm.getString("video_code");
                            String confidenceStr = alarm.getString("confidences");
                            String lable = alarm.getString("lable");
                            Double confidenceDouble = Double.parseDouble(confidenceStr);
                            if (confidenceDouble > 80) {
                                AlarmThread at = new AlarmThread(lable, alarmTime, videoCode);
                                at.start();
                            }
                        } else {
                            // logger.debug("报警id:"+id+",该报警已处理");
                        }
                    }
                } else {
                    logger.debug("报警列表为空");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException cpe) {
            cpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // 温湿度, 硫化氢, 甲烷, 氧气
    public void showChannelDataInLed() {
        // 查询相关设备及数据
        List<Map<String, Object>> dataList = foshanDao.getDeviceAndData();

        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("avgValue", "0");
        tempMap.put("warn", "0");

        Map<String, String> humMap = new HashMap<>();
        humMap.put("avgValue", "0");
        humMap.put("warn", "0");

        Map<String, String> liuhuaqingMap = new HashMap<>();
        liuhuaqingMap.put("avgValue", "0");
        liuhuaqingMap.put("warn", "0");

        Map<String, String> jiawanMap = new HashMap<>();
        jiawanMap.put("avgValue", "0");
        jiawanMap.put("warn", "0");

        Map<String, String> yangqiMap = new HashMap<>();
        yangqiMap.put("avgValue", "0");
        yangqiMap.put("warn", "0");

        double tempSum = 0;
        int tempDevCount = 0;
        int tempWarn = 0;
        int p0 = 0;
        // 计算温度平均值, 以及报警状态
        for (Map<String, Object> data : dataList) {
            String devType = data.get("devType").toString();
            String chType = data.get("chType").toString();
            Object warn = data.get("warn");
            Object param0 = data.get("param0");
            Object param1 = data.get("param1");
            Object param2 = data.get("param2");
            Object realValue = data.get("realValue");
            if (devType.equals("169") && chType.equals("101")) {
                tempDevCount = tempDevCount + 1;

                if (realValue == null) {
                    realValue = "0";
                }
                int iRealValue = Integer.parseInt(realValue.toString());

                if (param0 == null) {
                    param0 = "0";
                }
                int iParam0 = Integer.parseInt(param0.toString());

                if (param1 == null) {
                    param1 = "1";
                }
                int iParam1 = Integer.parseInt(param1.toString());

                if (param2 == null) {
                    param2 = "0";
                }
                int iParam2 = Integer.parseInt(param2.toString());

                double dvalue = (iRealValue - iParam2) / iParam1;
                tempSum = tempSum + dvalue;

                if (!warn.equals("0")) {
                    int iWarn = Integer.parseInt(warn.toString());
                    if (iWarn > tempWarn)
                        tempWarn = iWarn;
                }

                p0 = iParam0;
            }
        }
        tempMap.put("avgValue", String.format("%." + p0 + "f", tempSum / tempDevCount));
        tempMap.put("warn", String.valueOf(tempWarn));

        double humSum = 0;
        int humDevCount = 0;
        int humWarn = 0;
        // 计算湿度平均值, 以及报警状态
        for (Map<String, Object> data : dataList) {
            String devType = data.get("devType").toString();
            String chType = data.get("chType").toString();
            Object warn = data.get("warn");
            Object param0 = data.get("param0");
            Object param1 = data.get("param1");
            Object param2 = data.get("param2");
            Object realValue = data.get("realValue");
            if (devType.equals("169") && chType.equals("102")) {
                humDevCount = humDevCount + 1;
                if (realValue == null) {
                    realValue = "0";
                }
                int iRealValue = Integer.parseInt(realValue.toString());

                if (param0 == null) {
                    param0 = "0";
                }
                int iParam0 = Integer.parseInt(param0.toString());

                if (param1 == null) {
                    param1 = "1";
                }
                int iParam1 = Integer.parseInt(param1.toString());

                if (param2 == null) {
                    param2 = "0";
                }
                int iParam2 = Integer.parseInt(param2.toString());
                double dvalue = (iRealValue - iParam2) / iParam1;
                humSum = humSum + dvalue;
                if (!warn.equals("0")) {
                    int iWarn = Integer.parseInt(warn.toString());
                    if (iWarn > humWarn)
                        humWarn = iWarn;
                }
                p0 = iParam0;
            }
        }
        humMap.put("avgValue", String.format("%." + p0 + "f", humSum / humDevCount));
        humMap.put("warn", String.valueOf(humWarn));

        int liuhuaqingSum = 0;
        int liuhuaqingDevCount = 0;
        int liuhuaqingWarn = 0;
        // 硫化氢 正常,异常状态显示
        for (Map<String, Object> data : dataList) {
            String devType = data.get("devType").toString();
            String chType = data.get("chType").toString();
            Object warn = data.get("warn");
            if (devType.equals("153") && chType.equals("122")) {
                liuhuaqingDevCount = liuhuaqingDevCount + 1;
                String realValue = data.get("realValue").toString();
                if (realValue == null) {
                    realValue = "0";
                }
                int iRealValue = Integer.parseInt(realValue);
                liuhuaqingSum = liuhuaqingSum + iRealValue;
                if (!warn.equals("0")) {
                    int iWarn = Integer.parseInt(warn.toString());
                    if (iWarn > liuhuaqingWarn)
                        liuhuaqingWarn = iWarn;
                }
            }
        }
        liuhuaqingMap.put("avgValue", String.valueOf(liuhuaqingSum / liuhuaqingDevCount));
        liuhuaqingMap.put("warn", String.valueOf(liuhuaqingWarn));

        int jiawangSum = 0;
        int jiawangDevCount = 0;
        int jiawangWarn = 0;
        // 甲烷 正常,异常状态显示
        for (Map<String, Object> data : dataList) {
            String devType = data.get("devType").toString();
            String chType = data.get("chType").toString();
            Object warn = data.get("warn");
            if (devType.equals("223") && chType.equals("110")) {
                jiawangDevCount = jiawangDevCount + 1;
                String realValue = data.get("realValue").toString();
                if (realValue == null) {
                    realValue = "0";
                }
                int iRealValue = Integer.parseInt(realValue);
                jiawangSum = jiawangSum + iRealValue;

                if (!warn.equals("0")) {
                    int iWarn = Integer.parseInt(warn.toString());
                    if (iWarn > jiawangWarn)
                        jiawangWarn = iWarn;
                }
            }
        }
        jiawanMap.put("avgValue", String.valueOf(jiawangSum / jiawangDevCount));
        jiawanMap.put("warn", String.valueOf(jiawangWarn));

        int yangqiSum = 0;
        int yangqiDevCount = 0;
        int yangqiWarn = 0;
        // 氧气正常,异常状态显示
        for (Map<String, Object> data : dataList) {
            String devType = data.get("devType").toString();
            String chType = data.get("chType").toString();
            Object warn = data.get("warn");
            if (devType.equals("224") && chType.equals("250")) {
                yangqiDevCount = yangqiDevCount + 1;
                String realValue = data.get("realValue").toString();
                if (realValue == null) {
                    realValue = "0";
                }
                int iRealValue = Integer.parseInt(realValue);
                yangqiSum = yangqiSum + iRealValue;

                if (!warn.toString().equals("0")) {
                    int iWarn = Integer.parseInt(warn.toString());
                    if (iWarn > yangqiWarn)
                        yangqiWarn = iWarn;
                }
            }
        }
        yangqiMap.put("avgValue", String.valueOf(yangqiSum / yangqiDevCount));
        yangqiMap.put("warn", String.valueOf(yangqiWarn));

        String tempWarnInfo = "正常";
        if (tempWarn != 0) {
            tempWarnInfo = "报警";
        }
        String humWarnInfo = "正常";
        if (humWarn != 0) {
            humWarnInfo = "报警";
        }
        String liuhuaqingWarnInfo = "正常";
        if (liuhuaqingWarn != 0) {
            liuhuaqingWarnInfo = "报警";
        }
        String jiawangWarnInfo = "正常";
        if (jiawangWarn != 0) {
            jiawangWarnInfo = "报警";
        }
        String yangqiWarnInfo = "正常";
        if (yangqiWarn != 0) {
            yangqiWarnInfo = "报警";
        }

        // 拼接显示内容
        String showMessage = "温度　" + tempMap.get("avgValue") + "℃ " + tempWarnInfo + "/r/n" + "湿度　"
                + humMap.get("avgValue") + "RH" + humWarnInfo + "/r/n" + "甲烷　" + jiawangWarnInfo + "/r/n" + "氧气　"
                + yangqiWarnInfo + "/r/n" + "硫化氢　" + liuhuaqingWarnInfo;

        logger.debug("showMessage:" + showMessage);

        Base64 base64 = new Base64();
        byte[] textByte = {};
        try {
            textByte = showMessage.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        // 编码
        String encodedText = base64.encodeToString(textByte);

        String receive = "{\"area_array\":[{\"textBinary\":0,\"areaType\":0,"
                + "\"color\":0,\"displayStyle\":5,\"fontBold\":0,\"fontName\":\"宋体\","
                + "\"fontSize\":12,\"height\":80,\"message\":\"" + encodedText + ""
                + "\",\"speed\":5,\"stayTime\":5000,\"width\":105,\"x\":0,\"y\":0," + "\"showTime\":"
                + showMessage.length()
                + ",\"id\":\"1\",\"runMode\":\"0\",\"immediatePlay\":\"0\",\"showTime\":\"20\",}],\"cmd_type\":\"show\"}";
        try {
            sendled.sendPacketToLed(receive);
        } catch (Exception e) {
            logger.debug("led 发送异常");
            logger.debug(e.toString());
            e.printStackTrace();
        }

    }

}
