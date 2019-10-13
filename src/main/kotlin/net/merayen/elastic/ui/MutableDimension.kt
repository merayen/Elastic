package net.merayen.elastic.ui

open class MutableDimension : Dimension {
	override var width: Float
	override var height: Float

	constructor(width: Float, height: Float) {
		this.width = width
		this.height = height
	}

	constructor(dimension: Dimension) {
		width = dimension.width
		height = dimension.height
	}

	override fun toString() = "Dimension(x=$width, y=$height)"
}
