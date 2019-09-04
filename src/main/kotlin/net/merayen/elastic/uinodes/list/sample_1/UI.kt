package net.merayen.elastic.uinodes.list.sample_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.node.Resizable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.sin

class UI : UINode() {
	val sampleWaveBox = SampleWaveBox()
	private val FIXED_HEIGHT = 150f

	init {
		layoutWidth = 200f
		layoutHeight = 150f

		val testWaveform = FloatArray(400)
		for(i in 0 until testWaveform.size)
			testWaveform[i] = sin(i / 10f)

		sampleWaveBox.applyWaveform(testWaveform)
	}

	override fun onInit() {
		super.onInit()

		sampleWaveBox.translation.x = 10f
		sampleWaveBox.translation.y = 20f
		add(sampleWaveBox)

		add(Resizable(this, object : Resizable.Handler {
			override fun onResize() {
				if (layoutWidth < 100) layoutWidth = 100f
				if (layoutWidth > 1000) layoutWidth = 1000f
				layoutHeight = FIXED_HEIGHT

				updateLayout()
			}
		}))

		updateLayout()
	}

	override fun onCreatePort(port: UIPort) {
		if(port.name == "control") {
			port.translation.y = 20f
			port.color = UIPort.AUX_PORT
		} else if(port.name == "out") {
			port.translation.x = layoutWidth
			port.translation.y = 20f
			port.color = UIPort.AUDIO_PORT
		}
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(100, 100, 100)
	}

	private fun updateLayout() {
		sampleWaveBox.layoutWidth = layoutWidth - 20
		sampleWaveBox.layoutHeight = layoutHeight - 50
		getPort("out")?.translation?.x = layoutWidth
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
	override fun onParameter(instance: BaseNodeProperties) {}
}