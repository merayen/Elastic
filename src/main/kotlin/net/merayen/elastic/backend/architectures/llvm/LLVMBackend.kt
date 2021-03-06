package net.merayen.elastic.backend.architectures.llvm

import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * Uses LLVM with clang-10 to generate an executable and runs it afterwards, making the streams to the executable
 * available.
 */
class LLVMBackend(code: String) {
	class ProcessDead(signal: Int) : RuntimeException("Signal: $signal")
	class DoesNotCompile(signal: Int) : RuntimeException("Signal: $signal")

	private val process: Process

	val inputStream: InputStream
	val outputStream: OutputStream
	//val errorStream: InputStream

	init {
		val path = "/tmp/elastic-ccode.c" // TODO store this locally
		val outputPath = "$path.out"

		File(path).writeText(code)

		val outputFile = File(outputPath)
		if (outputFile.exists())
			outputFile.delete()

		with(ProcessBuilder(listOf("clang-11", path, "-o", outputPath, "-pthread", "-g", "-Wall"))) {
			redirectOutput(ProcessBuilder.Redirect.INHERIT)
			redirectError(ProcessBuilder.Redirect.INHERIT)
			val t = System.currentTimeMillis()
			val process = start()
			process.waitFor()

			if (process.exitValue() != 0)
				throw DoesNotCompile(process.exitValue())

			println(
				"Compiling took ${System.currentTimeMillis() - t}ms for ${
					code.lineSequence().filter { it.trim().isNotEmpty() }.count()
				} LOC"
			)
		}

		process = with(ProcessBuilder(listOf(outputPath, outputPath))) {
			redirectOutput(ProcessBuilder.Redirect.PIPE)
			redirectError(ProcessBuilder.Redirect.INHERIT)
			start()
		}

		ensureAlive()

		// Wait for "HELLO"-message
		val hello = ByteArray(5)
		try {
			process.inputStream.read(hello)
		} catch (e: Exception) {
			ensureAlive()
			throw e
		}

		if (!hello.contentEquals(byteArrayOf('H'.toByte(), 'E'.toByte(), 'L'.toByte(), 'L'.toByte(), 'O'.toByte())))
			throw RuntimeException("Subprocess did not say hello to us in the manner we wanted it to: Got ${String(hello)}")

		ensureAlive()

		try {
			process.outputStream.write(byteArrayOf(10, 11, 12))
			process.outputStream.flush()
		} catch (e: Exception) {
			ensureAlive()
			throw e
		}

		ensureAlive()

		val answer = ByteArray(4)
		try {
			process.inputStream.read(answer)
		} catch (e: Exception) {
			ensureAlive()
			throw e
		}

		ensureAlive()

		if (!answer.contentEquals(byteArrayOf('G'.toByte(), 'O'.toByte(), 'O'.toByte(), 'D'.toByte())))
			throw RuntimeException("Subprocess did not answer to us in the manner we wanted it to: Got ${String(answer)}")

		inputStream = process.inputStream
		outputStream = process.outputStream
		//errorStream = process.errorStream
	}

	/**
	 * Ensure process is alive, otherwise throw exception.
	 */
	fun ensureAlive() {
		if (!process.isAlive) {
			throw ProcessDead(process.exitValue())
		}
	}
}