package com.github.lzenczuk.rxcrex.wsdispatcher;

import rx.Observable;
import rx.Subscription;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by dev on 09/06/17.
 */
public class WsDispatcher {
    private final Observable<WsConnection> connectionsStream;

    private final Pattern subscribePattern = Pattern.compile("subscribe_(\\w+)");
    private final Pattern unSubscribePattern = Pattern.compile("unsubscribe_\\w+");
    private final Pattern commandPattern = Pattern.compile("command_\\w+");

    public WsDispatcher(Observable<WsConnection> connectionsStream, Observable<Producer> producersStream) {
        this.connectionsStream = connectionsStream;

        //ConcurrentHashMap<String, Producer> producers = new ConcurrentHashMap<>();

        Observable<HashMap<String, Producer>> producers = producersStream.scan(new HashMap<String, Producer>(), (producersMap, producer) -> {
            producersMap.put(producer.getName(), producer);
            return producersMap;
        });

        connectionsStream.subscribe(wsConnection -> {

            Observable<HashMap<String, Subscription>> subscriptions = wsConnection.getInput()
                    .filter(message -> subscribePattern.matcher(message).matches())
                    .map(s -> s.split("_")[1])
                    .withLatestFrom(producers, (producerName, producerMap) -> {

                        if (producerMap.containsKey(producerName)) {
                            Subscription subscription = producerMap.get(producerName).getSource().subscribe(wsConnection.getOutput());
                            System.out.println("Subscribed to "+producerName);
                            return Optional.of(new ProducerSubscription(producerName, subscription));
                        }

                        System.out.println("Producer "+producerName+" not found");
                        return Optional.<ProducerSubscription>empty();
                    })
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .scan(new HashMap<String, Subscription>(), (producerSubscriptionsMap, ps) -> {
                        producerSubscriptionsMap.put(ps.getProducerName(), ps.getSubscription());
                        return producerSubscriptionsMap;
                    });

            wsConnection.getInput()
                    .filter(message -> unSubscribePattern.matcher(message).matches())
                    //.delay(100, TimeUnit.MILLISECONDS) - maybe should be here?
                    .map(s -> s.split("_")[1])
                    .withLatestFrom(subscriptions, (producerName, producerSubscriptionsMap) -> {
                        if(producerSubscriptionsMap.containsKey(producerName)){
                            producerSubscriptionsMap.get(producerName).unsubscribe();
                            return "Un subscribed to "+producerName;
                        }

                        return "Subscription to "+producerName+" not found";
                    })
                    .subscribe(System.out::println);

            /*wsConnection.getInput().subscribe(command -> {
                Matcher subscribeMatcher = subscribePattern.matcher(command);
                if(subscribeMatcher.matches()){
                    String producerName = subscribeMatcher.group(1);
                    if(producers.containsKey(producerName)){
                        producers.get(producerName).getSource().subscribe(wsConnection.getOutput());
                    }else{
                        wsConnection.getOutput().onNext("No such producer: "+producerName);
                    }
                }else if(unSubscribePattern.matcher(command).matches()){
                    System.out.println("unsubscribe");
                }else if(commandPattern.matcher(command).matches()){
                    System.out.println("command");
                }else{
                    System.out.println("unknown");
                }
            });*/
        });


    }

    private class ProducerSubscription{
        private final String producerName;
        private final Subscription subscription;

        public ProducerSubscription(String producerName, Subscription subscription) {
            this.producerName = producerName;
            this.subscription = subscription;
        }

        public String getProducerName() {
            return producerName;
        }

        public Subscription getSubscription() {
            return subscription;
        }
    }
}
