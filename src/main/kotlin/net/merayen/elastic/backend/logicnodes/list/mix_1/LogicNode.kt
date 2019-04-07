package net.merayen.elastic.backend.logicnodes.list.mix_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode

class LogicNode : BaseLogicNode() {

    override fun onCreate() {
        createPort(BaseLogicNode.PortDefinition("a"))
        createPort(BaseLogicNode.PortDefinition("b"))
        createPort(BaseLogicNode.PortDefinition("fac"))
        createPort(BaseLogicNode.PortDefinition("out", Format.AUDIO))
    }

    override fun onInit() {}

    override fun onParameterChange(key: String, value: Any) {
        set(key, value) // w/e
    }

    override fun onConnect(port: String) {}

    override fun onDisconnect(port: String) {}

    override fun onPrepareFrame(data: Map<String, Any>) {}

    override fun onFinishFrame(data: Map<String, Any>) {}

    override fun onRemove() {}

    override fun onData(data: Map<String, Any>) {}
}
