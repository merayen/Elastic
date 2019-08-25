package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.backend.logicnodes.list.group_1.Data
import net.merayen.elastic.backend.logicnodes.list.group_1.TransportStartPlaybackMessage
import net.merayen.elastic.backend.logicnodes.list.group_1.TransportStopPlaybackMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.top.viewbar.ViewBar
import kotlin.math.log
import kotlin.math.max
import kotlin.math.pow

internal class NodeViewBar(private val nodeView: NodeView) : ViewBar(NodeView::class) {
	private val bpmSlider = BPMSlider()
	private val monitorVolumeSlider = ParameterSlider()
	private val playButton = Button()
	private val stopButton = Button()

	override fun onInit() {
		super.onInit()

		bpmSlider.handler = object : BPMSlider.Handler {
			override fun onChange(bpm: Int) {
				val nodeId = nodeView.currentNodeId
				if (nodeId != null)
					sendMessage(NodeParameterMessage(nodeId, Data(bpm = bpm)))
			}
		}
		add(bpmSlider)


		monitorVolumeSlider.setHandler(object : ParameterSlider.IHandler {
			private var volume = 1.0

			override fun onChange(value: Double, programatic: Boolean) {
				volume = max(1/10.0.pow(12), value.pow(10))
			}

			override fun onButton(offset: Int) {

			}

			override fun onLabelUpdate(value: Double) = "%.0f dB".format(log(1/volume, 10.0) * 10)
		})
		add(monitorVolumeSlider)


		playButton.label = ">"
		playButton.handler = object : Button.IHandler {
			override fun onClick() {
				val nodeId = nodeView.currentNodeId

				if (nodeId != null)
					sendMessage(TransportStartPlaybackMessage(nodeId))
			}
		}
		add(playButton)


		stopButton.label = "||"
		stopButton.handler = object : Button.IHandler {
			override fun onClick() {
				val nodeId = nodeView.currentNodeId

				if (nodeId != null)
					sendMessage(TransportStopPlaybackMessage(nodeId))
			}
		}
		add(stopButton)
	}


}