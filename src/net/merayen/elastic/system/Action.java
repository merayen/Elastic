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
}
