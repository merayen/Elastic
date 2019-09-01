package net.merayen.elastic.system.actions

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.Action
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.system.intercom.backend.InitBackendMessage
import net.merayen.elastic.system.intercom.ui.InitUIMessage
import java.io.File
import java.util.*

/**
 * Creates a new blank project, with a few nodes.
 */
class NewProject(private val project_path: String) : Action() {
	internal var nodes = ArrayList<CreateNodeMessage>()

	init {
		if (File(project_path).exists())
			throw RuntimeException("Project already exists. Use LoadProject()-action instead.")
	}

	override fun run() {
		system.listenToMessagesFromBackend {
			for (message in it) {
					when(message) {
						is CreateNodeMessage -> nodes.add(message)
						is BeginResetNetListMessage -> nodes.clear();
						is ProcessMessage -> println("Process response");
					}
				}
			}

		init()
	}

	/**
	 * This is an example to show how it is possible to script Elastic, setting it up, creating nodes, connecting them and then have audio play out on
	 * the speakers.
	 * Whole Elastic can be controlled by just sending and receiving messages. This will open up for "multiplayer music creation" if we would like to
	 * go down that road.
	 */
	private fun init() {
		// Make sure everything is destroyed before we init
		system.end()

		// Start the backend (audio processing and logic)
		system.sendMessageToBackend(listOf(InitBackendMessage(44100, 16, 512, project_path)))

		// Init the UI, which will display a window
		system.sendMessageToUI(listOf(InitUIMessage()))

		// Create the top-most node which will contain everything
		system.sendMessageToBackend(listOf(CreateNodeMessage("group", 1, null)))

		// Wait until the top-most node has been created
		waitFor { nodes.size == 1 }

		// Create nodes inside our top-most node
		system.sendMessageToBackend(listOf(
			CreateNodeMessage("signalgenerator", 1, nodes[0].node_id),
			CreateNodeMessage("output", 1, nodes[0].node_id)
		))

		// Wait until all the nodes has been reported to have been created (we receive async messages back upon backend having created them)
		waitFor { nodes.size == 3 }

		// Set the position of the nodes
		val outputNodeData = net.merayen.elastic.backend.logicnodes.list.output_1.Data()
		outputNodeData.uiTranslation = BaseNodeData.UITranslation(200f, 0f)
		system.sendMessageToBackend(listOf(NodeParameterMessage(nodes[2].node_id, outputNodeData)))

		// Connect signal generator to output
		system.sendMessageToBackend(listOf(NodeConnectMessage(nodes[1].node_id, "output", nodes[2].node_id, "input")))

		// Set frequency parameter on one of the nodes
		val signalgeneratorNodeData = net.merayen.elastic.backend.logicnodes.list.signalgenerator_1.Data()
		signalgeneratorNodeData.frequency = 1000f
		system.sendMessageToBackend(listOf(NodeParameterMessage(nodes[1].node_id, signalgeneratorNodeData)))

		// Store the whole project (same as going to "File" --> "Save project")
		system.sendMessageToBackend(listOf(CreateCheckpointMessage()))
	}
}
