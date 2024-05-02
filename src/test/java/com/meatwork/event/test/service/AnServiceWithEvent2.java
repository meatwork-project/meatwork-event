package com.meatwork.event.test.service;

import com.meatwork.core.api.di.Service;
import com.meatwork.event.api.Observe;

/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
@Service
public class AnServiceWithEvent2 {

	@Observe
	public void totoEvent(TotoEvent totoEvent) {}

}
