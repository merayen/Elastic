package net.merayen.elastic.util

open class MutablePoint : Point {
	override var x = 0f
	override var y = 0f

	constructor() {
		x = 0f
		y = 0f
	}

	constructor(p: MutablePoint) {
		x = p.x
		y = p.y
	}

	constructor(x: Float, y: Float) {
		this.x = x
		this.y = y
	}

	override fun toString() = "[Point(x=$x, y=$y)]"
}
