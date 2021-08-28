package net.merayen.elastic.system.actions

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.Action
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.system.intercom.backend.ErrorMessage
import net.merayen.elastic.util.UniqueID
import java.util.*

/**
 * Creates a new project with a few nodes.
 */
class CreateDefaultProject(private val message: CreateDefaultProjectMessage) : Action() {
	class ProjectIsNotEmptyErrorMessage : ErrorMessage

	internal var nodes = ArrayList<CreateNodeMessage>()

	override fun run() {
		init()
	}

	/**
	 * This is an example to show how it is possible to script Elastic, setting it up, creating nodes, connecting them and then have audio play out on
	 * the speakers.
	 * Whole Elastic can be controlled by just sending and receiving messages. This will open up for "multiplayer music creation" if we would like to
	 * go down that road.
	 */
	private fun init() {
		//waitFor { finishedResetNetList }

		if (nodes.isNotEmpty()) {
			send(ProjectIsNotEmptyErrorMessage())
			return
		}

		// Create the top-most node which will contain everything
		send(CreateNodeMessage(UniqueID.create(), "group", 1, null))

		// Wait until the top-most node has been created
		waitFor { nodes.size == 1 }

		// Create nodes inside our top-most node
		send(CreateNodeMessage(UniqueID.create(), "wave", 1, nodes[0].node_id))
		send(CreateNodeMessage(UniqueID.create(), "out", 1, nodes[0].node_id))

		// Wait until all the nodes has been reported to have been created (we receive async messages back upon backend having created them)
		waitFor { nodes.size == 3 }

		// Set the position of the nodes
		val outNodeData = net.merayen.elastic.backend.logicnodes.list.out_1.Properties()
		outNodeData.uiTranslation = BaseNodeProperties.UITranslation(250f, 50f)
		send(NodePropertyMessage(nodes[2].node_id, outNodeData))

		// Connect signal generator to out
		send(NodeConnectMessage(nodes[1].node_id, "out", nodes[2].node_id, "in"))

		// Set frequency parameter on one of the nodes
		val signalgeneratorNodeData = net.merayen.elastic.backend.logicnodes.list.wave_1.Properties()
		signalgeneratorNodeData.frequency = 1000f
		send(NodePropertyMessage(nodes[1].node_id, signalgeneratorNodeData))
	}

	override fun onMessageFromBackend(message: ElasticMessage) {
		when (message) {
			is CreateNodeMessage -> nodes.add(message)
		}
	}
}
