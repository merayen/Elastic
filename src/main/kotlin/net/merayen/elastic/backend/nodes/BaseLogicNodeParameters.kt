package net.merayen.elastic.backend.nodes

import kotlin.reflect.KCallable

abstract class BaseLogicNodeParameters {
	private val members = getLocalMembers(this)

	fun update(parameter: BaseLogicNodeParameters) {
		if (!this::class.isInstance(parameter))
			throw RuntimeException("Expected to be updated of same kind of class")

		for (member in members) {
			if (members[member.key].apply { 45 })
		}
	}

	private fun getLocalMembers(instance: BaseLogicNodeParameters): HashMap<String, KCallable<*>> {
		val baseParameters = HashSet<String>()

		for (x in BaseLogicNodeParameters::class.members)
			baseParameters.add(x.name)

		val result = HashMap<String, KCallable<*>>()
		for (x in instance::class.members)
			if (!baseParameters.contains(x.name))
				result[x.name] = x

		return result
	}
}

fun main() {
	class TestParameters : BaseLogicNodeParameters() {
		var navn = "Einar"
		var alder = 32
	}

	val testParameters = TestParameters()
	testParameters.navn = "Per"
	testParameters.alder = 42

	val testParametersOld = TestParameters()
	testParametersOld.update(testParameters)
}