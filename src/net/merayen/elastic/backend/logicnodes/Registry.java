package net.merayen.elastic.backend.logicnodes;

import java.util.ArrayList;
import java.util.List;

public class Registry {
	public final static List<String> nodes = new ArrayList<String>();
	static {
		nodes.add("midi_1");
		nodes.add("midi_in_1");
		nodes.add("mix_1");
		nodes.add("output_1");
		nodes.add("poly_1");
		nodes.add("signalgenerator_1");
		nodes.add("in_1");
		nodes.add("out_1");
	}
}
