package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks

import net.merayen.elastic.ui.objects.top.views.arrangementview.Arrangement
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementTrack

class AudioTrack(nodeId: String, arrangement: Arrangement) : ArrangementTrack(nodeId, arrangement) {
	override fun onParameter(key: String, value: Any) {}
}