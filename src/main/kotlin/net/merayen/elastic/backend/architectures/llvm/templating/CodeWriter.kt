package net.merayen.elastic.backend.architectures.llvm.templating

/**
 * Encapsulates a StringBuilder and takes care of indentation.
 *
 * This ended up looking very ugly when in actual use... Delete?
 */
open class CodeWriter {
	private val result = StringBuilder()
	private var level = 0

	/**
	 * When 0 or more, we do not allow code to exit using break; continue; or return;
	 */
	private var noExitLevel = -1

	fun Include(value: String) = internalAdd("#include <$value>")

	fun IncludeLocal(value: String) = internalAdd("#include \"$value\"")

	fun Define(name: String, value: String) = internalAdd("#define $name $value")

	fun Struct(name: String, instances: List<String> = listOf(), block: () -> Unit) {
		internalAdd("struct $name", block, instances.joinToString(", ") + ";")
		internalAdd()
	}

	/**
	 * A member of a Struct.
	 */
	fun Member(type: String, name: String) = add("$type $name;")

	fun Statement(text: String) = internalAdd("$text;")

	fun Method(returnType: String, name: String, args: String = "", block: (() -> Unit)? = null) {
		if (block != null)
			internalAdd("$returnType $name($args)", block)
		else
			internalAdd("$returnType $name($args);")
	}

	fun Call(name: String, args: String = "") = internalAdd("$name($args);")

	fun If(condition: String, block: () -> Unit) = internalAdd("if ($condition)", block)

	fun ElseIf(condition: String, block: () -> Unit) = internalAdd("else if ($condition)", block)

	fun Else(block: () -> Unit) = internalAdd("else", block)

	fun While(text: String, block: () -> Unit) = internalAdd("while ($text)", block)

	fun For(init: String = "", until: String = "", inc: String = "", block: () -> Unit) = internalAdd("for ($init; $until; $inc)", block)

	fun Return(code: String = "") = internalAdd("return $code;")
	fun Return(code: Number) = internalAdd("return $code;")

	fun Continue() = internalAdd("continue;")

	fun Break() = internalAdd("break;")

	/**
	 * Encapsulate code in a block.
	 *
	 * @param disallowExit If true, user is not allowed to exit the block prematurely (continue; break; are forbidden if they escape the block, and return; is always forbidden)
	 */
	fun Block(disallowExit: Boolean = false, func: () -> Unit) {
		// TODO implement disallowExit?
		internalAdd(func = func)
	}

	fun Comment(text: String) {
		val lines = text.trimIndent().lines()
		if (lines.size > 1) {
			internalAdd("/*")
			for (line in lines)
				internalAdd(" * $line")
			internalAdd(" */")
		} else {
			internalAdd("// $text")
		}
	}

	private fun add(text: String = "", func: (() -> Unit)? = null, after: String = "") = internalAdd(text.trimIndent(), func, after)

	private fun internalAdd(text: String = "", func: (() -> Unit)? = null, after: String = "") {
		if (func != null) {
			result.appendLine(text.prependIndent("\t".repeat(level)) + " {")
			level += 1
			func()
			level -= 1
			result.appendLine("\t".repeat(level) + "}" + after)
		} else {
			result.appendLine(text.prependIndent("\t".repeat(level)) + after)
		}
	}

	override fun toString() = result.trim().lines().filter { it.trim().isNotEmpty() }.joinToString("\n")
}