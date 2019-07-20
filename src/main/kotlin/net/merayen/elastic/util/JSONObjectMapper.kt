package net.merayen.elastic.util

import org.json.simple.JSONArray
import kotlin.reflect.*
import kotlin.reflect.full.cast
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Converts JSONObject to POJOs.
 * Hardcoded for org.simple.json, because it seems we will stick with it for a while, and it is small.
 */
class JSONObjectMapper {
	class AnonymousClassesNotSupportedException : RuntimeException()
	class ClassNotRegistered(val className: String) : RuntimeException("Class '$className' not registered")
	class ClassAlreadyRegistered(val className: String) : RuntimeException(className)
	class GenericListsLimitedSupport() : RuntimeException("Only JSONObject, Number, String and Boolean is supported in List<>s")
	class MissingClassNameDefinitionInObject() : RuntimeException()
	class ClassMemberIsReadOnly(val className: String, val member: String) : RuntimeException("Can not apply value to '$member' on class '$className' as it is read-only")
	class ConstructorMissingDefault(val className: String, val parameter: String) : RuntimeException("Class '$className' constructor missing default value for parameter $parameter")

	private val registry = HashMap<String, KClass<out Any>>()
	private val listConverters = HashMap<String, Map<String, (it: Any?) -> Any?>>()

	companion object {
		val CLASSNAME_IDENTIFIER = "&className&"
	}

	/**
	 * Register a new class to (de)serialize to.
	 * Due to JVM type erasure for generics, JSONObjectMapper also supports lambdas to convert/cast array items to correct type
	 * @param klass Class that is formatted with "var value: String? = null"-members
	 * @param listConverter Map of lambdas that will ensure that types are converted/cast to correct type
	 */
	fun registerClass(klass: KClass<*>, listConverter: (Map<String, (it: Any?) -> Any?>)? = null) {
		val className = klass.simpleName ?: throw AnonymousClassesNotSupportedException()

		if (className in registry)
			throw ClassAlreadyRegistered(className)

		registry[className] = klass

		if (listConverter != null)
			listConverters[className] = listConverter
	}

	private val NOT_SET = Any()

	fun toObject(jsonobject: Map<*, *>): Any? {
		val className = (jsonobject.get(CLASSNAME_IDENTIFIER) ?: throw MissingClassNameDefinitionInObject()) as String
		val klass = registry[className] ?: throw ClassNotRegistered(className)
		val primaryConstructor = klass.primaryConstructor ?: return null

		verifyConstructor(className, primaryConstructor)

		// Build parameters that the class will be initialized with
		val args = HashMap<KMutableProperty<*>, Any?>()
		val memberProperties = klass.memberProperties.filter { !it.isConst && it.visibility == KVisibility.PUBLIC }
		for (member in memberProperties) {
			val name = member.name
			if (member is KMutableProperty<*>) {
				val subItem = jsonobject[name]

				if (member !in args) {
					if (subItem is Map<*, *>) {
						val sub = toObject(subItem)
						if (sub != null) // It was possible to convert
							args[member] = sub
					} else if (subItem is JSONArray) {
						val listConverter = listConverters[className]?.get(name)
						args[member] = subItem.map {
							if (listConverter != null) {
								listConverter(it)
							} else if (it is Map<*, *>)
								toObject(it)
							else if (it == null || it is Number || it is String || it is Boolean)
								it
							else
								throw GenericListsLimitedSupport()
						}
					} else if (subItem != null) {
						args[member] = argConvert(jsonobject[name], member.setter.parameters[1].type.jvmErasure)
					}
				}
			} else if (name in jsonobject) {
				throw ClassMemberIsReadOnly(className, name)
			}
		}

		// Create instance with just nulls
		val result = primaryConstructor.callBy(HashMap())

		// Then apply all the values afterwards
		for ((property, value) in args) {
			if (property.name in jsonobject)
				property.setter.call(result, value)
		}

		return result
	}

	/**
	 * Verifies that all the parameters in the constructor has defaults.
	 */
	private fun verifyConstructor(className: String, primaryConstructor: KFunction<Any>) {
		for (parameter in primaryConstructor.parameters) {
			if (!parameter.isOptional)
				throw ConstructorMissingDefault(className, parameter.name ?: "#UNKNOWN#")
		}
	}

	fun toMap(obj: Any): Map<String, Any?> {
		val className = obj::class.simpleName ?: throw AnonymousClassesNotSupportedException()
		registry[className] ?: throw ClassNotRegistered(className)

		val result = HashMap<String, Any?>()

		for (property in obj::class.memberProperties) {
			if (property.visibility == KVisibility.PUBLIC && property is KMutableProperty<*>) {
				if (property.name == "classRegistry")
					println("Noes")
				val jsonValue = valueToJSON(property.getter.call(obj))
				if (jsonValue !== UNDEFINED)
					result[property.name] = jsonValue
			}
		}

		result[CLASSNAME_IDENTIFIER] = className

		return result
	}

	private val UNDEFINED = Any()

	private fun valueToJSON(value: Any?): Any? {
		if (value == null || value is Number || value is Boolean || value is String) {
			return value
		} else if (value is List<*>) {
			val arr = JSONArray()
			arr.addAll(value.map { valueToJSON(it) })
			return arr
		} else if (registry[value::class.simpleName] === value::class) {
			return toMap(value)
		}

		return UNDEFINED
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