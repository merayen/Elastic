package net.merayen.elastic.uinodes.list.midi_1.editor

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.framework.Joystick

class MidiEditorMenuBar(private val handler: Handler) : UIObject(), FlexibleDimension {
	interface Handler {
		fun onChangeNoteStart(value: Float)
		fun onChangeNoteLength(value: Float)
		fun onTransposeNote(value: Int)
	}

	override var layoutWidth = 100f
	override var layoutHeight = 100f

	val quantizeScale = GridQuantize()

	private lateinit var changeNoteStart: Joystick
	private lateinit var changeNoteLength: Joystick
	private lateinit var transposeNote: Joystick

	private val layout = AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox(10f))

	override fun onInit() {
		changeNoteStart = Joystick(object : Joystick.Handler {
			override fun onMove(x: Float, y: Float) {
				handler.onChangeNoteStart(-y / 100)
			}

			override fun onDrop() {}

			override fun onLabel(x: Float, y: Float): String {
				return if (x != 0f || y != 0f)
					"Note start: ${-y / 100}"
				else
					"Note start"
			}
		})

		changeNoteLength = Joystick(object : Joystick.Handler {
			override fun onMove(x: Float, y: Float) {
				handler.onChangeNoteLength(-y / 100)
			}

			override fun onDrop() {}

			override fun onLabel(x: Float, y: Float): String {
				return if (x != 0f || y != 0f)
					"Note length: ${-y / 100}"
				else
					"Note length"
			}
		})

		transposeNote = Joystick(object : Joystick.Handler {
			override fun onMove(x: Float, y: Float) {
				handler.onTransposeNote((-y / 10).toInt())
			}

			override fun onDrop() {}

			override fun onLabel(x: Float, y: Float): String {
				return if (x != 0f || y != 0f)
					"Transpose: ${(-y / 10).toInt()}"
				else
					"Transpose"
			}
		})

		add(layout)
		layout.add(quantizeScale)
		layout.add(changeNoteStart)
		layout.add(changeNoteLength)
		layout.add(transposeNote)
		layout.add(MidiEditorToolbar())

	}
}