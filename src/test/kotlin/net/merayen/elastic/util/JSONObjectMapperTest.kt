package net.merayen.elastic.util

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.reflect.full.primaryConstructor

internal class JSONObjectMapperTest {
	data class Man(
			val name: String,
			val wife: Wife = Wife("My default wife",
					true,
					true,
					18,
					45f,
					1.7),
			val reads: List<Book>? = null
	)

	data class Wife(
			val nickname: String,
			val sexy: Boolean,
			val hysterical: Boolean,
			val age: Int?,
			val weight: Float?,
			val height: Double?
	)

	data class Book(
			val name: String,
			val description: String = "No description")

	data class Health(
			val issues: List<HealthIssue>
	)

	interface HealthIssue {
		val painPercentage: Int
	}

	data class BigToeHurts(
			override val painPercentage: Int
	) : HealthIssue

	data class Nausea(
			override val painPercentage: Int,
			val vomitsPerDay: Int
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
		val json = JSONParser().parse("""{"${"$"}className$": "Man", "name": "Thim"}""") as JSONObject
		assertEquals(mapper.toObject(json), Man("Thim"))
	}

	@Test
	fun testNonExistingClass() {
		val json = JSONParser().parse("""{"${"$"}className$": "Nonexistingclass", "name": "Thim"}""") as JSONObject
		assertNull(mapper.toObject(json))
	}

	@Test
	fun recursiveMapping() {
		val toTest = JSONParser().parse(
				"""
					{
						"${"$"}className$": "Man", "name": "The Man", "wife": {
							"${"$"}className$": "Wife", "nickname": "Darling", "sexy": true, "age": 40, "weight": 124, "height": 1.52, "hysterical": false
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
		val json = JSONParser().parse("""{"${"$"}className$": "Book"}""") as JSONObject

		val exception = assertThrows(JSONObjectMapper.JsonMissingKey::class.java) {
			mapper.toObject(json)
		}

		assertEquals("name", exception.key)
	}

	@Test
	fun testArrays() {
		val json = JSONParser().parse("""
			{
				"${"$"}className$": "Man",
				"name": "The Man"
				"reads": [
					{"${"$"}className$": "Book", "name": "How to be a Man", "description": "Understand your gender roles"},
					{"${"$"}className$": "Book", "name": "How to treat wife", "description": "Learn how to treat your wife"}
				]
			}
		""".trimIndent()) as JSONObject

		assertThrows(NotImplementedError::class.java) {
			mapper.toObject(json) // Not implemented yet
		}
	}

	@Test
	fun argConvert() {
		data class Test(val p0: Double, val p1: Float, val p2: Long, val p3: Int, val p4: Short, val p5: Byte, val p6: Char)

		val number = JSONParser().parse("37.5")

		// Check if all types are supported for conversion
		assertEquals(JSONObjectMapper().argConvert(number, Test::class.primaryConstructor!!.parameters[0].type), 37.5)
		assertEquals(JSONObjectMapper().argConvert(number, Test::class.primaryConstructor!!.parameters[1].type), 37.5f)
		assertEquals(JSONObjectMapper().argConvert(number, Test::class.primaryConstructor!!.parameters[2].type), 37L)
		assertEquals(JSONObjectMapper().argConvert(number, Test::class.primaryConstructor!!.parameters[3].type), 37)
		assertEquals(JSONObjectMapper().argConvert(number, Test::class.primaryConstructor!!.parameters[4].type), 37.toShort())
		assertEquals(JSONObjectMapper().argConvert(number, Test::class.primaryConstructor!!.parameters[5].type), 37.toByte())
		assertEquals(JSONObjectMapper().argConvert(number, Test::class.primaryConstructor!!.parameters[6].type), 37.toChar())
	}


	@Test
	fun testDumpSingleObject() {
		val result = mapper.toJson(Book("Bible", "Contains Jesus")).toJSONString()
		assertEquals("""
			{"${"$"}className$":"Book","name":"Bible","description":"Contains Jesus"}
		""".trimIndent(), result)
	}

	@Test
	fun testDumpDeepObject() {
		val result = mapper.toJson(Man("John")).toJSONString()
		assertEquals("""{"wife":{"sexy":true,"${"$"}className$":"Wife","nickname":"My default wife","weight":45.0,"hysterical":true,"age":18,"height":1.7},"${"$"}className$":"Man","name":"John","reads":null}""", result)
	}
}