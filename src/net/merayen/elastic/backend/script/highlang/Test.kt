package net.merayen.elastic.backend.script.highlang

fun main(args: Array<String>) {
	//testWorking()
	testUnknownVariable()
}


private fun run(program: String) {
	val lexer = Lexer(program)
	LexerOptimizer(LexerTraverse(lexer.result)).removeNoOpTokens()
	println(LexerPrinter(LexerTraverse(lexer.result)))
	Validation(lexer.result)
}


private fun testWorking() {
	run("""
		var variable = 123.456  # This is a comment
		var uninitialized  # Defaults to fp32 type. Initial value is undetermined
		var initialized: fp16 = 123.456
		var array[256]: fp16

		variable += 1.23 if variable > 5 + 2 else 3.21 + 3
		array[ 100 ] = array[variable]

		uninitialized += 1

		def function_a(a, b ,c)
			return a + b + c

		def function_b(variable)
			variable += 1
			return variable

		function_b(function_a(1, 2, variable))

		for i in range(1,5)
			function_b(i)
		while 1 + (2 * 5) / 4
			pass

		if (noe*5)**2 + (58.3 / 8 % 5)
			pass
		elif 8
			pass
		else
			pass
	""".trimIndent())
}


fun testUnknownVariable() {
	run("""
		unknown_variable = 5
	""".trimIndent())
}


fun testOverlapVariableWarning() {
	run("""
		var variable = 5

		def function(variable)
			return variable + 2

		function(10)
	""".trimIndent())
}