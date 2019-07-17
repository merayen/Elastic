package net.merayen.elastic.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ClassInstanceMergerTest {
	@Test
	fun testDifferentClassType() {
		data class A(val a: Int)
		data class B(val a: Int)

		assertThrows(ClassInstanceMerger.MustBeTheSameClass::class.java) {
			ClassInstanceMerger.merge(A(1), B(1), null)
		}
	}

	@Test
	fun testMerge() {
		data class Test(
				var name: String? = null,
				var age: Int? = null
		)

		val result = Test("Per", 42)
		ClassInstanceMerger.merge(Test("Johan"), result, null)
		assertEquals(Test("Johan", 42), result)
	}
}