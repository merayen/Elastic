package net.merayen.elastic.backend.context;

import java.util.Map;

import net.merayen.elastic.backend.data.project.Project;
import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.system.ElasticSystem;
import net.merayen.elastic.system.intercom.ProcessMessage;
import net.merayen.elastic.system.intercom.backend.InitBackendMessage;

class Env {
	Environment env;

	private static class Hack {Synchronization sync;}
	static Environment create(ElasticSystem system, InitBackendMessage message) {
		Mixer mixer = new Mixer();
		mixer.reconfigure(message.sample_rate, 1, message.depth);
		Hack hack = new Hack();

		hack.sync = new Synchronization(mixer, message.sample_rate, message.buffer_size, new Synchronization.Handler() {
			int i = 0;

			@Override
			public void needData() {
				system.sendMessageToBackend(new ProcessMessage());
				if(i++ % 20 == 0) {
					for(Map.Entry<String, AbstractDevice.Statistics> s : mixer.getStatistics().entrySet())
						System.out.println("Mixer diag: [" + s.getKey() + "] " + s.getValue().describe());
					System.out.println("Awaiting data avg: " + hack.sync.statistics.awaiting_data.info());
				}
			}

			@Override
			public void behind() {
				//System.out.println("Lagging behind");
			}
		});

		return new Environment(mixer, hack.sync, message.sample_rate, message.buffer_size, new Project(message.project_path));
	}
}
