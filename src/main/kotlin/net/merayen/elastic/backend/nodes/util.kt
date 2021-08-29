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


fun getLogicNodePropertiesClass(name: String, version: Int): Class<out BaseNodeProperties> {
	try {
		return Class.forName(String.format(
				CLASS_PATH,
				name,
				version,
				"Properties"
		)) as Class<out BaseNodeProperties>
	} catch (e: ClassNotFoundException) {
		throw RuntimeException("Could not find the Properties-class for ${name}_${version}. Forgotten to create it?")
	}
}


fun getPropertiesClassFromLogicNodeClass(klass: Class<out BaseLogicNode>): Class<out BaseNodeProperties> {
	val qualifiedName = klass.name ?: throw RuntimeException("Anonymous class not supported")

	if (!qualifiedName.endsWith(".LogicNode"))
		throw RuntimeException("Class must be called 'LogicNode'")

	val propertiesClassQualifiedName = qualifiedName.substring(0, qualifiedName.length - ".LogicNode".length) + ".Properties"

	return Class.forName(propertiesClassQualifiedName) as Class<BaseNodeProperties>
}


/**
 * Get an empty instance of a BaseNodeProperties for a LogicNode
 */
fun getNewNodeProperties(logicNode: BaseLogicNode): BaseNodeProperties {
	return getPropertiesClassFromLogicNodeClass(logicNode.javaClass).kotlin.primaryConstructor!!.callBy(mapOf())
}


fun createNewNodeProperties(name: String, version: Int): BaseNodeProperties {
	return getLogicNodePropertiesClass(name, version).kotlin.primaryConstructor!!.callBy(mapOf())
}


fun mapToLogicNodeProperties(name: String, version: Int, properties: MutableMap<String, Any?>): BaseNodeProperties {
	val mapper = getMapperForLogicPropertiesClass(getLogicNodeClass(name, version))

	// Add the name of the Properties-class, which is always "Properties"
	properties[JSONObjectMapper.CLASSNAME_IDENTIFIER] = "Properties"
	return mapper.toObject(properties) as BaseNodeProperties
}


fun getMapperForLogicPropertiesClass(klass: Class<out BaseLogicNode>): JSONObjectMapper {
	val propertiesClassInstance: BaseNodeProperties

	val kotlinClass = getPropertiesClassFromLogicNodeClass(klass).kotlin

	val primaryConstructor = kotlinClass.primaryConstructor
			?: throw RuntimeException("data class does not have a constructor")

	val nonNullAble = primaryConstructor.parameters.filter { !it.type.isMarkedNullable }

	if (nonNullAble.size > 0)
		throw RuntimeException("All arguments in data class must be nullable. Failed class: ${kotlinClass.qualifiedName}")

	propertiesClassInstance = primaryConstructor.callBy(mapOf())
	val mapper = JSONObjectMapper()

	for (klass in propertiesClassInstance.classRegistry)
		mapper.registerClass(klass, propertiesClassInstance.listTranslators)

	return mapper
}


fun logicNodePropertiesToMap(name: String, version: Int, properties: BaseNodeProperties): Map<String, Any?> {
	val mapper = getMapperForLogicPropertiesClass(getLogicNodeClass(name, version))
	return mapper.toMap(properties)
}


fun updateNodeProperties(name: String, version: Int, source: BaseNodeProperties, destination: MutableMap<String, Any?>) {
	val properties = mapToLogicNodeProperties(name, version, destination)
	ClassInstanceMerger.merge(source, properties)
	destination.putAll(logicNodePropertiesToMap(name, version, properties))
}


fun updateNodeProperties(name: String, version: Int, source: Map<String, Any?>, destination: BaseNodeProperties) {
	val mapper = getMapperForLogicPropertiesClass(getLogicNodeClass(name, version))
	val properties = mapper.toObject(source) as Any
	ClassInstanceMerger.merge(properties, destination)
}