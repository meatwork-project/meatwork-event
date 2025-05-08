package com.meatwork.event.api;

import com.meatwork.core.api.di.CDI;
import com.meatwork.core.api.di.Service;
import com.meatwork.core.api.service.ApplicationStartup;
import com.meatwork.core.api.service.MeatworkApplication;
import com.meatwork.event.internal.Event;
import com.meatwork.event.internal.EventGraph;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
@Service
public class EventOnStartup implements ApplicationStartup {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventOnStartup.class);

    @Override
    public void run(Class<?> applicationCls, String[] args) {

        MeatworkApplication annotation = applicationCls.getAnnotation(MeatworkApplication.class);
        if (annotation == null) {
            LOGGER.error("No MeatworkApplication annotation found for main class {}", applicationCls.getName());
            throw new RuntimeException("No MeatworkApplication annotation found for main class " + applicationCls.getName());
        }
        var list = new ArrayList<>(Arrays.asList(annotation.packages()));
        list.add("com.meatwork");
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
