package net.merayen.elastic.util

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

internal class JSONObjectMapperTest {
	data class Man(
			var name: String? = null,
			var wife: Wife? = Wife("My default wife",
					true,
					true,
					18,
					45f,
					1.7),
			var reads: List<Book>? = null
	)

	data class Wife(
			var nickname: String? = null,
			var sexy: Boolean? = null,
			var hysterical: Boolean? = null,
			var age: Int? = null,
			var weight: Float? = null,
			var height: Double? = null
	)

	data class Book(
			var name: String? = null,
			var description: String? = "No description")

	data class Health(
			var issues: List<HealthIssue>? = null
	)

	interface HealthIssue {
		var painPercentage: Int?
	}

	data class BigToeHurts(
			override var painPercentage: Int? = null
	) : HealthIssue

	data class Nausea(
			override var painPercentage: Int? = null,
			val vomitsPerDay: Int? = null
	) : HealthIssue

	private lateinit var mapper: JSONObjectMapper

	@BeforeEach
	fun setUp() {
		mapper = JSONObjectMapper()
		mapper.registerClass(Man::class)
		mapper.registerClass(Wife::class)
		mapper.registerClass(Book::class)
		mapper.registerClass(Health::class)
		mapper.registerClass(BigToeHurts::class)
		mapper.registerClass(Nausea::class)
	}

	@Test
	fun convert() {
		val json = JSONParser().parse("""{"&className&": "Man", "name": "Thim"}""") as JSONObject
		assertEquals(mapper.toObject(json), Man("Thim"))
	}

	@Test
	fun testNonExistingClass() {
		val json = JSONParser().parse("""{"&className&": "Nonexistingclass", "name": "Thim"}""") as JSONObject
		assertThrows(JSONObjectMapper.ClassNotRegistered::class.java) {
			mapper.toObject(json)
		}
	}

	@Test
	fun recursiveMapping() {
		val toTest = JSONParser().parse(
				"""
					{
						"&className&": "Man", "name": "The Man", "wife": {
							"&className&": "Wife", "nickname": "Darling", "sexy": true, "age": 40, "weight": 124, "height": 1.52, "hysterical": false
						}
					}""".trimMargin()
		) as JSONObject

		val result = mapper.toObject(toTest)
		assertEquals(
				Man("The Man", wife = Wife("Darling", sexy = true, age = 40, weight = 124f, height = 1.52, hysterical = false)),
				result
		)
	}

	@Test
	fun testDefaultValues() {
		val json = JSONParser().parse("""{"&className&": "Book"}""") as JSONObject

		/*val exception = assertThrows(JSONObjectMapper.JsonMissingKey::class.java) {
			mapper.toObject(json)
		}*/

		val result = mapper.toObject(json) as Book

		assertEquals(null, result.name)
		assertEquals("No description", result.description)
	}

	@Test
	fun testInvalidDataClasses() {
		data class InvalidClass(val name: String? = null)

		val mapper = JSONObjectMapper()
		mapper.registerClass(InvalidClass::class)
		val exception = assertThrows(JSONObjectMapper.ClassMemberIsReadOnly::class.java) {
			mapper.toObject(JSONParser().parse("""{"&className&": "InvalidClass", "name": "Can not be set"}""") as JSONObject) as InvalidClass
		}

		assertEquals("InvalidClass", exception.className)
		assertEquals("name", exception.member)
	}

	@Test
	fun testConstructorMissingDefault() {
		data class InvalidClass(var name: String)

		val mapper = JSONObjectMapper()
		mapper.registerClass(InvalidClass::class)
		val exception = assertThrows(JSONObjectMapper.ConstructorMissingDefault::class.java) {
			mapper.toObject(JSONParser().parse("""{"&className&": "InvalidClass", "name": "Won't be set"}""") as JSONObject) as InvalidClass
		}

		assertEquals("InvalidClass", exception.className)
		assertEquals("name", exception.parameter)
	}

	@Test
	fun testMissingClassName() {
		assertThrows(JSONObjectMapper.MissingClassNameDefinitionInObject::class.java) {
			mapper.toObject(JSONParser().parse("""{"djkfshk": "Invalid"}""") as JSONObject)
		}
	}

	@Test
	fun testJSONArrayToObject() {
		val mapper = JSONObjectMapper()
		mapper.registerClass(Man::class)
		mapper.registerClass(Book::class)

		val json = JSONParser().parse("""
			{
				"&className&": "Man",
				"name": "The Man"
				"reads": [
					{"&className&": "Book", "name": "How to be a Man", "description": "Understand your gender roles"},
					{"&className&": "Book", "name": "How to treat wife", "description": "Learn how to treat your wife"}
				]
			}
		""".trimIndent()) as JSONObject

		assertEquals(
				Man(
						"The Man",
						reads = listOf(
								Book("How to be a Man", "Understand your gender roles"),
								Book("How to treat wife", "Learn how to treat your wife")
						)
				),
				mapper.toObject(json)
		)
	}

	@Test
	fun testConvertingNumbers() {
		data class Test(val p0: Double, val p1: Float, val p2: Long, val p3: Int, val p4: Short, val p5: Byte, val p6: Char)

		val number = JSONParser().parse("37.5")

		// Check if all types are supported for conversion
		assertEquals(37.5, mapper.argConvert(number, Test::class.primaryConstructor!!.parameters[0].type.jvmErasure))
		assertEquals(37.5f, mapper.argConvert(number, Test::class.primaryConstructor!!.parameters[1].type.jvmErasure))
		assertEquals(37L, mapper.argConvert(number, Test::class.primaryConstructor!!.parameters[2].type.jvmErasure))
		assertEquals(37, mapper.argConvert(number, Test::class.primaryConstructor!!.parameters[3].type.jvmErasure))
		assertEquals(37.toShort(), mapper.argConvert(number, Test::class.primaryConstructor!!.parameters[4].type.jvmErasure))
		assertEquals(37.toByte(), mapper.argConvert(number, Test::class.primaryConstructor!!.parameters[5].type.jvmErasure))
		assertEquals(37.toChar(), mapper.argConvert(number, Test::class.primaryConstructor!!.parameters[6].type.jvmErasure))
	}

	@Test
	fun testConvertingStrings() {
		data class Test(val p0: String, val p1: String?)
		val text = JSONParser().parse("\"Test\"")
		val nullText = JSONParser().parse("null")

		assertEquals("Test", mapper.argConvert(text, Test::class.primaryConstructor!!.parameters[0].type.jvmErasure))
		assertEquals(null, mapper.argConvert(nullText, Test::class.primaryConstructor!!.parameters[1].type.jvmErasure))
	}

	@Test
	fun testPrimitiveValuesInArray() {
		data class Entry(var array: List<Int>? = null)

		val mapper = JSONObjectMapper()
		mapper.registerClass(Entry::class)

		val obj = mapper.toObject(JSONParser().parse("""{"&className&": "Entry", "array": ["du",2,3.6,4,5,6,7,8,9,true,false,null]}""") as JSONObject) as Entry

		assertEquals("""{"array":["du",2,3.6,4,5,6,7,8,9,true,false,null],"&className&":"Entry"}""", JSONObject.toJSONString(mapper.toMap(obj)))

		assertEquals(3, obj.array!![2])

		assertThrows(ClassCastException::class.java) {
			obj.array!![0] as Number
		}
	}

	@Test
	fun testDumpingClassesWithNonDumpableMember() {
		data class TheClass(var name: String? = null) {
			val shouldNotBeDumped = 1337
		}

		val mapper = JSONObjectMapper()
		mapper.registerClass(TheClass::class)

		val result = mapper.toObject(JSONParser().parse("""{"&className&": "TheClass", "name": "Peopleperson"}""") as JSONObject) as TheClass

		assertEquals("Peopleperson", result.name)
	}

	@Test
	fun testNonConstructorMembers() {
		data class DaClass(var name: String? = null) {
			var age = 42
		}

		val mapper = JSONObjectMapper()
		mapper.registerClass(DaClass::class)

		val result = mapper.toObject(JSONParser().parse("""{"&className&": "DaClass", "name": "Smith John", "age": 59}""") as JSONObject) as DaClass

		assertEquals("Smith John", result.name)
		assertEquals(59, result.age)

		assertEquals("""{"&className&":"DaClass","name":"Smith John","age":59}""", JSONObject.toJSONString(mapper.toMap(result)))
	}

	@Test
	fun testNotSupportingAnonymousClasses() {
		assertThrows(JSONObjectMapper.AnonymousClassesNotSupportedException::class.java) {
			JSONObjectMapper().registerClass(object {}::class)
		}
	}

	@Test
	fun testDumpSingleObject() {
		val result = JSONObject.toJSONString(mapper.toMap(Book("Bible", "Contains Jesus")))
		assertEquals("""
			{"&className&":"Book","name":"Bible","description":"Contains Jesus"}
		""".trimIndent(), result)
	}

	@Test
	fun testDumpDeepObject() {
		val result = JSONObject.toJSONString(mapper.toMap(Man("John")))
		assertEquals("""{"&className&":"Man","wife":{"&className&":"Wife","sexy":true,"nickname":"My default wife","weight":45.0,"hysterical":true,"age":18,"height":1.7},"name":"John","reads":null}""", result)
	}

	@Test
	fun testDumpUnknownClass() {
		data class NonregisteredClass(val blabla: String)
		assertThrows(JSONObjectMapper.ClassNotRegistered::class.java) {
			mapper.toMap(NonregisteredClass("Hohoho"))
		}
	}

	@Test
	fun testDumpRestore() {
		val data = Man(
				"The Guy",
				reads = arrayListOf(
						Book("Food", "How men should make food"),
						Book("Cars", "How men should fix cars")
				)
		)
		val result = mapper.toObject(mapper.toMap(data))

		assertEquals(data, result)
	}
}