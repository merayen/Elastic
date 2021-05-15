package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.LLVMDSPModule
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.mix_1.Properties
import net.merayen.elastic.system.intercom.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs

internal class MixTest : LLVMNodeTest() {
	@Test
	fun `mix with no input`() {
		val supervisor = init()
		supervisor.ingoing.send(ProcessRequestMessage())

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertEquals(1, response.outSignal.size)
			assertTrue("out" in response.outSignal)
			assertTrue(response.outSignal["out"]!!.all { it == 0f })
		}

		supervisor.ingoing.send(
			listOf(
				NodePropertyMessage("mix", Properties(mix = 0.5f)),
				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertTrue("out" in response.outSignal)
			assertTrue(response.outSignal["out"]!!.all { it == 0f })
		}
	}

	@Test
	fun `single signal input`() {
		val supervisor = init()
		supervisor.ingoing.send(
			listOf(
				NodeConnectMessage("signal0", "out", "mix", "a"),
				NodePropertyMessage("mix", Properties(mix = 0.25f)),
				NodePropertyMessage("signal0", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = 100f)),
				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertTrue(response.outSignal["out"]!!.all { abs(it - 75f) < 0.00001f })
		}

		// Disconnect input a on mix node, and connect to b instead
		supervisor.ingoing.send(
			listOf(
				NodeDisconnectMessage("signal0", "out", "mix", "a"),
				NodeConnectMessage("signal0", "out", "mix", "b"),
				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertTrue(response.outSignal["out"]!!.all { abs(it - 25f) < 0.00001f })
		}
	}

	@Test
	fun `two signal inputs`() {
		val supervisor = init()
		supervisor.ingoing.send(
			listOf(
				NodeConnectMessage("signal0", "out", "mix", "a"),
				NodeConnectMessage("signal1", "out", "mix", "b"),

				NodePropertyMessage("signal0", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = 20f)),
				NodePropertyMessage("signal1", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = 60f)),
				NodePropertyMessage("mix", Properties(mix = 0.5f)),

				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData

			assertTrue("out" in response.outSignal)
			assertTrue(response.outSignal["out"]!!.all { abs(it - 40f) < 0.00001f })
		}

		// Test with fac input too
		supervisor.ingoing.send(
			listOf(
				NodeConnectMessage("signal2", "out", "mix", "fac"),
				NodePropertyMessage("signal2", net.merayen.elastic.backend.logicnodes.list.value_1.Properties(value = .1f)),
				ProcessRequestMessage(),
			)
		)

		supervisor.onUpdate()

		run {
			val response = supervisor.outgoing.receiveAll().first { it is Group1OutputFrameData } as Group1OutputFrameData
			assertTrue(response.outSignal["out"]!!.all { abs(it - 24f) < 0.00001f })
		}
	}

	/**
	 * Creates common NetList. You will need to connect the mix's input ports yourself afterwards.
	 */
	private fun init(): LLVMDSPModule {
		val supervisor = createSupervisor()
		supervisor.ingoing.send(
			listOf(
				CreateNodeMessage("signal0", "value", "top"),
				CreateNodePortMessage("signal0", "out", Format.SIGNAL),

				CreateNodeMessage("signal1", "value", "top"),
				CreateNodePortMessage("signal1", "out", Format.SIGNAL),

				CreateNodeMessage("signal2", "value", "top"),
				CreateNodePortMessage("signal2", "out", Format.SIGNAL),

				CreateNodeMessage("mix", "mix", "top"),
				CreateNodePortMessage("mix", "a"),
				CreateNodePortMessage("mix", "b"),
				CreateNodePortMessage("mix", "fac"),
				CreateNodePortMessage("mix", "out", Format.SIGNAL),

				CreateNodeMessage("out", "out", "top"),
				CreateNodePortMessage("out", "in"),

				NodeConnectMessage("mix", "out", "out", "in"),
			)
		)
		return supervisor
	}
}