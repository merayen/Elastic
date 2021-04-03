package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.logicnodes.Format
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

enum class PortRegistry(val cls: KClass<out PortStruct>, val format: Format) {
	AUDIO(Audio::class, Format.AUDIO),
	SIGNAL(Signal::class, Format.SIGNAL),
	MIDI(Midi::class, Format.MIDI),
	VIRTUAL(Virtual::class, Format.VIRTUAL);

	companion object {
		fun getPortStruct(format: Format, frameSize: Int, debug: Boolean): PortStruct {
			return values().first { it.format == format }.cls.primaryConstructor!!.call(frameSize, debug)
		}
	}
}