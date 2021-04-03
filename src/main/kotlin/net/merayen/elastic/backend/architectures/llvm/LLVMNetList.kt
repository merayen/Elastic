package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.analyzer.NetListUtil
import net.merayen.elastic.backend.analyzer.NodeProperties
import net.merayen.elastic.backend.architectures.llvm.nodes.PreProcessor
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.CreateNodeMessage
import net.merayen.elastic.system.intercom.CreateNodePortMessage
import net.merayen.elastic.system.intercom.NodeConnectMessage
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.UniqueID

/**
 * Changes the NetList to a LLVM backend specialized one.
 *
 * Does not copy the NetList, be careful.
 */
class LLVMNetList private constructor(private val netList: NetList) {
	companion object {
		fun process(netList: NetList) {
			LLVMNetList(netList)
		}
	}

	private val nodeProperties = NodeProperties(netList)
	private val netListUtil = NetListUtil(netList)

	init {
		addPreProcessorNodes()
	}

	/**
	 * Adds pre-processor nodes in front of all nodes that has children.
	 *
	 * This allows the pre-processor node run code in group nodes when the data to the group node is ready. The group
	 * node can then prepare before its children nodes gets executed.
	 */
	private fun addPreProcessorNodes() {
		// Verify that there are no preprocessor node in the NetList already, otherwise we are chewing on a NetList we already
		// have processed
		if (netList.nodes.any { nodeProperties.getName(it) == getName(PreProcessor::class) })
			error("NetList instance has already been processed by us")

		val groupNodeIds = netListUtil.groupNodeIds
		val groupNodes = netList.nodes.filter { it.id in groupNodeIds }

		for (groupNode in groupNodes) {
			if (nodeProperties.getParent(groupNode) == null)
				continue // Top-most should not have a preprocessor (the preprocessor would be placed on top-level, which is illegal)

			// Create a random ID that will be used as an id on the preprocessor node and the input port on the group node
			// which connects the preprocessor and the group node
			val id = UniqueID.create()

			// Create the preprocessor, hidden node
			NetListMessages.apply(
				netList,
				CreateNodeMessage(id, getName(PreProcessor::class), nodeProperties.getParent(groupNode))
			)

			// Connect preprocessor node to all the nodes that are inputs to the group node
			for (inputPort in nodeProperties.getInputPorts(groupNode)) {
				val inputLines = netList.getConnections(groupNode, inputPort)
				if (inputLines.size > 1)
					error("There should be no more than 1 line connected to an input port")

				if (inputLines.isEmpty()) continue

				val line = inputLines[0]
				val newPreprocessorInputPortId = UniqueID.create()
				NetListMessages.apply(netList, CreateNodePortMessage(id, newPreprocessorInputPortId))

				if (line.node_a == groupNode)
					NetListMessages.apply(netList, NodeConnectMessage(line.node_b.id, line.port_b, id, newPreprocessorInputPortId))
				else
					NetListMessages.apply(netList, NodeConnectMessage(line.node_a.id, line.port_a, id, newPreprocessorInputPortId))
			}

			// Create virtual output port on the preprocessor
			NetListMessages.apply(netList, CreateNodePortMessage(id, "out", Format.VIRTUAL))

			// Create virtual input port on the group node which will connect to the preprocessor node
			NetListMessages.apply(netList, CreateNodePortMessage(groupNode.id, id))

			// Connect preprocessor to group node
			NetListMessages.apply(netList, NodeConnectMessage(id, "out", groupNode.id, id))
		}
	}

}