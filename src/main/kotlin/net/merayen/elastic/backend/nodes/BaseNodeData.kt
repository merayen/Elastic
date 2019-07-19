package net.merayen.elastic.backend.nodes

import kotlin.reflect.KClass

open class BaseNodeData(
		var name: String? = null,
		var version: Int? = null,
		var parent: String? = null,
		var uiTranslation: UITranslation? = null
) {
	data class UITranslation(var x: Float, var y: Float)

	val classRegistry = ArrayList<KClass<*>>()
}