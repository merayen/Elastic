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


		def my_function(a, b ,c)
			pass

		var hoh = 1

		noe(hei(1337))
		hoh(7)

		for i in range(1,5)
			noe(123)
		while 1 + (2 * 5) / 4
			pass

		if (noe*5)**2 + (58.3 / 8 % 5)
			pass
		elif 8
			pass
		else
			pass
	""".trimIndent()

	val lexer = Lexer(programSimple)
	LexerOptimizer(LexerTraverse(lexer)).removeNoOpTokens()
	println(LexerPrinter(LexerTraverse(lexer)))
	val l = lexer
}