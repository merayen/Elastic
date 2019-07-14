package net.merayen.elastic.util

class Point {
	var x = 0f
	var y = 0f

	constructor() {
		x = 0f
		y = 0f
	}

	constructor(p: Point) {
		x = p.x
		y = p.y
	}

	constructor(x: Float, y: Float) {
		this.x = x
		this.y = y
	}

	override fun toString() = "[Point(x=$x, y=$y)]"
}
