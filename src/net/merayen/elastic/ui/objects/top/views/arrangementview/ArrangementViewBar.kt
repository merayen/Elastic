package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.top.viewbar.ViewBar
import java.awt.Robot

internal class ArrangementViewBar : ViewBar() {
	private val tool = DropDown()

	override fun onInit() {
		super.onInit()

		tool.addMenuItem(DropDown.Item(Label("Hei"), TextContextMenuItem("Velg meg!")))
		add(tool)
	}
}