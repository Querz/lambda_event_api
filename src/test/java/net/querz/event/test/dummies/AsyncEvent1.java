package net.querz.event.test.dummies;

import net.querz.event.Cancellable;
import net.querz.event.Event;

import java.util.UUID;

public class AsyncEvent1 extends Event implements Cancellable {

	private Thread thread;
	public UUID id;
	private boolean cancelled;

	public AsyncEvent1(UUID id, boolean async) {
		super(async);
		this.id = id;
	}

	public Thread getThread() {
		return thread;
	}

	void setThread() {
		thread = Thread.currentThread();
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
}
