package net.merayen.elastic.backend.logicnodes.list.mix_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

class LogicNode : BaseLogicNode() {
    override fun onCreate() {
        createInputPort("a")
        createInputPort("b")
        createInputPort("fac")
        createOutputPort("out", Format.AUDIO)
    }

    override fun onInit() {}

    override fun onParameterChange(instance: BaseNodeProperties) {
        updateProperties(instance) // w/e
    }

    override fun onConnect(port: String) {}

    override fun onDisconnect(port: String) {}

    override fun onFinishFrame(data: OutputFrameData?) {}

    override fun onRemove() {}

    override fun onData(data: NodeDataMessage) {}
}
