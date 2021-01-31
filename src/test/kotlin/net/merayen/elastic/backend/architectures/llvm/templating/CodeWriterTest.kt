package net.merayen.elastic.backend.architectures.llvm.templating

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CodeWriterTest {
	@Test
	fun nested() {
		fun inner(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("noe") {
					Call("something")
				}
			}
		}

		val result = object : CodeWriter() {
			init {
				Comment("""
					This is a multi-
					line comment!
					""")
				Include("stdio.h")
				IncludeLocal("local_header.h")

				Define("SOMETHING", "123")

				Statement("static int something = 1337")

				Comment("This is a struct")
				Struct("Person", listOf("instance1", "instance2")) {
					Member("char*", "name")
					Member("int", "age")
				}

				Method("void", "main", "char args[], int length") {
					If("length != 2") {
						Call("printf", """"Length must be 2! Got %i", length""")
					}
					ElseIf("length == 1337") {
						Statement("""printf("That was a very nice length of %i", length)""")
					}
					Else {
						Statement("""printf("Meh")""")
					}
					inner(this)
				}
			}
		}

		assertEquals("""
			/*
			 * This is a multi-
			 * line comment!
			 */
			#include <stdio.h>
			#include "local_header.h"
			#define SOMETHING 123
			static int something = 1337;
			// This is a struct
			struct Person {
				char* name;
				int age;
			}
			instance1, instance2;
			void main(char args[], int length) {
				if (length != 2) {
					printf("Length must be 2! Got %i", length);
				}
				else if (length == 1337) {
					printf("That was a very nice length of %i", length);
				}
				else {
					printf("Meh");
				}
				if (noe) {
					something();
				}
			}""".trimIndent(),
			result.toString()
		)
	}
}