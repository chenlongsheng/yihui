/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.common.websocket;


import com.jeeplus.common.websocket.myclient.MyWebSocketClient;
import com.jeeplus.common.websocket.onchat.ChatServer;
import com.jeeplus.modules.processData.dao.ProcessDataDao;
import com.jeeplus.modules.processData.web.DeviceCdpdfController;

import org.java_websocket.WebSocketImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ejb.Init;
import javax.servlet.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class WebSockertFilter implements Filter {

    public static MyWebSocketClient webSocketClient;


    @Autowired
    public ProcessDataDao processDataDaoi;

    /**
     * 初始化
     */
    public void init(FilterConfig fc) throws ServletException {

        this.startWebsocketChatServer();

    }


    /**
     * 启动即时聊天服务
     */
    public void startWebsocketChatServer() {
     // String ws = "ws://192.168.128.40:1080";
           String ws = "ws://192.168.3.118:1080";
        //String ws="ws://127.0.0.1:8181/ws/v1/cabinet/status/"+ UUID.randomUUID().toString();
      /* try {
           WebSockertFilter.webSocketClient = new MyWebSocketClient(new URI(ws));
           WebSockertFilter.webSocketClient.connect();
            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (WebSockertFilter.webSocketClient.isClosed()) {
                        System.out.println("进入websocket定时器");
                        WebSockertFilter.webSocketClient.reconnect();
                    }
                }
            }, 1000, 8000);
        } catch (URISyntaxException e) {
            //  e.printStackTrace();
        }*/
    }

    // 计时器
    public void timer() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9); // 控制时
        calendar.set(Calendar.MINUTE, 0); // 控制分
        calendar.set(Calendar.SECOND, 0); // 控制秒

        Date time = calendar.getTime(); // 得出执行任务的时间

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                // PersonService personService =
                // (PersonService)ApplicationContext.getBean("personService");

                // System.out.println("-------设定要指定任务--------");
            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }

    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
            throws IOException, ServletException {

    }

}
