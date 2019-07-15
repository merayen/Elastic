package net.merayen.playground

import org.json.simple.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

data class Person(val name: String, val age: Number, val analRetention: AnalRetention? = null)
data class AnalRetention(val percentage: Number)

/**
 * Converts JSONObject to POJOs
 */
class MapToObjectConverter {
	class AnonymousClassesNotSupportedException : RuntimeException()
	class CanNotConvertType(val inType: String, val outType: String) : RuntimeException("$inType can not be converted to $outType")

	private val registry = HashMap<String, KClass<out Any>>()

	fun registerClass(klass: KClass<out Any>) {
		val className = klass.simpleName ?: throw AnonymousClassesNotSupportedException()

		registry[className] = klass
	}

	fun convert(jsonobject: JSONObject): Any? {
		val className = jsonobject.get("\$className$") ?: return null
		val klass = registry[className] ?: return null
		val primaryConstructor = klass.primaryConstructor ?: return null

		val args = HashMap<KParameter, Any?>()
		for (parameter in primaryConstructor.parameters) {
			val name = parameter.name ?: continue // Unnamed constructor parameters (varargs? Not supporting that...)

			if (name in jsonobject) {
				// Recursion
				val subItem = jsonobject[name]
				if (subItem is JSONObject) {
					val sub = convert(subItem)
					if (sub != null) // It was possible to convert
						args[parameter] = sub
				} else {
					args[parameter] = jsonobject[name]
					println(parameter.type)
					//args[parameter] = argConvert(jsonobject[name])
				}
			}
		}

		return primaryConstructor.callBy(args)
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
				"kotlin.Float" -> return obj.toFloat()
				"kotlin.Long" -> return obj.toLong()
				"kotlin.Int" -> return obj.toInt()
				"kotlin.Short" -> return obj.toShort()
				"kotlin.Byte" -> return obj.toByte()
				"kotlin.Char" -> return obj.toChar()
			}
		}

		throw CanNotConvertType(obj::class.simpleName ?: "#unknown#", to.toString())
	}
}


fun main() {
	data class Fuk(val test: Short)
	val fuk = Fuk(1337)

	val tall: Number = 4.5f

	println(Double::class.isInstance(tall))
	println(fuk::class.primaryConstructor?.parameters?.get(0)?.type.toString() == "kotlin.Short")

	return
	val jayson = """{"${"$"}className$": "Person", "name": "Einar", "age": 31, "analRetention": {"${"$"}className$": "AnalRetention", "percentage": 97}}"""

	val converter = MapToObjectConverter()
	converter.registerClass(Person::class)
	converter.registerClass(AnalRetention::class)
	val result = converter.convert(org.json.simple.parser.JSONParser().parse(jayson) as JSONObject)
	println(result)
}