package net.merayen.elastic.backend.script.parser

class Parser(val text: String) {
	abstract class Item(val name: String, val children: ArrayList<Item> = ArrayList())

	class Function(name: String) : Item(name)

	abstract class Parameter(name: String) : Item(name)
	class Variable(name: String) : Parameter(name)
	class ArrayVariable(name: String): Parameter(name)
	class Constant(name: String) : Parameter(name)

	val result = Function("program")

	init {
		val lines = text.split("\n")
				.map { // Clears out any comments
					if ('#' in it)
						it.substring(0, it.indexOf('#'))
					else
						it
				}
				.filter { it.trim().length > 0 }
		parse(result, lines)
	}

	private fun parse(item: Item, lines: List<String>) {
		val parameterIndices = calculateFunctionParameterIndices(lines)

		for (parameterIndex in parameterIndices) {
			val level = calculateLevel(lines[parameterIndex])
			val value = lines[parameterIndex].substring(level).trim()

			val resultItem = when {
				value[0] == '@' -> Variable(value)
				value.startsWith("[]") -> ArrayVariable(value)
				value[0] in "-0123456789" -> Constant(value)
				else -> Function(value)
			}

			item.children.add(resultItem)

			parse(resultItem, lines.subList(parameterIndex, lines.size))
		}
	}

	private fun calculateLevel(text: String): Int {
		var level = 0

		for (x in text)
			if (x == '\t')
				level++
			else
				break

		return level
	}

	private fun calculateFunctionParameterIndices(lines: List<String>): List<Int> {
		val currentLevel = calculateLevel(lines[0])

		val result = ArrayList<Int>()
		var i = 1
		for (line in lines.subList(1, lines.size)) {
			val level = calculateLevel(line)

			if (level < currentLevel + 1)
				break // End of our tree
			else if (level == currentLevel + 1)
				result.add(i) // This is a parameter for current function

			i++
		}

		return result
	}
}

class ParserPrinter(val parser: Parser) {
	override fun toString() = elementToString(parser.result, 0)

	private fun elementToString(item: Parser.Item, level: Int): String {
		var result = item.name.toString() + "  # " + item.javaClass.simpleName

		result = "\t".repeat(level) + result + "\n"

		for (child in item.children)
			result += elementToString(child, level + 1)

		return result
	}
}