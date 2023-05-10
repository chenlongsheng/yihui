///**
// * 
// */
//package com.jeeplus.modules.settings.service;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.IdentityHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.ThreadPoolExecutor;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.adobe.xmp.impl.Base64;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.jeeplus.common.persistence.MapEntity;
//import com.jeeplus.common.utils.StringUtils;
//import com.jeeplus.modules.settings.dao.RefeshDevDataDao;
//
//
//@Service
//@Lazy(false)
//public class RefeshDevDataService {
//
//    /**
//     * 日志对象
//     */
//    protected Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    RefeshDevDataDao refeshDevDataDao;
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Value("${rabbitmq.username}")
//    private String username;
//
//    @Value("${rabbitmq.password}")
//    private String password;
//
//    @Value("${rabbitmq.httpPath}")
//    private String httpPath;
//
//    @Value("${rabbitmq.virtualHost}")
//    private String virtualHost;
//
//    static int count = 0;
//
//    private final Map<Object, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();
//
//    Properties properties = new Properties();
//    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
//
//    public String getRabbitMqData(String httpPa) {
//
//        try {
//            String httpURL = httpPa;
////            System.out.println("rabbitmq.httpPath: " + httpPath);
//
//            URL url = new URL(httpURL);
//
//            URLConnection uc = url.openConnection();
////            System.out.println("rabbitmq.username: " + username);
////            System.out.println("rabbitmq.password: " + password);
//
//            String encoded = new String(Base64.encode(new String(username + ":" + password).getBytes()));
//            uc.setRequestProperty("Authorization", "Basic " + encoded);
//            uc.connect();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(uc.getInputStream()));
//            String line;
//            while ((line = rd.readLine()) != null) {
////                System.out.println(line);
//                return line;
//            }
//            rd.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    public List<String> updateMqOnline() {
//        List<String> listMq = new ArrayList<String>();
//        JSONArray ja = JSON.parseArray(getRabbitMqData(httpPath));
//        System.out.println("接收总共:" + ja.size());
//        for (int i = 0; i < ja.size(); i++) {
//            MapEntity program = JSONObject.parseObject(ja.get(i).toString(), MapEntity.class);
//            String name = program.get("name").toString();// 获取所有的name
//
//            if (name.contains("dev.command")) {
//
//                JSONArray jar = JSON.parseArray(getRabbitMqData(httpPath + name + "/bindings"));// 按每个name,获取routing_key
//
//                for (int j = 0; j < jar.size(); j++) {
//                    MapEntity programr = JSONObject.parseObject(jar.get(j).toString(), MapEntity.class);
//
//                    if (programr.get("source").toString().equals("org.10")) {
//                        String routingName = programr.get("routing_key").toString();
////                        System.out.println("routingName===  " + routingName);
//                        String[] sp = routingName.split("\\.");
//
//                        if (sp[2].equals("sn")) {
////                            listMq.add(sp[3]);
//                        } else {
//                            listMq.add(sp[2]);
//
//                        }
//                    }
//                }
//            }
//
//        }
//
//        return listMq;
//    }
//
//    @Scheduled(initialDelay = 5 * 000, fixedRate = 600 * 1000L)
//    public void checkOnline() throws Exception {
//
//        if (StringUtils.isBlank(httpPath)) {
//            if (count == 0) {
//                logger.debug("RefeshDevDataServicel轮询结束停止");
//                count++;
//            }
//            return;
//        }
//        List<String> listMq = updateMqOnline();
//        System.out.println(listMq);
//        List<String> onlineList = new ArrayList<String>();
//        List<String> notOnlineList = new ArrayList<String>();
//        List<MapEntity> deviceOnlines = refeshDevDataDao.getDeviceOnline();
//
//        for (MapEntity mapEntity : deviceOnlines) {
//            String macsn = mapEntity.get("macsn").toString();
//            String online = mapEntity.get("online").toString();
//
//            if (listMq.contains(macsn)) {
//                mapEntity.put("mqOnline", "1");
//            } else {
//                mapEntity.put("mqOnline", "0");
//            }
//        }
//        // ---------------------
//        // System.out.println(deviceOnlines);
//        count = 0;
//        for (MapEntity mapEntity : deviceOnlines) {
//
//            String online = mapEntity.get("online").toString();
//            String mqOnline = mapEntity.get("mqOnline").toString();
//            String devType = mapEntity.get("devType").toString();
//            String macsn = mapEntity.get("macsn").toString();
//
//            String id = mapEntity.get("id").toString();
//
//            if (!online.equals(mqOnline) && mqOnline.equals("0")) {
//
//                notOnlineList.add(id); // 添加网关id
//
////              if (devType.equals("150")) {
//
//                List<String> macsByDevIds = refeshDevDataDao.getMacsByDevId(id);// 获取网关和网关下的设备id
//
//                for (String mac : macsByDevIds) {
//
//                    Object objRealData = redisTemplate.opsForValue()
//                            .get(virtualHost + "_devdataproc_device_mac_" + mac);
//                    logger.debug("objRealData== " + objRealData);
//
//                    if (objRealData != null) {
//
//                        count++;
//
////                      System.out.println("objRealData=  " + objRealData);
//                        JSONObject user = JSONObject.parseObject(objRealData.toString());
//                        Map<String, Object> userMap = new HashMap<>();
//                        // 循环转换
//                        for (Map.Entry<String, Object> entry : user.entrySet()) {
//
//                            if (entry.getKey().equals("online")) {
//                                userMap.put(entry.getKey(), "0");
//                            } else {
//                                userMap.put(entry.getKey(), entry.getValue());
//                            }
//                        }
//                        redisTemplate.opsForValue().set(virtualHost + "_devdataproc_device_mac_" + mac,
//                                JSONObject.toJSONString(userMap));
//
//                        logger.debug("JSONObject.toJSONString(userMap)==   " + JSONObject.toJSONString(userMap));
//
//                    }
//                }
////            }
//            }
//
//        }
//        
//        
//        logger.debug("count ==" + count);
////        System.out.println("onlineList" + onlineList);
//        logger.debug("notOnlineList" + notOnlineList);
////        if (onlineList != null && onlineList.size() > 0) {
////            refeshDevDataDao.onlineList(onlineList);
////        }
//        if (notOnlineList != null && notOnlineList.size() > 0) {
//            refeshDevDataDao.notOnlineList(notOnlineList);
//
//        }
//    }
//
//}
