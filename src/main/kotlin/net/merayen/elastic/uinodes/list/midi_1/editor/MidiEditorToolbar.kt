package net.merayen.elastic.uinodes.list.midi_1.editor

import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class MidiEditorToolbar : UIObject() {
	val layout = AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox(2f))

	override fun onInit() {
		add(layout)
		layout.add(Button("P"))
		layout.add(Button("U"))
		layout.add(Button("E"))
		layout.add(Button("I"))
		layout.add(Button("X"))
	}
}