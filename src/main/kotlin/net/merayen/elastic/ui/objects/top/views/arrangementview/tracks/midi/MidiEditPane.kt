package net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.midi

import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.BaseEditPane
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.EventZone
import net.merayen.elastic.uinodes.list.midi_1.editor.Editor

class MidiEditPane(val nodeId: String) : BaseEditPane() {
	private var nodeEditor: Editor? = null

	var eventZone: EventZone? = null
		set(value) {
			if (field != null)
				remove(nodeEditor!!)

			if (value != null) {
				val nodeEditor = Editor(nodeId)

				this.nodeEditor = nodeEditor
				add(nodeEditor)
			}

			field = value
		}

	override fun onUpdate() {
		super.onUpdate()
		nodeEditor?.layoutWidth = layoutWidth
		nodeEditor?.layoutHeight = layoutHeight
	}
}