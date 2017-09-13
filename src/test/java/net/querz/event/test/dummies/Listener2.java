package net.querz.event.test.dummies;

import net.querz.event.test.EventCallCollector;

public class Listener2 {

	public void onAsyncEvent1(AsyncEvent1 event) {
		event.setThread();
		System.out.println("called AsyncEvent1 (async: " + event.isAsync() + ")");
		EventCallCollector.add(event.id, "AsyncEvent1", event);
	}
}
