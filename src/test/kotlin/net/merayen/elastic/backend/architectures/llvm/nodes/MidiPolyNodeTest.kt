package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.CreateNodePortMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.system.intercom.ProcessRequestMessage
import org.junit.jupiter.api.Test

internal class MidiPolyNodeTest : LLVMNodeTest() {
	@Test
	fun `create voices`() {
		val supervisor = createSupervisor()
		supervisor.ingoing.send(CreateNodeMessage("midi", "midi", 1, "top"))
		supervisor.ingoing.send(CreateNodePortMessage("midi", "out", Format.MIDI))

		supervisor.ingoing.send(CreateNodeMessage("midi_poly", "midi_poly", 1, "top"))
		supervisor.ingoing.send(CreateNodePortMessage("midi_poly", "in"))
		supervisor.ingoing.send(CreateNodePortMessage("midi_poly", "out", Format.SIGNAL))

		// midi_poly children nodes
		supervisor.ingoing.send(CreateNodeMessage("wave", "wave", "midi_poly"))
		supervisor.ingoing.send(CreateNodePortMessage("wave", "out", Format.SIGNAL))

		supervisor.ingoing.send(CreateNodeMessage("out", "out", "midi_poly"))
		supervisor.ingoing.send(CreateNodePortMessage("out", "in"))

		// Output node to read the result
		supervisor.ingoing.send(CreateNodeMessage("output", "out", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("output", "in"))

		supervisor.ingoing.send(NodeConnectMessage("midi", "out", "midi_poly", "in"))
		supervisor.ingoing.send(NodeConnectMessage("wave", "out", "out", "in"))
		supervisor.ingoing.send(NodeConnectMessage("midi_poly", "out", "output", "in"))
		// TODO play midi notes (directly) via the midi node

		val processRequestMessage = ProcessRequestMessage()
		//processRequestMessage.input["midi_poly"] = NodePropertyMessage("midi_poly")

		supervisor.ingoing.send(processRequestMessage)
		supervisor.onUpdate()
	}
}