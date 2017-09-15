package net.querz.event.test;

import junit.framework.TestCase;
import net.querz.event.Event;
import net.querz.event.EventFunction;
import net.querz.event.EventManager;
import net.querz.event.test.dummies.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EventTest extends TestCase {

	public void testEventManager() throws ExecutionException, InterruptedException {
		EventManager m = new EventManager();
		Listener1 l1 = new Listener1();
		EventFunction<Event1> fData1 = m.registerEvent(l1::onEvent1, Event1.class, 0);
		Event1 e1 = new Event1(UUID.randomUUID(), "test1");
		m.call(e1);
		EventCallCollector.assertEventExists(e1.id, "Event1", e1);

		//skip parent event to reach another parent event
		m.registerEvent(l1::onSubSubEvent1, SubSubEvent1.class, 0);
		m.registerEvent(l1::onSubEvent2, SubEvent2.class, 0);
		SubSubEvent1 sse1 = new SubSubEvent1(UUID.randomUUID(), "test2");
		m.call(sse1);
		EventCallCollector.assertEventExists(sse1.id, "SubSubEvent1", sse1);
		EventCallCollector.assertEventExists(sse1.id, "Event1", sse1);
		EventCallCollector.assertEventDoesNotExist(sse1.id, "SubEvent2");
		EventCallCollector.assertEventDoesNotExist(sse1.id, "SubEvent1");

		//unregistering event
		m.unregisterEvent(fData1);
		Event1 e2 = new Event1(UUID.randomUUID(), "test3");
		m.call(e2);
		EventCallCollector.assertEventDoesNotExist(e2.id, "Event1");

		//event with higher priority will be called after an event with lower priority
		m.registerEvent(l1::onEvent1, Event1.class, 1);
		m.registerEvent(l1::onEvent1_1, Event1.class, 0);
		Event1 e3 = new Event1(UUID.randomUUID(), "test4");
		m.call(e3);
		EventCallCollector.assertCallOrder(e3.id, "Event1_1", "Event1", e3);

		//async event
		Listener2 l2 = new Listener2();
		EventFunction<AsyncEvent1> fData2 = m.registerEvent(l2::onAsyncEvent1, AsyncEvent1.class, 0);

		AsyncEvent1 ae1 = new AsyncEvent1(UUID.randomUUID(), true);
		Future<Boolean> f1 = m.call(ae1);
		boolean cancelled = f1.get();
		assertFalse(cancelled);
		assertNotNull(ae1.getThread());
		assertNotSame(Thread.currentThread(), ae1.getThread());
		EventCallCollector.assertEventExists(ae1.id, "AsyncEvent1", ae1);

		m.unregisterEvent(fData2);
		m.registerEvent(l2::onAsyncEvent1Cancel, AsyncEvent1.class, 0);

		AsyncEvent1 ae2 = new AsyncEvent1(UUID.randomUUID(), true);
		Future<Boolean> f2 = m.call(ae2);
		cancelled = f2.get();
		assertTrue(cancelled);
		assertNotNull(ae2.getThread());
		assertNotSame(Thread.currentThread(), ae2.getThread());
		EventCallCollector.assertEventExists(ae2.id, "AsyncEvent1Cancel", ae2);

		AsyncEvent1 ae1Sync = new AsyncEvent1(UUID.randomUUID(), false);
		m.call(ae1Sync);
		assertEquals(Thread.currentThread(), ae1Sync.getThread());

		System.out.println(m);
	}
}
