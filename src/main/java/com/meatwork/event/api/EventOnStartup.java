package com.meatwork.event.api;

import com.meatwork.core.api.di.CDI;
import com.meatwork.core.api.di.Service;
import com.meatwork.core.api.service.ApplicationStartup;
import com.meatwork.event.internal.Event;
import com.meatwork.event.internal.EventGraph;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
@Service
public class EventOnStartup implements ApplicationStartup {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventOnStartup.class);

    @Override
    public void run(String[] args) throws Exception {
        List<String> list = ModuleLayer.boot().modules().stream().flatMap(it -> it.getPackages().stream()).toList();
        Reflections reflections = new Reflections(list);
        Set<Class<?>> subTypesOf = reflections.getTypesAnnotatedWith(Service.class);
        for (Class<?> aClass : subTypesOf) {
            for (Method declaredMethod : aClass.getDeclaredMethods()) {
                if (declaredMethod.isAnnotationPresent(Observe.class)) {
                    Parameter[] parameters = declaredMethod.getParameters();

                    if (parameters.length != 1) {
                        LOGGER.error(
                            "parameters cannot be more than 1 or less than 1 for class {}:{}",
                            aClass.getName(),
                            declaredMethod.getName()
                        );
                        throw new RuntimeException(
                            "parameters cannot be more than 1 or less than 1 %s:%s".formatted(
                                    aClass.getName(),
                                    declaredMethod.getName()
                                )
                        );
                    }
                    Parameter parameter = parameters[0];
                    String topic = parameter.getType().getName();

                    Event factory = EventGraph.get(topic);
                    if (factory == null) {
                        boolean isSynchronizerEvent = parameter.getType().isAnnotationPresent(Synchronizer.class);
                        Event event = new Event(topic, isSynchronizerEvent);
                        event.addConsumer(createConsumer(aClass, declaredMethod));
                        EventGraph.subscribe(event);
                    } else {
						factory.addConsumer(createConsumer(aClass, declaredMethod));
                    }
                }
            }
        }
    }

    private Consumer<Object> createConsumer(Class<?> aClass, Method method) {
        return object -> {
            try {
                method.invoke(CDI.get(aClass), object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("cannot invoke method {}:{} {}", aClass.getName(), method.getName(), e.getMessage());
                throw new RuntimeException(e);
            }
        };
    }
}
