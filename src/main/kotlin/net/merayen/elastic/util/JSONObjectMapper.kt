package net.merayen.elastic.util

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

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

	private val registry = HashMap<String, KClass<out Any>>()

	fun registerClass(klass: KClass<out Any>) {
		val className = klass.simpleName ?: throw AnonymousClassesNotSupportedException()

		registry[className] = klass
	}

	fun toObject(jsonobject: JSONObject): Any? {
		val className = (jsonobject.get("\$className$") ?: return null) as String
		val klass = registry[className] ?: return null
		val primaryConstructor = klass.primaryConstructor ?: return null

		val args = HashMap<KParameter, Any?>()
		for (parameter in primaryConstructor.parameters) {
			val name = parameter.name ?: continue // Unnamed constructor parameters (varargs? Not supporting that...)

			if (name in jsonobject) {
				val subItem = jsonobject[name]
				if (subItem is JSONObject) {
					val sub = toObject(subItem)
					if (sub != null) // It was possible to convert
						args[parameter] = sub
				} else if (subItem is JSONArray) {
					TODO()
				} else {
					args[parameter] = argConvert(jsonobject[name], parameter.type)
				}
			} else if (!parameter.isOptional) {
				throw JsonMissingKey(name, className)
			}
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
	fun argConvert(obj: Any?, to: KType): Any? {
		if (obj == null) return null

		if (to.toString() == "kotlin.String")
			return obj.toString()

		if (obj is Number) {
			when (to.toString()) {
				"kotlin.Double" -> return obj.toDouble()
				"kotlin.Double?" -> return obj.toDouble()
				"kotlin.Float" -> return obj.toFloat()
				"kotlin.Float?" -> return obj.toFloat()
				"kotlin.Long" -> return obj.toLong()
				"kotlin.Long?" -> return obj.toLong()
				"kotlin.Int" -> return obj.toInt()
				"kotlin.Int?" -> return obj.toInt()
				"kotlin.Short" -> return obj.toShort()
				"kotlin.Short?" -> return obj.toShort()
				"kotlin.Byte" -> return obj.toByte()
				"kotlin.Byte?" -> return obj.toByte()
				"kotlin.Char" -> return obj.toChar()
				"kotlin.Char?" -> return obj.toChar()
			}
		} else if (obj is Boolean) {
			if (to.toString() == "kotlin.Boolean")
				return obj
		}

		throw CanNotConvertType(obj::class.simpleName ?: "#unknown#", to.toString())
	}
}