package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.BackendReadyMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.window.Window
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import net.merayen.elastic.ui.objects.top.views.splashview.SplashView

class SplashViewController(top: Top) : Controller(top) {
	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {
		if (message is BackendReadyMessage) {

			var setupWindow = false
			for (view in getViews(SplashView::class.java)) {
				val nodeView = view.swap(NodeView::class)

				if (!setupWindow) {
					val window = nodeView.search.parentByType(Window::class.java)
					if (window != null) {
						window.layoutWidth = 1000f
						window.layoutHeight = 1000f
						window.isDecorated = true
						window.center()
						setupWindow = true
					}
				}
			}
		}
	}

	override fun onMessageFromUI(message: ElasticMessage) {}
}