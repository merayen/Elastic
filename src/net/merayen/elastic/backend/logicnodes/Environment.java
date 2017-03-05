package net.merayen.elastic.backend.logicnodes;

import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.backend.nodes.LogicEnvironment;

/**
 * Environment that uses the current
 */
@SuppressWarnings("serial")
public class Environment extends LogicEnvironment {
	public final Synchronization synchronization;
	public final Mixer mixer;
	public final int sample_rate;
	public final int buffer_size;
	//public DataManager data_manager; // TODO add a middle-class that communicates with the DataManager?

	public Environment(Mixer mixer, Synchronization synchronization, int sample_rate, int buffer_size) {
		this.synchronization = synchronization;
		this.mixer = mixer;
		this.sample_rate = sample_rate;
		this.buffer_size = buffer_size;
		//this.data_manager = dm;
	}
}
