package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.architectures.llvm.nodes.*

val nodeRegistry = mapOf(
	"group" to Group::class,
	"value" to Value::class,
	//"elapsed" to Elapsed::class,
	"add" to Add::class,
	"midi_poly" to MidiPoly::class,
	"poly" to MidiPoly::class,
	"midi" to Midi::class,
	"midi_out" to MidiOut::class,
	//"multiply" to Multiply::class,
	//"sine" to Sine::class,
	"out" to Out::class,
	"output" to Out::class,
	"wave" to Wave::class,
)