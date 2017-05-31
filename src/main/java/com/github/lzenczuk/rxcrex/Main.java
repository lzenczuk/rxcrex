package com.github.lzenczuk.rxcrex;


import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.RxNetty;
import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dev on 29/05/17.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        RxNetty.newWebSocketServerBuilder(8078, connection -> {
            System.out.println("Receive connection");

            PublishSubject<Void> closeConnection = PublishSubject.create();

            Observable<WebSocketFrame> inputObservable = connection.getInput();

            AtomicInteger counter = new AtomicInteger();

            new Observer<String>(){

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

            inputObservable.subscribe(webSocketFrame -> {
                if(webSocketFrame instanceof CloseWebSocketFrame){
                    System.out.println("Close web socket frame. Closing connection.");
                    closeConnection.onCompleted();
                }else {
                    System.out.println("Receive " + webSocketFrame.toString());
                    int n = counter.addAndGet(1);
                    System.out.println("N = " + n);
                    if (n > 5) {
                        System.out.println("Closing connection");
                        closeConnection.onCompleted();
                    }
                }
            });

            return closeConnection;
        }).enableWireLogging(LogLevel.INFO).build().startAndWait();
    }
}
