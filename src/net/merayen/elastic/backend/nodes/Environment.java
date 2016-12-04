package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;

/**
 * Common 
 */
public class Environment {
	public final Mixer mixer;
	public final Synchronization synchronization;

	public Environment(Mixer mixer, Synchronization synchronization) {
		this.mixer = mixer;
		this.synchronization = synchronization;
	}
}
