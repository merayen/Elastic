package net.merayen.elastic.ui.objects.top.views.transportview

import net.merayen.elastic.ui.objects.top.views.View

class TransportView : View() {
	private val bar = TransportViewBar()
	private val buttons = TransportViewButtons()

	override fun onInit() {
		super.onInit()
		add(bar)
		this.add(buttons)
		buttons.translation.y = 20f
	}

	override fun cloneView(): View {
		return TransportView()
	}

}
