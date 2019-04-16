package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.BaseEditPane
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.BaseTimeLine

class EventPane : UIObject(), FlexibleDimension {
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight

	var editMode = false
		set(value) {
			field = value
			updateView()
		}

	/**
	 * Shown when editMode is set to true.
	 */
	var editPane: BaseEditPane? = null
		set(value) {
			field = value
			updateView()
		}

	/**
	 * Shown when editMode is set to false
	 */
	var timeLine: BaseTimeLine? = null
		set(value) {
			field = value
			updateView()
		}

	override fun onInit() {
		updateView()
	}

	override fun onDraw(draw: Draw) {
		//draw.setColor(255,0,255)
		//draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		timeLine?.layoutHeight = layoutHeight
		timeLine?.minimumWidth = layoutWidth
	}

	private fun updateView() {
		val timeLine = timeLine
		val editPane = editPane

		if (editMode) {
			if (timeLine?.parent != null)
				remove(timeLine)

			if (editPane != null && editPane.parent == null)
				add(editPane)
		} else {
			if (timeLine != null && timeLine.parent == null)
				add(timeLine)

			if (editPane?.parent != null)
				remove(editPane)
		}
	}
}