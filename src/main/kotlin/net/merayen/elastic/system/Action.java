package net.merayen.elastic.system;

public abstract class Action {
	protected ElasticSystem system;

	protected abstract void run();

	public synchronized void start(ElasticSystem es) {
		system = es;
		run();
	}

	protected void waitFor(Func func) {
		try {
			while(!func.run()) {
				system.update();
				synchronized (this) {
					wait(1);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public interface Func {
		boolean run();
	}
}
