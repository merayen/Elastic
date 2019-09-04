package net.merayen.elastic.backend.nodes

import kotlin.reflect.KClass

abstract class BaseNodeProperties(
		var name: String? = null,
		var version: Int? = null,
		var parent: String? = null,
		var uiTranslation: UITranslation? = null
) {
	data class UITranslation(var x: Float? = null, var y: Float? = null)

	val classRegistry = arrayListOf<KClass<*>>(
			this::class,
			UITranslation::class
	)

	/**
	 * Add your own list translators if you use any List<...>-types, to ensure correct types.
	 * E.g, you want List<Float>, but that information is not contained in the JSON, so you need to convert it first.
	 */
	val listTranslators = mutableMapOf<String, (it: Any?) -> Any?>()
}