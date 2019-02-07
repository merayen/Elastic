package net.merayen.elastic.backend.context;

import net.merayen.elastic.backend.logicnodes.Environment;

public abstract class Action {
	protected BackendContext backendContext;
	protected Environment env;

	protected Action() {}

	protected abstract void run();

	public final void start(BackendContext backendContext) {
		synchronized (backendContext.env.project) {
			this.backendContext = backendContext;
			env = backendContext.env;
			run();
		}
	}
}
