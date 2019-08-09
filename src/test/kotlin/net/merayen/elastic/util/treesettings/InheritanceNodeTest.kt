package net.merayen.elastic.util.treesettings

import org.junit.jupiter.api.Assertions

internal class InheritanceNodeTest {
	data class MyName(val name: String)
	data class MyAge(val age: Int)

	private lateinit var top: InheritanceNode
	private lateinit var left: InheritanceNode
	private lateinit var right: InheritanceNode
	private lateinit var rightBottom: InheritanceNode

	@org.junit.jupiter.api.BeforeEach
	fun setUp() {
		top = InheritanceNode()
		top.put(MyName("Merayen"))

		left = InheritanceNode()
		left.parent = top
		left.put(MyAge(24))

		right = InheritanceNode()
		right.put(MyName("Per"))
		right.parent = top

		rightBottom = InheritanceNode()
		rightBottom.parent = right
	}

	@org.junit.jupiter.api.Test
	fun getParent() {
		Assertions.assertEquals(left.parent, top)
		Assertions.assertEquals(right.parent, top)
		Assertions.assertEquals(top.parent, null)
	}

	@org.junit.jupiter.api.Test
	fun setParent() {
		val t1 = InheritanceNode()
		val t2 = InheritanceNode()
		val t3 = InheritanceNode()
		Assertions.assertThrows(InheritanceNode.ItemCanNotBeItsOwnParent::class.java) {
			t1.parent = t1
		}

		t2.parent = t1
		t3.parent = t2

		Assertions.assertThrows(InheritanceNode.AlreadyInTreeException::class.java) {
			t1.parent = t3
		}

	}

	@org.junit.jupiter.api.Test
	fun getSetting() {
		Assertions.assertEquals(top[MyName::class], MyName("Merayen"))
		Assertions.assertEquals(left[MyName::class], MyName("Merayen"))
		Assertions.assertEquals(right[MyName::class], MyName("Per"))
		Assertions.assertNull(rightBottom[MyAge::class])
	}

	@org.junit.jupiter.api.Test
	fun isLocal() {
		Assertions.assertTrue(top.isLocal(MyName::class))
		Assertions.assertFalse(top.isLocal(MyAge::class))
		Assertions.assertFalse(rightBottom.isLocal(MyName::class))
		Assertions.assertFalse(rightBottom.isLocal(MyAge::class))
		Assertions.assertTrue(left.isLocal(MyAge::class))
	}

	@org.junit.jupiter.api.Test
	fun set() {
		val t1 = InheritanceNode()
		val someone = MyName("Someone")
		val someoneElse = MyName("Someone else")
		t1.put(someone)
		Assertions.assertSame(t1[MyName::class], someone)
		t1.put(someoneElse)
		Assertions.assertSame(t1[MyName::class], someoneElse)
	}

	@org.junit.jupiter.api.Test
	fun clear() {
		val t1 = InheritanceNode()
		t1.put(MyName("Gunnar"))
		t1.put(MyAge(42))
		t1.clear(MyName::class)
		Assertions.assertNull(t1[MyName::class])
	}
}