package net.merayen.elastic.backend.logicnodes;

import net.merayen.elastic.backend.data.project.Project;
import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.backend.nodes.LogicEnvironment;

/**
 * Environment that uses the current
 */
@SuppressWarnings("serial")
public class Environment extends LogicEnvironment {

	public Environment(Mixer mixer, Synchronization synchronization, int sample_rate, int buffer_size,
			Project project) {
		super(mixer, synchronization, sample_rate, buffer_size, project);
		// TODO Auto-generated constructor stub
	}} // TODO delete}
