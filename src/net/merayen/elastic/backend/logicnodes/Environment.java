package net.merayen.elastic.backend.logicnodes;

import net.merayen.elastic.backend.data.DataManager;
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
	public final DataManager data_manager;

	public Environment(Mixer mixer, Synchronization synchronization, DataManager dm) {
		this.synchronization = synchronization;
		this.mixer = mixer;
		this.data_manager = dm;
	}
}
