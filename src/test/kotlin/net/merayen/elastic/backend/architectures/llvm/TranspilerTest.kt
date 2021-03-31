package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeAudioOut
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TranspilerTest {
	@Test
	fun `1 + 2 = 3`() {
		val supervisor = LLVMDSPModule()
		// supervisor.listenCodeGen = {
		// 	println(it.split("\n").mapIndexed { i, x -> "${i + 1}\t$x" }.joinToString("\n"))
		// }

		val messages = addOneAndTwo()

		supervisor.ingoing.send(messages)
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()

		Thread.sleep(1000) // Meh, fix

		supervisor.onEnd()

		val result = supervisor.outgoing.receiveAll().first { it is Output1NodeAudioOut }

		assertTrue { result is Output1NodeAudioOut }

		result as Output1NodeAudioOut

		assertTrue { result.audio.size == 2 }

		assertTrue { result.audio[0]!!.all { it == 3f } }
	}
}