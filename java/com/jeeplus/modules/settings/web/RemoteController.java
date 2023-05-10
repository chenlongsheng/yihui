/**
 *
 */
package com.jeeplus.modules.settings.web;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.websocket.WebSockertFilter;
import com.jeeplus.modules.processData.web.DeviceCdpdfController;
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeeplus.common.mq.rabbitmq.RabbitMQConnection;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.modules.settings.entity.TChannel;
import com.jeeplus.modules.settings.entity.TDeviceDetail;
import com.jeeplus.modules.settings.service.TChannelService;
import com.jeeplus.modules.settings.service.TDeviceDetailService;
import com.jeeplus.modules.sys.utils.UserUtils;

import com.rabbitmq.client.Channel;

/**
 * @author admin
 */
@Controller
@RequestMapping(value = "settings/remote")
public class RemoteController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TChannelService tChannelService;
    @Autowired
    private TDeviceDetailService tDeviceDetailService;

    @Autowired
    RabbitMQConnection rabbitMQProducer;

    @RequestMapping(value = {"remoteChannelist"})
    @ResponseBody
    public String remoteChannelist(String orgId) {
        return ServletUtils.buildRs(true, "获取远程控制设备集合", tChannelService.remoteChannelist(orgId));
    }

    @RequestMapping(value = {"remoteControl"})
    @ResponseBody
    public String remoteControl(String channelId, String opr) throws TimeoutException {

        //channelId  通道id
        //opr  0是关 ,1是开
        try {
        TChannel tChannel = tChannelService.get(channelId);
        TDeviceDetail tDeviceDetail = tDeviceDetailService.get(tChannel.getDevId().toString());


        String message = "{\"data\":{\"mac\":\"" + tDeviceDetail.getMac() + "\",\"bus_addr\":\"" + tDeviceDetail.getBusAddr()
                + "\",\"channels\":{\"" + tChannel.getChNo() + "\":{\"value\":\"" + opr + "\"}}},\"cmd\":\"cmdDev\"}";

//        JSONObject json = new JSONObject();
//        json.put("cmd", "cmdDev");
//        json.put("data", message);


            WebSockertFilter.webSocketClient.send(message);

            System.out.println("/**============================================================**/");
            System.out.println(message);
            System.out.println("/**============================================================**/");
        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "失败", "");
        }
        return ServletUtils.buildRs(true, "成功", "");
    }


    @RequestMapping(value = {"ctrlPtz"})
    @ResponseBody
    public String ctrlPtz(String sn, String chNo, String value, String param1, String param2, String param3)
            throws TimeoutException {

//        {"cmd":"cmdNvr","data":{...}}
        String message = null;
        try {


        message = "{\"data\":{\"sn\":\"" + sn + "\",\"channels\":{\"" + chNo + "\":{\"cmd\":\"ctrlptz\",\"value\":\""
                + value + "\",\"param1\":\"" + param1 + "\",\"param2\":\"" + param2 + "\",\"param3\":\"" + param3
                + "\"}}},\"cmd\":\"cmdNvr\"}";
//            JSONObject json = new JSONObject();
//            json.put("cmd", "cmdNvr");
//            json.put("data", message);


            System.out.println("/**============================================================111**/");
            System.out.println(message);
            System.out.println("/**=======================================ss=====================**/");
            WebSockertFilter.webSocketClient.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            return ServletUtils.buildRs(false, "失败", "");
        }
        return ServletUtils.buildRs(true, "成功", "");

    }


    // mq报警联动接口
    @RequestMapping(value = {"mqLinkChannel"})
    @ResponseBody
    public String mqLinkChannel(String srcId, String level) {

        List<MapEntity> destList = tChannelService.getDestList(level, srcId);
        for (MapEntity entity : destList) {
            Long destId = (Long) entity.get("destId"); // 目标通道或者人员通知
            Integer linkType = (Integer) entity.get("linkType"); // 联动id,type_id = 5时候
            Long param = (Long) entity.get("param"); // 目标参数
            if (linkType < 10) {
//				if(linkType==8&&param<=-3) {
//				}
                try {
                    this.remoteControl(destId + "", param + "");
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            } else if (linkType > 10) { // 通知联动
                MapEntity orderBySrcId = tChannelService.getOrderBySrcId(srcId);// 获取报警工单

                MapEntity userMobile = tChannelService.getUserMobile(destId + "");// 获取用户电话imail
                String s = linkType + "";
                DecimalFormat df = new DecimalFormat("#,##");

                String sendTypes = df.format(Integer.parseInt(s));
                String[] sendType = sendTypes.split(",");
                for (int i = 0; i < sendType.length; i++) {
                    int key = Integer.parseInt(sendType[i]);
                    if (key == 11) {// 微信app
                    } else if (key == 22) { // 短信
                        System.out.println("=======" + sendMoben(orderBySrcId, param + ""));
                        System.out.println((String) userMobile.get("mobile"));
                        com.jeeplus.modules.warm.util.SendMessage.sendSMS((String) userMobile.get("mobile"),
                                sendMoben(orderBySrcId, param + "")); // 发送
                    } else if (key == 33) {// 语音
                    } else if (key == 44) {// 邮件
                        com.jeeplus.modules.warm.util.SendMessage.sendTextMail((String) userMobile.get("email"),
                                sendMoben(orderBySrcId, param + ""));
                    }
                }
            }
        }
        return ServletUtils.buildRs(true, "成功", "");
    }

    public String sendMoben(MapEntity map, String id) { // 报警map,模板id

        MapEntity entity = tChannelService.getTemplate(id);// 获取单挑模板
        String templateDtail = (String) entity.get("templateDetail");// 模板内容
        String alarmTime = (String) map.get("alarmTime");
        String alarmAddr = (String) map.get("alarmAddr");
        String devName = (String) map.get("devName");
        String devTypeName = (String) map.get("devTypeName");
        int alarmType = (int) map.get("alarmType");
        String alarm = "";
        if (alarmType == 0) {
            alarm = "设备故障";
        } else {
            alarm = "数据异常";
        }
        int alarmLevel = (int) map.get("alarmLevel");
        String level = "";
        if (alarmLevel == 1 || alarmLevel == 2) {
            level = alarmLevel + "(一般报警)";
        } else {
            level = alarmLevel + "(严重报警)";
        }
        templateDtail = templateDtail.replace("${createDate}", alarmTime);
        templateDtail = templateDtail.replace("${orgName}", alarmAddr);
        templateDtail = templateDtail.replace("${deviceName}", devName);
        templateDtail = templateDtail.replace("${devType}", devTypeName);
        templateDtail = templateDtail.replace("${alarmType}", alarm);
        templateDtail = templateDtail.replace("${alarmLevel}", level);
        return templateDtail;
    }


    @RequestMapping(value = {"uploadRemoteControl"})
    @ResponseBody
    public String uploadRemoteControl(String sn) throws TimeoutException {

        Channel rmqChannel = null;
        try {
            // 创建一个通道
            rmqChannel = rabbitMQProducer.createChannel();
            String message = "{\"change_type\":\"22\"}";
            // 发送消息到队列中
            System.out.println(message);
            rmqChannel.basicPublish("org.10", "db.datachange.sn." + sn, null, message.getBytes("UTF-8"));
            logger.debug("/**============================================================**/");
            logger.debug("Producer Send +'" + message + "'  to " + "db.datachange.sn." + sn);
            logger.debug("/**============================================================**/");
            // 关闭通道和连接
            rmqChannel.close();
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

    @RequestMapping(value = {"downloadRemoteControl"})
    @ResponseBody
    public String downloadRemoteControl(String orgId) throws TimeoutException {

        List<MapEntity> selectSnByOrgId = tDeviceDetailService.selectSnByOrgId(orgId);
        Channel rmqChannel = null;
        try {
            for (MapEntity mapEntity : selectSnByOrgId) {
                String sn = (String) mapEntity.get("sn");
                // 创建一个通道
                rmqChannel = rabbitMQProducer.createChannel();
                String message = "{\"change_type\":\"21\"}";
                // 发送消息到队列中
                System.out.println(message);
                rmqChannel.basicPublish("org.10", "db.datachange.sn." + sn, null, message.getBytes("UTF-8"));
                logger.debug("/**============================================================**/");
                logger.debug("Producer Send +'" + message + "'  to " + "db.datachange.sn." + sn);
                logger.debug("/**============================================================**/");
                // 关闭通道和连接
                rmqChannel.close();
            }
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

    @RequestMapping(value = {"sendRemoteControl"})
    @ResponseBody
    public String sendRemoteControl(String orgId, String message) throws TimeoutException {

        List<MapEntity> selectSnByOrgId = tDeviceDetailService.selectSnByOrgId(orgId);
        Channel rmqChannel = null;
        try {
            for (MapEntity mapEntity : selectSnByOrgId) {
                String sn = (String) mapEntity.get("sn");
                // 创建一个通道
                rmqChannel = rabbitMQProducer.createChannel();

                message = "{\"change_type\":\"" + 23 + "\",\"body\":" + message + "}";

                // 发送消息到队列中
                System.out.println(message);
                rmqChannel.basicPublish("org.10", "db.datachange.sn." + sn, null, message.getBytes("UTF-8"));
                logger.debug("/**============================================================**/");
                logger.debug("Producer Send +'" + message + "'  to " + "db.datachange.sn." + sn);
                logger.debug("/**============================================================**/");
                // 关闭通道和连接
                rmqChannel.close();
            }
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

    /**
     * 重启软件版本
     *
     * @param sn
     * @return
     * @throws TimeoutException
     */
    @RequestMapping(value = {"restartNVRRemoteControl"})
    @ResponseBody
    public String restartNVRRemoteControl(String sn) throws TimeoutException {

        Channel rmqChannel = null;
        try {

            // 创建一个通道
            rmqChannel = rabbitMQProducer.createChannel();

            String message = "{\"channels\":{\"" + "-1" + "\":{\"cmd\":\"" + "reboot" + "\"}}}";

//		    {"channels":{"-1":{"cmd":"reboot"}}};			
            // 发送消息到队列中
            System.out.println(message);
            rmqChannel.basicPublish("org.10", "dev.command.sn." + sn, null, message.getBytes("UTF-8"));
            logger.debug("/**============================================================**/");
            logger.debug("Producer Send +'" + message + "'  to " + "dev.command.sn." + sn);
            logger.debug("/**============================================================**/");
            // 关闭通道和连接
            rmqChannel.close();

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

    // 获取所有的软件
    @RequestMapping(value = {"selectFireware"})
    @ResponseBody
    public String selectFireware() {
        return ServletUtils.buildRs(true, "成功", tDeviceDetailService.selectFireware());
    }

    /**
     * 升级软件版本
     *
     * @param sn
     * @param md5
     * @param url
     * @return
     * @throws TimeoutException
     */
    @RequestMapping(value = {"updateNVRRemoteControl"})
    @ResponseBody
    public String updateNVRRemoteControl(String sn, String md5, String url) throws TimeoutException {

        Channel rmqChannel = null;
        try {

            // 创建一个通道
            rmqChannel = rabbitMQProducer.createChannel();

            String message = "{\"channels\":{\"" + "-1" + "\":{\"cmd\":\"" + "upgrade" + "\",\"md5\":\"" + md5
                    + "\",\"url\":\"" + url + "\"}}}";

            // {"channels":{"-1":{"cmd":"upgrade","md5":"c58","url":"htp"}}};
            //  {"channels":{"-1":{"cmd":"upgrade","md5":"649","url":"313"}}}
            // 发送消息到队列中
            System.out.println(message);
            rmqChannel.basicPublish("org.10", "dev.command.sn." + sn, null, message.getBytes("UTF-8"));
            logger.debug("/**============================================================**/");
            logger.debug("Producer Send +'" + message + "'  to " + "dev.command.sn." + sn);
            logger.debug("/**============================================================**/");
            // 关闭通道和连接
            rmqChannel.close();

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
