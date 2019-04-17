package net.merayen.elastic.ui.util

import net.merayen.elastic.ui.Rect
import kotlin.math.max
import kotlin.math.min

fun boolean(rect1: Rect, rect2: Rect): Rect {
	// (lambda x1,w1,x2,w2: (min(x1+w1, max(x1,x2)), max(x1,min(x1+w1,x2+w2)) - max(x1,x2)))(2,3, 2,2)

	val rect3 = Rect()

	rect3.x1 = min(rect1.x2, max(rect1.x1,rect2.x1))
	rect3.y1 = min(rect1.y2, max(rect1.y1,rect2.y1))

	rect3.x2 = max(rect1.x1, min(rect1.x2,rect2.x2))
	rect3.y2 = max(rect1.y1, min(rect1.y2,rect2.y2))

	return rect3
}