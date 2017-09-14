package net.querz.event;

import java.util.function.Consumer;

public class EventManager {

	private EventTree events;

	public EventManager() {
		events = new EventTree();
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

	public void call(Event event) {
		events.execute(event);
	}

	@Override
	public String toString() {
		return events.toString();
	}
}
