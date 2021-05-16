package net.merayen.elastic.backend.architectures.llvm.cmethods

fun clamp(string: String): String {
	return "$string < 0 ? 0 : $string > 1 ? 1 : $string"
}

