package net.merayen.elastic.ui

import java.util.ArrayList
import java.util.Collections

class Search(private val obj: UIObject) {

	// TODO neste gang: endre mange til å være getWindow() i stedet
	val top: UIObject
		get() {
			var top = obj
			while (top.parent != null)
				top = top.parent!!

			return top
		}

	// Not tested
	val allChildren: List<UIObject>
		get() {
			val result = ArrayList<UIObject>()
			val stack = ArrayList<UIObject>()

			stack.add(obj)

			while (stack.size > 0) {
				val current = stack.removeAt(0)
				for (o in current.children) {
					result.add(o)
					stack.add(o)
				}
			}

			return result
		}

	val children: List<UIObject>
		get() = Collections.unmodifiableList(obj.children)

	/**
	 * Search downwards for a type
	 */
	fun childrenByType(cls: Class<out UIObject>): ArrayList<UIObject> {
		val result = ArrayList<UIObject>()

		for (x in allChildren)
			if (cls.isInstance(x))
				result.add(x)

		return result
	}

	fun <T : UIObject> parentByType(cls: Class<T>): T? {
		var x = obj.parent
		while (x != null && !cls.isAssignableFrom(x.javaClass))
			x = x.parent

		return if (x == null) null else x as T
	}

	fun <T> parentByInterface(cls: Class<T>): T? {
		var x = obj.parent
		while (x != null && !cls.isAssignableFrom(x.javaClass))
			x = x.parent

		return x as T
	}
}
