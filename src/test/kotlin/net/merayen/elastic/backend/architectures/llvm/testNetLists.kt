package net.merayen.elastic.backend.architectures.llvm

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.value_1.Properties
import net.merayen.elastic.system.intercom.*

fun addOneAndTwo(): List<ElasticMessage> {
	val value1Properties = Properties(value = 1f)
	val value2Properties = Properties(value = 2f)

	return listOf(
		// Create nodes
		CreateNodeMessage("top", "group", 1, null),
		CreateNodeMessage("value1", "value", 1, "top"),
		CreateNodeMessage("value2", "value", 1, "top"),
		CreateNodeMessage("add", "add", 1, "top"),
		CreateNodeMessage("out", "out", 1, "top"),

		// Add ports
		CreateNodePortMessage("value1", "out", true, Format.SIGNAL),
		CreateNodePortMessage("value2", "out", true, Format.SIGNAL),
		CreateNodePortMessage("add", "in1", false, null),
		CreateNodePortMessage("add", "in2", false, null),
		CreateNodePortMessage("add", "out", true, Format.SIGNAL),
		CreateNodePortMessage("out", "in", false, null),

		// Connect the ports
		NodeConnectMessage("value1", "out", "add", "in1"),
		NodeConnectMessage("value2", "out", "add", "in2"),
		NodeConnectMessage("add", "out", "out", "in"),

		// Send parameter value
		NodePropertyMessage("value1", value1Properties),
		NodePropertyMessage("value2", value2Properties),
	)
}