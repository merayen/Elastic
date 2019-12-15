package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.TimePopupParameter
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.top.viewbar.ViewBar

internal class ArrangementViewBar : ViewBar(ArrangementView::class) {
	private val tool = DropDown(object : DropDown.Handler {
		override fun onChange(selected: DropDown.Item) {}
	})

	private val start = TimePopupParameter()

	override fun onInit() {
		super.onInit()

		tool.addMenuItem(DropDown.Item(Label("Select"), TextContextMenuItem("Select")))
		tool.layoutWidth = 50f
		tool.layoutHeight = 20f
		add(tool)

		add(start)
	}
}