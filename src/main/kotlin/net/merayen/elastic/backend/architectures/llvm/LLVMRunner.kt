package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.netlist.NetList

class LLVMRunner(
	val netList: NetList,
	val llvm: LLVMBackend,
	val communicator: LLVMCommunicator
) {
	fun end() {
		communicator.send("QUIT".toByteArray())
		val response = communicator.poll()
		if (response.get().toChar() != 'Q' || response.get().toChar() != 'U' || response.get().toChar() != 'I' || response.get().toChar() != 'T')
			throw RuntimeException("Expected 'QUIT' message from dsp backend")
	}
}
