package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeAudioOut
import net.merayen.elastic.system.intercom.ProcessRequestMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ProcessBenchmarkTest {
	@Test
	fun `benchmark with huge buffer`() {
		val supervisor = LLVMDSPModule(true)
		supervisor.ingoing.send(addOneAndTwo())

		val t = System.currentTimeMillis() + 1000

		var framesProcessed = 0
		while (t > System.currentTimeMillis()) {
			supervisor.ingoing.send(ProcessRequestMessage())
			supervisor.onUpdate()
			while (supervisor.outgoing.isEmpty())
				Thread.sleep(0)

			val messages = supervisor.outgoing.receiveAll()
			assertEquals(1, messages.size)
			messages.all { message ->
				message is Output1NodeAudioOut && message.audio.first()!!.all { it == 3.0f }
			}
			framesProcessed++
		}

		println("Frames processed in 1 seconds: $framesProcessed")

		supervisor.onEnd()
	}
}