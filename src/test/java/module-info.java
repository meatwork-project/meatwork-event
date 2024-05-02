/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
module com.meatwork.event.test {

	requires com.meatwork.event;
	requires org.junit.jupiter.api;
	requires com.meatwork.core;
	requires org.mockito.junit.jupiter;
	requires org.mockito;

	exports com.meatwork.event.test.service;

	opens com.meatwork.event.test;
}