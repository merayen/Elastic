package net.merayen.elastic.backend.architectures.llvm.templating

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class UtilKtTest {
	@Test
	fun `test include`() {
		fun create() = """
			one();
			two();
		""".trimIndent()

		val result = """
			void noe() {
				${include(4, create())}
			}""".trimIndent()

		assertEquals("""
			void noe() {
				one();
				two();
			}
		""".trimIndent(), result)
	}
}