package net.merayen.elastic.backend.logicnodes

import java.util.*

object NodeRegistry {
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
		nodes.add("compressor_1")
		nodes.add("sample_1")
		nodes.add("eq_1")
		nodes.add("midi_transpose_1")
		nodes.add("histogram_1")
		nodes.add("wave_1")
		nodes.add("metronome_1")
		nodes.add("cutoff_1")
		nodes.add("autocorrelation_1")
		nodes.add("frequency_1")
		nodes.add("math_1")
		nodes.add("oscilloscope_1")
		nodes.add("value_1")
		nodes.add("to_audio_1")
		nodes.add("from_midi_1")
	}
}
