package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent

/**
 * Virtual port that has no data, only connecting nodes.
 */
class Virtual(frameSize: Int, debug: Boolean) : PortStruct(frameSize, debug) {
	override val cClass = object : CClass("virtual") {
		override fun onWriteMethods(codeWriter: CodeWriter, allocComponent: AllocComponent?) {}
		override fun onWriteMembers(codeWriter: CodeWriter) {}
	}
}