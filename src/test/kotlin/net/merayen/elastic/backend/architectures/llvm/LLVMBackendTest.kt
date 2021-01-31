package net.merayen.elastic.backend.architectures.llvm

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LLVMBackendTest {
	@Test
	fun `check if LLVM compiles and runs`() { // Test is outdated. Fix!
		val expectedString = "Hi from LLVMBackendTest from C-code!"

		val llvm = LLVMBackend("""
			#include <stdio.h>

			int main() {
				printf("$expectedString");
				return 0;
			}
		""".trimIndent())
		assertEquals(expectedString, String(llvm.inputStream.readAllBytes()))
	}
}