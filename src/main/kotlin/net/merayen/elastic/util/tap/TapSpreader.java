package net.merayen.elastic.util.tap;

import java.util.ArrayList;
import java.util.List;

public class TapSpreader<T> {
	public static abstract class Func<U> {
		public abstract void receive(U obj);
	}

	private final List<Tap<T>> upcoming = new ArrayList<>();
	private final List<Tap<T>> removing = new ArrayList<>();
	private final List<Tap<T>> list = new ArrayList<>();

	public Tap<T> create() {
		Tap<T> t = new Tap<>(this);

		synchronized (upcoming) {
			upcoming.add(t);
		}

		return t;
	}

	public synchronized void push(T object) {
		synchronized (upcoming) {
			while(!upcoming.isEmpty())
				list.add(upcoming.remove(0));

			while(!removing.isEmpty())
				list.remove(removing.remove(0));
		}

		for(Tap<T> tap : list)
			if(tap.func != null)
				tap.func.receive(object);
	}

	synchronized void remove(Tap<T> tap) {
		removing.add(tap);
	}
}
