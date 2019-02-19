package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Color
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Button
import net.merayen.elastic.ui.objects.components.TextInput
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

internal class TrackHeader : UIObject() {
	internal interface Handler {
		fun onRemove()
		fun onMute()
		fun onSolo()
		fun onArmRecord()
	}

	var layoutWidth = 0F
	var layoutHeight = 0F
	var handler: Handler? = null

	private val buttons = AutoLayout(LayoutMethods.HorizontalBox(2f))

	private val trackName = TextInput()

	override fun onInit() {
		val self = this
		buttons.add(object : Button() {
			init {
				label = "X"
				handler = object : Button.IHandler {
					override fun onClick() {
						self.handler?.onRemove()
					}
				}
			}
		})
		buttons.add(object : Button() {
			init {
				label = "M"
				textColor = Color(1f, 1f, 1f)
				backgroundColor = Color(1f, 0f, 0f)
				handler = object : Button.IHandler {
					override fun onClick() {self.handler?.onMute()}
				}
			}
		})
		buttons.add(object : Button() {
			init {
				label = "S"
				textColor = Color()
				backgroundColor = Color(1f, 1f, 0f)
				handler = object : Button.IHandler {
					override fun onClick() {self.handler?.onSolo()}
				}
			}
		})
		buttons.add(object : Button() {
			init {
				label = "R"
				textColor = Color(1f, 1f, 1f)
				backgroundColor = Color(1f, 0.5f, 0.5f)
				handler = object : Button.IHandler {
					override fun onClick() {self.handler?.onArmRecord()}
				}
			}
		})

		buttons.translation.x = 5f
		buttons.translation.y = 5f
		add(buttons)

		trackName.translation.x = 5f
		trackName.translation.y = 25f
		add(trackName)
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(0.7f, 0.7f, 0.7f)
		draw.fillRect(2f, 2f, layoutWidth - 4, layoutHeight - 4)
	}
}
