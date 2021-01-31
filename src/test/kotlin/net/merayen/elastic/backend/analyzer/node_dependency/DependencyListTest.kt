package net.merayen.elastic.backend.analyzer.node_dependency

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DependencyListTest {
	@Test
	fun dependsOn() {
		val nl = DependencyList<String>()
		nl["a"] = hashSetOf()
		nl["b"] = hashSetOf("a")
		nl["c"] = hashSetOf("b")
		nl.validate()

		assertTrue(nl.getAllDependents("a").isEmpty())

		assertEquals(setOf("a"), nl.getAllDependents("b"))
		assertEquals(setOf("b", "a"), nl.getAllDependents("c"))

		assertFalse { nl.hasCyclicDependencies() }

		assertEquals(setOf("a"), nl.getSources())
		assertEquals(setOf("c"), nl.getTargets())

		assertEquals(setOf<String>(), nl.getForks())
		assertEquals(setOf<String>(), nl.getJoins())
		assertEquals(setOf("a", "b", "c"), nl.getSerials())
	}

	@Test
	fun `independent nodes`() {
		val nl = DependencyList<String>()
		nl["a"] = hashSetOf() // Depends on no one but b depends on it
		nl["b"] = hashSetOf("a") // Depends on a
		nl["c"] = hashSetOf() // No one depends on it, and does not depend on anyone
		nl.validate()

		assertFalse { nl.hasCyclicDependencies() }
		assertEquals(setOf("a"), nl.getSources())
		assertEquals(setOf("b"), nl.getTargets())
		assertEquals(setOf("c"), nl.getIndependent())

		assertEquals(setOf<String>(), nl.getForks())
		assertEquals(setOf<String>(), nl.getJoins())
		assertEquals(setOf("a", "b", "c"), nl.getSerials())
	}

	@Test
	fun selfDependency() {
		val nl = DependencyList<String>()
		nl["a"] = hashSetOf("a")
		nl.validate()

		assertEquals(setOf("a"), nl.getAllDependents("a"))
		assertTrue { nl.hasCyclicDependencies() }
		assertTrue { nl.getSources().isEmpty() }
		assertTrue { nl.getTargets().isEmpty() }

		assertEquals(setOf<String>(), nl.getForks())
		assertEquals(setOf<String>(), nl.getJoins())
		assertEquals(setOf("a"), nl.getSerials())
	}

	@Test
	fun indirectSelfDependency() {
		val nl = DependencyList<String>()
		nl["a"] = hashSetOf("c")
		nl["b"] = hashSetOf("a")
		nl["c"] = hashSetOf("b")
		nl.validate()

		assertEquals(setOf("a", "b", "c"), nl.getAllDependents("a"))
		assertTrue { nl.hasCyclicDependencies() }
		assertTrue { nl.getSources().isEmpty() }
		assertTrue { nl.getTargets().isEmpty() }

		assertEquals(setOf<String>(), nl.getForks())
		assertEquals(setOf<String>(), nl.getJoins())
		assertEquals(setOf("a", "b", "c"), nl.getSerials())
	}

	@Test
	fun `spreaders and mergers`() {
		val nl = DependencyList<String>()
		nl["a"] = hashSetOf()
		nl["b"] = hashSetOf("a")
		nl["c"] = hashSetOf("a")
		nl["d"] = hashSetOf("b")
		nl["e"] = hashSetOf("b")
		nl["f"] = hashSetOf("d", "e")
		nl["g"] = hashSetOf("f", "c")
		nl.validate()

		assertEquals(setOf("a", "b"), nl.getForks())
		assertEquals(setOf("f", "g"), nl.getJoins())
		assertEquals(setOf("d", "e", "c"), nl.getSerials())
	}

	@Test
	fun `non-existent node dependency`() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf("a") // Should not throw
		dl.validate()
		dl["b"] = hashSetOf("unknown")

		assertThrows(DependencyList.Invalid::class.java) { dl.validate() }
	}

	@Test
	fun `walk single`() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf()

		val result = dl.walk()
		assertEquals(1, result.size)
		assertEquals(listOf("a"), result[0])
	}

	@Test
	fun `walk single fork`() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf()
		dl["b"] = hashSetOf("a")
		dl["c"] = hashSetOf("a")

		val result = dl.walk()
		assertEquals(3, result.size)
		assertTrue(result == listOf(listOf("a"), listOf("a", "b"), listOf("a", "c")) || result == listOf(listOf("a"), listOf("a", "c"), listOf("a", "b")))
	}

	@Test
	fun `walk join`() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf()
		dl["b"] = hashSetOf()
		dl["c"] = hashSetOf("a", "b")

		val result = dl.walk()
		assertEquals(4, result.size)
	}
}