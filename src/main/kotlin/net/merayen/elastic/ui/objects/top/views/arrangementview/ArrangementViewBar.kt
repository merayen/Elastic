package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.ui.objects.components.BeatPopupParameter
import net.merayen.elastic.ui.objects.components.DropDown
import net.merayen.elastic.ui.objects.components.Label
import net.merayen.elastic.ui.objects.components.PopupParameter1D
import net.merayen.elastic.ui.objects.contextmenu.TextContextMenuItem
import net.merayen.elastic.ui.objects.top.viewbar.ViewBar

internal class ArrangementViewBar : ViewBar(ArrangementView::class) {
	private val tool = DropDown(object : DropDown.Handler {
		override fun onChange(selected: DropDown.Item) {}
	})

	/**
	 * Lenght in bars for the current project
	 */
	private val length = BeatPopupParameter()

	/**
	 *
	 */
	private val bpm = PopupParameter1D()

	override fun onInit() {
		super.onInit()

		tool.addMenuItem(DropDown.Item(Label("Select"), TextContextMenuItem("Select")))
		tool.layoutWidth = 50f
		tool.layoutHeight = 20f
		add(tool)

		length.handler = object : BeatPopupParameter.Handler {
			override fun onChange(value: Int) {}
			override fun onLabel(value: Int) = "Score length: $value"
		}

		length.value = 0
		add(length)
	}
}