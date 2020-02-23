package net.merayen.elastic.ui.objects.components.listbox

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.UIClip
import net.merayen.elastic.ui.objects.components.Scroll
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout
import net.merayen.elastic.ui.objects.components.autolayout.LayoutMethods

class GridListBox : UIClip(), FlexibleDimension {
	val items = object : AutoLayout<LayoutMethods.ListBox>(LayoutMethods.ListBox()) {
		override fun onDraw(draw: Draw) {
			super.onDraw(draw)
			draw.setColor(0.2f, 0.2f, 0.2f)
		}

		override fun add(uiobject: UIObject) {
			if (uiobject !is GridListBoxItem)
				throw RuntimeException("Only GridListBoxItems are allowed")
		}
	}

	private val scroll = Scroll(items)

	override fun onInit() {
		add(scroll)
	}

	override fun onUpdate() {
		scroll.layoutWidth = layoutWidth
		scroll.layoutHeight = layoutHeight
	}
}