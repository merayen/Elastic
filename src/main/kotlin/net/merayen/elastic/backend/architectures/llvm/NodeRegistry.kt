package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.architectures.llvm.nodes.*
import kotlin.reflect.KClass

val nodeRegistry = mapOf(
	"_preprocessor" to PreProcessor::class, // Special node only used by LLVM backend
	"add" to Add::class,
	"clamp" to Clamp::class,
	"cos" to Cosine::class,
	"div" to Divide::class,
	"group" to Group::class,
	"gt" to GreaterThan::class,
	"lt" to LessThan::class,
	"meter" to Meter::class,
	"midi" to Midi::class,
	"midi_poly" to MidiPoly::class,
	"mix" to Mix::class,
	"mod" to Modulo::class,
	"mul" to Multiply::class,
	"oscilloscope" to Oscilloscope::class,
	"out" to Out::class,
	"output" to Out::class,
	"poly" to MidiPoly::class,
	"projectcars2" to ProjectCars2::class,
	"sin" to Sine::class,
	"sub" to Subtract::class,
	"tan" to Tangent::class,
	"to_audio" to ToAudio::class,
	"value" to Value::class,
	"wave" to Wave::class,
	"xy_map" to XYMap::class,
	//"elapsed" to Elapsed::class,
	//"multiply" to Multiply::class,
	//"sine" to Sine::class,
)

/**
 * Get the node name of a TranspilerNode
 *
 * Name is used in e.g CreateNodeMessage("&lt;this name&gt;", ...)
 */
fun getName(cls: KClass<out TranspilerNode>): String {
	return nodeRegistry.entries.first { it.value == cls }.key
}