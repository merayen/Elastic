package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.LLVMSupervisor
import net.merayen.elastic.system.intercom.CreateNodeMessage

internal abstract class LLVMNodeTest {
	protected fun createSupervisor(): LLVMSupervisor {
		val supervisor = LLVMSupervisor("/tmp/none", true)

		supervisor.ingoing.send(CreateNodeMessage("top", "group", 1, null))
		supervisor.onUpdate()

		return supervisor
	}
}