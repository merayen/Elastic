package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent

class Signal(frameSize: Int, debug: Boolean) : PortStruct(frameSize, debug) {
	override val clsName = "PortDataSignal"

	override val cClass = object : CClass(clsName) {
		override fun onWriteMembers(codeWriter: CodeWriter) {
			codeWriter.Member("float", "signal[$frameSize]")
		}

		override fun onWriteMethods(codeWriter: CodeWriter, allocComponent: AllocComponent?) {}
	}
}
