package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.top.views.arrangementview.tracks.common.BaseTimeLine

class EventPane : UIObject(), FlexibleDimension {
	override var layoutWidth = 0f
	override var layoutHeight = 0f

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight

	var timeLine: BaseTimeLine? = null
		set(value) {
			val timeLine = timeLine
			if (timeLine != null)
				remove(timeLine)

			field = value

			if(value != null)
				add(value)
		}

	override fun onDraw(draw: Draw) {
		draw.setColor(255,0,255)
		draw.rect(0f, 0f, layoutWidth, layoutHeight)
	}

	override fun onUpdate() {
		timeLine?.layoutHeight = layoutHeight
		timeLine?.minimumWidth = layoutWidth
	}
}