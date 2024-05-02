package com.meatwork.event.internal;

import com.meatwork.event.api.Observe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
public class Event {

	private final List<Consumer<Object>> consumerList = new ArrayList<>();
	private final String topic;
	private final boolean isSynchronizer;

	public Event(String topic, boolean isSynchronizer) {
		this.topic = topic;
		this.isSynchronizer = isSynchronizer;
	}

	public void execute(Object event) {
		consumerList.forEach(it -> it.accept(event));
	}

	public void addConsumer(Consumer<Object> consumer) {
		consumerList.add(consumer);
	}

	public String getTopic() {
		return topic;
	}

	public boolean isSynchronizer() {
		return isSynchronizer;
	}
}
