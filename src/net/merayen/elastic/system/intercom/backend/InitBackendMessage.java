package net.merayen.elastic.system.intercom.backend;

/**
 * Initializes the backend. If sent again, backend won't restart, but will reconfigure itself
 * dynamically based on the given parameters.
 */
public class InitBackendMessage extends BackendMessage {
	public final int sample_rate; // e.g 44100
	public final int depth; // 8, 16, 24
	public final int buffer_size; // 256, 512...
	public final String project_path;

	public InitBackendMessage(int sample_rate, int depth, int buffer_size, String project_path) {
		this.sample_rate = sample_rate;
		this.depth = depth;
		this.buffer_size = buffer_size;
		this.project_path = project_path;
	}
}
