package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.backend.architectures.local.lets.Outlet;

import java.util.Map;

public class Debug {
	static String debug(Supervisor supervisor) {
		StringBuilder r = new StringBuilder();
		r.append("Frame state:\n");
		for(LocalProcessor lp : supervisor.processor_list.getAllProcessors()) {
			boolean issue = false;

			StringBuilder s = new StringBuilder(String.format("\t%s/%d (%s):\n", lp.getClass(), lp.session_id, lp.hashCode()));

			for(Map.Entry<String, Outlet> outlet : lp.outlets.entrySet()) {
				if(!outlet.getValue().satisfied()) {
					issue = true;
					r.append(String.format("\t\tOutlet\t%s\n", outlet.getKey()));
				}
			}

			if(issue)
				r.append(s);
		}

		return r.toString();
	}
}
