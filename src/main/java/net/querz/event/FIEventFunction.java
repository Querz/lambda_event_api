package net.querz.event;

@FunctionalInterface
public interface FIEventFunction<T extends Event> {
	void execute(T event);
}
