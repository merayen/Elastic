package net.merayen.elastic.backend.logicnodes

import java.util.ArrayList

object Registry {
	val nodes: MutableList<String> = ArrayList()

	init {
		nodes.add("midi_1")
		nodes.add("midi_in_1")
		nodes.add("mix_1")
		nodes.add("output_1")
		nodes.add("poly_1")
		nodes.add("signalgenerator_1")
		nodes.add("in_1")
		nodes.add("out_1")
		nodes.add("adsr_1")
		nodes.add("midi_spread_1")
		nodes.add("delay_1")
	}
}
