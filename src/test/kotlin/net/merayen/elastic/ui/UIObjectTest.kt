package net.merayen.elastic.ui

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class UIObjectTest {

	@Test
	fun testTopMostProperty() {
		val top = UIObject()
		val child1 = UIObject()
		val child11 = UIObject()
		val child2 = UIObject()

		top.add(child1)
		top.add(child2)
		child1.add(child11)


		for (x in arrayOf(top, child1, child11, child2))
			Assertions.assertEquals(
				top,
				x.topMost
			)

		top.remove(child1)

		arrayOf(top, child2).forEach {
			Assertions.assertEquals(
				top,
				it.topMost
			)
		}

		arrayOf(child1, child11).forEach {
			Assertions.assertEquals(
				child1,
				it.topMost
			)
		}
	}
}