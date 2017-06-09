package com.github.lzenczuk.rxcrex.wsdispatcher;

import rx.Observable;
import rx.Observer;

/**
 * Created by dev on 09/06/17.
 */
public class WsConnection {
    private final Observable<String> input;
    private final Observer<String> output;

    public WsConnection(Observable<String> input, Observer<String> output) {
        this.input = input;
        this.output = output;
    }

    public Observable<String> getInput() {
        return input;
    }

    public Observer<String> getOutput() {
        return output;
    }
}
