package net.merayen.elastic.util;

public class FireEvery {
	public interface Handler {
		public void run(float delta);
	}

	private float ms;
	private long last = System.currentTimeMillis();
	private final Handler func;

	public FireEvery(float ms, Handler func) {
		this.ms = ms;
		this.func = func;
	}

	public void update() {
		long delta = System.currentTimeMillis() - last;

		if(delta / 1000f > ms) {
			last = System.currentTimeMillis();
			func.run(delta);
		}
	}
}
