package com.meatwork.event.test;

import com.meatwork.event.api.Broadcaster;
import com.meatwork.event.api.EventOnStartup;
import com.meatwork.event.internal.Event;
import com.meatwork.event.internal.EventGraph;
import com.meatwork.event.test.service.AnServiceWithEvent;
import com.meatwork.event.test.service.AnServiceWithEvent2;
import com.meatwork.event.test.service.TotoEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/*
 * Copyright (c) 2016 Taliro.
 * All rights reserved.
 */
@ExtendWith(MockitoExtension.class)
public class EventTest {

    @Mock
    private AnServiceWithEvent anServiceWithEvent;

    @Mock
    private AnServiceWithEvent2 anServiceWithEvent2;

	@Test
	public void testEventIsSaved() throws Exception {
		new EventOnStartup().run(ApplicationMock.class, null);
		Event event = EventGraph.get(TotoEvent.class.getName());
		Assertions.assertNotNull(event);
	}

    @Test
    public void testEventIsCalled() {
	    Event event = new Event(
			    TotoEvent.class.getName(),
			    true
	    );
		event.addConsumer(object -> anServiceWithEvent.totoEvent((TotoEvent) object));
		event.addConsumer(object -> anServiceWithEvent2.totoEvent((TotoEvent) object));
	    EventGraph.subscribe(event);
        Broadcaster.broadcast(new TotoEvent("i'm toto"));
        Mockito.verify(anServiceWithEvent, Mockito.times(1)).totoEvent(Mockito.any());
        Mockito.verify(anServiceWithEvent2, Mockito.times(1)).totoEvent(Mockito.any());
    }
}
