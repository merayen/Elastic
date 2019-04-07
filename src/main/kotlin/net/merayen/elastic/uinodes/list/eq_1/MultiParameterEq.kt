package net.merayen.elastic.uinodes.list.eq_1

import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

class MultiParameterEq : UIObject(), FlexibleDimension {
	override var layoutWidth = 100f
	override var layoutHeight = 100f

	private val eqPoints = MultiParameterEqData()

	private val editPanel = MultiParameterEqEditPanel()

	private val multiParameterEqCurveBox = MultiParameterEqCurveBox(eqPoints, object : MultiParameterEqCurveBox.Handler {
		override fun onCreatePoint() {}
		override fun onDeletePoint() {}
		override fun onChangePoint() {}
	})

	override fun onInit() {
		add(multiParameterEqCurveBox)
		add(editPanel)
	}

	override fun onDraw(draw: Draw) {}

	override fun onUpdate() {
		multiParameterEqCurveBox.layoutWidth = layoutWidth
		multiParameterEqCurveBox.layoutHeight = layoutHeight - 30f

		editPanel.translation.x = 10f
		editPanel.translation.y = layoutHeight - 25f
	}
}