package net.merayen.elastic.ui.objects.top.views.arrangementview

import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.controller.ArrangementController
import net.merayen.elastic.ui.objects.top.easymotion.Branch
import net.merayen.elastic.ui.objects.top.views.View

class ArrangementView : View() {
	private val bar = ArrangementViewBar()
	private val arrangement = Arrangement()

	var arrangementViewController: ArrangementController? = null  // Automatically set by ArrangementViewController

	private var loaded = false

	override fun onInit() {
		super.onInit()

		add(arrangement)
		arrangement.translation.y = 40f

		add(bar)

		sendMessage(ArrangementController.Hello())
	}

	override fun onUpdate() {
		super.onUpdate()
		arrangement.layoutWidth = layoutWidth
		arrangement.layoutHeight = layoutHeight - 40

		val arrangementViewController = arrangementViewController
		if (!loaded && arrangementViewController != null) {
			for (message in arrangementViewController.getNetListRefreshMessages())
				handleMessage(message)

			loaded = true
		}
	}

	override val easyMotionBranch = object : Branch(this) {}

	override fun cloneView(): View {
		return ArrangementView()
	}

	fun handleMessage(message: ElasticMessage) = arrangement.handleMessage(message)

	fun load(messages: ArrayList<ElasticMessage>) {
		if (loaded)
			return

		loaded = true

		for (message in messages)
			handleMessage(message)
	}
}