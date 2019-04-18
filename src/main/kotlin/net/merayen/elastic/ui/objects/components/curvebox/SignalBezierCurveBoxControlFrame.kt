package net.merayen.elastic.ui.objects.components.curvebox

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.buttons.Button
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class SignalBezierCurveBoxControlFrame : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	val bezier = SignalBezierCurveBox()
	private val buttons = AutoLayout(LayoutMethods.HorizontalBox(2f, 0f))

	override fun onInit() {
		bezier.translation.y = 15f
		add(bezier)
		add(buttons)

		buttons.add(object : Button() {
			init {
				label = "+"
				fontSize = 8f
				handler = object : IHandler {
					override fun onClick() {
						bezier.insertPoint(1)
					}
				}
			}
		})
	}

	override fun onUpdate() {
		bezier.layoutWidth = layoutWidth
		bezier.layoutHeight = Math.max(0f, layoutHeight - 15)
	}
}