package net.merayen.elastic.ui.objects.components

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.util.Movable

class Scroll(private val uiobject: UIObject) : UIObject() {
	var layoutWidth = 100f
	var layoutHeight = 100f
	var barWidth = 10f
	var barLength = 20f

	private val clip = UIClip()
	private val barX = Bar(true)
	private val barY = Bar(false)

	private var contentWidth = 0f
	private var contentHeight = 0f
	private var moving = false

	private inner class Bar constructor(private val x: Boolean) : UIObject(), FlexibleDimension {
		override var layoutWidth = if(x) 20f else 10f
		override var layoutHeight = if(x) 10f else 20f

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
		add(clip)
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
		//println("${contentHeight.toInt()}\t${uiobject.translation.y.toInt()}")

		clip.layoutWidth = layoutWidth - barWidth
		clip.layoutHeight = layoutHeight - barWidth

		if (uiobject.translation.x > 0f)
			uiobject.translation.x = 0f

		if (uiobject.translation.y > 0f)
			uiobject.translation.y = 0f

		if (uiobject.translation.x < -(contentWidth - layoutWidth + barLength) && contentWidth > layoutWidth)
			uiobject.translation.x = -(contentWidth - layoutWidth + barLength)

		if (uiobject.translation.y < -(contentHeight - layoutHeight + barLength) && contentHeight > layoutHeight)
			uiobject.translation.y = -(contentHeight - layoutHeight + barLength)

		barX.translation.y = layoutHeight - barWidth
		barY.translation.x = layoutWidth - barWidth

		if(!moving)
			updateBars()

		limitBars()
	}

	private fun updateBars() {
		if (contentWidth - layoutWidth > 0) {
			if (barX.parent == null)
				add(barX)

			barX.translation.y = layoutHeight - barWidth
		} else if (barX.parent != null) {
			remove(barX)
		}

		if (contentHeight - layoutHeight > 0) {
			if (barY.parent == null)
				add(barY)

			barY.translation.x = layoutWidth - barWidth
		} else if (barY.parent != null) {
			remove(barY)
		}
	}

	private fun limitBars() {
		barX.translation.x = Math.max(0f, Math.min(layoutWidth - barWidth - barLength, barX.translation.x))
		barY.translation.y = Math.max(0f, Math.min(layoutHeight - barWidth - barLength, barY.translation.y))
	}

	private fun updateFromBars() {
		uiobject.translation.x = barX.translation.x / (layoutWidth - barWidth) * -(contentWidth - layoutWidth + barLength)
		uiobject.translation.y = barY.translation.y / (layoutHeight - barWidth) * -(contentHeight - layoutHeight + barLength)
		//uiobject.translation.y = sin((System.currentTimeMillis() % (Math.PI * 2000)) / 1000).toFloat() * 50f - 50
	}
}
