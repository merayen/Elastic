package net.merayen.elastic.backend.nodes

import net.merayen.elastic.util.ClassInstanceMerger
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
	val constructor = getLogicNodeClass(name, version).constructors[0]
	if (constructor.parameters.size > 0)
		throw RuntimeException("LogicNode should not have a constructor with parameters")

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
		throw RuntimeException("Could not find the Data-class for ${name}_${version}. Forgotten to create it?")
	}
}


fun getDataClassFromLogicNodeClass(klass: Class<out BaseLogicNode>): Class<out BaseNodeData> {
	val qualifiedName = klass.name ?: throw RuntimeException("Anonymous class not supported")

	if (!qualifiedName.endsWith(".LogicNode"))
		throw RuntimeException("Class must be called 'LogicNode'")

	val dataClassQualifiedName = qualifiedName.substring(0, qualifiedName.length - ".LogicNode".length) + ".Data"

	return Class.forName(dataClassQualifiedName) as Class<BaseNodeData>
}


/**
 * Get an empty instance of a BaseNodeData for a LogicNode
 */
fun getNewNodeData(logicNode: BaseLogicNode): BaseNodeData {
	return getDataClassFromLogicNodeClass(logicNode.javaClass).kotlin.primaryConstructor!!.callBy(mapOf())
}


fun createNewNodeData(name: String, version: Int): BaseNodeData {
	return getLogicNodeDataClass(name, version).kotlin.primaryConstructor!!.callBy(mapOf())
}


fun mapToLogicNodeData(name: String, version: Int, data: MutableMap<String, Any?>): BaseNodeData {
	val mapper = getMapperForLogicDataClass(getLogicNodeClass(name, version))

	// Add the name of the Data-class, which is always "Data"
	data[JSONObjectMapper.CLASSNAME_IDENTIFIER] = "Data"
	return mapper.toObject(data) as BaseNodeData
}


fun getMapperForLogicDataClass(klass: Class<out BaseLogicNode>): JSONObjectMapper {
	val dataClassInstance: BaseNodeData

	val kotlinClass = getDataClassFromLogicNodeClass(klass).kotlin

	val primaryConstructor = kotlinClass.primaryConstructor
			?: throw RuntimeException("data class does not have a constructor")

	val nonNullAble = primaryConstructor.parameters.filter { !it.type.isMarkedNullable }

	if (nonNullAble.size > 0)
		throw RuntimeException("All arguments in data class must be nullable. Failed class: ${kotlinClass.qualifiedName}")

	dataClassInstance = primaryConstructor.callBy(mapOf())
	val mapper = JSONObjectMapper()

	for (klass in dataClassInstance.classRegistry)
		mapper.registerClass(klass, dataClassInstance.listTranslators)

	return mapper
}


fun logicNodeDataToMap(name: String, version: Int, data: BaseNodeData): Map<String, Any?> {
	val mapper = getMapperForLogicDataClass(getLogicNodeClass(name, version))
	return mapper.toMap(data)
}


fun updateNodeData(name: String, version: Int, source: BaseNodeData, destination: MutableMap<String, Any?>) {
	val data = mapToLogicNodeData(name, version, destination)
	ClassInstanceMerger.merge(source, data)
	destination.putAll(logicNodeDataToMap(name, version, data))
}


fun updateNodeData(name: String, version: Int, source: Map<String, Any?>, destination: BaseNodeData) {
	val mapper = getMapperForLogicDataClass(getLogicNodeClass(name, version))
	val data = mapper.toObject(source) as Any
	ClassInstanceMerger.merge(data, destination)
}