package com.github.lzenczuk.rxcrex.poloniex;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.websocket.WebSocketClient;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by dev on 07/06/17.
 */
public class PoloniexWsClientTest {

    @Test
    @Ignore
    public void testConnection() throws InterruptedException {
        PoloniexWsClient client = new PoloniexWsClient();
        client.run();
    }

}
