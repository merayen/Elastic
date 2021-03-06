package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeSignalOut
import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.sin

internal class WaveTest : LLVMNodeTest() {
	@Test
	fun `generate sinewave audio`() {
		val supervisor = createSupervisor()
		supervisor.ingoing.send(CreateNodeMessage("wave", "wave", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("wave", "out", Format.SIGNAL))
		supervisor.ingoing.send(CreateNodeMessage("out", "out", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("out", "in"))
		supervisor.ingoing.send(NodePropertyMessage("wave", Properties(type = Properties.Type.SINE.name, frequency = 10f)))
		supervisor.ingoing.send(NodeConnectMessage("wave", "out", "out", "in"))
		supervisor.ingoing.send(ProcessRequestMessage())
		supervisor.onUpdate()

		val result = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData

		var position = 0.0
		for ((i, sample) in result.outSignal["out"]!!.withIndex()) {
			assertEquals((sin(position * 2 * PI) * 1000).toInt() / 1000.0, (sample * 1000).toInt() / 1000.0)
			position += 10.0 / 44100.0
		}
	}
}