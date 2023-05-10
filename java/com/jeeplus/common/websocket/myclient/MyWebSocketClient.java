package com.jeeplus.common.websocket.myclient;

import com.jeeplus.modules.processData.service.ProcesDataService;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author PanBangLin
 * @version 1.0
 * @date 2022/4/19 22:38
 */
@Slf4j
public class MyWebSocketClient extends org.java_websocket.client.WebSocketClient {



    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public MyWebSocketClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    public MyWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("[websocket] 连接成功");
        //devOrder.subscribeDev("33255773800487108280");
    }


    @Override
    public void onMessage(String message) {

        ProcesDataService.proAllData(message);

    }

    @Override
    public void onMessage(ByteBuffer bytes) {

        log.info("[websocket] 收到消息={}", ByteUtils.getString(bytes));

    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("[websocket] 退出连接");
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        log.info("[websocket] 连接错误={}", e.getMessage());
    }
}