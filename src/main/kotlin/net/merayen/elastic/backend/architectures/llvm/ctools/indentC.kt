package net.merayen.elastic.backend.architectures.llvm.ctools

import kotlin.math.max

fun indentC(text: String): String {
	val lines = text.lines().map { it.trim() }.toMutableList()
	var indents = 0

	val startRegex = Regex(".*\\{ *((//)*.*)*$")
	val endRegex = Regex(".*} *[*a-z-A-Z\\[\\]_]* *;*$")

	for ((index, line) in lines.withIndex()) {
		val ignore = line.startsWith("//") || (startRegex.matches(line) && endRegex.matches(line))

		if (!ignore && endRegex.matches(line))
			indents--

		indents = max(0, indents)
		lines[index] = lines[index].prependIndent("\t".repeat(indents))

		if (!ignore && startRegex.matches(line))
			indents++
	}

	return lines.joinToString("\n")
}