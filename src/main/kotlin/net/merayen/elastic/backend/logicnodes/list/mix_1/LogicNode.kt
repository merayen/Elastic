package net.merayen.elastic.backend.logicnodes.list.mix_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
    override fun onCreate() {
        createPort(PortDefinition("a"))
        createPort(PortDefinition("b"))
        createPort(PortDefinition("fac"))
        createPort(PortDefinition("out", Format.AUDIO))
    }

    override fun onInit() {}

    override fun onParameterChange(instance: BaseNodeData) {
        updateProperties(instance) // w/e
    }

    override fun onConnect(port: String) {}

    override fun onDisconnect(port: String) {}

    override fun onFinishFrame(data: OutputFrameData) {}

    override fun onRemove() {}

    override fun onData(data: NodeDataMessage) {}
}
