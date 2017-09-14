package net.querz.event;

import java.util.function.Consumer;

public class EventFunction<T extends Event> implements Comparable<EventFunction> {
	Consumer<T> function;
	Class<? extends Event> eventClass;
	private int priority;

	EventFunction(Consumer<T> function, Class<T> eventClass, int priority) {
		this.function = function;
		this.eventClass = eventClass;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	//sort by priority
	@Override
	public int compareTo(EventFunction e) {
		return Integer.compare(priority, e.priority);
	}

	@Override
	public String toString() {
		return "{function=" + function.getClass().getSimpleName() + "; priority=" + priority + "}";
	}
}
