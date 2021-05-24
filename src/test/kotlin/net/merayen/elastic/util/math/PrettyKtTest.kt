package net.merayen.elastic.util.math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PrettyKtTest {
	@Test
	fun `test prettyNumber`() {
		assertEquals("0.0", prettyNumber(0))
		assertEquals("1.0", prettyNumber(1))
		assertEquals("999.0", prettyNumber(999))
		assertEquals("1.0K", prettyNumber(1000))
		assertEquals("1.9K", prettyNumber(1949))
		assertEquals("2.0K", prettyNumber(1950))
		assertEquals("2.0K", prettyNumber(2000))
		assertEquals("1.0M", prettyNumber(1000000))
		assertEquals("1.0G", prettyNumber(1000000000L))
		assertEquals("1.0T", prettyNumber(1000000000000L))
		assertEquals("1.0P", prettyNumber(1000000000000000L))
		assertEquals("A lot", prettyNumber(1000000000000000000L))
		assertEquals("0.0", prettyNumber(0.0000000000000000001))
		assertEquals("1.0f", prettyNumber(0.000000000000001))
		assertEquals("1.0p", prettyNumber(0.000000000001))
		assertEquals("1.0n", prettyNumber(0.000000001))
		assertEquals("1.0µ", prettyNumber(0.000001))
		assertEquals("1.0m", prettyNumber(0.00099))
		assertEquals("1.0m", prettyNumber(0.001))
	}
}