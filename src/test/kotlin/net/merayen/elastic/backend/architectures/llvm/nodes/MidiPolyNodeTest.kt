package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.ProcessRequestMessage
import org.junit.jupiter.api.Test

internal class MidiPolyNodeTest : LLVMNodeTest() {
	@Test
	fun `create voices`() {
		val supervisor = create()
		supervisor.ingoing.send(CreateNodeMessage("midi_poly", "midi_poly", 1, "top"))
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()
	}
}