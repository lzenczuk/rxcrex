package com.github.lzenczuk.rxcrex.poloniex;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.websocket.WebSocketClient;

/**
 * Created by dev on 07/06/17.
 */
public class PoloniexWsClient {

    public void run() throws InterruptedException {
        WebSocketClient<WebSocketFrame, WebSocketFrame> webSocketClient = RxNetty.newWebSocketClientBuilder("api.poloniex.com", 443).build();

        System.out.println("Calling connect");
        webSocketClient.connect().subscribe(connection -> {
            System.out.println("Connected");

            connection.getInput().subscribe(webSocketFrame -> {
                System.out.println("Receive frame: "+webSocketFrame);
            });

            System.out.println("Sending message");
            connection.writeAndFlush(new  TextWebSocketFrame("test"));
        });

        System.out.println("Waiting 10s...");
        Thread.sleep(10000L);
        System.out.println("Shut down application");
    }
}
