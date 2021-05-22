package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.LLVMDSPModule
import net.merayen.elastic.backend.architectures.llvm.nodeRegistry
import net.merayen.elastic.system.intercom.ConfigureBackendMessage
import net.merayen.elastic.system.intercom.CreateNodeMessage
import kotlin.reflect.KClass

internal abstract class LLVMNodeTest {
	protected fun createSupervisor(
		debug: Boolean = true,
		nodeRegistrySource: Map<String, KClass<out TranspilerNode>> = nodeRegistry
	): LLVMDSPModule {
		val supervisor = LLVMDSPModule()
		supervisor.nodeRegistrySource = nodeRegistrySource
		supervisor.debug = debug

		supervisor.ingoing.send(
			listOf(
				ConfigureBackendMessage(sampleRate = 44100, frameSize = 256, depth = 16),
				CreateNodeMessage("top", "group", 1, null),
			)
		)

		supervisor.onUpdate()

		return supervisor
	}
}