package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.oscilloscope_1.OscilloscopeSignalDataMessage
import net.merayen.elastic.backend.logicnodes.list.value_1.Properties
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OscilloscopeTest : LLVMNodeTest() {
	@Test
	fun `simple value`() {
		val supervisor = createSupervisor(true)
		supervisor.ingoing.send(
			listOf(
				CreateNodeMessage("value", "value", "top"),
				CreateNodePortMessage("value", "out", Format.SIGNAL),
				NodePropertyMessage("value", Properties(value = 1f)),

				CreateNodeMessage("oscilloscope", "oscilloscope", "top"),
				CreateNodePortMessage("oscilloscope", "in"),
				NodePropertyMessage(
					"oscilloscope",
					net.merayen.elastic.backend.logicnodes.list.oscilloscope_1.Properties(
						amplitude = 1f,
						offset = 0f,
						time = 0.001f,
						trigger = .5f,
						auto = false,
					)
				),

				NodeConnectMessage("value", "out", "oscilloscope", "in"),
			)
		)

		for (i in 0 until 10) { // Let it process some frames so that the oscilloscope will trigger
			supervisor.ingoing.send(ProcessRequestMessage())
			supervisor.outgoing.receiveAll()
			supervisor.onUpdate()
		}

		var groupMessage: Group1OutputFrameData? = null
		var oscilloscopeMessage: OscilloscopeSignalDataMessage? = null

		for (message in supervisor.outgoing.receiveAll()) {
			if (message is Group1OutputFrameData)
				groupMessage = message
			else if (message is OscilloscopeSignalDataMessage)
				oscilloscopeMessage = message
		}

		assertNotNull(groupMessage)
		assertNotNull(oscilloscopeMessage)
	}
}