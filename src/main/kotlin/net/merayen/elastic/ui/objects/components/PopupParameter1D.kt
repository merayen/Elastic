package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.framework.PopupParameter

/**
 * Presents UI for a single parameter.
 */
class PopupParameter1D : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 20f
	var automaticSizing = true

	interface Handler {
		fun onMove(value: Float)
		fun onChange(value: Float)
		fun onLabel(value: Float): String
	}

	private inner class Window : UIObject() {
		override fun onDraw(draw: Draw) {
			draw.setColor(150, 150, 150)
			draw.fillRect(-20f, 0f, 20f, popup_height)
			draw.setColor(50, 50, 50)
			draw.rect(-20f, 0f, 20f, popup_height)
		}
	}

	private val minified = object : UIObject() {
		override fun onDraw(draw: Draw) {
			draw.setColor(0.2f, 0.2f, 0.2f)
			draw.fillRect(0f, 0f,  layoutWidth + margin * 2, 12f + margin * 2)

			draw.setStroke(1f)
			draw.setColor(0.7f, 0.7f, 0.7f)
			draw.rect(0f, 0f, layoutWidth + margin * 2, 12f + margin * 2)
		}
	}

	private val box: PopupParameter
	private val window: UIObject
	private val window_container: UIObject // Only for offsetting the popup
	var handler: Handler? = null
	var popup_height = 200f

	private val margin = 4f // ???

	@JvmField
	val label = Label()

	@JvmField
	var drag_scale = 1f

	override fun onInit() {
		add(box)
		minified.add(label)
	}

	override fun onUpdate() {
		box.popup_height = popup_height
		window.translation.y = label.getHeight() / 2
		box.drag_scale_y = drag_scale
		label.translation.x = margin
		label.translation.y = margin
		getWidth()

		if (automaticSizing)
			layoutWidth = label.labelWidth
	}

	val pane: UIObject
		get() = box.minified

	var value: Float
		get() = box.y
		set(value) {
			box.y = value
			label.text = handler?.onLabel(value) ?: ""
		}

	init {
		window_container = UIObject()
		window = Window()
		window_container.add(window)

		box = PopupParameter(minified, window_container)
		box.setHandler(object : PopupParameter.Handler {
			override fun onMove() {
				box.popup.translation.x = 0f // Constrain X-axis
				handler?.onMove(box.y)
				label.text = handler?.onLabel(box.y) ?: ""
			}

			override fun onGrab() {}
			override fun onDrop() {
				handler?.onChange(box.popup.translation.y / popup_height)
				label.text = handler?.onLabel(box.y) ?: ""
			}
		})
		box.popup_height = popup_height
	}
}