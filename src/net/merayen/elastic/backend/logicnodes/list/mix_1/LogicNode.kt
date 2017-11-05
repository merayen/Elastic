package net.merayen.elastic.backend.logicnodes.list.mix_1

import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.nodes.BaseLogicNode

class LogicNode : BaseLogicNode() {

    override fun onCreate() {
        createPort(object : BaseLogicNode.PortDefinition() {
            init {
                name = "a"
            }
        })

        createPort(object : BaseLogicNode.PortDefinition() {
            init {
                name = "b"
            }
        })

        createPort(object : BaseLogicNode.PortDefinition() {
            init {
                name = "fac"
            }
        })

        createPort(object : BaseLogicNode.PortDefinition() {
            init {
                name = "out"
                output = true
                format = Format.AUDIO
            }
        })
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
