package net.merayen.elastic.ui.objects.top.views.nodeview

import net.merayen.elastic.backend.logicnodes.list.group_1.Properties
import net.merayen.elastic.backend.logicnodes.list.group_1.SetBPMMessage
import net.merayen.elastic.backend.logicnodes.list.group_1.TransportStartPlaybackMessage
import net.merayen.elastic.backend.logicnodes.list.group_1.TransportStopPlaybackMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.top.viewbar.ViewBar
import kotlin.math.*

internal class NodeViewBar(private val nodeView: NodeView) : ViewBar(NodeView::class) {
	private val bpmSlider = BPMSlider()
	private val monitorVolumeSlider = ParameterSlider()
	private val channelCountSlider = ParameterSlider()
	private val playButton = Button()
	private val stopButton = Button()

	private var channelCount = 1

	override fun onInit() {
		super.onInit()

		bpmSlider.handler = object : BPMSlider.Handler {
			override fun onChange(bpm: Int) {
				val nodeId = nodeView.nodeViewController!!.topNodeId
				if (nodeId != null)
					sendMessage(SetBPMMessage(nodeId, bpm))
			}
		}
		add(bpmSlider)

		monitorVolumeSlider.setHandler(object : ParameterSlider.Handler {
			private var volume = 1.0

			override fun onChange(value: Double, programatic: Boolean) {
				volume = max(1 / 10.0.pow(12), value.pow(10))
			}

			override fun onButton(offset: Int) {}

			override fun onLabelUpdate(value: Double) = "%.0f dB".format(log(1 / volume, 10.0) * 10)
		})
		add(monitorVolumeSlider)

		channelCountSlider.setHandler(object : ParameterSlider.Handler {
			override fun onChange(value: Double, programatic: Boolean) {
				val newChannelCount = (value + 1).roundToInt()
				if (newChannelCount != channelCount) {
					channelCount = newChannelCount

					val nodeId = nodeView.nodeViewController!!.topNodeId
					if (nodeId != null)
						sendMessage(NodePropertyMessage(nodeId, Properties(channelCount = channelCount)))
				}
			}

			override fun onLabelUpdate(value: Double) = "${(channelCountSlider.value + 1).roundToInt()}"

			override fun onButton(offset: Int) {
				val newChannelCount = max(1, min(2, channelCount + offset))
				channelCountSlider.value = (newChannelCount - 1.0)
			}
		})
		add(channelCountSlider)

		playButton.label = ">"
		playButton.handler = object : Button.IHandler {
			override fun onClick() {
				val nodeId = nodeView.nodeViewController!!.topNodeId
				if (nodeId != null)
					sendMessage(TransportStartPlaybackMessage(nodeId))
			}
		}
		add(playButton)

		stopButton.label = "||"
		stopButton.handler = object : Button.IHandler {
			override fun onClick() {
				val nodeId = nodeView.nodeViewController!!.topNodeId
				if (nodeId != null)
					sendMessage(TransportStopPlaybackMessage(nodeId))
			}
		}
		add(stopButton)
	}

	fun handleMessage(message: ElasticMessage) {
		when (message) {
			is NodePropertyMessage -> {
				val instance = message.instance
				if (instance is Properties) {
					if (message.node_id == nodeView.nodeViewController!!.topNodeId) {
						val channelCount = instance.channelCount
						if (channelCount != null) {
							channelCountSlider.value = channelCount - 1.0
							this.channelCount = channelCount
						}
					}
				}
			}
		}
	}
}