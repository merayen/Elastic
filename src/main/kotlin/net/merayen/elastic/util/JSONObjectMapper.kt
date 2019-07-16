package net.merayen.elastic.util

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.cast
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

/**
 * Converts JSONObject to POJOs.
 * Hardcoded for org.simple.json, because it seems we will stick with it for a while.
 * DOES NOT SUPPORT ARRAYS. Seems like they will need to be List<Any> if we are going to support this? :(
 */
class JSONObjectMapper {
	class AnonymousClassesNotSupportedException : RuntimeException()
	class CanNotConvertType(val inType: String, val outType: String) : RuntimeException("'$inType' can not be converted to '$outType'")
	class JsonMissingKey(val key: String, val className: String) : RuntimeException("JSON missing key '$key' for class '$className'")
	class ClassNotRegistered(val className: String) : RuntimeException("Class '$className' not registered")
	class ClassAlreadyRegistered(val className: String) : RuntimeException(className)

	private val registry = HashMap<String, KClass<out Any>>()
	private val translatorRegistry = HashMap<String,((name: String, value: Any?) -> Any?)?>()

	fun registerClass(klass: KClass<out Any>, translator: ((name: String, value: Any?) -> Any?)? = null) {
		val className = klass.simpleName ?: throw AnonymousClassesNotSupportedException()

		if (className in registry)
			throw ClassAlreadyRegistered(className)

		registry[className] = klass
		translatorRegistry[className] = translator
	}

	private val NOT_SET = Any()

	fun toObject(jsonobject: JSONObject): Any? {
		val className = (jsonobject.get("\$className$") ?: return null) as String
		val klass = registry[className] ?: return null
		val primaryConstructor = klass.primaryConstructor ?: return null
		val translator = translatorRegistry[className]

		val args = HashMap<KParameter, Any?>()
		for (parameter in primaryConstructor.parameters) {
			val name = parameter.name ?: continue // Unnamed constructor parameters (varargs? Not supporting that...)

			val subItem = jsonobject[name]
			var translatedSubItem: Any? = NOT_SET

			if (translator != null) {
				translatedSubItem = translator(name, subItem)

				if (translatedSubItem != null)
					args[parameter] = translatedSubItem
			}

			if (parameter !in args) {
				if (subItem is JSONObject) {
					val sub = toObject(subItem)
					if (sub != null) // It was possible to convert
						args[parameter] = sub
				} else if (subItem is JSONArray) {
					// We don't deal with array
				} else if (subItem != null) {
					args[parameter] = argConvert(jsonobject[name], parameter.type.jvmErasure)
				}
			}

			if (!parameter.isOptional && parameter !in args)
				throw JsonMissingKey(name, className)
		}

		try {
			return primaryConstructor.callBy(args)
		} catch (e: IllegalArgumentException) {
			println("nei")
			throw e
		}
	}

	fun toJson(obj: Any): JSONObject {
		val className = obj::class.simpleName ?: throw AnonymousClassesNotSupportedException()
		val klass = registry[className] ?: throw ClassNotRegistered(className)

		val primaryConstructor = klass.primaryConstructor
				?: throw RuntimeException("Class to serialize must have a primary constructor")

		val result = JSONObject()

		for (parameter in primaryConstructor.parameters) {
			val name = parameter.name ?: continue // Unnamed constructor parameters (varargs? Not supporting that...)

			for (property in obj::class.memberProperties) {
				if (property.name == name && property.visibility == KVisibility.PUBLIC) {
					val value = property.getter.call(obj)

					if (value == null) {
						result[name] = null
					} else if (value is Number || value is Boolean || value is String) {
						result[name] = value
					} else {
						result[name] = toJson(value)
					}
				}
			}
		}

		result["${"$"}className$"] = className

		return result
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

	fun <T : KClass<out Any>>argConvert(obj: Any?, to: T): T {
		return argConvert(obj, to) as T
	}
}