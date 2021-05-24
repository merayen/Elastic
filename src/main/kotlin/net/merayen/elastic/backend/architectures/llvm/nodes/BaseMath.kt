package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.basemath.BaseMathProperties
import net.merayen.elastic.system.intercom.NodePropertyMessage

/**
 * We have 1 node for each type of math operation.
 * Here is the base node to keep common functionality at a single place.
 */
abstract class BaseMath(nodeId: String) : TranspilerNode(nodeId) {
	final override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			codeWriter.Member("float", "port_values[${getInputPorts().size}]")
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length != ${getInputPorts().size * 4}") {
					writePanic(codeWriter, "Unexpected length") // Really necessary...? Only in DEBUG mode perhaps?
				}

				Call("memcpy", "this->parameters.port_values, data, length")
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			val outLet = getOutletType("out") ?: return // No reason to do anything

			if (outLet != Format.SIGNAL)
				error("out port must be Format.SIGNAL")

			if (getInputPorts().any { it.substring(0 until 2) != "in" })
				error("All input ports must start with 'in'")

			val inputPorts = getInputPorts().sortedBy { it.substring(2).toInt() }

			if (inputPorts.map { getInletType(it) }.any { it != null && it != Format.SIGNAL })
				return // Incompatible port found, do nothing (maybe implement a converter later on sometime?)

			with(codeWriter) {
				// Make pointer variables for each port that are easy to access
				for ((i, input) in inputPorts.withIndex())
					if (!hasInlet(input))
						Member("float", "$input = this->parameters.port_values[$i]")

				writeForEachVoice(codeWriter) {

					// Make pointer variables for each port that are easy to access
					for (input in inputPorts)
						if (hasInlet(input))
							Member("float*", "$input = ${writeInlet(input)}.signal")

					writeForEachSample(codeWriter) {
						codeWriter.Statement(
							"${writeOutlet("out")}.signal[sample_index] = " +
							onWriteProcessSample(
								inputPorts.map {
									if (hasInlet(it))
										"$it[sample_index]" // Access the buffer of the connected inlet at correct sample index
									else
										it // Access the buffered parameters.port_values[...], as port is not connected
								},
							)
						)
					}
				}
			}
		}
	}

	abstract fun onWriteProcessSample(inExpressions: List<String>): String

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as BaseMathProperties
		instance.portValues?.apply {
			if (size != getInputPorts().size)
				error("Input port count ${getInputPorts().size} does not match the port value count $size")

			sendDataToDSP(size * 4) {
				for (value in this)
					it.putFloat(value)
			}
		}
	}
}