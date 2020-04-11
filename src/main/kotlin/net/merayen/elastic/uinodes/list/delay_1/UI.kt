package net.merayen.elastic.uinodes.list.delay_1

import net.merayen.elastic.backend.logicnodes.list.delay_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
    private val delayTime: ParameterSlider

    init {
        val self = this
        delayTime = ParameterSlider()
        delayTime.translation.x = 10f
        delayTime.translation.y = 20f
        delayTime.setHandler(object : ParameterSlider.Handler {
            override fun onLabelUpdate(value: Double): String {
                return String.format("%.2f", value)
            }

            override fun onChange(value: Double, programatic: Boolean) {
                self.sendProperties(Properties(delayTime = value.toFloat()))
            }

            override fun onButton(offset: Int) {
                delayTime.value = delayTime.value + offset / 50.0
            }
        })

        add(delayTime)
    }

    override fun onInit() {
        super.onInit()
        layoutWidth = 100f
        layoutHeight = 50f
        titlebar.title = "Delay"
    }

    override fun onCreatePort(port: UIPort) {
        if (port.name == "input") {
            port.translation.y = 20f
        }

        if (port.name == "output") {
            port.translation.x = 100f
            port.translation.y = 20f
        }
    }

    override fun onRemovePort(port: UIPort) {}

    override fun onData(message: NodeDataMessage) {}

    override fun onProperties(instance: BaseNodeProperties) {
        if (instance is Properties) {
            val delayTimeData = instance.delayTime
            if (delayTimeData != null)
                delayTime.value = delayTimeData.toDouble()
        }
    }
}
