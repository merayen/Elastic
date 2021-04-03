package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeSignalOut
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TranspilerTest {
	@Test
	fun `1 + 2 = 3`() {
		val supervisor = LLVMDSPModule(true)
		// supervisor.listenCodeGen = {
		// 	println(it.split("\n").mapIndexed { i, x -> "${i + 1}\t$x" }.joinToString("\n"))
		// }

		val messages = addOneAndTwo()

		supervisor.ingoing.send(messages)
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()

		supervisor.onEnd()

		val result = supervisor.outgoing.receiveAll().first()

		assertTrue { result is Output1NodeSignalOut }

		result as Output1NodeSignalOut

		assertTrue { result.signal.all { it == 3f } }
	}
}