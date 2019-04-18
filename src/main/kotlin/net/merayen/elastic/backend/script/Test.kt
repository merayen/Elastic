package net.merayen.elastic.backend.script

import net.merayen.elastic.backend.script.interpreter.Interpreter
import net.merayen.elastic.backend.script.parser.Parser

fun main(args: Array<String>) {
	var program = """
		# This program calculates prime numbers in the range 1 to 100
		# Python:
		#	def is_prime(n):
		#		if n <= 1:
		#			return False
		#
		#		if n <= 3:
		#			return True
		#
		#		if n % 2 == 0 or n % 3 == 0:
		#			return False
		#
		#		i = 5
		#		while i * i <= n:
		#			if n % i == 0 or n % (i + 2) == 0:
		#				return False
		#			i += 6
		#
		#		return True
		#
		#		for i in range(100):
		#			if is_prime(i):
		#				print(i)


		program
			dim
				@n

			dim
				@result
				1000

			dim
				@result_i

			dim
				@i

			dim
				@we found a prime number!  # ...yes. This is a valid variable name.

			let
				@i
				0

			let
				@n
				0

			let
				@result_i
				0

			while
				less-than
					@n
					7900
				do

					let
						@we found a prime number!
						0

					if
						less-than
							@n
							2
						then
							# Not a prime number
						else
							if
								less-than
									@n
									4
								then
									let
										@we found a prime number!  # Again... This is a valid variable name. Author of this language is retarded.
										1
								else
									if
										any  # or
											equal
												mod
													@n
													2
												0
											equal
												mod
													@n
													3
												0
										then
											let
												@we found a prime number!
												0
										else
											let
												@i
												5
											while
												all
													not
														equal
															round
																@i
															-1
													less-than-or-equal
														round  # Need to do this when we test for equal, as floating points are not that precise as integers
															multiply
																@i
																@i
														round  # Need to do this when we test for equal, as floating points are not that precise as integers
															@n
												do
													if
														any
															equal
																round  # Need to do this when we test for equal, as floating points are not that precise as integers
																	mod
																		@n
																		@i
																0
															equal
																round  # Need to do this when we test for equal, as floating points are not that precise as integers
																	mod
																		@n
																		add
																			@i
																			2
																0
														then
															let
																@we found a prime number!
																0
															let
																@i
																-1  # Breaks the loop... lol. Will implement "break" that will escape "while" and "program"
														else
															let
																@i
																add
																	@i
																	6
											let
												@we found a prime number!
												1
					if
						all
							@we found a prime number!
							not
								equal
									round
										@i
									-1
						then
							let-index  # A prime number!
								@result
								@result_i
								@n
							increment
								@result_i
					increment
						@n
		""".trimIndent()

	Parser(program) // Just to warm up the JVM

	var parsing = -System.currentTimeMillis()
	val parser = Parser(program)
	parsing += System.currentTimeMillis()

	for(i in 0 until 100) // Just to warm up the JVM
		Interpreter(parser).run()

	var setup = -System.currentTimeMillis()
	val interpreter = Interpreter(parser)
	setup += System.currentTimeMillis()

	var run = -System.currentTimeMillis()
	interpreter.run()
	run += System.currentTimeMillis()

	println("Memory after being run:\n" + interpreter.environment.memoryToString())

	println("Execution times:\n\tParsing source: ${parsing}ms\n\tReading tree: ${setup}ms\n\tExecuting program: ${run}ms")
}