package net.querz.event.test.dummies;

import net.querz.event.test.EventCallCollector;

public class Listener2 {

	public void onAsyncEvent1(AsyncEvent1 event) {
		System.out.println("called AsyncEvent1 (async: " + event.isAsync() + ")");
		event.setThread();
		EventCallCollector.add(event.id, "AsyncEvent1", event);
	}

	public void onAsyncEvent1Cancel(AsyncEvent1 event) {
		System.out.println("called AsyncEvent1Cancel (async: " + event.isAsync() + ")");
		event.setThread();
		EventCallCollector.add(event.id, "AsyncEvent1Cancel", event);
		event.setCancelled(true);
	}
}
