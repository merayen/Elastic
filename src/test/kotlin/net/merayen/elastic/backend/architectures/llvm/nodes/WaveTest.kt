package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.CreateNodePortMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.system.intercom.ProcessRequestMessage
import org.junit.jupiter.api.Test

internal class WaveTest : LLVMNodeTest() {
	@Test
	fun `generate sinewave`() {
		val supervisor = createSupervisor()
		supervisor.ingoing.send(CreateNodeMessage("wave", "wave", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("wave", "out", Format.AUDIO))
		supervisor.ingoing.send(CreateNodeMessage("out", "out", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("out", "in"))
		supervisor.ingoing.send(NodeConnectMessage("wave", "out", "out", "in"))
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()
		println(supervisor.outgoing.receiveAll())
	}
}