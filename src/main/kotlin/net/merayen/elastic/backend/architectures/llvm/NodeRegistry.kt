package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.architectures.llvm.nodes.*
import kotlin.reflect.KClass

val nodeRegistry = mapOf(
	"group" to Group::class,
	"value" to Value::class,
	//"elapsed" to Elapsed::class,
	"add" to Add::class,
	"midi_poly" to MidiPoly::class,
	"poly" to MidiPoly::class,
	"midi" to Midi::class,
	//"multiply" to Multiply::class,
	//"sine" to Sine::class,
	"oscilloscope" to Oscilloscope::class,
	"out" to Out::class,
	"output" to Out::class,
	"wave" to Wave::class,
	"to_audio" to ToAudio::class,
	"_preprocessor" to PreProcessor::class, // Special node only used by LLVM backend
)

/**
 * Get the node name of a TranspilerNode
 *
 * Name is used in e.g CreateNodeMessage("&lt;this name&gt;", ...)
 */
fun getName(cls: KClass<out TranspilerNode>): String {
	return nodeRegistry.entries.first { it.value == cls }.key
}