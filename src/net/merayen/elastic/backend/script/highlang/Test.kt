package net.merayen.elastic.backend.script.highlang

fun main(args: Array<String>) {
	val program = """
		var aFloatArray[512]
		var aFloatVariable = 0
		var anotherFloatVariable  # Not initialized. Has an undetermined value

		def my_function(aFloatArray[], aFloatVariable)  # arguments are scoped and hides the global ones due to equal names
			for i in range(
	""".trimIndent()

	val programSimple = """
		var hei = 123.456
		var hoh = 1
		kjhkk
	""".trimIndent()

	val lexer = Lexer(programSimple)
	println(LexerPrinter(LexerTraverse(lexer)))
	val l = lexer
}