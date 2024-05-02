package com.meatwork.event.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
public class EventGraph {

	private static final Map<String, Event> map = new HashMap<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(EventGraph.class);

	private EventGraph() {}

	public static Event get(String topic) {
		return map.get(topic);
	}

	public static void subscribe(Event factory) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"event subscribed: {}",
					factory.getTopic()
			);
		}
		map.put(factory.getTopic(), factory);
	}



}
