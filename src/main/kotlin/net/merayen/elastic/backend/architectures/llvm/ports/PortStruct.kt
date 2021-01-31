package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass

abstract class PortStruct(protected val frameSize: Int) {
	abstract val clsName: String
	abstract val cClass: CClass
}