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
			TODO("run code on the node on the right side (the top/midi_poly node)")
		}
	}
}