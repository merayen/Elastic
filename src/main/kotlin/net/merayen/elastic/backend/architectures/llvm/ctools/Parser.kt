package net.merayen.elastic.backend.architectures.llvm.ctools

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Parses a C-file and makes tokens out of it.
 *
 * Minimalistic parsing, only for beautifying the C-code.
 */
class Parser(val text: String) {
	var position = 0
}

abstract class Token(val parser: Parser) {
	class Fail : RuntimeException()

	fun no() = Fail()

	private var read = 0

	fun <T : Token>consume(cls: KClass<out T>): T? {
		try {
			val instance = cls.primaryConstructor!!.call(parser)
			parser.position += instance.read
			return instance
		} catch (e: Fail) {
			return null
		}
	}

	fun consume(vararg text: String): String? {
		for (x in text)
			if (parser.text.slice(parser.position until x.length) == x)
				return x

		return null
	}

	fun consume_until(vararg text: String): String {
		TODO()
	}
}

class Body(parser: Parser) : Token(parser) {
	init {
	}
}

class Whitespace(parser: Parser) : Token(parser) {
	init {
		while (true) {
			consume(" ", "\t", "")
		}
	}
}

fun parse(text: String) {
	val parser = Parser(text)
	val body = Body(parser)
}