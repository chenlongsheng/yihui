package com.jeeplus.modules.processData.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.persistence.MapEntity;
import com.jeeplus.common.websocket.WebSockertFilter;
import com.jeeplus.modules.processData.dao.ProcessDataDao;
import com.jeeplus.modules.processData.entity.Channels;
import com.jeeplus.modules.processData.entity.Devices;
import com.jeeplus.modules.processData.entity.ImageDevCh;
import com.jeeplus.modules.processData.entity.Images;
import com.jeeplus.modules.processData.web.DeviceCdpdfController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcesDataService implements ServletContextAware, ApplicationListener<ContextRefreshedEvent> {

    public static List<MapEntity> list = new ArrayList<MapEntity>();
    public static ProcessDataDao processDataDao;
    @Autowired
    public ProcessDataDao processDataDao1;

    public static int check = 0;

    private static ServletContext servletContext;


    private static String bathPath;

    @Value("${bathPath}")
    private String bathPath0;

    public static void proAllData(String message) {


        JSONObject jsonObj = null;
        try {
            jsonObj = JSON.parseObject(message);

        String cmd = jsonObj.getString("cmd");

        if (cmd.equals("getAll")) {

            processDataDao.truncateDetail();
            System.out.println("cmd==  " + cmd);

            JSONObject data = jsonObj.getJSONObject("data");

            JSONObject devices = data.getJSONObject("devices");

            JSONObject channels = data.getJSONObject("channels");
            JSONObject images = data.getJSONObject("images");
            JSONObject image_dev_chs = data.getJSONObject("image_dev_ch");

            for (String deviceId : devices.keySet()) {
                JSONObject deviceObject = devices.getJSONObject(deviceId);

                Devices devs = JSONObject.parseObject(deviceObject.toString(), Devices.class);
                System.out.println("device====" + devs.toString());
                try {
                    processDataDao.insertDevice(devs);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            for (String channelId : channels.keySet()) {
                JSONObject channelIdObject = channels.getJSONObject(channelId);

                Channels channel = JSONObject.parseObject(channelIdObject.toString(), Channels.class);
                System.out.println("channel====" + channel.toString());

                try {
                    processDataDao.inserChannels(channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            for (String imageId : images.keySet()) {
                JSONObject imageIdObject = images.getJSONObject(imageId);
                Images image = JSONObject.parseObject(imageIdObject.toString(), Images.class);
                try {
                    String[] split = image.getImage().split("/");
                    String img = split[split.length - 1];
                    image.setImage(img);

                    System.out.println("image====" + img);
                    getPic(img);

                    processDataDao.inserImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (String image_dev_ch : image_dev_chs.keySet()) {
                JSONObject image_dev_chObject = image_dev_chs.getJSONObject(image_dev_ch);

                ImageDevCh imageDev = JSONObject.parseObject(image_dev_chObject.toString(), ImageDevCh.class);

                try {
                    processDataDao.inserImageDevCh(imageDev);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (check == 1 && cmd.equals("stateDev")) {

            JSONObject data = jsonObj.getJSONObject("data");
            String mac = data.getString("mac");
            String state = data.getString("state");
            processDataDao.updateOnline(mac, state);

        } else if (check == 1 && cmd.equals("upRealData")) {

            // from_unixtime(1451997924,'%Y-%d');

            JSONObject data = jsonObj.getJSONObject("data");

            String online = data.getString("online");

            String id = data.getString("id");

//            System.out.println("id==" + id + "online===" + online);

            JSONObject channels = data.getJSONObject("channels");
            if (channels != null) {
                for (String channelId : channels.keySet()) {
                    JSONObject channelIdObject = channels.getJSONObject(channelId);

                    MapEntity entity = JSONObject.parseObject(channelIdObject.toString(), MapEntity.class);
                    list.add(entity);
                }
                try {
                    processDataDao.updatedevicetail(id, online);
                    processDataDao.updateRealData(list);
                    list.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (cmd.equals("cloudMsg")) {

            JSONObject data = jsonObj.getJSONObject("data");
            String msg = data.getString("msg");
            if (msg.equals("1")) {
                WebSockertFilter.webSocketClient.send("{\"cmd\":\"getAll\"}");
            }

        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getPic(String pic) {
        System.out.println("===========getall");
//        String picurl = "http://192.168.3.170:8080/smartpark";
        String pic_path = servletContext.getRealPath("/");

        try {
            System.out.println("bathPath:===" + bathPath);
            // 远程图片地址
            URL url = new URL(bathPath + "/static_modules/emap_upload/" + pic);

            // 打开连接
            URLConnection conn = url.openConnection();

            // 获取输入流
            InputStream in = conn.getInputStream();
            //  String pic_path = request.getSession().getServletContext().getRealPath("/");
            Path path = Paths.get(pic_path + "static_modules/emap_upload/" + pic);
            System.out.println("pic====" + pic_path + "static_modules/emap_upload/" + pic);
            // 将图片保存到static文件夹中
            new File(pic_path + "static_modules/emap_upload/" + pic).delete();

            Files.copy(in, path);
            // 关闭流
            in.close();

            System.out.println("图片已下载到本地static文件夹！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {// 保证只执行一次
            processDataDao = processDataDao1;
            bathPath = bathPath0;
            check = 1;
        }
    }


    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
