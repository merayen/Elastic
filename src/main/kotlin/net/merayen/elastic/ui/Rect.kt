package net.merayen.elastic.ui

import kotlin.math.max
import kotlin.math.min

class Rect {
	var x1 = 0f
	var y1 = 0f
	var x2 = 0f
	var y2 = 0f

	val width: Float
		get() = max(0f, x2 - x1)

	val height: Float
		get() = max(0f, y2 - y1)

	constructor(x1: Float, y1: Float, x2: Float, y2: Float) {
		this.x1 = x1
		this.y1 = y1
		this.x2 = x2
		this.y2 = y2
	}

	constructor(r: Rect) {
		x1 = r.x1
		y1 = r.y1
		x2 = r.x2
		y2 = r.y2
	}

	constructor()

	override fun toString() = String.format(
			"[Rect(x1=%f, y1=%f, x2=%f, y2=%f, w=%f, h=%f)]",
			x1, y1, x2, y2, width, height
		)

	/**
	 * Clip this rectangle with another rectangle.
	 */
	fun clip(r: Rect) = clip(r.x1, r.y1, r.x2, r.y2)

	fun clip(x1: Float, y1: Float, x2: Float, y2: Float) {
		this.x1 = max(this.x1, x1)
		this.y1 = max(this.y1, y1)
		this.x2 = min(this.x2, x2)
		this.y2 = min(this.y2, y2)
	}

	fun enlarge(r: Rect) = enlarge(r.x1, r.y1, r.x2, r.y2)

	fun enlarge(x1: Float, y1: Float, x2: Float, y2: Float) {
		this.x1 = min(this.x1, x1)
		this.y1 = min(this.y1, y1)
		this.x2 = max(this.x2, x2)
		this.y2 = max(this.y2, y2)
	}

	fun copy() = Rect(x1, y1, x2, y2)
}
