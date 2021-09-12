package net.merayen.elastic.uinodes.list.xmap_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import net.merayen.elastic.ui.objects.components.buttons.Button

class BezierGraphToolbar : AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox(margin = 2f)),
	FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	/**
	 * Makes the selected points symmetrical
	 */
	private val symmetricalButton = Button("Symmetrical")

	override fun onInit() {
		super.onInit()

		symmetricalButton.handler = object : Button.IHandler {
			override fun onClick() {
				TODO("make the current selected point symmetrical...?")
			}
		}

		symmetricalButton.fontSize = 8f

		add(symmetricalButton)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)

		draw.setColor(.4f, .4f, .4f)
		draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		super.onUpdate()
	}
}