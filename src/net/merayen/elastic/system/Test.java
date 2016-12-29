package net.merayen.elastic.system;

import java.util.ArrayList;

import net.merayen.elastic.Config;
import net.merayen.elastic.backend.interfacing.AbstractDevice;
import net.merayen.elastic.backend.interfacing.devicetypes.AudioOutputDevice;
import net.merayen.elastic.backend.logicnodes.Environment;
import net.merayen.elastic.backend.mix.Mixer;
import net.merayen.elastic.backend.mix.Synchronization;
import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster.Message;

public class Test {
	public static void test() {
		new Test();
	}

	private ElasticSystem system;

	private final int SAMPLE_RATE = 44100;
	private final int DEPTH = 16;
	private final int BUFFER_SIZE = 256;

	int fires;
	long start = System.currentTimeMillis();
	long ispinne;
	private Test() {
		Environment env = createEnvironment(() -> {
			system.sendMessageToBackend(new ProcessMessage());

			//fires++;
			//if(ispinne++ % 100 == 0)
			//	System.out.printf("Fires/sec: %f / %d\n", (fires) / ((System.currentTimeMillis() - start) / 1000.0), SAMPLE_RATE / BUFFER_SIZE);
		});

		system = new ElasticSystem(env);

		env.synchronization.start();

		try {
			Thread.sleep(1000); // Give some time for the UI to create the viewports and the NodeView
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		ArrayList<CreateNodeMessage> nodes = new ArrayList<>();

		system.listen(new ElasticSystem.IListener() {
			@Override
			public void onMessageToUI(Message message) {
				if(message instanceof CreateNodeMessage) {
					nodes.add((CreateNodeMessage)message);
				} else if(message instanceof ResetNetListMessage) {
					nodes.clear();
				} else if(message instanceof ProcessMessage) {
					ProcessMessage pm = (ProcessMessage)message;
					System.out.printf("Process response\n");
				}

				//System.out.println("Message from Backend to UI: " + message);
			}

			@Override
			public void onMessageToBackend(Message message) {
				if(Config.processor.debug.messages)
					System.out.println("Message from UI to Backend: " + message);
			}
		});

		system.sendMessageToBackend(new CreateNodeMessage("test", 100));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1));
		system.sendMessageToBackend(new CreateNodeMessage("output", 1));
		system.sendMessageToBackend(new CreateNodeMessage("mix", 1));
		system.sendMessageToBackend(new CreateNodeMessage("signalgenerator", 1));

		waitFor(() -> nodes.size() == 6);

		// Place the nodes
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(0).node_id, "ui.java.translation.y", 300f));
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(1).node_id, "ui.java.translation.x", 50f));
		system.sendMessageToBackend(new NodeParameterMessage(nodes.get(3).node_id, "ui.java.translation.x", 200f));

		// Connect signal generator to output
		system.sendMessageToBackend(new NodeConnectMessage(nodes.get(1).node_id, "output", nodes.get(3).node_id, "input"));

		final long t = System.currentTimeMillis() + 1 * 1000;
		waitFor(() -> System.currentTimeMillis() > t);

		// Test dumping
		String dump = system.dump().toJSONString();
		System.out.println(dump);

		/*system.end();

		// Test restoring the dump
		try {
			system = ElasticSystem.load((JSONObject)new org.json.simple.parser.JSONParser().parse(dump));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}*/

		// Now just run
		startProcessing();

		system.end();
	}

	private void startProcessing() {
		/*Mixer mixer = new Mixer();

		String output_device = getFirstOutputDevice(mixer);

		mixer.reconfigure(SAMPLE_RATE, 2, DEPTH);

		sync = new Synchronization(mixer, SAMPLE_RATE, BUFFER_SIZE, new Synchronization.Handler() {
			private long last = System.currentTimeMillis();
			private long start = System.currentTimeMillis();
			private float[] audio = SoundTest.makeSound(SAMPLE_RATE, 15f, new float[]{1000}, 1f);
			private float[] output = new float[BUFFER_SIZE];
			private int position;

			int asked;

			@Override
			public void needData() {
				asked++;
				if(asked % 10 == 0)
					;//System.out.printf("Frame duration: %d ms, total duration: %d ms\n", System.currentTimeMillis() - last, System.currentTimeMillis() - start);
				last = System.currentTimeMillis();

				system.sendMessageToBackend(new ProcessMessage());

				for(int i = 0; i < BUFFER_SIZE; i++)
					output[i] = audio[position++];

				mixer.send(output_device, new Audio(new float[][]{
					output,
					output
				}));

				mixer.dispatch(BUFFER_SIZE);

				sync.push();
				//if(asked % 5 != 0) sync.push();
			}

			@Override
			public void behind() {
				System.out.println("Lagging behind");
				//sync.push();
			}
		});*/

		final long u = System.currentTimeMillis() + 3600 * 1000;

		waitFor(new Func() {
			long s = System.currentTimeMillis();

			@Override
			public boolean noe() {
				/*if(s + 1000 < System.currentTimeMillis()) {
					s = System.currentTimeMillis();
					system.sendMessageToBackend(new ProcessMessage());
				}*/
				return System.currentTimeMillis() > u;
			}
		});
	}

	class omg {Synchronization s;}
	private Environment createEnvironment(Runnable needDataFunc) {
		Mixer mixer = new Mixer();
		mixer.reconfigure(SAMPLE_RATE, 1, DEPTH);

		final omg o = new omg();
		Synchronization sync = new Synchronization(mixer, SAMPLE_RATE, BUFFER_SIZE, new Synchronization.Handler() {
			@Override
			public void needData() {
				needDataFunc.run();
			}

			@Override
			public void behind() {
				//System.out.println("Lagging behind");
				//o.s.push();
				//mixer.dispatch(BUFFER_SIZE);
				//sync.push();
			}
		});

		o.s = sync;

		return new Environment(mixer, sync);
	}

	private String getFirstOutputDevice(Mixer mixer) {
		for(AbstractDevice ad : mixer.getAvailableDevices()) { // Test the first output device
			if(ad instanceof AudioOutputDevice) {
				return ad.getID();
			}
		}

		throw new RuntimeException("No output audio device found");
	}

	interface Func {
		public boolean noe();
	}

	private void waitFor(Func func) {
		try {
			while(!func.noe()) {
				system.update();
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
