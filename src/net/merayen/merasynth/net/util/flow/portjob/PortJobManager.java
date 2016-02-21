package net.merayen.merasynth.net.util.flow.portjob;

import java.util.ArrayList;
import java.util.List;

import net.merayen.merasynth.netlist.datapacket.DataPacket;

/**
 * Manages jobs on ports on a node.
 * TODO Delete?
 */
public class PortJobManager<T extends PortJob> {
	private List<T> jobs = new ArrayList<T>();

	public void handle(String port_name, DataPacket dp) {
		for(PortJob job : jobs)
			if(job.port_name.equals(port_name))
				job.onReceive(dp);
	}

	public void add(T job) {
		job.setPJM(this);
		jobs.add(job);
	}

	public void remove(T job) { // Called by PortJob
		jobs.remove(job);
	}

	public boolean isDone() {
		return jobs.size() == 0;
	}
}
