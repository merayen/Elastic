package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeOutputData
import net.merayen.elastic.system.intercom.ProcessRequestMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

internal class ProcessBenchmarkTest {
	@Test
	//@Timeout(2)
	fun `benchmark with huge buffer`() {
		val supervisor = LLVMSupervisor("/tmp/none", false)
		supervisor.ingoing.send(addOneAndTwo())

		val t = System.currentTimeMillis() + 10000

		var framesProcessed = 0
		while (t > System.currentTimeMillis()) {
			supervisor.ingoing.send(ProcessRequestMessage())
			supervisor.onUpdate()
			while (supervisor.outgoing.isEmpty())
				Thread.sleep(0)

			val messages = supervisor.outgoing.receiveAll()
			assertEquals(1, messages.size)
			for (message in messages) {
				if (message is Output1NodeOutputData)
					for (sample in message.audio.first()!!.iterator())
						assertEquals(3.0f, sample)
			}
			framesProcessed++
		}

		println("Frames processed in 10 seconds: $framesProcessed")

		supervisor.onEnd()
	}
}