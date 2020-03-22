package net.merayen.elastic.ui.util

/**
 * Data structure for arranging objects in a 2D space, where one can navigate using e.g the arrow keys.
 * Each object has 4 sides, where one of the sides can be connected to another object.
 */
class ArrowNavigation {
	enum class Direction {
		LEFT,
		UP,
		RIGHT,
		DOWN
	}

	class Point {
		var left: Point? = null
		var right: Point? = null
		var up: Point? = null
		var down: Point? = null

		fun removeLink(point: Point) {
			if (left === point)
				left = null
			if (right === point)
				right = null
			if (up === point)
				up = null
			if (down === point)
				down = null
		}

		fun findLast(direction: Direction): Point {
			var current = this

			for (i in 0 until 10000) {
				current = when (direction) {
					Direction.LEFT -> current.left ?: return current
					Direction.RIGHT -> current.right ?: return current
					Direction.UP -> current.up ?: return current
					Direction.DOWN -> current.down ?: return current
				}
			}

			throw RuntimeException("Can not trace. Loop?")
		}
	}

	private val points = HashSet<Point>()
	var current: Point? = null

	fun move(direction: Direction) {
		if (points.isEmpty())
			return

		if (current == null) {
			current = points.first() // Not really caring what is decided as "first"
			return
		}

		current = when (direction) {
			Direction.LEFT -> current?.left ?: return
			Direction.RIGHT -> current?.right ?: return
			Direction.UP -> current?.up ?: return
			Direction.DOWN -> current?.down ?: return
		}
	}

	fun move(point: Point) {
		current = point
	}

	fun newPoint(): Point {
		val point = Point()
		points.add(point)
		return point
	}

	fun remove(point: Point) {
		if (current === point)
			current = null

		for (p in points)
			p.removeLink(point)

		points.remove(point)
	}

	fun clear() {
		current = null
		points.clear()
	}
}