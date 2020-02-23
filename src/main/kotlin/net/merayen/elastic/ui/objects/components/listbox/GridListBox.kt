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

class GridListBox : UIClip(), FlexibleDimension {
	private val list = object : AutoLayout<LayoutMethods.ListBox>(LayoutMethods.ListBox()) {
		override fun onDraw(draw: Draw) {
			super.onDraw(draw)
			draw.setColor(0.2f, 0.2f, 0.2f)
		}
	}

	private class HeaderItem(private val label: String): AutoLayout<LayoutMethods.HorizontalBox>(LayoutMethods.HorizontalBox()) {
		override fun onDraw(draw: Draw) {
			draw.setFont("", 10f)
			draw.text(label, 2f, 12f)
		}
	}

	private val header = UIObject()

	private val items = ArrayList<GridListBoxItem>()

	private val scroll = Scroll(list)

	private val lastUpdate = System.currentTimeMillis()

	override fun onInit() {
		super.add(scroll)
	}

	override fun add(uiobject: UIObject) {
		throw RuntimeException("Objects can not be added directly")
	}

	override fun add(uiobject: UIObject, index: Int) {
		throw RuntimeException("Objects can not be added directly")
	}

	override fun remove(uiobject: UIObject) {
		throw RuntimeException("Objects can not be added directly")
	}

	override fun removeAll() {
		throw RuntimeException("Objects can not be added directly")
	}

	override fun onUpdate() {
		scroll.layoutWidth = layoutWidth
		scroll.layoutHeight = layoutHeight
	}

	fun setHeader(columns: List<String>) {
		header.removeAll()

		for (column in columns)
			header.add(HeaderItem(column))
	}

	fun getItems() = Collections.unmodifiableList(items)

	fun addItem(item: GridListBoxItem) {
		items.add(item)

		list.add(item)
	}
}