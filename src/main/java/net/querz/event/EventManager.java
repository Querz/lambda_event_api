package net.querz.event;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class EventManager {

	private EventTree events = new EventTree();
	private ExecutorService threadpool;

	public EventManager() {
		this.threadpool = Executors.newCachedThreadPool();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> this.threadpool.shutdownNow()));
	}

	public EventManager(ExecutorService threadpool) {
		this.threadpool = threadpool;
	}

	public <T extends Event> EventFunction<T> registerEvent(Consumer<T> function, Class<T> eventClass) {
		return registerEvent(function, eventClass, 0);
	}

	public <T extends Event> EventFunction<T> registerEvent(Consumer<T> function, Class<T> eventClass, int priority) {
		return events.add(function, eventClass, priority);
	}

	public <T extends Event> void unregisterEvent(EventFunction<T> functionData) {
		events.remove(functionData);
	}


	public Future<Boolean> call(Event event) {
		if (event.isAsync()) {
			return threadpool.submit(new FutureEvent<>(event));
		} else {
			events.execute(event);
		}
		return null;
	}

	@Override
	public String toString() {
		return events.toString();
	}

	class FutureEvent<T extends Event> implements Callable<Boolean> {
		T event;

		FutureEvent(T event) {
			this.event = event;
		}

		@Override
		public Boolean call() throws Exception {
			events.execute(event);
			return event instanceof Cancellable && ((Cancellable) event).isCancelled();
		}
	}
}
