package com.github.lzenczuk.rxcrex.wsdispatcher;

import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * Created by dev on 09/06/17.
 */
public class TickProducer implements Producer {

    private Observable<String> tickGenerator = Observable.interval(1, TimeUnit.SECONDS).map(Object::toString);

    @Override
    public String getName() {
        return "tick";
    }

    @Override
    public Observable<String> getSource() {
        return tickGenerator;
    }
}
