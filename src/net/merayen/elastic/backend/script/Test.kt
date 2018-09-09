package net.merayen.elastic.backend.script

import net.merayen.elastic.backend.script.interpreter.Interpreter
import net.merayen.elastic.backend.script.parser.Parser

fun main(args: Array<String>) {
	var program = """
		# This is a program
		program
			dim
				[]my_array
				101

			let
				[]my_array
				0
				1

			let
				@i
				0
			while  # while i < 100
				less-than
					@i
					100
				do
					let  # my_array[i+1] = my_array[i]
						[]my_array
						add
							@i
							1
						get
							[]my_array
							@i

					increment  # i++
						@i
						1
		""".trimIndent()

	var parsing = -System.currentTimeMillis();
	val parser = Parser(program)
	parsing += System.currentTimeMillis()

	var setup = -System.currentTimeMillis()
	val interpreter = Interpreter(parser)
	setup += System.currentTimeMillis()

	var run = -System.currentTimeMillis()
	interpreter.run()
	run += System.currentTimeMillis()

	println("Variables: ${interpreter.environment.variables}")
	print("Array variables: ")
	for (array in interpreter.environment.arrayVariables) {
		print("\t${array.key}\n\t")
		for (f in array.value) {
			print(f.toString().padEnd(10))
		}
	}

	println()

	println("Execution times:\n\tParsing source: ${parsing}ms\n\tReading tree: ${setup}ms\n\tExecuting program: ${run}ms")
}