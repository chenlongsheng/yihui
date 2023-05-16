package com.jeeplus.modules.processData.web;

import com.jeeplus.common.utils.ServletUtils;
import com.jeeplus.common.web.BaseController;
import com.jeeplus.common.websocket.WebSockertFilter;
import com.jeeplus.common.websocket.myclient.MyWebSocketClient;
import com.jeeplus.modules.processData.dao.ProcessDataDao;
import com.jeeplus.modules.sys.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping(value = "settings/cdpdf")
public class DeviceCdpdfController extends BaseController {

   // public static MyWebSocketClient webSocketClient;

    public static HttpServletRequest request;

    @Autowired
    private AreaService areaService;

    @Autowired
    public ProcessDataDao processDataDao;

    @RequestMapping(value = {"getAll"})
    @ResponseBody
    public String getAll(HttpServletRequest requesthttp) {


        request = requesthttp;
        try {
      //      WebSockertFilter.webSocketClient.send("{\"cmd\":\"getAll\"}");
        } catch (Exception e) {
            return ServletUtils.buildRs(false, "发送指令失败", "");
        }
        return ServletUtils.buildRs(true, "发送指令成功", "");
    }


    @RequestMapping(value = {"getPic"})
    @ResponseBody
    public void getPic(HttpServletRequest request) {

        try {
            // 远程图片地址
            URL url = new URL("http://192.168.3.170:8080/smartpark/static_modules/emap_upload/dz-c.png");

            // 打开连接
            URLConnection conn = url.openConnection();
            // 获取输入流
            InputStream in = conn.getInputStream();

            String pic_path = request.getSession().getServletContext().getRealPath("/");
            Path path = Paths.get(pic_path + "static_modules/image2.png");

            // 将图片保存到static文件夹中
            Files.copy(in, path);
            // 关闭流
            in.close();

            System.out.println("图片已下载到本地st11atic文件夹！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}





