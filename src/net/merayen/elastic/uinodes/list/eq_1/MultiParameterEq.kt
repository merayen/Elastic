package net.merayen.elastic.uinodes.list.eq_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class MultiParameterEq : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private val eqPoints = MultiParameterEqData()

	private val multiParameterEqCurveBox = MultiParameterEqCurveBox(eqPoints, object : MultiParameterEqCurveBox.Handler {
		override fun onCreatePoint() {
			println("hei")
		}

		override fun onDeletePoint() {}
		override fun onChangePoint() {}
	})

	override fun onInit() {
		add(multiParameterEqCurveBox)
	}

	override fun onDraw(draw: Draw) {

	}

	override fun onUpdate() {
		multiParameterEqCurveBox.layoutWidth = layoutWidth
		multiParameterEqCurveBox.layoutHeight = layoutHeight
	}
}