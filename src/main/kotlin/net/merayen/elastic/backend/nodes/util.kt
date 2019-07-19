package net.merayen.elastic.backend.nodes

import net.merayen.elastic.util.JSONObjectMapper
import kotlin.reflect.full.primaryConstructor

private val CLASS_PATH = "net.merayen.elastic.backend.logicnodes.list.%s_%d.%s"


fun getLogicNodeClass(name: String, version: Int): Class<out BaseLogicNode> {
	val cls: Class<out BaseLogicNode>
	try {
		return Class.forName(String.format(
				CLASS_PATH,
				name,
				version,
				"LogicNode"
		)) as Class<out BaseLogicNode>
	} catch (e: ClassNotFoundException) {
		throw RuntimeException(e)
	}
}

fun createLogicNode(name: String, version: Int): BaseLogicNode {
	return getLogicNodeClass(name, version).constructors[0].newInstance() as BaseLogicNode
}


fun getLogicNodeDataClass(name: String, version: Int): Class<out BaseNodeData> {
	val cls: Class<out BaseLogicNode>
	try {
		return Class.forName(String.format(
				CLASS_PATH,
				name,
				version,
				"Data"
		)) as Class<out BaseNodeData>
	} catch (e: ClassNotFoundException) {
		throw RuntimeException(e)
	}
}


fun getDataClassFromLogicNodeClass(klass: Class<out BaseLogicNode>): Class<out BaseNodeData> {
	val qualifiedName = klass.name ?: throw RuntimeException("Anonymous class not supported")

	if (!qualifiedName.endsWith(".LogicNode"))
		throw RuntimeException("Class must be called 'LogicNode'")

	val dataClassQualifiedName = qualifiedName.substring(0, qualifiedName.length - ".LogicNode".length) + ".Data"

	return Class.forName(dataClassQualifiedName) as Class<BaseNodeData>
}


fun mapToLogicNodeData(name: String, version: Int, data: Map<String, Any?>): BaseNodeData {
	val mapper = getMapperForLogicDataClass(getLogicNodeClass(name, version))
	return mapper.toObject(data) as BaseNodeData
}


fun getMapperForLogicDataClass(klass: Class<out BaseLogicNode>): JSONObjectMapper {
	val dataClassInstance: BaseNodeData

	val kotlinClass = getDataClassFromLogicNodeClass(klass).kotlin
	//if (!kotlinClass.isData)
	//	throw RuntimeException("Class ${kotlinClass.qualifiedName} must be a 'data class'")

	val primaryConstructor = kotlinClass.primaryConstructor
			?: throw RuntimeException("data class does not have a constructor")

	val nonNullAble = primaryConstructor.parameters.filter { !it.type.isMarkedNullable }

	if (nonNullAble.size > 0)
		throw RuntimeException("All arguments in data class must be nullable. Failed class: ${kotlinClass.qualifiedName}")

	dataClassInstance = primaryConstructor.callBy(mapOf())
	val mapper = JSONObjectMapper()

	mapper.registerClass(kotlinClass)

	for (klass in dataClassInstance.classRegistry)
		mapper.registerClass(klass)

	return mapper
}


fun logicNodeDataToMap(name: String, version: Int, data: BaseNodeData): Map<String, Any?> {
	val mapper = getMapperForLogicDataClass(getLogicNodeClass(name, version))
	return mapper.toMap(data)
}