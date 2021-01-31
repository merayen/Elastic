package net.merayen.elastic.uinodes.list.mix_1

import net.merayen.elastic.backend.logicnodes.list.mix_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.KeyboardEvent
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch

class UI : UINode(), EasyMotionBranch {
    private var mixPortParameter: PortParameter? = null
    private val slider = ParameterSlider()

    init {
        slider.setHandler(object : ParameterSlider.Handler {
            override fun onButton(offset: Int) {
                slider.value += offset / 50.0
            }

            override fun onLabelUpdate(value: Double): String {
                return Math.round(value * 200 - 100).toString() + "%"
            }

            override fun onChange(value: Double, programmatic: Boolean) {
                sendProperties(Properties(mix=value.toFloat()))
            }
        })

    }

    override fun onInit() {
        super.onInit()
        layoutWidth = 100f
        layoutHeight = 100f

        easyMotionBranch.controls[setOf(KeyboardEvent.Keys.Q)] = Branch.Control {
            Branch.Control.STEP_BACK
        }

        easyMotionBranch.controls[setOf(KeyboardEvent.Keys.F)] = Branch.Control {
            slider
        }
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

    override fun onData(message: NodeDataMessage) {}

    override fun onProperties(instance: BaseNodeProperties) {
        if (instance is Properties) {
            val mixData = instance.mix
            if (mixData != null)
                slider.value = mixData.toDouble()
        }
    }
}
