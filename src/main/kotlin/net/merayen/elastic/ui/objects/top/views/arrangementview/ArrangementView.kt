package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.system.intercom.NetListRefreshRequestMessage
import net.merayen.elastic.ui.controller.ArrangementController
import net.merayen.elastic.ui.objects.top.views.View
import net.merayen.elastic.util.Postmaster

class ArrangementView : View() {
	private val bar = ArrangementViewBar()
	private val arrangement = Arrangement()

	override fun onInit() {
		super.onInit()

		add(arrangement)
		arrangement.translation.y = 20f

		add(bar)

		sendMessage(ArrangementController.Hello())
		sendMessage(NetListRefreshRequestMessage())
	}

	override fun onUpdate() {
		super.onUpdate()
		arrangement.layoutWidth = layoutWidth
		arrangement.layoutHeight = layoutHeight - 20
	}

	override fun cloneView(): View {
		return ArrangementView()
	}

	fun handleMessage(message: Postmaster.Message) = arrangement.handleMessage(message)
}