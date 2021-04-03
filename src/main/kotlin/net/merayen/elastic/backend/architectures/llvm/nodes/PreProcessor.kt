package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * Hidden node that is only used by the LLVM backend, automatically added before group nodes.
 *
 * Gets placed in front of every group node (top, midi_poly etc) to have code run before nodes inside the group
 * node gets run. This is to e.g allow the group node to create a voice of all its children nodes before they
 * process. Then the group node will have its process() method run after all the children nodes has processed.
 */
class PreProcessor(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteProcess(codeWriter: CodeWriter) {
			// We run code our right side (a group node) that it will soon processs
			val ports = getOutputPorts()
			if (ports.size != 1)
				error("preprocessor node should always have exactly 1 output port")

			val lines = shared.netList.getConnections(node, ports[0])
			if (lines.size != 1)
				error("preprocessor must have exactly 1 line connected to the group node")

			val groupNodes = shared.netListUtil.getRightNodes(node)
			if (groupNodes.size != 1)
				error("Only 1 node can be connected to the preprocessor node on the right side")

			val groupNode = groupNodes.first()

			// Write preprocess code from the group node into us
			// This will be run when all the nodes we depend on (the left nodes for the group node) have processed
			shared.nodes[groupNode.id]!!.nodeClass.onWritePreprocess(codeWriter)
		}
	}
}