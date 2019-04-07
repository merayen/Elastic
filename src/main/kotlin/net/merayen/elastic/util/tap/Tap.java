package net.merayen.elastic.util.tap;

import java.io.Closeable;

import net.merayen.elastic.util.tap.TapSpreader.Func;

public class Tap<T> implements Closeable {
	private final TapSpreader<T> listener;
	Func<T> func;

	Tap(TapSpreader<T> listener) {
		this.listener = listener;
	}

	public void set(Func<T> func) {
		this.func = func;
	}

	public void close() {
		listener.remove(this);
	}
}
