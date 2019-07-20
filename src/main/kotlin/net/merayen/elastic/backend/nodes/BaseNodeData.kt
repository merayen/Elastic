package net.merayen.elastic.backend.nodes

import kotlin.reflect.KClass

abstract class BaseNodeData(
		var name: String? = null,
		var version: Int? = null,
		var parent: String? = null,
		var uiTranslation: UITranslation? = null,
		var logicNode: String? = null
) {
	data class UITranslation(var x: Float? = null, var y: Float? = null)

	val classRegistry = arrayListOf<KClass<*>>(
			this::class,
			UITranslation::class
	)
}