package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.Transpiler
import net.merayen.elastic.backend.architectures.llvm.ports.PortRegistry
import net.merayen.elastic.backend.architectures.llvm.ports.PortStruct
import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.architectures.llvm.transpilercode.LogComponent
import net.merayen.elastic.backend.architectures.llvm.transpilercode.writePanic
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.netlist.Node
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Generic transpiler node that all nodes should inherit.
 */
abstract class TranspilerNode(val nodeId: String) {
	var debug = false
	lateinit var shared: Transpiler.TranspilerData // Gets set by Transpiler
	lateinit var node: Node
	internal var log: LogComponent? = null
	internal lateinit var alloc: AllocComponent

	val channelCount = 2 // Fixed for now
	val frameSize: Int
		get() = shared.frameSize

	val dataToDSP = ArrayList<ByteBuffer>()

	val nodeIndex: Int
		get() = shared.nodeIndex

	val name: String
		get() = shared.nodeProperties.getName(node)

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
					writePanic(codeWriter, "Corrupt data?")
				}
				onWriteDataReceiver(codeWriter)
			}

			addInstanceMethod(codeWriter, "void", "send_data") {
				onWriteDataSender(codeWriter)
				codeWriter.Call("send", "0, NULL") // Sending an empty packet indicates that we are done sending data
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
						portStruct.cClass.writeCall(codeWriter, "init", "&$instanceVariable->outlets[voice_index]->${name}_let")

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
						Member("struct ${struct.cClass.name}", "${name}_let")
				}

				Struct("", listOf("parameters")) {
					onWriteParameters(codeWriter)
				}

				if (this@TranspilerNode is GroupInterface)
					Member("char", "voices[${shared.voiceCount}]")
			}
		}

		/**
		 * Halt the thread executing and wait for a debugger to continue the program.
		 *
		 * After attaching a debugger, e.g lldb, do: `stp = 0`
		 */
		protected fun startDebug(codeWriter: CodeWriter) {
			if (!debug) return
			with(codeWriter) {
				Block {
					Member("int", "stp = 1")
					While("stp") {}
				}
			}
		}

		/**
		 * The node parameters should be defined here.
		 */
		protected open fun onWriteParameters(codeWriter: CodeWriter) {}

		/**
		 * Code that gets run every time before processing a frame.
		 *
		 * Here voices can be created.
		 */
		protected open fun onWritePrepare(codeWriter: CodeWriter) {}

		/**
		 * Build your pre-processing code here.
		 *
		 * This code is run when all input data has been processsed, before any children nodes has been processed.
		 *
		 * Use this to e.g create voices on children node before they process.
		 *
		 * You can safely read data from the input ports on this node, but do note that none of the children nodes
		 * of this node has processed.
		 */
		open fun onWritePreprocess(codeWriter: CodeWriter) {}

		/**
		 * Build your processing code here.
		 *
		 * This code is run when all input data has been processed and all children nodes has been processed.
		 */
		protected open fun onWriteProcess(codeWriter: CodeWriter) {}

		/**
		 * Output code that should handle incoming data from the parent process.
		 *
		 * Variable to read from is "void* data;".
		 *
		 * The length is "int length;"
		 *
		 * If something is wrong with the incoming data, use "writePanic(...)" to stop immediately.
		 */
		protected open fun onWriteDataReceiver(codeWriter: CodeWriter) {}

		/**
		 * Node should send its data here.
		 *
		 * Code gets run when all nodes has processed.
		 *
		 * The code MUST call send() exactly once, even if it has nothing!
		 *
		 * Don't override if no data is to be sent.
		 */
		protected open fun onWriteDataSender(codeWriter: CodeWriter) {}

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
		fun writeForEachVoice(codeWriter: CodeWriter, block: () -> Unit) {
			if (getParent() == null)
				error("Can not iterate over the voices on the topmost node")

			with(codeWriter) {
				For("int voice_index = 0", "voice_index < ${shared.voiceCount}", "voice_index++") {
					If("${writeVoicesVariable()}[voice_index] == 0") {
						Continue()
					}
					block()
				}
			}
		}

		/**
		 * Create a for-loop for each sample.
		 *
		 * Variable: sample_index
		 */
		fun writeForEachSample(codeWriter: CodeWriter, block: () -> Unit) {
			codeWriter.For("int sample_index = 0", "sample_index < $frameSize", "sample_index++") {
				block()
			}
		}

		/**
		 * Create a for-loop for each channel. AUDIO ONLY.
		 *
		 * Variable: channel_index
		 */
		fun writeForEachChannel(codeWriter: CodeWriter, block: () -> Unit) {
			codeWriter.For("int channel_index = 0", "channel_index < $channelCount", "channel_index++") {
				block()
			}
		}

		/**
		 * Write code to access an outlet from another node.
		 */
		fun writeInlet(portName: String, voiceIndex: String = "voice_index"): String {
			val lines = shared.netList.getConnections(node, portName)
			if (lines.isEmpty())
				throw error("Inlet '$portName' on node '${node.id}' does not exist")

			if (lines.size != 1)
				throw error("Inlet should have exactly 1 connection only")

			val line = lines[0]

			val outputNode = if (line.node_a == node) line.node_b else line.node_a
			val outputPort = if (line.node_a == node) line.port_b else line.port_a

			return "nodedata_${outputNode.id}->outlets[$voiceIndex]->${outputPort}_let"
		}

		protected fun hasInlet(name: String): Boolean {
			val port = shared.nodeProperties.getInputPorts(node).any { it == name }
			if (!port)
				return false

			val lines = shared.netList.getConnections(node, name)
			if (lines.isEmpty())
				return false

			return true
		}

		/**
		 * Check if the current node has outlet by name and that it is connected.
		 */
		protected fun hasOutlet(name: String): Boolean {
			val port = shared.nodeProperties.getOutputPorts(node).any { it == name }
			if (!port)
				return false

			val lines = shared.netList.getConnections(node, name)
			if (lines.isEmpty())
				return false

			return true
		}

		/**
		 * Writes C code to send data back from DSP (us) to backend.
		 */
		private var sendDataToBackendCounter = 0
		protected fun sendDataToBackend(
			codeWriter: CodeWriter,
			lengthExpression: String,
			zero: Boolean = false,
			func: (data: String) -> Any
		) { // TODO maybe do bound checking in debug mode?
			with(codeWriter) {
				val variable = "_data_${sendDataToBackendCounter++}"
				If("$lengthExpression > 0") {
					// We send empty packet to declare that we are done sending data, therefore we can
					// not allow node itself send empty packets as that would confuse us when receiving.

					if (zero)
						alloc.writeCalloc(codeWriter, "void*", variable, lengthExpression, "1")
					else
						alloc.writeMalloc(codeWriter, "void*", variable, lengthExpression) // node id + length of payload

					func(variable)

					Call("send", "$lengthExpression, $variable")

					alloc.writeFree(codeWriter, variable)
				}
			}
		}

		/**
		 * Same as sendDataToBackend(), only that this sends an existing buffer.
		 */
		protected fun sendPointerToBackend(codeWriter: CodeWriter, variableExpression: String, lengthExpression: String) {
			with(codeWriter) {
				If("$lengthExpression <= 0") {
					writePanic(codeWriter, "Length must be more than 0")
				}

				Call("send", "$lengthExpression, $variableExpression")
			}
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
	 *
	 * Called once after each processed frame.
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

	protected fun writeOutlet(portName: String, voiceIndex: String = "voice_index"): String {
		return "nodedata_$nodeId->outlets[$voiceIndex]->${portName}_let"
	}

	/**
	 * Write the parameter variable, accessed outside the CClass instance methods.
	 *
	 * E.g, if you want to access the parameter from another CClass or method outside current instance methods,
	 * use this method. Otherwise, just use `this->parameters.your_parameter` instead.
	 */
	protected fun writeOuterParameterVariable(parameterName: String): String {
		return "nodedata_$nodeId->parameters.$parameterName"
	}

	/**
	 * Returns the voices variable containing which voices are active in the session the current node has.
	 *
	 * If this is the topmost node, there are no session.
	 */
	fun writeVoicesVariable(): String {
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
	 * Check if the current node has inlet by name and that it is connected.
	 */
	fun getInletType(name: String): Format? {
		val ports = shared.nodeProperties.getInputPorts(node).filter { it == name }

		if (ports.isEmpty())
			return null // Port not found

		if (ports.size > 1) // Just checking, to be sure
			error("A node should not have multiple ports with the same name")

		val lines = shared.netList.getConnections(node, name)
		if (lines.isEmpty())
			return null // Not connected

		if (lines.size != 1)
			error("Inlet should never have more than 1 line connected")

		return when {
			lines[0].node_a === node -> shared.nodeProperties.getFormat(
				shared.netList.getPort(
					lines[0].node_b,
					lines[0].port_b
				)
			)
			lines[0].node_b === node -> shared.nodeProperties.getFormat(
				shared.netList.getPort(
					lines[0].node_a,
					lines[0].port_a
				)
			)
			else -> error("Invalid connection")
		}
	}

	protected fun getOutletType(name: String): Format? {
		val ports = shared.nodeProperties.getOutputPorts(node).filter { it == name }

		if (ports.isEmpty())
			return null

		if (ports.size != 1) // Just checking, to be sure
			error("A node should not have multiple ports with the same name")

		if (shared.netList.getConnections(node, name).isEmpty())
			return null // Not connected

		return shared.nodeProperties.getFormat(node, name)
	}

	/**
	 * Writes code that calls all children nodes' voice creation logic.
	 *
	 * Must be run only in onWritePrepare() or onWritePreprocess()
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
			If("voice_index < 0 || voice_index >= ${shared.voiceCount}") { writePanic(codeWriter) }
			If("$instanceVariable->voices[voice_index] == 0") { writePanic(codeWriter, "Voice already destroyed") }

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

			val portStruct = PortRegistry.getPortStruct(format, shared.frameSize, debug)

			result[port] = portStruct
		}

		return result
	}

	fun getPortStruct(name: String): PortStruct = PortRegistry.getPortStruct(getInletType(name)!!, frameSize, debug)

	/**
	 * Returns the actual port on the node, even though it is not connected.
	 *
	 * Use getOutletType() and check for null to see if the port really exists in the DSP.
	 */
	fun getOutputPorts(): List<String> = shared.nodeProperties.getOutputPorts(node)

	/**
	 * Returns the actual port on the node, even though it is not connected.
	 *
	 * Use getInletType() and check for null to see if the port really exists in the DSP.
	 */
	fun getInputPorts(): List<String> = shared.nodeProperties.getInputPorts(node)

	protected fun writePanic(codeWriter: CodeWriter, message: String = "", args: String = "") {
		writePanic(codeWriter, "[node_name=${shared.nodeProperties.getName(node)}, node_id=$nodeId] $message", args, debug)
	}

	protected fun writeDebug(codeWriter: CodeWriter) {
		with(codeWriter) {
			log?.write(codeWriter, "*** Waiting for debugger ***")
			Statement("volatile int D = 0")
			While ("D == 0") {
				Call("usleep", "100000")
			}
		}
	}

	protected fun writeLog(codeWriter: CodeWriter, message: String, args: String = "") {
		log?.write(codeWriter, "[node_name=${shared.nodeProperties.getName(node)}, node_id=$nodeId] $message", args)
	}

	private fun hasOutlets() = shared.nodeProperties.getOutputPorts(node).isNotEmpty()
}
