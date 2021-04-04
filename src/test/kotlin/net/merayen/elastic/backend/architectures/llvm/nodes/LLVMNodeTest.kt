package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.LLVMDSPModule
import net.merayen.elastic.system.intercom.CreateNodeMessage

internal abstract class LLVMNodeTest {
	protected fun createSupervisor(debug: Boolean = true): LLVMDSPModule {
		val supervisor = LLVMDSPModule()
		supervisor.debug = debug

		supervisor.ingoing.send(CreateNodeMessage("top", "group", 1, null))
		supervisor.onUpdate()

		return supervisor
	}
}