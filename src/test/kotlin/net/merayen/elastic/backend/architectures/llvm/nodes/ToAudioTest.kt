package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeAudioOut
import net.merayen.elastic.backend.logicnodes.list.wave_1.Properties
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.sin

internal class ToAudioTest : LLVMNodeTest() {
	@Test
	fun `signal to audio stereo`() {
		val supervisor = createSupervisor()
		supervisor.ingoing.send(CreateNodeMessage("wave", "wave", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("wave", "out", Format.SIGNAL))
		supervisor.ingoing.send(NodePropertyMessage("wave", Properties(frequency = 10f, type = Properties.Type.SINE.name)))

		supervisor.ingoing.send(CreateNodeMessage("to_audio", "to_audio", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("to_audio", "in"))
		supervisor.ingoing.send(CreateNodePortMessage("to_audio", "out", Format.AUDIO))

		supervisor.ingoing.send(CreateNodeMessage("out", "out", "top"))
		supervisor.ingoing.send(CreateNodePortMessage("out", "in"))

		supervisor.ingoing.send(NodeConnectMessage("wave", "out", "to_audio", "in"))
		supervisor.ingoing.send(NodeConnectMessage("to_audio", "out", "out", "in"))

		supervisor.ingoing.send(ProcessRequestMessage())

		supervisor.onUpdate()

		val result = supervisor.outgoing.receiveAll().first {it is Group1OutputFrameData } as Group1OutputFrameData

		for ((i, sample) in result.outAudio["out"]!![0].withIndex()) {
			assertEquals((sin(i * (10 / 44100.0) * 2 * PI) * 1000).toInt() / 1000f, (sample * 1000).toInt() / 1000f)
		}
	}
}