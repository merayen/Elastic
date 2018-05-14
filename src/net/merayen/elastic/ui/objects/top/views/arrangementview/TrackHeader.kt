package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Button
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

internal class TrackHeader : UIObject() {
	internal interface Handler {
		fun onRemove()
	}

	var layoutWidth = 0F
	var layoutHeight = 0F
	var handler: Handler? = null

	private val buttons = AutoLayout(LayoutMethods.HorizontalBox(10f))

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

		add(buttons)
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(50, 0, 50)
		draw.fillRect(2f, 2f, layoutWidth - 4, layoutHeight - 4)
	}
}
