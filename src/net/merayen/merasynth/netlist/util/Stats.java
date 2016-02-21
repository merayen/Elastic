package net.merayen.merasynth.netlist.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.merayen.merasynth.net.util.flow.portmanager.ManagedPort;
import net.merayen.merasynth.net.util.flow.portmanager.ProcessorManagedPort;
import net.merayen.merasynth.process.AudioProcessor;

public class Stats {
	public class PortStats {
		public long bytes_transferred;
		public int active; // How many ports that are active
		public int total; // Count of this port (== count of processors)
	}

	public final Map<String, PortStats> ports = new HashMap<>();

	public final int processor_count;

	Stats(AudioNode<?> node) {
		Collection<?> processors = node.processor_controller.getProcessors();
		processor_count = processors.size();

		for(ManagedPort p : node.port_manager.getPorts()) {
			PortStats ps = new PortStats();

			if(p.state != null)
				ps.bytes_transferred = p.state.total_bytes_transferred;

			ps.total = processor_count;

			ports.put(p.port_name, ps);
		}

		for(Object p : processors) {
			if(p instanceof AudioProcessor) {
				AudioProcessor ap = (AudioProcessor)p;
				for(ProcessorManagedPort pmp : ap.ports.getPorts()) {
					PortStats ps = ports.get(pmp.managed_port.port_name);
					if(pmp.isActive())
						ps.active++;
				}
			}
		}
	}
}
