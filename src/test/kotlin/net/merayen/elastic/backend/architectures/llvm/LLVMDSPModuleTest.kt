package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.system.intercom.CreateNodeMessage
import org.junit.jupiter.api.Test

internal class LLVMDSPModuleTest {
	@Test
	fun `create node`() {
		val supervisor = LLVMDSPModule(true)
		supervisor.ingoing.send(listOf(
			CreateNodeMessage("group0", "group", 1, null),
			CreateNodeMessage("time0", "elapsed", 1, null),
			CreateNodeMessage("multiply0", "multiply", 1, null),
			CreateNodeMessage("sine0", "sine", 1, null),
			CreateNodeMessage("out0", "out", 1, null)
		))
	}
}