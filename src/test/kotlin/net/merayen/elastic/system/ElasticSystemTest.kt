package net.merayen.elastic.system

import net.merayen.elastic.backend.architectures.local.JavaLocalDSPBackend
import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.system.intercom.CreateDefaultProjectMessage
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.system.util.ElasticCommunicator
import net.merayen.elastic.ui.JavaUI
import net.merayen.elastic.util.UniqueID
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test

internal class ElasticSystemTest {
	private var system: ElasticSystem? = null
	private var communicator: ElasticCommunicator? = null
	private val tmpProject = System.getProperty("java.io.tmpdir") + "/ElasticSystemTest.elastic"

	@BeforeEach
	fun setUp() {
		val system = ElasticSystem(
			tmpProject,
			uiModule = JavaUI::class,
			dspModule = JavaLocalDSPBackend::class,
			backendModule = JavaBackend::class
		)

		val communicator = ElasticCommunicator(system)

		communicator.send(CreateDefaultProjectMessage())

		this.system = system
		this.communicator = communicator
	}

	@AfterEach
	fun tearDown() {
		communicator?.close()
		system?.close()
	}

	/**
	 * Test creating a sine wave sound with the local, reference DSP-implementation.
	 * No UI is enabled.
	 */
	@Test
	@Timeout(10)
	@Disabled("Not compatible with the new LLVM backend")
	fun testSimpleSineWave() {
		val communicator = communicator!!

		communicator.send(CreateNodeMessage(UniqueID.create(), "group", 1, null))

		var topId: String? = null

		communicator.waitForBackendMessage {
			if (it is CreateNodeMessage && it.parent == null) {
				topId = it.node_id
				true
			} else {
				false
			}
		}

		// Create the nodes that will play the sine-wave
		communicator.send(CreateNodeMessage(UniqueID.create(), "signalgenerator", 1, topId))
		communicator.send(CreateNodeMessage(UniqueID.create(), "output", 1, topId))

		var signalgeneratorNodeId: String? = null
		var outputNodeId: String? = null

		// Wait for the nodes to be created
		communicator.waitForBackendMessage {
			if (it is CreateNodeMessage) {
				when (it.name) {
					"signalgenerator" -> signalgeneratorNodeId = it.node_id
					"output" -> outputNodeId = it.node_id
				}
			}

			communicator.netlist.nodes.size == 3
		}

		// Connect signal generator to the output
		communicator.send(
			NodeConnectMessage(signalgeneratorNodeId, "output", outputNodeId, "input")
		)

		// Wait for Elastic to connect the nodes
		communicator.waitForBackendMessage {
			it is NodeConnectMessage
		}

		//communicator.send(EnableUIMessage(EnableUIMessage.SurfaceType.SWING))

		val t = System.currentTimeMillis()
		communicator.waitFor {
			t + 10000 < System.currentTimeMillis()
		}
	}
}