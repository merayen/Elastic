package net.merayen.elastic.backend.analyzer.node_dependency

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class WorkUnitDependencyListTest {
	@Test
	fun `simple case`() {
		val dl = DependencyList<String>()
		dl["top"] = hashSetOf()

		dl["value1"] = hashSetOf()
		dl["value2"] = hashSetOf()

		dl["group"] = hashSetOf("value1")
		dl["group_in"] = hashSetOf()
		dl["group_something1"] = hashSetOf("group_in")
		dl["group_something2"] = hashSetOf("group_in")
		dl["group_out"] = hashSetOf("group_something1", "group_something2")

		dl["add"] = hashSetOf("value1", "value2")
		dl["something1"] = hashSetOf("add")
		dl["something2"] = hashSetOf("add")
		dl["mix1"] = hashSetOf("something1", "something2")

		dl["mix2"] = hashSetOf("group", "group_in", "group_something1", "group_something2", "group_out", "mix1")

		dl["out"] = hashSetOf("mix2")

		val pg = WorkUnitDependencyList(dl)

	}

	@Test
	fun `serial nodes`() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf()
		dl["b"] = hashSetOf("a")
		dl["c"] = hashSetOf("b")
		dl["d"] = hashSetOf("c")

		val wudl = WorkUnitDependencyList(dl)

		assertEquals(4, wudl.keys.size)
	}

	@Test
	fun parallel() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf()
		dl["b"] = hashSetOf("a")
		dl["c"] = hashSetOf("a")

		val wudl = WorkUnitDependencyList(dl)
		assertEquals(3, wudl.size)

		val sources = wudl.getSources()
		assertEquals(1, sources.size)
	}

	@Test
	fun forking() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf()
		dl["b"] = hashSetOf()
		dl["c"] = hashSetOf("a", "b")

		val wudl = WorkUnitDependencyList(dl)
		assertEquals(3, wudl.size)

		val aWorkUnits = wudl.keys.filter { b -> "a" in b.nodes }
		assertEquals(1, aWorkUnits.size)

		val bWorkUnits = wudl.keys.filter { b -> "b" in b.nodes }
		assertEquals(1, bWorkUnits.size)

		val cWorkUnits = wudl.keys.filter { b -> "c" in b.nodes }
		assertEquals(1, cWorkUnits.size)

		assertTrue(wudl[aWorkUnits.first()]!!.isEmpty())
		assertEquals(hashSetOf(aWorkUnits.first(), bWorkUnits.first()), wudl[cWorkUnits.first()]) // Work unit C should depend on A and B
	}

	@Test
	fun `collapse serial nodes`() {
		val dl = DependencyList<String>()
		dl["a"] = hashSetOf() // Left-most, its own group

		// Single group, should be 1 group
		dl["b"] = hashSetOf("a")

		// 2 serially connected nodes, should be 1 group
		dl["c"] = hashSetOf("a")
		dl["d"] = hashSetOf("c")

		// 3 serially connected nodes, should be 1 group
		dl["e"] = hashSetOf("a")
		dl["f"] = hashSetOf("e")
		dl["g"] = hashSetOf("f")

		dl["h"] = hashSetOf("a") // This one should be grouped with the one below
		dl["i"] = hashSetOf("h")
		dl["j"] = hashSetOf("i") // These two extracts forks from i, should be 2 groups in total
		dl["k"] = hashSetOf("i")

		dl.validate()

		val wudl = WorkUnitDependencyList(dl)
		assertEquals(11, wudl.size)
		wudl.collapseSerials()
		println(wudl.values.map { it.map { it.nodes } })
		assertEquals(7, wudl.size)
	}
}