package com.jeeplus.common.mq.rabbitmq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.mq.mqtt.MqttProducer;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.HttpUtils;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.websocket.onchat.ChatServer;
import com.jeeplus.common.websocket.onchat.ChatServerPool;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TDevice;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.entity.TOrg;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TOrgExecUnitBindService;
import com.jeeplus.modules.starnet.service.RemoteMessService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.warm.service.PdfOrderService;
import com.jeeplus.modules.warm.service.PdfPrincipalService;
import com.rabbitmq.client.*;

import kafka.utils.Json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;

import javax.servlet.ServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RabbitMQCustomer {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unused")
    private ServiceFacade serviceFacade;

    @Autowired
    PdfOrderService pdfOrderService;

    @Autowired
    TChannelService channelService;

    @Autowired
    PdfPrincipalService pdfPrincipalService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

//    @Autowired
//    MqttProducer mqttProducer;

    @Autowired
    TOrgExecUnitBindService tOrgExecUnitBindService;

    @Autowired
    RemoteMessService remoteMessService;

    ServletResponse response;

    public void setServiceFacade(ServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    private ConnectionFactory factory = new ConnectionFactory();

    // 创建一个新的连接
    private Connection connection = null;

    private String rabbitmqHost;

    private String rabbitmqUsername;

    private String rabbitmqPassword;

    private int rabbitmqPort;

    private String rabbitmqVirtualHost;

    public String getRabbitmqHost() {
        return rabbitmqHost;
    }

    public void setRabbitmqHost(String rabbitmqHost) {
        this.rabbitmqHost = rabbitmqHost;
    }

    public String getRabbitmqUsername() {
        return rabbitmqUsername;
    }

    public void setRabbitmqUsername(String rabbitmqUsername) {
        this.rabbitmqUsername = rabbitmqUsername;
    }

    public String getRabbitmqPassword() {
        return rabbitmqPassword;
    }

    public void setRabbitmqPassword(String rabbitmqPassword) {
        this.rabbitmqPassword = rabbitmqPassword;
    }

    public int getRabbitmqPort() {
        return rabbitmqPort;
    }

    public void setRabbitmqPort(int rabbitmqPort) {
        this.rabbitmqPort = rabbitmqPort;
    }

    public String getRabbitmqVirtualHost() {
        return rabbitmqVirtualHost;
    }

    public void setRabbitmqVirtualHost(String rabbitmqVirtualHost) {
        this.rabbitmqVirtualHost = rabbitmqVirtualHost;
    }

    public static BlockingQueue<JSONObject> devAlarmQueue = new LinkedBlockingQueue<JSONObject>();
    public static BlockingQueue<JSONObject> devDataQueue = new LinkedBlockingQueue<JSONObject>();

    private final static String DEV_ALARM_QUEUE = "dev.alarm";
    private final static String DEV_DATA_QUEUE = "dev.status";

    class ChDataThread extends Thread {
        public ChDataThread() {
        }

        public void run() {
            while (true) {
                JSONObject devData = null;
                try {
                    devData = devDataQueue.take();
                    logger.debug("take a devData");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (devData != null) {
                    /*
                     * 通道数组暂不处理 List<ChData> channellist = devData.getItems(); for(ChData chdata :
                     * channellist) { String chid = chdata.getCh_id().toString();
                     * if(JedisUtils.exists(chid)) { String value = JedisUtils.get(chid); if(value
                     * != chdata.getStatus().toString()) { //更新数据库
                     * 
                     * //更新缓存 JedisUtils.set(chdata.getCh_id().toString(),
                     * chdata.getStatus().toString(),3600*24); } } else { //更新数据库
                     * 
                     * //插入缓存 JedisUtils.set(chdata.getCh_id().toString(),
                     * chdata.getStatus().toString(),3600*24); } }
                     */
                }
            }
        }
    }

    class AlarmDataThread extends Thread {
        public AlarmDataThread() {

        }

        public void run() {
            while (true) {
                JSONObject devAlarm = null;
                try {
                    devAlarm = devAlarmQueue.take();
                    logger.debug("take devAlarmData");

                    if (devAlarm != null) {

                        MapEntity entity;
                        String addr = "";
                        String chAddr = "";
                        String devname = "";
                        String orgId = "";
                        String alarmResume = "";
                        String phones = "";
                        List<MapEntity> phoneList = new ArrayList<MapEntity>();
                        JSONArray items = devAlarm.getJSONArray("items");
                        Integer online = devAlarm.getInteger("online");
                        JSONArray resumeItems = devAlarm.getJSONArray("resume_items");

                        if (online != null) {
                            entity = new MapEntity();
                            // 设备离线
                            String devId = devAlarm.getString("dev_id");
                            MapEntity ent = channelService.getDeviveName(devId);
                            addr = ent.get("addr").toString();
                            String devType = ent.get("devType").toString();
                            devname = ent.get("name").toString();
                            orgId = ent.get("orgId").toString();

                            phoneList = pdfPrincipalService.getPrincipalPhoneBydevId(devId);
                            if (online == 0) {
                                alarmResume = "设备离线";
                            } else {
                                alarmResume = "设备上线";
                            }
                            phones = "";
                            String message = "";
                            for (int j = 0; j < phoneList.size(); j++) {
                                String phone = phoneList.get(j).get("phone").toString();
                                String sn = phoneList.get(j).get("sn").toString();
                                message = phoneList.get(j).get("orgName").toString() + "(" + entity.get("addr")
                                        + entity.get("devName").toString() + ")" + alarmResume;
                                remoteMessService.remoteControl(sn,
                                        remoteMessService.getVoiceDatas(phoneList.get(j), alarmResume));
                                remoteMessService.remoteControl(sn, remoteMessService.getTextDatas(message,
                                        phoneList.get(j).get("phone").toString(), alarmResume));
                            }
                            entity.put("chId", devId);
                            if (online == 0) {
                                entity.put("status", 0);
                            } else {
                                entity.put("status", 3);
                            }
                            entity.put("devType", devType);
                            entity.put("addr", addr);
                            entity.put("content", message);
                            entity.put("phones", phones);
                            pdfPrincipalService.insertMessageLog(entity);
                        } else if (items != null) { // 发生报警
                            entity = new MapEntity();
                            for (int i = 0; i < items.size(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                String chId = item.getString("ch_id");
                                String alarmLevel = item.getString("alarm_level");
                                Integer checkExpirationTime = pdfPrincipalService.checkExpirationTime("1", chId,
                                        alarmLevel);// 1表示报警消息日志
                                String realValue = item.getString("alarm_value");
                                System.out.println("realValue:" + realValue);
                                TChannel channel = channelService.get(chId);
                                orgId = channel.getLogicOrgId();
                                String devType = channel.getDevType();
                                devname = channel.getDevName() + channel.getName();
                                addr = channel.getAddr();
                                if (StringUtils.isNotBlank(alarmLevel)) {
                                    alarmResume = "发生" + alarmLevel + "级报警";
                                } else {
                                    alarmResume = "发生报警";
                                }
                                phones = "";
                                String message = "";
                                if (checkExpirationTime == null || checkExpirationTime > 0) { // 查询是否在过期时间内
                                    phoneList = pdfPrincipalService.getPrincipalPhoneBychId(chId);
                                    for (int j = 0; j < phoneList.size(); j++) {

                                        String phone = phoneList.get(j).get("phone").toString();
                                        String sn = phoneList.get(j).get("sn").toString();
                                        message = phoneList.get(j).get("orgName").toString() + "("
                                                + phoneList.get(j).get("addr")
                                                + phoneList.get(j).get("devName").toString() + ")" + alarmResume;
                                        remoteMessService.remoteControl(sn,
                                                remoteMessService.getTextDatas(message, phone, alarmResume));
                                        remoteMessService.remoteControl(sn,
                                                remoteMessService.getVoiceDatas(phoneList.get(j), alarmResume));
                                    }
                                }
                                entity.put("chId", chId);
                                entity.put("status", 1);// 报警信息
                                entity.put("level", alarmLevel);
                                entity.put("devType", devType);
                                entity.put("addr", addr);
                                entity.put("alarmValue", realValue);
                                entity.put("content", message);
                                entity.put("phones", phones);
                                pdfPrincipalService.insertMessageLog(entity);
                            }
                        } else if (resumeItems != null) {// 恢复正常
                            entity = new MapEntity();
                            alarmResume = "恢复正常";

                            for (int i = 0; i < resumeItems.size(); i++) {
                                JSONObject item = resumeItems.getJSONObject(i);
                                String chId = item.getString("ch_id");

                                String realValue = item.getString("real_value");
                                System.out.println("realValue:" + realValue);
                                TChannel channel = channelService.get(chId);
                                orgId = channel.getLogicOrgId();
                                String devType = channel.getDevType();
                                Long typeId = channel.getTypeId();
                                if (typeId == 2) {
                                    realValue = "";
                                }
                                devname = channel.getDevName() + channel.getName();
                                addr = channel.getAddr();
                                devname = channel.getName();
                                phones = "";
                                String message = "";
                                phoneList = pdfPrincipalService.getPrincipalPhoneBychId(chId);
                                for (int j = 0; j < phoneList.size(); j++) {
                                    String phone = phoneList.get(j).get("phone").toString();
                                    String sn = phoneList.get(j).get("sn").toString();
                                    message = phoneList.get(j).get("orgName").toString() + "(" + entity.get("addr")
                                            + entity.get("devName").toString() + ")" + alarmResume;
                                    remoteMessService.remoteControl(sn,
                                            remoteMessService.getVoiceDatas(phoneList.get(j), alarmResume));
                                    remoteMessService.remoteControl(sn,
                                            remoteMessService.getTextDatas(message, phone, alarmResume));
                                }
                                entity.put("chId", chId);
                                entity.put("status", 2);
                                entity.put("devType", devType);
                                entity.put("addr", addr);
                                entity.put("alarmValue", realValue);
                                entity.put("content", message);
                                entity.put("phones", phones);
                                pdfPrincipalService.insertMessageLog(entity);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    public void startProccessData() {
        this.factory.setHost(rabbitmqHost);
        this.factory.setUsername(rabbitmqUsername);
        this.factory.setPassword(rabbitmqPassword);
        this.factory.setPort(rabbitmqPort);
        this.factory.setVirtualHost(rabbitmqVirtualHost);
        // 关键所在，指定线程池
        ExecutorService service = Executors.newFixedThreadPool(10);
        factory.setSharedExecutor(service);
        // 设置自动恢复
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(2);// 设置 每10s ，重试一次
        factory.setTopologyRecoveryEnabled(false);// 设置不重新声明交换器，队列等信息。
        try {
            connection = factory.newConnection();

            /** 报警消息 */
            // 创建一个通道
            Channel devAlarmChannel = connection.createChannel();
            // 声明要关注的队列
            devAlarmChannel.queueDeclare(DEV_ALARM_QUEUE, true, false, false, null);
            logger.debug("Customer Alarm Waiting Received messages");
            // DefaultConsumer类实现了Consumer接口，通过传入一个频道，
            // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
            final Consumer alarmConsumer = new DefaultConsumer(devAlarmChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
//					logger.debug("alarmConsumer Received '" + message + "'");
                    JSONObject devAlarm = null;

                    try {
                        devAlarm = JSONObject.parseObject(message);
                        devAlarmQueue.put(devAlarm);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        logger.debug(e.toString());
                    }
                }
            };
            // 自动回复队列应答 -- RabbitMQ中的消息确认机制
            devAlarmChannel.basicConsume(DEV_ALARM_QUEUE, true, alarmConsumer);

            // 断开重新连接后,重新创建用户channel,消费消息队列
            ((Recoverable) connection).addRecoveryListener(new RecoveryListener() {
                @Override
                public void handleRecovery(Recoverable recoverable) {
                    try {
                        // 创建一个通道
                        Channel devDataChannel = connection.createChannel();
                        // 声明要关注的队列
                        devDataChannel.queueDeclare(DEV_ALARM_QUEUE, true, false, false, null);
                        // 自动回复队列应答 -- RabbitMQ中的消息确认机制
                        devDataChannel.basicConsume(DEV_ALARM_QUEUE, true, alarmConsumer);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void handleRecoveryStarted(Recoverable recoverable) {
                    // TODO Auto-generated method stub

                }
            });
            new AlarmDataThread().start();

            /** 数据消息 */
            // 创建一个通道
            Channel devDataChannel = connection.createChannel();
            // 声明要关注的队列
            devDataChannel.queueDeclare(DEV_DATA_QUEUE, true, false, false, null);
            logger.debug("Customer Data Waiting Received messages");
            // DefaultConsumer类实现了Consumer接口，通过传入一个频道，
            // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
            final Consumer devDataConsumer = new DefaultConsumer(devDataChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                        byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    logger.debug("devDataConsumer Received '" + message + "'");
                    JSONObject json = JSONObject.parseObject(message);
                    try {
                        devDataQueue.put(json);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            // 自动回复队列应答 -- RabbitMQ中的消息确认机制
            devDataChannel.basicConsume(DEV_DATA_QUEUE, true, devDataConsumer);

            ((Recoverable) connection).addRecoveryListener(new RecoveryListener() {
                @Override
                public void handleRecovery(Recoverable recoverable) {

                    try {
                        // 创建一个通道
                        Channel devDataChannel = connection.createChannel();
                        // 声明要关注的队列
                        devDataChannel.queueDeclare(DEV_DATA_QUEUE, true, false, false, null);
                        // 自动回复队列应答 -- RabbitMQ中的消息确认机制
                        devDataChannel.basicConsume(DEV_DATA_QUEUE, true, devDataConsumer);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void handleRecoveryStarted(Recoverable recoverable) {
                    // TODO Auto-generated method stub

                }
            });
            new ChDataThread().start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
