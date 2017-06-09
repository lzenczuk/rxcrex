package com.github.lzenczuk.rxcrex.wsdispatcher;

import org.junit.Test;
import rx.*;
import rx.subjects.PublishSubject;

/**
 * Created by dev on 09/06/17.
 */
public class WsDispatcherTest {

    @Test
    public void test() throws InterruptedException {
        PublishSubject<WsConnection> connectionsObservable = PublishSubject.create();
        PublishSubject<Producer> producersObservable = PublishSubject.create();

        WsDispatcher wsDispatcher = new WsDispatcher(connectionsObservable, producersObservable);

        PublishSubject<String> connectionInput = PublishSubject.<String>create();
        PublishSubject<String> connectionOutput = PublishSubject.<String>create();

        connectionOutput.subscribe(s -> System.out.println("Receive on output: "+s));

        WsConnection connection = new WsConnection(connectionInput, connectionOutput);

        connectionsObservable.onNext(connection);

        connectionInput.onNext("subscribe_tick");
        Thread.sleep(5000);
        connectionInput.onNext("unsubscribe_tick");
        Thread.sleep(5000);

        producersObservable.onNext(new TickProducer());

        connectionInput.onNext("subscribe_tick");
        Thread.sleep(5000);
        connectionInput.onNext("unsubscribe_tick");
        Thread.sleep(5000);

        System.out.println("... done");
    }

}
