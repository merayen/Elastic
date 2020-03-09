package net.merayen.elastic.ui.objects.components.listbox

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods
import java.util.*
import kotlin.collections.ArrayList

class ListBox : UIClip(), FlexibleDimension {
	val list = object : AutoLayout<LayoutMethods.ListBox>(LayoutMethods.ListBox()) {
		override fun onDraw(draw: Draw) {
			super.onDraw(draw)
			draw.setColor(0.2f, 0.2f, 0.2f)
			draw.fillRect(0f, 0f, layoutWidth, layoutHeight)
		}
	}

	private val scroll = Scroll(list)

	override fun onInit() {
		super.add(scroll)
	}

	override fun add(uiobject: UIObject) = throw RuntimeException("Objects can not be added directly")

	override fun add(uiobject: UIObject, index: Int) = throw RuntimeException("Objects can not be added directly")

	override fun remove(uiobject: UIObject) = throw RuntimeException("Objects can not be added directly")

	override fun removeAll() = throw RuntimeException("Objects can not be added directly")

	override fun onUpdate() {
		scroll.layoutWidth = layoutWidth
		scroll.layoutHeight = layoutHeight
	}
}