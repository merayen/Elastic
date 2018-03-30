package net.merayen.elastic.uinodes.list.mix_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
    private var mixPortParameter: PortParameter? = null
    private val slider = ParameterSlider()

    init {
        slider.setHandler(object : ParameterSlider.IHandler {
            override fun onButton(offset: Int) {
                slider.value += offset / 50.0
            }

            override fun onLabelUpdate(value: Double): String {
                return Math.round(value * 200 - 100).toString() + "%"
            }

            override fun onChange(value: Double, programmatic: Boolean) {
                sendParameter("mix", value)
            }
        })
    }

    override fun onInit() {
        super.onInit()
        layoutWidth = 100f
        layoutHeight = 100f
    }

    override fun onCreatePort(port: UIPort) {
        if (port.name == "a") {
            port.translation.y = 20f
        }

        if (port.name == "b") {
            port.translation.y = 40f
        }

        if (port.name == "fac") {
            port.translation.y = 75f
            val delaySlider = PortParameter(this, port, slider, UIObject())
            delaySlider.translation.x = 10f
            delaySlider.translation.y = 70f
            add(delaySlider)
            this.mixPortParameter = delaySlider
        }

        if (port.name == "out") {
            port.translation.x = 100f
            port.translation.y = 20f
        }
    }

    override fun onRemovePort(port: UIPort) {}

    override fun onMessage(message: NodeParameterMessage) {}

    override fun onData(message: NodeDataMessage) {}

    override fun onParameter(key: String, value: Any) {
        if(key == "mix")
            slider.value = (value as Number).toDouble()
    }
}
