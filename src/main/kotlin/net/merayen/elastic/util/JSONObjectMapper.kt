package net.merayen.elastic.util

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.cast
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Converts JSONObject to POJOs.
 * Hardcoded for org.simple.json, because it seems we will stick with it for a while.
 * DOES NOT SUPPORT ARRAYS WITH GENERICS OTHER THAN OBJECTS (JSONObject). Seems like they will need to be in a List<Any> if we are going to support this? :(
 */
class JSONObjectMapper {
	class AnonymousClassesNotSupportedException : RuntimeException()
	class JsonMissingKey(val key: String, val className: String) : RuntimeException("JSON missing key '$key' for class '$className'")
	class ClassNotRegistered(val className: String) : RuntimeException("Class '$className' not registered")
	class ClassAlreadyRegistered(val className: String) : RuntimeException(className)
	class GenericListsLimitedSupport() : RuntimeException("Only JSONObject, Number, String and Boolean is supported in List<>s")

	private val registry = HashMap<String, KClass<out Any>>()
	private val translatorRegistry = HashMap<String,((name: String, value: Any?) -> Any?)?>()

	private val CLASSNAME_IDENTIFIER = "&className&"

	fun registerClass(klass: KClass<out Any>, translator: ((name: String, value: Any?) -> Any?)? = null) {
		val className = klass.simpleName ?: throw AnonymousClassesNotSupportedException()

		if (className in registry)
			throw ClassAlreadyRegistered(className)

		registry[className] = klass
		translatorRegistry[className] = translator
	}

	private val NOT_SET = Any()

	fun toObject(jsonobject: JSONObject): Any? {
		val className = (jsonobject.get(CLASSNAME_IDENTIFIER) ?: return null) as String
		val klass = registry[className] ?: return null
		val primaryConstructor = klass.primaryConstructor ?: return null
		val translator = translatorRegistry[className]

		val args = HashMap<KParameter, Any?>()
		for (parameter in primaryConstructor.parameters) {
			val name = parameter.name ?: continue // Unnamed constructor parameters (varargs? Not supporting that...)

			val subItem = jsonobject[name]

			if (translator != null) {
				val translatedSubItem = translator(name, subItem)

				if (translatedSubItem != null)
					args[parameter] = translatedSubItem
			}

			if (parameter !in args) {
				if (subItem is JSONObject) {
					val sub = toObject(subItem)
					if (sub != null) // It was possible to convert
						args[parameter] = sub
				} else if (subItem is JSONArray) {
					args[parameter] = subItem.map {
						if (it is JSONObject)
							toObject(it)
						else if (it is Number || it is String || it is Boolean)
							it
						else
							throw GenericListsLimitedSupport()
					}
				} else if (subItem != null) {
					args[parameter] = argConvert(jsonobject[name], parameter.type.jvmErasure)
				}
			}

			if (!parameter.isOptional && parameter !in args)
				throw JsonMissingKey(name, className)
		}

		return primaryConstructor.callBy(args)
	}

	fun toJson(obj: Any): JSONObject {
		val className = obj::class.simpleName ?: throw AnonymousClassesNotSupportedException()
		val klass = registry[className] ?: throw ClassNotRegistered(className)

		val primaryConstructor = klass.primaryConstructor
				?: throw RuntimeException("Class to serialize must have a primary constructor")

		val result = JSONObject()

		for (parameter in primaryConstructor.parameters) {
			val name = parameter.name ?: continue // Unnamed constructor parameters (varargs? Not supporting that...)

			for (property in obj::class.memberProperties)
				if (property.name == name && property.visibility == KVisibility.PUBLIC)
					result[name] = valueToJSON(property.getter.call(obj))
		}

		result[CLASSNAME_IDENTIFIER] = className

		return result
	}

	private fun valueToJSON(value: Any?): Any? {
		if (value == null || value is Number || value is Boolean || value is String) {
			return value
		} else if (value is List<*>) {
			val arr = JSONArray()
			arr.addAll(value.map { valueToJSON(it) })
			return arr
		} else {
			return toJson(value)
		}
	}

	/**
	 * Tries to convert arg down to correct instance
	 */
	fun argConvert(obj: Any?, to: KClass<out Any>): Any? {
		if (obj == null) return null

		if (obj is Number) {
			when (to.qualifiedName) {
				"kotlin.Double" -> return obj.toDouble()
				"kotlin.Float" -> return obj.toFloat()
				"kotlin.Long" -> return obj.toLong()
				"kotlin.Int" -> return obj.toInt()
				"kotlin.Short" -> return obj.toShort()
				"kotlin.Byte" -> return obj.toByte()
				"kotlin.Char" -> return obj.toChar()
			}
		}

		return to.cast(obj)
	}
}