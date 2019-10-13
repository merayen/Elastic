package net.merayen.elastic.backend.architectures.local

import net.merayen.elastic.backend.architectures.IArchitecture

class Info : IArchitecture {
	override val description = "Runs the DSP locally inside the JVM. The reference implementation."
	override val name = "Local"
}
