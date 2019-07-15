package net.merayen.elastic.util.treesettings

import org.junit.jupiter.api.Assertions

internal class InheritanceTreeTest {

	data class MyName(val name: String)
	data class MyAge(val age: Int)

	private lateinit var top: InheritanceTree
	private lateinit var left: InheritanceTree
	private lateinit var right: InheritanceTree
	private lateinit var rightBottom: InheritanceTree

	@org.junit.jupiter.api.BeforeEach
	fun setUp() {
		top = InheritanceTree()
		top.put(MyName("Merayen"))

		left = InheritanceTree()
		left.parent = top
		left.put(MyAge(24))

		right = InheritanceTree()
		right.put(MyName("Per"))
		right.parent = top

		rightBottom = InheritanceTree()
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
		val t1 = InheritanceTree()
		val t2 = InheritanceTree()
		val t3 = InheritanceTree()
		Assertions.assertThrows(InheritanceTree.ItemCanNotBeItsOwnParent::class.java) {
			t1.parent = t1
		}

		t2.parent = t1
		t3.parent = t2

		Assertions.assertThrows(InheritanceTree.AlreadyInTreeException::class.java) {
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
		val t1 = InheritanceTree()
		val someone = MyName("Someone")
		val someoneElse = MyName("Someone else")
		t1.put(someone)
		Assertions.assertSame(t1[MyName::class], someone)
		t1.put(someoneElse)
		Assertions.assertSame(t1[MyName::class], someoneElse)
	}

	@org.junit.jupiter.api.Test
	fun clear() {
		val t1 = InheritanceTree()
		t1.put(MyName("Gunnar"))
		t1.put(MyAge(42))
		t1.clear(MyName::class)
		Assertions.assertNull(t1[MyName::class])
	}
}