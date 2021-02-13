package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.system.intercom.ProcessRequestMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

internal class ProcessBenchmarkTest {
	@Test
	@Timeout(2)
	fun `benchmark with huge buffer`() {
		val supervisor = LLVMSupervisor("/tmp/none", false)
		supervisor.ingoing.send(addOneAndTwo())

		val t = System.currentTimeMillis() + 5000

		while (t > System.currentTimeMillis()) {
			supervisor.ingoing.send(ProcessRequestMessage())
			supervisor.onUpdate()
			while(supervisor.outgoing.isEmpty())
				Thread.sleep(0)

			val messages = supervisor.outgoing.receiveAll()
			assertEquals(1, messages.size)
			println(messages.first())
		}
		println("Ja")

		supervisor.onEnd()
	}
}