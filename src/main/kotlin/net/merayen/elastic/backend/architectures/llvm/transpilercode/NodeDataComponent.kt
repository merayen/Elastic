package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.nodes.TranspilerNode
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

class NodeDataComponent(private val log: LogComponent, private val debug: Boolean) {
	fun writeDefinition(codeWriter: CodeWriter, nodes: Map<String, TranspilerNode>) {
		with(codeWriter) {
			Method("void", "handle_ingoing_nodedata", "int length, void* data") {
				If("length < 4") { ohshit(codeWriter) }
				if (debug) log.write(codeWriter, "handle_ingoing_nodedata receiving packet at size %i", "length")

				if (nodes.isNotEmpty()) {
					If("*(int *)data < 0") {
						ohshit(codeWriter)
					}
					for (node in nodes.values) {
						ElseIf("*(int *)data == ${node.nodeIndex}") {
							node.nodeClass.writeCall(codeWriter, "receive_data", "${node.instanceVariable}, length - 4, data + 4")
						}
					}
					Else {
						ohshit(codeWriter, "Node with index %i not found", "*(int *)data")
					}
				}
			}

			Method("void", "send_outgoing_nodedata") {
				Call("send_text", "\"NODEDATA\"")

				for (node in nodes.toSortedMap().values)
					node.nodeClass.writeCall(codeWriter, "send_data", node.instanceVariable)

				Call("send_text", "\"DONE\"")
			}
		}
	}

	fun writeHeaders(codeWriter: CodeWriter) {
		codeWriter.Method("void", "handle_ingoing_nodedata", "int length, void* data")
	}
}