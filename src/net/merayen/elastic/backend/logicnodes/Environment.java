package net.merayen.elastic.backend.logicnodes;

import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.backend.nodes.LogicEnvironment;
import net.merayen.elastic.backend.storage.resource.ResourceManager;

/**
 * Environment that uses the current
 */
@SuppressWarnings("serial")
public class Environment extends LogicEnvironment {
	public final Synchronization synchronization;
	public final Mixer mixer;
	public final ResourceManager resource_manager;;

	public Environment(Mixer mixer, Synchronization synchronization, ResourceManager rm) {
		this.synchronization = synchronization;
		this.mixer = mixer;
		this.resource_manager = rm;
	}
}
