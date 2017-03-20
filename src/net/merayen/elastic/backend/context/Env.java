package net.merayen.elastic.backend.context;

import net.merayen.elastic.backend.data.project.Project;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.system.ElasticSystem;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;

class Env {
	Environment env;

	static Environment create(ElasticSystem system, InitBackendMessage message) {
		Mixer mixer = new Mixer();
		mixer.reconfigure(message.sample_rate, 1, message.depth);

		Synchronization sync = new Synchronization(mixer, message.sample_rate, message.buffer_size, new Synchronization.Handler() {
			@Override
			public void needData() {
				system.sendMessageToBackend(new ProcessMessage());
			}

			@Override
			public void behind() {
				//System.out.println("Lagging behind");
			}
		});

		return new Environment(mixer, sync, message.sample_rate, message.buffer_size, new Project(message.project_path));
	}
}
