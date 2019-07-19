package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.audio

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.views.arrangementview.Arrangement
import net.merayen.elastic.ui.objects.top.views.arrangementview.ArrangementTrack

class AudioTrack(nodeId: String, arrangement: Arrangement) : ArrangementTrack(nodeId, arrangement) {
	override fun clearSelections() {}

	override fun onParameter(instance: BaseNodeData) {}

	override fun onSelectionRectangle(selectionRectangle: UIObject) {}
}