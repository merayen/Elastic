package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeSignalOut
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

		supervisor.onEnd()

		val result = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData

		assertTrue { result.outSignal["out"]!!.all { it == 3f } }
	}
}