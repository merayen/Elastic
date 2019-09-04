package net.merayen.elastic.ui.objects.top.views.splashview

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.event.UIEvent
import net.merayen.elastic.ui.objects.components.SelectionRectangle
import net.merayen.elastic.ui.objects.top.views.View
import kotlin.math.pow

class SplashView : View() {
	private val bar = SplashViewBar()
	private val selectionRectangle = SelectionRectangle(this)

	override fun onInit() {
		super.onInit()
		add(bar)
		add(selectionRectangle)
	}

	override fun cloneView() = SplashView()

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		draw.setColor(255, 255, 100)
		draw.setFont("", 100f)
		draw.text("Elastic", 10f, 600f)

		val u = Math.sin(System.currentTimeMillis() / 500.0).toFloat()
		val y = 500 - Math.abs(u * 400)
		draw.fillOval(450f, y, 100f, 100f - u.pow(10f) * 5f)

		draw.setStroke(2f)
		draw.setColor(0.5f, 0.5f, 1f)
		draw.line(0f, 600f, layoutWidth, 600f)
	}

	override fun onUpdate() {
		super.onUpdate()
		bar.layoutWidth = getWidth()
	}

	override fun onEvent(event: UIEvent) {
		super.onEvent(event)
		selectionRectangle.handle(event)
	}
}
