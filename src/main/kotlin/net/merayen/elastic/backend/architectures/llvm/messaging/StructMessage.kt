package net.merayen.elastic.backend.architectures.llvm.messaging

import java.nio.ByteBuffer

abstract class StructMessage {
	/**
	 * Size of the header in bytes.
	 * Class that inherits need to call the super and allocate the returned size plus its own size.
	 */
	open fun size() = 0
	abstract fun dump(buffer: ByteBuffer)
	abstract fun load(data: ByteBuffer)
 }