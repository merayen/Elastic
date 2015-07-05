package net.merayen.merasynth.ui.event;

public class DelayEvent implements IEvent {
	/*
	 * Postpones execution to next frame.
	 */
	private Runnable runnable;

	public DelayEvent(Runnable runnable) {
		this.runnable = runnable;
	}

	public void run() {
		this.runnable.run();
		this.runnable = null;
	}
}
