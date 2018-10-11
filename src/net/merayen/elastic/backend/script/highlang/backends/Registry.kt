package net.merayen.elastic.backend.script.highlang.backends

import net.merayen.elastic.backend.script.highlang.backends.c.CCompiler
import net.merayen.elastic.backend.script.highlang.backends.interpreter.InterpreterCompiler
import kotlin.reflect.KClass


enum class Registry(val compiler: KClass<out AbstractCompiler>) {
	INTERPRETER(InterpreterCompiler::class),
	C(CCompiler::class)
}