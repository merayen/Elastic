package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeAudioOut
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeMidiOut
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeSignalOut
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData
import java.nio.ByteBuffer

/**
 * Takes audio or signal and sends it out of the current group.
 *
 * The parent node decides what happens with the data send into this node. If this node is under the topmost node,
 * it probably gets played onto your speakers.
 *
 * TODO We might want the parent node of this Out-node to actually read the data it receives, in the C code...? Not just forward it? The parent node probably wants to process the data... Maybe store the output data in a buffer instead? Or just let the parent node read the outlet connected to this node?
 */
class Out(nodeId: String) : TranspilerNode(nodeId) {
	override val nodeClass = object : NodeClass() {}
}
