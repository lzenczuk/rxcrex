package com.github.lzenczuk.rxcrex;


import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.RxNetty;
import rx.Observer;
import rx.subjects.PublishSubject;

import java.nio.charset.Charset;

/**
 * Created by dev on 29/05/17.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        RxNetty.newWebSocketServerBuilder(8078, connection -> {
            System.out.println("Receive connection");

            PublishSubject<Void> closeConnection = PublishSubject.create();

            connection.getInput().filter(webSocketFrame -> {

                System.out.println("Is final: "+webSocketFrame.isFinalFragment());

                if(webSocketFrame instanceof PingWebSocketFrame){
                    System.out.println("Ping");
                    connection.writeAndFlush(new PongWebSocketFrame(webSocketFrame.content().retain()));
                    return false;
                }

                if(webSocketFrame instanceof PongWebSocketFrame){
                    System.out.println("Pong");
                    return false;
                }

                if(webSocketFrame instanceof CloseWebSocketFrame){
                    System.out.println("Close frame");
                    closeConnection.onCompleted();
                    return false;
                }

                return true;
            }).subscribe(webSocketFrame -> {
                System.out.println("Receive strings: "+webSocketFrame.content().toString(Charset.defaultCharset()));
                webSocketFrame.retain();
            });

            Observer<String> outputObserver = new Observer<String>() {

                @Override
                public void onCompleted() {
                    System.out.println("Don't need output observer any more. Closing connection.");
                    closeConnection.onCompleted();
                }

                @Override
                public void onError(Throwable e) {
                    System.out.println("Error in output stream. Do nothing.");
                    e.printStackTrace();
                }

                @Override
                public void onNext(String s) {
                    connection.writeAndFlush(new TextWebSocketFrame(s));
                }
            };

            return closeConnection;
        }).enableWireLogging(LogLevel.INFO).build().startAndWait();
    }
}
