package net.merayen.elastic.backend.script.highlang.backends

import net.merayen.elastic.backend.script.highlang.HighlangProcessor
import net.merayen.elastic.backend.script.highlang.backends.c.CCompiler
import net.merayen.elastic.backend.script.highlang.backends.interpreter.InterpreterCompiler
import net.merayen.elastic.backend.script.highlang.backends.javascript.JavascriptTranspiler
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor


enum class HighlangBackendRegistry(private val compiler: KClass<out AbstractCompiler>) {
	INTERPRETER(InterpreterCompiler::class),
	C(CCompiler::class),
	JAVASCRIPT(JavascriptTranspiler::class);

	companion object {
		fun create(entry: HighlangBackendRegistry, highlangProcessor: HighlangProcessor) = entry.compiler.primaryConstructor!!.call(highlangProcessor)
	}
}