package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.util.Movable
import kotlin.math.max
import kotlin.math.min

class Scroll(private val uiobject: UIObject) : UIObject() {
	var layoutWidth = 100f
	var layoutHeight = 100f
	var barWidth = 10f
	private var barLengthX = 20f
	private var barLengthY = 20f

	private val clip = UIClip()
	private val barX = Bar(true)
	private val barY = Bar(false)

	private var contentWidth = 0f
	private var contentHeight = 0f
	private var moving = false

	private inner class Bar constructor(private val x: Boolean) : UIObject(), FlexibleDimension {
		override var layoutWidth = 10f
		override var layoutHeight = 10f

		private val movable = Movable(this, this)

		override fun onInit() {
			if (x)
				movable.drag_scale_y = 0f
			else
				movable.drag_scale_x = 0f

			movable.setHandler(object : Movable.IMoveable {
				override fun onMove() {
					limitBars()
					updateFromBars()
				}

				override fun onGrab() {
					moving = true
				}

				override fun onDrop() {
					moving = false
				}
			})
		}

		override fun onDraw(draw: Draw) {
			draw.setColor(0.5f, 0.5f, 0.5f)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
		}

		override fun onEvent(event: UIEvent) {
			movable.handle(event)
		}
	}

	override fun onInit() {
		clip.add(uiobject)
		super.add(clip)
		updateBars()
	}

	override fun onDraw(draw: Draw) {
		draw.setColor(50, 50, 50)
		draw.fillRect(0f, layoutHeight - barWidth, layoutWidth - barWidth, barWidth)
		draw.fillRect(layoutWidth - barWidth, 0f, barWidth, layoutHeight - barWidth)
	}

	override fun onUpdate() {
		updateFromBars()

		contentWidth = uiobject.getWidth()
		contentHeight = uiobject.getHeight()

		clip.layoutWidth = layoutWidth - barWidth
		clip.layoutHeight = layoutHeight - barWidth

		if (uiobject.translation.x > 0f)
			uiobject.translation.x = 0f

		if (uiobject.translation.y > 0f)
			uiobject.translation.y = 0f

		if (uiobject.translation.x < -(contentWidth - layoutWidth + barLengthX) && contentWidth > layoutWidth)
			uiobject.translation.x = -(contentWidth - layoutWidth + barLengthX)

		if (uiobject.translation.y < -(contentHeight - layoutHeight + barLengthY) && contentHeight > layoutHeight)
			uiobject.translation.y = -(contentHeight - layoutHeight + barLengthY)

		barX.translation.y = layoutHeight - barWidth
		barY.translation.x = layoutWidth - barWidth

		if(!moving) {
			updateBars()
			updateBarLengths()
		}

		limitBars()
	}

	private fun updateBars() {
		if (contentWidth - (layoutWidth - barWidth) > 0) {
			if (barX.parent == null)
				super.add(barX)

			barX.translation.y = layoutHeight - barWidth
		} else if (barX.parent != null) {
			remove(barX)
		}

		if (contentHeight - (layoutHeight - barWidth) > 0) {
			if (barY.parent == null)
				super.add(barY)

			barY.translation.x = layoutWidth - barWidth
		} else if (barY.parent != null) {
			remove(barY)
		}
	}

	private fun limitBars() {
		barX.translation.x = max(0f, min(layoutWidth - barWidth - barLengthX, barX.translation.x))
		barY.translation.y = max(0f, min(layoutHeight - barWidth - barLengthY, barY.translation.y))
	}

	private fun updateFromBars() {
		uiobject.translation.x = barX.translation.x / (layoutWidth - barWidth) * -(contentWidth - layoutWidth + barLengthX)
		uiobject.translation.y = barY.translation.y / (layoutHeight - barWidth) * -(contentHeight - layoutHeight + barLengthY)
	}

	private fun updateBarLengths() {
		barLengthX = max(20f, min(layoutWidth * 2 - barWidth * 2 - contentWidth, layoutWidth - barWidth))
		barLengthY = max(20f, min(layoutHeight * 2 - barWidth * 2 - contentHeight, layoutHeight - barWidth))

		barX.layoutWidth = barLengthX
		barY.layoutHeight = barLengthY
	}

	override fun add(uiobject: UIObject) = throw RuntimeException("Items can not be added to Scroll directly")
	override fun add(uiobject: UIObject, index: Int) = throw RuntimeException("Items can not be added to Scroll directly")
}
