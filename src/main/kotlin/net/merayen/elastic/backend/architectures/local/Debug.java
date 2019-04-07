package net.merayen.elastic.backend.architectures.local;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;

public class Debug {
	static String debug(Supervisor supervisor) {
		StringBuilder r = new StringBuilder();
		r.append("Frame state:\n");
		for(LocalProcessor lp : supervisor.processor_list.getAllProcessors()) {
			boolean issue = false;

			StringBuilder s = new StringBuilder(String.format("\t%s/%d (%s):\n", lp.getClass(), lp.session_id, lp.hashCode()));

			for(Map.Entry<String, Inlet> inlet : lp.inlets.entrySet()) {
				int read = inlet.getValue().read;
				if(read != supervisor.buffer_size) {
					issue = true;
					s.append(String.format("\t\tInlet\t%s: read=%d\n", inlet.getKey(), read));
				}
			}

			for(Map.Entry<String, Outlet> outlet : lp.outlets.entrySet()) {
				int written = outlet.getValue().written;
				if(written != supervisor.buffer_size) {
					issue = true;
					r.append(String.format("\t\tOutlet\t%s: written=%d\n", outlet.getKey(), written));
				}
			}

			if(issue)
				r.append(s);
		}

		return r.toString();
	}
}
