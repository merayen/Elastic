package net.merayen.elastic.ui.objects.top.views.splashview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.top.views.View

class SplashView : View() {
	private val bar = SplashViewBar()

	override fun onInit() {
		super.onInit()
		add(bar)
	}

	override fun cloneView(): View {
		return SplashView()
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setColor(255, 255, 100)
		draw.setFont("", 100f)
		draw.text("Elastic", 10f, 150f)

		draw.fillOval(450f, (Math.sin(System.currentTimeMillis() / 1000.0) * 400).toFloat() + 400, 100f, 100f)
	}

	override fun onUpdate() {
		super.onUpdate()
		bar.layoutWidth = getWidth()
	}
}
