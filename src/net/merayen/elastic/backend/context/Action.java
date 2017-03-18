package net.merayen.elastic.backend.context;

import net.merayen.elastic.backend.logicnodes.Environment;

public abstract class Action {
	protected BackendContext backend_context;
	protected Environment env;

	protected Action() {}

	protected abstract void run();

	public final void start(BackendContext bc) {
		backend_context = bc;
		env = bc.env;
		run();
	}
}
