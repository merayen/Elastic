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


		def min_funksjon(a, b ,c)
			pass

		var hoh = 1

		noe(hei(1337))

		for i in range(1,5)
			noe(123)

		while 1
			pass

	""".trimIndent()

	val lexer = Lexer(programSimple)
	LexerOptimizer(LexerTraverse(lexer)).removeNoOpTokens()
	println(LexerPrinter(LexerTraverse(lexer)))
	val l = lexer
}