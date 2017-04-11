package net.merayen.elastic.backend.nodes;

import java.util.HashMap;

import net.merayen.elastic.backend.data.project.Project;
import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;

public abstract class LogicEnvironment extends HashMap<String, Object> {
	public final Synchronization synchronization;
	public final Mixer mixer;
	public final int sample_rate;
	public final int buffer_size;
	public final Project project; // TODO add a middle-class that communicates with the DataManager?

	public LogicEnvironment(Mixer mixer, Synchronization synchronization, int sample_rate, int buffer_size, Project project) {
		this.synchronization = synchronization;
		this.mixer = mixer;
		this.sample_rate = sample_rate;
		this.buffer_size = buffer_size;
		this.project = project;
	}
}
