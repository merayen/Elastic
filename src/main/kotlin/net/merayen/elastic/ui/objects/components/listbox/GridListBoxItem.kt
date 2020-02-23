package net.merayen.elastic.ui.objects.components.listbox

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class GridListBoxItem(elements: HashMap<String, UIObject>) : UIObject() {
	init {
		for ((name, obj) in elements) {
			if (obj !is FlexibleDimension)
				throw RuntimeException("$obj must inherit FlexibleDimension")

			add(obj)
		}
	}

	fun updateLayout(columns: List<String>, widths: List<Float>) {
		for ((i, item) in children.withIndex()) {
			item as FlexibleDimension

			item.layoutWidth = widths[i]
			item.translation.x = if (i > 1) widths[i - 1] else 0f
			item.translation.y = 0f
		}
	}
}

