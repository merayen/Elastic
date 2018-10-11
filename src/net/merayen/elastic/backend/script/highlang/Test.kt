package net.merayen.elastic.backend.script.highlang

import java.lang.RuntimeException

fun main(args: Array<String>) {
	testWorking()
	testUnknownVariable()
	testDoubleDeclaration()
	testForLoop()
	testWhileLoop()
	testImport()
}


private fun run(program: String): Validation {
	val lexer = Lexer(program)
	LexerOptimizer(LexerTraverse(lexer.result)).removeNoOpTokens()
	println(LexerPrinter(LexerTraverse(lexer.result)))
	return Validation(lexer.result)
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


private fun testUnknownVariable() {
	val v = run("""
		unknown_variable = 5
	""".trimIndent())

	if (v.items.size != 1)
		no()
	if (v.items[0].type != Validation.Type.VARIABLE_NOT_DECLARED)
		no()
}


private fun testDoubleDeclaration() {
	val v = run("""
		var my_var = 1337
		var my_var = 1338
	""".trimIndent())

	if (v.errors.size != 1)
		no()
	if (v.errors[0].type != Validation.Type.VARIABLE_ALREADY_DECLARED)
		no()
	if (v.errors[0].token.line != 2)
		no()
}


private fun testForLoop() {
	val v = run("""
		native def range(a, b)

		for i in range(1, 50)
			pass
	""".trimIndent())

	if (v.errors.size > 0)
		no()
}


private fun testWhileLoop() {
	val v = run("""
		var i = 0
		while i < 10
			i += 1
	""".trimIndent())

	if (v.errors.size > 0)
		no()
}


private fun testImport() {
	val v = run("""
		import mylib
	""".trimIndent())
}


private fun testOverlapVariableWarning() {
	run("""
		var variable = 5

		def function(variable)
			return variable + 2
	""".trimIndent())
}


private fun no() {
	throw RuntimeException("Validation error")
}