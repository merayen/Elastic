package net.merayen.elastic.backend.script.highlang

import net.merayen.elastic.backend.script.highlang.backends.AbstractRuntime
import net.merayen.elastic.backend.script.highlang.backends.HighlangBackendRegistry

fun test(args: Array<String>) {
	testWorking()
	/*testUnknownVariable()
	testParentScopeVariableAccess()
	testVariableUsageBeforeDeclaration()
	testVariableNotDeclaredInFunction()
	testVariableShadowsFunctionArgument()
	testArgumentUsageInFunction()
	testReverseArgumentUsage()
	testDoubleDeclaration()
	testForLoop()
	testWhileLoop()
	testImport()*/

	//testVariableDeclaration()
}


private class TestRun(program: String) {
	val validation: Validation
	var runtimeResult: HashMap<String, Any>? = null

	init {
		val lexer = Lexer(program)
		LexerOptimizer(LexerTraverse(lexer.result)).removeNoOpTokens()
		println(LexerPrinter(LexerTraverse(lexer.result)))
		val highlangProcessor = HighlangProcessor(lexer.result)
		validation = Validation(highlangProcessor)

		if (validation.errors.isEmpty()) {
			val compiler = HighlangBackendRegistry.create(HighlangBackendRegistry.INTERPRETER, highlangProcessor)
			compiler.compile()

			val runtime = compiler.getRuntime()
			runtime.run()

			runtimeResult = runtime.getRuntimeVariables()
		}
	}
}


private fun parse(program: String) = TestRun(program).validation


private fun run(program: String): AbstractRuntime {
	val lexer = Lexer(program)
	LexerOptimizer(LexerTraverse(lexer.result)).removeNoOpTokens()
	println(LexerPrinter(LexerTraverse(lexer.result)))

	val highlangProcessor = HighlangProcessor(lexer.result)
	val validation = Validation(highlangProcessor)

	if (validation.errors.size > 0)
		throw RuntimeException("Errors during validation")

	val compiler = HighlangBackendRegistry.create(HighlangBackendRegistry.INTERPRETER, highlangProcessor)
	compiler.compile()

	val runtime = compiler.getRuntime()
	runtime.run()

	return runtime
}


private fun testWorking() {
	parse("""
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
	val v = parse("""
		unknown_variable = 5
	""".trimIndent())

	if (v.items.size != 1)
		no()
	if (v.items[0].type != Validation.Type.VARIABLE_NOT_DECLARED)
		no()
}


private fun testParentScopeVariableAccess() {
	val v = parse("""
		var my_var = 1337
		def func()
			my_var += 1
	""".trimIndent())

	if (v.items.size > 0)
		no()
}


private fun testVariableUsageBeforeDeclaration() {
	val v = parse("""
		def func()
			my_var += 1
			var my_var
	""".trimIndent())

	if (v.items.size != 1)
		no()
	if (v.items[0].type != Validation.Type.VARIABLE_NOT_DECLARED)
		no()
}

private fun testVariableNotDeclaredInFunction() {
	val v = parse("""
		def func()
			hei += 1
	""".trimIndent())

	if (v.items.size != 1)
		no()
	if (v.items[0].type != Validation.Type.VARIABLE_NOT_DECLARED)
		no()
}


private fun testVariableShadowsFunctionArgument() {
	val v = parse("""
		def func(argument)
			var argument
	""".trimIndent())

	if (v.items.size > 0)
		no()
}


private fun testArgumentUsageInFunction() {
	val v = parse("""
		def func(argument)
			argument += 1
	""".trimIndent())

	if (v.items.size > 0)
		no()
}


private fun testReverseArgumentUsage() {
	val v = parse("""
		def func()
			var argument
		argument += 1
	""".trimIndent())

	if (v.items.size != 1)
		no()

	if (v.items[0].type != Validation.Type.VARIABLE_NOT_DECLARED)
		no()
}


private fun testDoubleDeclaration() {
	val v = parse("""
		var my_var = 1337
		var my_var = 1338
	""".trimIndent())

	if (v.errors.size != 1)
		no()
	if (v.errors[0].type != Validation.Type.VARIABLE_ALREADY_DECLARED_IN_SCOPE)
		no()
	if (v.errors[0].token.line != 2)
		no()
}


private fun testForLoop() {
	val v = parse("""
		native def range(a, b)

		for i in range(1, 50)
			pass
	""".trimIndent())

	if (v.errors.size > 0)
		no()
}


private fun testWhileLoop() {
	val v = parse("""
		var i = 0
		while i < 10
			i += 1
	""".trimIndent())

	if (v.errors.size > 0)
		no()
}


private fun testImport() {
	parse("""
		import mylib
	""".trimIndent())
}


private fun testVariableDeclaration() {
	run("""
	module navn
		var global_var = 3
	module main
		def func()
			navn.global_var += 1
""".trimIndent())

	/*if (v.getRuntimeVariables().size != 1)
		no()*/

	/*if ((v.getRuntimeVariables().iterator().next().value as D == 5.0)
		no()*/
}


private fun no() {
	throw RuntimeException("Validation error")
}