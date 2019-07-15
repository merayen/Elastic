package net.merayen.elastic.util.treesettings

import kotlin.reflect.KClass

/**
 * Hierarchical settings for trees, with inheritance.
 */
class InheritanceTree {
	class AlreadyInTreeException : RuntimeException()
	class ItemCanNotBeItsOwnParent : RuntimeException()

	var parent: InheritanceTree? = null
		set(value) {
			if (value == this)
				throw ItemCanNotBeItsOwnParent()

			if (value != null)
				if (this in value.getTree())
					throw AlreadyInTreeException()

			field = value
		}

	private val settings = HashMap<KClass<out Any>, Any>()

	operator fun get(klass: KClass<out Any>): Any? {
		var current: Any? = null

		for (treeSettings in getTree())
			current = treeSettings.settings[klass] ?: continue

		return current
	}

	/**
	 * Checks if the setting is set locally
	 */
	fun isLocal(klass: KClass<out Any>) = settings.contains(klass)

	/**
	 * Set a setting locally.
	 */
	fun put(instance: Any) {
		settings[instance::class] = instance
	}

	/**
	 * Clearing a setting on a child will make it inherit from parent.
	 */
	fun clear(klass: KClass<out Any>) {
		settings.remove(klass)
	}

	private fun getTree(): List<InheritanceTree> {
		val r = ArrayList<InheritanceTree>()

		r.add(this)

		while (r[r.size - 1].parent != null)
			r.add(r[r.size - 1].parent ?: break)

		r.reverse()
		return r
	}
}