package net.merayen.elastic.ui

class ImmutableDimension : Dimension {
	private var _width: Float
	private var _height: Float

	override var width: Float
		get() = _width
		set(_) = throw UnsupportedOperationException()

	override var height: Float
		get() = _height
		set(_) = throw UnsupportedOperationException()

	constructor(width: Float, height: Float) {
		_width = width
		_height = height
	}

	constructor(dimension: Dimension) {
		_width = dimension.width
		_height = dimension.height
	}
}