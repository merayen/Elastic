package net.merayen.elastic.uinodes.list.delay_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
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
        delayTime.setHandler(object : ParameterSlider.IHandler {
            override fun onLabelUpdate(value: Double): String {
                return String.format("%.2f", value)
            }

            override fun onChange(value: Double, programatic: Boolean) {
                self.sendParameter("delay_time", value.toFloat())
            }

            override fun onButton(offset: Int) {
                delayTime.value = delayTime.value + offset / 50.0
            }
        })

        add(delayTime)
    }

    override fun onInit() {
        super.onInit()
        width = 100f
        height = 50f
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

    override fun onMessage(message: NodeParameterMessage) {}

    override fun onData(message: NodeDataMessage) {}

    override fun onParameter(key: String, value: Any) {
        if (key == "delay_time")
            delayTime.value = (value as Number).toDouble()
    }
}
