/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
module com.meatwork.event {

	requires com.meatwork.core;
	requires org.slf4j;
	requires org.reflections;

	exports com.meatwork.event.api;
	exports com.meatwork.event.internal to com.meatwork.event.test;
}