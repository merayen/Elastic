package net.merayen.elastic.util

class ImmutablePoint : Point {
	private val _x: Float
	private val _y: Float

	override var x: Float
		get() = _x
		set(_) = throw UnsupportedOperationException()

	override var y: Float
		get() = _y
		set(_) = throw UnsupportedOperationException()

	constructor(x: Float, y: Float) {
		_x = x
		_y = y
	}

	constructor(point: Point) {
		_x = point.x
		_y = point.y
	}
}