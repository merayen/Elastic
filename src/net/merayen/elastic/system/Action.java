package net.merayen.elastic.system;

import net.merayen.elastic.backend.logicnodes.Environment;

public abstract class Action {
	protected ElasticSystem system;

	protected abstract void run();

	public synchronized void start(ElasticSystem es) {
		system = es;
		run();
	}

	protected Environment getEnvironment() {
		return system.backend.getEnvironment();
	}

	protected void waitFor(Func func) {
		try {
			while(!func.run()) {
				system.update();
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public interface Func {
		boolean run();
	}
}
