package net.merayen.elastic.backend.architectures.llvm

import java.nio.ByteBuffer
import java.nio.ByteOrder

class LLVMCommunicator(private val backend: LLVMBackend) { // Is this bullshit?
	@Volatile
	var running = true

	private val sizeBuffer = ByteBuffer.allocate(4)

	init {
		sizeBuffer.order(ByteOrder.nativeOrder())
	}

	@Synchronized
	fun send(arr: ByteArray) {
		backend.ensureAlive()
		sizeBuffer.rewind()
		sizeBuffer.putInt(arr.size)
		sizeBuffer.rewind()

		println("Sending ${arr.size} bytes: ${arr.filter { it in 48..126 }.joinToString("") { it.toChar().toString() }}")

		try {
			backend.outputStream.write(sizeBuffer.array())
			backend.outputStream.write(arr)
			backend.outputStream.flush()
		} catch (e: Exception) {
			backend.ensureAlive()
			throw e
		}
	}

	/**
	 * Waits for receiving a complete MessagePacket from the subprocess.
	 * Waits until the complete package has been received.
	 * Returns the payload of the packet (also, not including the header that states the packet size).
	 *
	 * WARNING: The buffer returned is owned by this class. Copy it if you need to use the data before next poll()-call.
	 */
	@Synchronized
	fun poll(): ByteBuffer {
		backend.ensureAlive()

		val size: Int
		try {
			size = ByteBuffer.wrap(backend.inputStream.readNBytes(4)).order(ByteOrder.nativeOrder()).int
		} catch (e: Exception) {
			backend.ensureAlive()
			throw e
		}

		if (size > 1073741824)
			throw RuntimeException("Message received from subprocess is larger than 1MB!")

		if (size < 0)
			throw RuntimeException("Negative size in package from subprocess. Data corrupt?")

		val result: ByteArray
		try {
			result = backend.inputStream.readNBytes(size)
		} catch (e: Exception) {
			backend.ensureAlive()
			throw e
		}

		println("Receiving $size bytes: ${result.filter { it in 48..126 }.joinToString("") { it.toChar().toString() }}")

		return ByteBuffer.wrap(result).order(ByteOrder.nativeOrder())
	}
}