package net.merayen.elastic.backend.script.highlang

import net.merayen.elastic.backend.script.highlang.backends.HighlangBackendRegistry

fun main(args: Array<String>) {
	if (args.isEmpty()) {
		println("Missing command")
		return
	}

	when (args[0]) {
		"transpile" -> transpile(args.sliceArray(IntRange(1, args.size - 1)))
		else -> println("Unknown command")
	}
}

private fun transpile(args: Array<String>) {
	val options = HashMap<String, String>()

	for (x in args) {
		val subargs = x.split("=", limit = 2)

		if (subargs.size == 1)
			options[subargs[0]] = ""
		else
			options[subargs[0]] = subargs[1]
	}

	if (options["--target"] == null) {
		println("Transpile to what?")
		return
	}

	// Transpile from stdin
	val rawSource = generateSequence { readLine() }.joinToString("\n") { it }
	val parseResult = parse(rawSource)
	if ("--ast" in options)
		println(LexerPrinter(LexerTraverse(parseResult.lexer.result)))

	if (options["--target"] == "javascript") {
		val compiler = HighlangBackendRegistry.create(HighlangBackendRegistry.JAVASCRIPT, parseResult.highlangProcessor)
		compiler.compile()
	}

}

private fun parse(program: String) = Run(program)

private class Run(program: String) {
	val validation: Validation
	var runtimeResult: HashMap<String, Any>? = null
	val lexer = Lexer(program)
	val highlangProcessor: HighlangProcessor

	init {
		LexerOptimizer(LexerTraverse(lexer.result)).removeNoOpTokens()

		highlangProcessor = HighlangProcessor(lexer.result)
		validation = Validation(highlangProcessor)

		/*if (validation.errors.isEmpty()) {
			val compiler = HighlangBackendRegistry.create(HighlangBackendRegistry.INTERPRETER, highlangProcessor)
			compiler.compile()

			val runtime = compiler.getRuntime()
			runtime.run()

			runtimeResult = runtime.getRuntimeVariables()
		}*/
	}
}