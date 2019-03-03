package net.merayen.elastic.ui.objects.components.autolayout

import net.merayen.elastic.ui.FlexibleDimension
import net.merayen.elastic.ui.UIObject

object LayoutMethods {

	class HorizontalBox : AutoLayout.Placement {
		var margin = 0f
		var maxWidth = Float.MAX_VALUE
		private var width = 0f
		private var height = 0f

		constructor()

		constructor(margin: Float, max_width: Float) {
			this.margin = margin
			this.maxWidth = max_width
		}

		constructor(margin: Float) {
			this.margin = margin
		}

		override fun place(objects: List<UIObject>) {
			var x = margin
			var y = margin
			var rowHeight = 0f
			height = 0f
			width = 0f

			for (obj in objects) {
				val obj_width = obj.getWidth()
				val obj_height = obj.getHeight()

				if (x + obj_width + margin > maxWidth) {
					x = margin
					y += rowHeight + margin
					rowHeight = 0f
				}

				obj.translation.x = x
				obj.translation.y = y

				x += obj_width + margin

				rowHeight = Math.max(rowHeight, obj_height)
				height = Math.max(height, y + obj_height)
				width = Math.max(width, x + obj_width)
			}
		}

		override fun getWidth() = width
		override fun getHeight() = height
	}


	/**
	 * Arranges UIObjects in a horizontal line. Supports both UIObjects with fixed width and FlexibleDimension-UIObjects that can be resized by this
	 * AutoLayout-sizer.
	 */
	class HorizontalLiquidBox : AutoLayout.Placement, FlexibleDimension {
		class Constraint(val factor: Float = 0f) {
			internal var calculatedFactor = 0f
		}

		override var layoutWidth = 0f
		override var layoutHeight = 0f
		val constraints = HashMap<UIObject, Constraint>()

		override fun place(objects: List<UIObject>) {
			calculate(objects)

			var x = 0f
			for(obj in objects) {
				val constraint = constraints[obj] ?: throw RuntimeException("Should not happen")
				obj.translation.x = x
				obj.translation.y = 0f
				if(obj is FlexibleDimension) {
					obj.layoutWidth = constraint.calculatedFactor * layoutWidth
					obj.layoutHeight = layoutHeight
					x += obj.layoutWidth
				} else {
					x += obj.getWidth()
				}
			}
		}

		override fun getWidth() = layoutWidth

		override fun getHeight() = layoutHeight

		fun applyConstraint(uiobject: UIObject, constraint: Constraint) {
			constraints.put(uiobject, constraint)
		}

		private fun calculate(objects: List<UIObject>) {
			for (obj in objects)
				if (obj !in constraints)
					constraints.put(obj, Constraint())

			if (layoutWidth <= 0 || layoutHeight <= 0) {
				for (obj in objects)
					if (obj is FlexibleDimension)
						obj.layoutWidth = 0f
				return
			}

			var fixedWidthSum = 0f
			var flexibleWidthSum = 0f
			for (obj in objects)
				if (obj !is FlexibleDimension)
					fixedWidthSum += obj.getWidth()
				else
					flexibleWidthSum += constraints[obj]!!.factor / obj.translation.scale_x

			val flexibleRatio = (1 - fixedWidthSum / layoutWidth) / flexibleWidthSum
			if (flexibleRatio > 0) {
				for (obj in objects) {
					if (obj is FlexibleDimension) {
						val constraint = constraints[obj]!!
						constraint.calculatedFactor = constraint.factor * flexibleRatio
					}
				}
			}
		}
	}
}