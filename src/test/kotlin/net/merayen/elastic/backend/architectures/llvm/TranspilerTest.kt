package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeOutputData
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.util.NetListMessages
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class TranspilerTest {
	@Test
	fun `simple test`() { // This test is crap. What does it do
		val netlist = NetList()

		NetListMessages.apply(netlist, CreateNodeMessage("top", "group", 1, null))
		NetListMessages.apply(netlist, CreateNodeMessage("a", "value", 1, "top"))
		NetListMessages.apply(netlist, CreateNodeMessage("b", "out", 1, "top"))

		val transpiler = Transpiler(netlist, 44100, 16, 256, 4, 256, true)
		println(transpiler.transpile())
	}

	@Test
	fun `1 + 2 = 3`() {
		val supervisor = LLVMSupervisor("/tmp/none")
		supervisor.listenCodeGen = {
			println(it.split("\n").mapIndexed { i, x -> "${i + 1}\t$x" }.joinToString("\n"))
		}

		val messages = addOneAndTwo()

		supervisor.ingoing.send(messages)
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()

		Thread.sleep(1000) // Meh, fix

		supervisor.onEnd()

		val result = supervisor.outgoing.receiveAll().first { it is Output1NodeOutputData }

		assertTrue { result is Output1NodeOutputData }

		result as Output1NodeOutputData

		assertTrue { result.audio.size == 2 }

		assertTrue { result.audio[0]!!.all { it == 3f } }
	}
}