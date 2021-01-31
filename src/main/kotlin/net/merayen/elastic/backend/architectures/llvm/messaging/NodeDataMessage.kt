package net.merayen.elastic.backend.architectures.llvm.messaging

import java.nio.ByteBuffer

/**
 * Send or receive data from a node.
 */
abstract class NodeDataMessage(val nodeId: String) : StructMessage() {
	private val nodeIdBytes = nodeId.toByteArray()
	override fun size() = super.size() + 1 + nodeIdBytes.size

	init {
		if (nodeIdBytes.size >= 127)
			throw RuntimeException("nodeId must be less than 127 bytes")
	}

	override fun dump(buffer: ByteBuffer) {
		buffer.put(nodeIdBytes.size.toByte())
		buffer.put(nodeId.toByteArray())
	}
}