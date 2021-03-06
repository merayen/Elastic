package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.Transpiler
import net.merayen.elastic.backend.architectures.llvm.ports.PortRegistry
import net.merayen.elastic.backend.architectures.llvm.ports.PortStruct
import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.templating.include
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.architectures.llvm.transpilercode.LogComponent
import net.merayen.elastic.backend.architectures.llvm.transpilercode.ohshit
import net.merayen.elastic.netlist.Node
import net.merayen.elastic.system.intercom.NetListMessage
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Generic transpiler node that all nodes should inherit.
 */
abstract class TranspilerNode(val nodeId: String, val nodeIndex: Int) {
	var debug = false
	lateinit var shared: Transpiler.TranspilerData // Gets set by Transpiler
	lateinit var node: Node
	internal var log: LogComponent? = null
	internal lateinit var alloc: AllocComponent

	val channelCount = 2 // Fixed for now
	val frameSize: Int
		get() = shared.frameSize

	val dataToDSP = ArrayList<ByteBuffer>()

	abstract inner class NodeClass : CClass("Node_$nodeId") {
		override fun onWriteMethodHeaders(codeWriter: CodeWriter) {
			addInstanceMethodHeader(codeWriter, "void", "prepare")
			addInstanceMethodHeader(codeWriter, "void", "create_voice", "int voice_index")
		}

		override fun onWriteMethods(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			addInstanceMethod(codeWriter, "void", "prepare") {
				onWritePrepare(codeWriter)
				if (this is GroupInterface) {
					if (getParent() == null) { // We are the topmost node, and a group, so we will create a voice of our children upon launch
						with(codeWriter) {
							If("$instanceVariable->voices[0] == 0") {
								writeVoiceCreation(codeWriter)
							}
						}
					}
				}
			}

			addInstanceMethod(codeWriter, "void", "process") {
				if (shared.debug) log?.write(codeWriter, "process:start $nodeId")
				onWriteProcess(codeWriter)
				if (shared.debug) log?.write(codeWriter, "process:done $nodeId")
			}

			addInstanceMethod(codeWriter, "void", "receive_data", "int length, void* data") {
				codeWriter.If("length < 0") {
					ohshit(codeWriter, "Corrupt data?")
				}
				onWriteDataReceiver(codeWriter)
			}

			addInstanceMethod(codeWriter, "void", "send_data") {
				onWriteDataSender(codeWriter)
			}

			addInstanceMethod(codeWriter, "void", "create_voice", "int voice_index") {
				if (hasOutlets()) {
					// Instantiate new outlets struct of outlets
					allocComponent!!.writeCalloc(
						codeWriter,
						"",
						"$instanceVariable->outlets[voice_index]",
						"1",
						"sizeof(struct NodePorts_$nodeId)"
					)

					// Init all the outlets on our outlets
					for ((name, portStruct) in getPortStructs())
						portStruct.cClass.writeCall(codeWriter, "init", "&$instanceVariable->outlets[voice_index]->$name")

					// Then run the custom code for creating the voice of this node
					onWriteCreateVoice(codeWriter)
				}
			}

			addInstanceMethod(codeWriter, "void", "destroy_voice", "int voice_index") {
				onWriteDestroyVoice(codeWriter)
			}
			//addInstanceMethod(codeWriter, "void", "create_voice", "int voice_id, int voice_index", "") // TODO create instance outlets
		}

		override fun onWriteMembers(codeWriter: CodeWriter) {
			with(codeWriter) {
				Struct("NodePorts_$nodeId", listOf("*outlets[${shared.voiceCount}]")) {
					for ((name, struct) in getPortStructs())
						Member("struct ${struct.cClass.name}", name)
				}

				Struct("", listOf("parameters")) {
					onWriteParameters(codeWriter)
				}

				if (this@TranspilerNode is GroupInterface)
					Member("char", "voices[${shared.voiceCount}]")
			}
		}

		/**
		 * The node parameters should be defined here.
		 */
		protected open fun onWriteParameters(codeWriter: CodeWriter) {}

		/**
		 * Code that gets run every time before processing a frame. May also be run right after PROCESS as to make the
		 * backend responsive as possible for next PROCESS-request.
		 */
		protected open fun onWritePrepare(codeWriter: CodeWriter) {}

		/**
		 * Return your processing code here.
		 */
		protected open fun onWriteProcess(codeWriter: CodeWriter) {}

		/**
		 * Output code that should handle incoming data from the parent process.
		 *
		 * Variable to read from is "void* data;".
		 *
		 * The length is "int length;"
		 *
		 * If something is wrong with the incoming data, use "ohshit(...)" to stop immediately.
		 */
		protected open fun onWriteDataReceiver(codeWriter: CodeWriter) {}

		/**
		 * Node should send its data here.
		 *
		 * Code gets run when all nodes has processed.
		 *
		 * The code MUST call send() even if it has nothing!
		 *
		 * Don't override if no data is to be sent.
		 */
		protected open fun onWriteDataSender(codeWriter: CodeWriter) {
			codeWriter.Call("send", "0, NULL")
		}

		/**
		 * Code that creates a new voice. E.g, calling malloc() for the node's voice data.
		 *
		 * Remember to clean up any allocated data in onWriteDestroyVoice().
		 *
		 * Current node never creates a voice of itself, only its direct children.
		 */
		protected open fun onWriteCreateVoice(codeWriter: CodeWriter) {}

		/**
		 * Code that destroys a voice.
		 *
		 * Clean up any custom data for this node's voice here.
		 *
		 * Outlets/inlets get free'd automatically.
		 */
		protected open fun onWriteDestroyVoice(codeWriter: CodeWriter) {}

		/**
		 * Create a for-loop for each voice.
		 *
		 * Variable: voice_index
		 */
		protected fun writeForEachVoice(codeWriter: CodeWriter, block: () -> Unit) {
			codeWriter.For("int voice_index = 0", "voice_index < ${shared.voiceCount}", "voice_index++") {
				codeWriter.If("${writeVoicesVariable()}[voice_index] == 0") {
					codeWriter.Continue()
				}
				block()
			}
		}

		/**
		 * Create a for-loop for each sample.
		 *
		 * Variable: sample_index
		 */
		protected fun writeForEachSample(codeWriter: CodeWriter, block: () -> Unit) {
			codeWriter.For("int sample_index = 0", "sample_index < $frameSize", "sample_index++") {
				block()
			}
		}

		/**
		 * Create a for-loop for each channel.
		 *
		 * Variable: channel_index
		 */
		protected fun writeForEachChannel(codeWriter: CodeWriter, block: () -> Unit) {
			codeWriter.For("int channel_index = 0", "channel_index < $channelCount", "channel_index++") {
				block()
			}
		}

		protected fun writeInlet(portName: String, voiceIndex: String = "voice_index"): String {
			val lines = shared.netList.getConnections(node, portName)
			if (lines.size != 1)
				throw RuntimeException("Should not happen")

			val line = lines[0]

			val outputNode = if (line.node_a == node) line.node_b else line.node_a
			val outputPort = if (line.node_a == node) line.port_b else line.port_a

			return "nodedata_${outputNode.id}->outlets[$voiceIndex]->$outputPort"
		}
	}

	/**
	 * A CClass for holding the Node-data.
	 */
	abstract val nodeClass: NodeClass

	/**
	 * Called everytime before we ask the DSP backend to process a frame.
	 *
	 * Send audio data etc here using sendDataToDSP.
	 */
	open fun onPrepareFrame() {}

	/**
	 * When the node receives a property, this node needs to convert it to format that
	 * can be sent to the DSP.
	 *
	 * Call this.sendData(...) to actually send to dsp backend
	 */
	open fun onMessage(message: NodePropertyMessage) {}

	/**
	 * When the node receives data, this node needs to convert it to a format that
	 * can be sent to the DSP.
	 *
	 * Call this.sendData(...) to actually send to dsp backend
	 */
	open fun onMessage(message: NodeDataMessage) {}

	/**
	 * Data from the DSP. In the same format as the one sent with sendDataToDSP()
	 */
	open fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> = listOf()

	protected fun sendDataToDSP(size: Int, func: (ByteBuffer) -> Unit) {
		val messageType = "NODEDATA"
		val data = ByteBuffer.allocate(messageType.length + 4 + size) // packet type, nodeIndex, payload
		data.order(ByteOrder.nativeOrder())

		data.put(messageType.toByteArray())
		data.putInt(nodeIndex)

		func(data)

		if (data.hasRemaining())
			throw RuntimeException("Node $nodeId called writeDataToDSP without writing to the whole buffer. ${data.remaining()} bytes remaining")

		data.rewind()

		dataToDSP.add(data)
	}

	/**
	 * Retrieve and clear the outgoing DSP-data.
	 */
	fun retrieveDataToDSP(): List<ByteBuffer> {
		val result = dataToDSP.toList()
		dataToDSP.clear()
		return result
	}

	/**
	 * Must be called by a supervisor before every frame that is to be processed.
	 */
	fun prepareFrame() = onPrepareFrame()

	protected fun writeForEachVoice(text: String): String {
		return """
			for (int voice_index = 0; voice_index < ${shared.voiceCount}; voice_index++)
				${include(4, text)}
		""".trim()
	}

	protected fun writeOutlet(portName: String, voiceIndex: String = "voice_index"): String {
		return "nodedata_$nodeId->outlets[$voiceIndex]->$portName"
	}

	protected fun writeParameterVariable(parameterName: String): String {
		return "nodedata_$nodeId->parameters.$parameterName"
	}

	fun writeVoicesVariable(): String {
		if (this is GroupInterface)
			return "nodedata_$nodeId->voices"
		else
			return "nodedata_${getParent()!!.nodeId}->voices"
	}

	val instanceVariable = "nodedata_$nodeId"

	fun handle(message: NodeMessage) {
		if (message is NodePropertyMessage)
			onMessage(message)
		else if (message is NodeDataMessage)
			onMessage(message)
	}

	fun writeStruct(codeWriter: CodeWriter) {
		nodeClass.writeStruct(codeWriter, listOf("*$instanceVariable"))
	}

	/**
	 * Writes code that calls all children nodes' voice creation logic.
	 *
	 * Must be run only when preparing (all nodes gets prepare() called, also not when processing a frame.
	 */
	fun writeVoiceCreation(codeWriter: CodeWriter) {
		if (this !is GroupInterface)
			throw RuntimeException("Only group nodes can create voices")

		with(codeWriter) {
			// Find next available voice index
			Statement("int voice_index = -1")
			For("int i = 0", "i < ${shared.voiceCount}", "i++") {
				If("$instanceVariable->voices[i] == 0") {
					Statement("voice_index = i")
					Break()
				}
			}

			If("voice_index == -1") {
				Return() // No available voices was found
			}

			Statement("$instanceVariable->voices[voice_index] = 1")

			for (node in getChildren())
				node.nodeClass.writeCall(codeWriter, "create_voice", "${node.instanceVariable}, voice_index")
		}
	}

	/**
	 * Destroy all this node's children voice by index.
	 *
	 * Must be run only when preparing (all nodes gets prepare() called, also not when processing a frame).
	 *
	 * Input argument: voice_index
	 */
	fun writeVoiceDestruction(codeWriter: CodeWriter) {
		with(codeWriter) {
			If("voice_index < 0 || voice_index >= ${shared.voiceCount}") { ohshit(codeWriter) }
			If("$instanceVariable->voices[voice_index] == 0") { ohshit(codeWriter, "Voice already destroyed") }

			for (node in getChildren())
				node.nodeClass.writeCall(codeWriter, "destroy_voice", "${node.instanceVariable}, voice_index")
		}
	}

	protected fun getParent(): TranspilerNode? {
		for ((group, children) in shared.groups)
			for (child in children)
				if (child.nodeId == nodeId)
					return group

		return null
	}

	protected fun getChildren(): List<TranspilerNode> {
		return shared.groups[this] ?: ArrayList()
	}

	/**
	 * Returns all the port structs for the output outlets of this class.
	 */
	private fun getPortStructs(): Map<String, PortStruct> {
		val result = HashMap<String, PortStruct>()

		for (port in shared.nodeProperties.getOutputPorts(node)) {
			val nodePort = shared.netList.getPort(node, port)
			val format = shared.nodeProperties.getFormat(nodePort)

			val portStruct = PortRegistry.getPortStruct(format, shared.frameSize)

			result[port] = portStruct
		}

		return result
	}

	protected fun writePanic(codeWriter: CodeWriter, message: String) {
		ohshit(codeWriter, message, debug = debug)
	}

	private fun hasOutlets() = shared.nodeProperties.getOutputPorts(node).isNotEmpty()
}
