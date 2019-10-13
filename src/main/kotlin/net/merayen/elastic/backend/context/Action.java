package net.merayen.elastic.backend.context;

public abstract class Action {
	protected JavaBackend javaBackend;
	protected JavaBackend.Environment env;

	protected Action() {}

	protected abstract void run();

	public final void start(JavaBackend javaBackend) {
		synchronized (javaBackend.getEnvironment().getProject()) {
			this.javaBackend = javaBackend;
			env = javaBackend.getEnvironment();
			run();
		}
	}
}
