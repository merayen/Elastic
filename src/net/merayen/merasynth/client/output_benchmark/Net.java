package net.merayen.merasynth.client.output_benchmark;

import java.util.Collection;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AllowNewSessionsRequest;
import net.merayen.merasynth.netlist.util.AudioNode;
import net.merayen.merasynth.util.AverageStat;

public class Net extends AudioNode<Processor> {
	Stats stats = new Stats();

	private int sample_rate = 44100; // TODO get this from event
	private int request_size = 1024;
	private long last_update;

	private AverageStat<Integer> avg_throughput = new AverageStat<Integer>(10);

	public Net(Supervisor supervisor) {
		super(supervisor, Processor.class);
	}

	public double onUpdate() {
		if(this.getPort("input") != null) // hasPort(...) is a dirty hack until we wait for gluenode to finish initing before updating netnode
			requestData();
		return 0.001;
	}

	private void requestData() {
		send("input", new AllowNewSessionsRequest()); // We allow nodes to the left to create new sessions

		for(Processor p : processor_controller.getProcessors()) // Request all sessions for audio
			p.requestData(request_size);
	}

	/**
	 * Called by the processors, whenever they receive audio.
	 * Updates the statistics and tries to request, if possible
	 */
	public void notifyReceived() {
		// Update statistics
		Collection<Processor> ps = processor_controller.getProcessors();
		int length = ps.size();
		for(Processor p : ps) { // Request all sessions for audio
			stats.samples_received += p.samples_received / length;
			p.samples_received = 0;
		}

		long t = System.currentTimeMillis();
		if(last_update + 500 < t) {
			double time_diff = (t - last_update) / 1000.0;
			avg_throughput.add((int)((stats.samples_received - stats.last_samples_received) / time_diff));
			stats.avg_samples_received = (int)avg_throughput.getAvg();
			stats.avg_playback_speed = stats.avg_samples_received / (float)sample_rate;
			stats.last_samples_received = stats.samples_received;
			last_update = t;
		}

		requestData();
	}
}
