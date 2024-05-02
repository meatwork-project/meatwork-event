package com.meatwork.event.api;

import com.meatwork.event.internal.Event;
import com.meatwork.event.internal.EventGraph;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
public final class Broadcaster {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void broadcast(Object object) {
        Event event = EventGraph.get(object.getClass().getName());
        if (event.isSynchronizer()) {
            event.execute(object);
        } else {
            executorService.submit(() -> event.execute(object));
        }
    }
}
