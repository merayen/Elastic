package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.util.Postmaster

class AudioTrack(nodeId: String, arrangement: Arrangement) : ArrangementTrack(nodeId, arrangement) {
	override fun onParameter(key: String, value: Any) {}
}