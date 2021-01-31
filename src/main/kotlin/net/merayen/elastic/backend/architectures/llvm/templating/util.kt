package net.merayen.elastic.backend.architectures.llvm.templating

/**
 * Special method to indent from second line and down due to how templating
 * works in Kotlin.
 */
fun include(tabs: Int, text: String): String {
	val split = text.split('\n', limit=2)

	if (split.size == 1)
		return split[0]

	val last = split[1].prependIndent("\t".repeat(tabs))

	return "${split[0]}\n${last}"
}