package com.github.lzenczuk.rxcrex.wsdispatcher;

import rx.Observable;

/**
 * Created by dev on 09/06/17.
 */
public interface Producer {
    String getName();
    Observable<String> getSource();
}
