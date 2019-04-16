package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.system.intercom.NodeParameterMessage

abstract class ArrangementTrack(val nodeId: String, private val arrangement: Arrangement) {
	val trackPane = TrackPane()
	val eventPane = EventPane()

	abstract fun onParameter(key: String, value: Any)

	fun sendParameter(key: String, value: Any) = arrangement.sendMessage(NodeParameterMessage(nodeId, key, value))
}
