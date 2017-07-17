package net.merayen.elastic.backend.architectures.local;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;

public class Debug {
	static String debug(Supervisor supervisor) {
		String r = "";
		r += "Frame state:\n";
		for(LocalProcessor lp : supervisor.processor_list.getAllProcessors()) {
			r += String.format("\t%s/%d (%s):\n", lp.getClass().getSimpleName(), lp.session_id, lp.hashCode());

			for(Map.Entry<String, Inlet> inlet : lp.inlets.entrySet())
				r += String.format("\t\tInlet\t%s: read=%d\n", inlet.getKey(), inlet.getValue().read);

			for(Map.Entry<String, Outlet> outlet : lp.outlets.entrySet())
				r += String.format("\t\tOutlet\t%s: written=%d\n", outlet.getKey(), outlet.getValue().written);
		}

		return r;
	}
}
