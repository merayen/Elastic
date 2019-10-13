package net.merayen.elastic.backend.nodes

import net.merayen.elastic.backend.analyzer.NetListUtil
import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.context.JavaBackend
import net.merayen.elastic.netlist.Node
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.util.JSONObjectMapper
import net.merayen.elastic.util.NetListMessages

/**
 * This is the "central control". All node-related messages from UI and processor is sent into this class.
 * The LogicNodes then validates and forwards messages to the processor.
 *
 * Message-wise, we stand in between the UI (frontend) and the processor.
 */
class Supervisor(val env: JavaBackend.Environment, private val handler: Handler) {
	val logicnode_list = LogicNodeList(this)

	@Volatile
	private var is_processing: Boolean = false // Set to true when LogicNodes (and nodes) are processing, as we then can not take any messages

	interface Handler {
		// TODO soon: Replace these two with just "onSendMessage", as ElasticSystem should route them correctly
		fun onSendToUI(message: ElasticMessage)

		fun onSendToDSP(message: ElasticMessage)

		/**
		 * Called when processing a frame has been finished.
		 */
		fun onProcessDone()
	}

	private fun createNode(node_id: String?, name: String, version: Int, parent: String?) {
		val project = env.project

		val node: Node
		if (node_id == null)
			node = project.netList.createNode()
		else
			node = project.netList.createNode(node_id)

		project.nodeProperties.setName(node, name)
		project.nodeProperties.setVersion(node, version)
		project.nodeProperties.setParent(node, parent)

		// Make properties (de)serializable by adding {..."&className&": "Data"...}
		node.properties[JSONObjectMapper.CLASSNAME_IDENTIFIER] = "Properties"

		logicnode_list.addAsLogicNode(node)
	}

	@Synchronized
	fun receiveMessage(message: ElasticMessage) {
		executeMessage(message)
	}

	@Synchronized
	private fun executeMessage(message: ElasticMessage) {
		val project = env.project

		val netlist = project.netList

		when (message) {
			is CreateNodeMessage -> createNode(message.node_id, message.name, message.version, message.parent)
			is NodePropertyMessage -> {
				NetListMessages.apply(netlist, message) // Apply it already here, and allow the logicnode to change it back

				val logicNode = logicnode_list.get(message.node_id)

				logicNode.onParameterChange(message.instance)
			}
			is NodeDataMessage -> logicnode_list.get(message.nodeId).processData(message)
			is NodeConnectMessage -> { // Notifies LogicNodes about changing of connections
				val np = NodeProperties(netlist)

				// Validate the connection
				val output_a = np.isOutput(netlist.getPort(message.node_a, message.port_a))
				val output_b = np.isOutput(netlist.getPort(message.node_b, message.port_b))

				if (output_a == output_b)
					return  // Only inputs and outputs can be connected

				if (message.node_a == message.node_b)
					return  // Node can not be connected to itself

				if (!output_a && netlist.getConnections(netlist.getNode(message.node_a), message.port_a).size > 0)
					return  // Input ports can only have 1 line connected

				if (!output_b && netlist.getConnections(netlist.getNode(message.node_b), message.port_b).size > 0)
					return  // Input ports can only have 1 line connected

				val connect_message = NodeConnectMessage(message.node_a, message.port_a, message.node_b, message.port_b)
				NetListMessages.apply(netlist, message)
				handler.onSendToUI(connect_message) // Acknowledge connection

				logicnode_list.get(message.node_a).notifyConnect(message.port_a)
				logicnode_list.get(message.node_b).notifyConnect(message.port_b)

			}
			is NodeDisconnectMessage -> { // Notifies LogicNodes about changing of connections
				val disconnect_message = NodeDisconnectMessage(message.node_a, message.port_a, message.node_b, message.port_b)
				NetListMessages.apply(netlist, message)
				handler.onSendToUI(disconnect_message) // Acknowledge disconnection

				logicnode_list.get(message.node_a).notifyDisconnect(message.port_a)
				logicnode_list.get(message.node_b).notifyDisconnect(message.port_b)

			}
			is CreateNodePortMessage -> {
				NetListMessages.apply(netlist, message)
				handler.onSendToUI(message)

			}
			is RemoveNodePortMessage -> throw RuntimeException("Not allowed. Only logic nodes can do this action")
			is RemoveNodeMessage -> {

				val netListUtil = NetListUtil(netlist)
				val toRemove = netListUtil.getChildrenDeep(netlist.getNode(message.node_id))

				toRemove.add(netlist.getNode(message.node_id))

				for (nodeToDelete in toRemove) {
					logicnode_list.get(nodeToDelete.id).onRemove()
					logicnode_list.remove(nodeToDelete.id)

					NetListMessages.apply(netlist, RemoveNodeMessage(nodeToDelete.id))
					handler.onSendToUI(RemoveNodeMessage(nodeToDelete.id))
				}
			}
			is ProcessRequestMessage -> processFrame()
			is ProcessResponseMessage -> handleResponseFromProcessor(message)
		}
	}

	/**
	 * Used by BaseLogicNode to send messages to UI and DSP.
	 */
	fun send(message: ElasticMessage) {
		handler.onSendToUI(message)
	}

	/**
	 * Messages sent from processing should be sent into this function.
	 */
	private fun handleResponseFromProcessor(message: ProcessResponseMessage) {
		val project = env.project

		val netList = project.netList

		// Call all LogicNodes to work on the frame
		for (node in netList.nodes) {
			val bln = logicnode_list.get(node.id)
			bln.onFinishFrame(message.output[node.id])
		}

		handler.onProcessDone()

		// Execute all messages that are waiting due to LogicNode and processor processing a frame previously
		is_processing = false

		// Send any statistics report to UI if available
		val statisticsReportMessage = message.statisticsReportMessage
		if (statisticsReportMessage != null)
			handler.onSendToUI(statisticsReportMessage)
	}

	fun removePort(logic_node: BaseLogicNode, name: String) {
		val project = env.project

		val netlist = project.netList

		if (netlist.getPort(logic_node.node, name) == null)
			throw RuntimeException(String.format("Port %s does not exist on Node", name))

		// Notify the node(s) connected on the other side that we will disconnect
		for (line in netlist.getConnections(logic_node.node, name))
			if (line.node_a.id == logic_node.id)
				logicnode_list.get(line.node_b.id).notifyDisconnect(line.port_b)
			else
				logicnode_list.get(line.node_a.id).notifyDisconnect(line.port_a)

		netlist.removePort(logic_node.node, name)

		send(RemoveNodePortMessage(logic_node.id, name))
	}

	private fun processFrame() {
		val netlist = env.project.netList

		if (is_processing)
			throw RuntimeException("Already processing")

		is_processing = true

		// Build a new ProcessMessage with data from the logic nodes to the processor.
		val message = ProcessRequestMessage()

		for (n in netlist.nodes) {
			val bln = logicnode_list.get(n.id)

			message.input[n.id] = bln.onPrepareFrame()
		}

		handler.onSendToDSP(message) // Forward process request message to processor
	}
}
