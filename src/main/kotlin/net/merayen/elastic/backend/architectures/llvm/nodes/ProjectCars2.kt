package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.projectcars2_1.LogicNode.Companion.paramNames
import net.merayen.elastic.backend.logicnodes.list.projectcars2_1.ProjectCars2UDPData
import net.merayen.elastic.system.intercom.NodeDataMessage

class ProjectCars2(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				for (param in paramNames)
					Member("float", param)
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteInit(codeWriter, allocComponent)

			with(codeWriter) {
				for (param in paramNames)
					Statement("this->parameters.$param = 0")
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				for ((i, param) in paramNames.withIndex())
					Statement("this->parameters.$param = *((float *)data + $i)")
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			with(codeWriter) {
				for (outlet in paramNames) {
					if (getOutletType(outlet) == Format.SIGNAL) {
						writeForEachVoice(codeWriter) {
							writeForEachSample(codeWriter) {
								Statement("${writeOutlet(outlet)}.signal[sample_index] = this->parameters.$outlet")
							}
						}
					}
				}
			}
		}
	}

	override fun onMessage(message: NodeDataMessage) {
		if (message is ProjectCars2UDPData) {
			sendDataToDSP(4 * paramNames.size) {  // Params must be in exact same order as paramNames!
				it.putFloat(message.rpm)
				it.putFloat(message.enginePower)
				it.putFloat(message.engineTorque)
				it.putFloat(message.fuelLevel)
				it.putFloat(message.oilTemp)
				it.putFloat(message.throttle)
				it.putFloat(message.breaks)
				it.putFloat(message.clutch)
			}
		}
	}
}