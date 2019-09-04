package net.merayen.elastic.ui.objects.top.viewport

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LayoutTest {
	@Test
	fun testResizing() {
		val a = "a"
		val b = "b"
		val c = "c"
		val d = "d"
		val e = "e"

		val l = Layout(a)

		var layout: List<Layout.CalculatedPosition> = l.layout

		assertEquals(1, layout.size)

		validate(layout, 0, 0f, 0f, 1f, 1f, a)

		l.splitVertical(a, b)

		layout = l.layout

		assertEquals(2, layout.size)

		validate(layout, 0, 0f, 0f, 1f, 1f, a)
		validate(layout, 1, 1f, 0f, 0f, 1f, b)

		l.resizeWidth(b, 0.5f) // This operation should be ignored, as resizing left-most object is not allowed

		layout = l.layout

		validate(layout, 0, 0f, 0f, 1f, 1f, a)
		validate(layout, 1, 1f, 0f, 0f, 1f, b)

		l.splitVertical(b, c)
		l.resizeWidth(a, 0.25f)
		l.resizeWidth(b, 0.10f)

		layout = l.layout

		assertEquals(3, layout.size)

		validate(layout, 0, 0.00f, 0f, 0.25f, 1f, a)
		validate(layout, 1, 0.25f, 0f, 0.10f, 1f, b)
		validate(layout, 2, 0.35f, 0f, 0.65f, 1f, c)

		// Now create a vertical out of "b", with "d" below, inside the horizontal ruler
		l.splitHorizontal(b, d)
		l.resizeHeight(b, 0.75f)

		layout = l.layout

		validate(layout, 0, 0.00f, 0f, 0.25f, 1f, a)
		validate(layout, 1, 0.25f, 0f, 0.10f, 0.75f, b)
		validate(layout, 2, 0.35f, 0f, 0.65f, 1f, c)
		validate(layout, 3, 0.25f, 0.75f, 0.10f, 0.25f, d)

		// Now try to resize "d"'s layoutWidth, which should resize its whole parent ruler, as it is a vertical ruler
		l.resizeWidth(d, 0.01f)

		layout = l.layout

		validate(layout, 0, 0.00f, 0f, 0.25f, 1f, a) // Should not have been changed
		validate(layout, 1, 0.25f, 0f, 0.01f, 0.75f, b)
		validate(layout, 2, 0.26f, 0f, 0.74f, 1f, c)
		validate(layout, 3, 0.25f, 0.75f, 0.01f, 0.25f, d)

		// Now try to resize a to take all the space, which is not possible, as a can only resize itself over its right item, and no more
		l.resizeWidth(a, 1f)
		layout = l.layout

		validate(layout, 0, 0.00f, 0f, 0.26f, 1f, a) // Should only have taken 0.01f more layoutWidth due do right item (vertical ruler containing *b* and *d*) has a layoutWidth of only 0.01f
		validate(layout, 1, 0.26f, 0f, 0f, 0.75f, b) // Should now have zero with, as *a* has span itself over
		validate(layout, 2, 0.26f, 0f, 0.74f, 1f, c) // Should not have changed
		validate(layout, 3, 0.26f, 0.75f, 0f, 0.25f, d) // Should now have zero with, as *a* has span itself over

		// Now resize *a* to 0 layoutWidth. *b* should take over the whole layoutWidth
		l.resizeWidth(a, 0f)
		layout = l.layout

		validate(layout, 0, 0.00f, 0.00f, 0.00f, 1.00f, a) // Should only have taken 0.01f more layoutWidth due do right item (vertical ruler containing *b* and *d*) has a layoutWidth of only 0.01f
		validate(layout, 1, 0.00f, 0.00f, 0.26f, 0.75f, b) // Should now have zero with, as *a* has span itself over
		validate(layout, 2, 0.26f, 0.00f, 0.74f, 1.00f, c) // Should not have changed
		validate(layout, 3, 0.00f, 0.75f, 0.26f, 0.25f, d) // Should now have zero with, as *a* has span itself over
	}

	private fun validate(layout: List<Layout.CalculatedPosition>, i: Int, x: Float, y: Float, width: Float, height: Float, obj: Any) {
		assertSame(layout[i].obj, obj)

		assertFalse(Math.abs(layout[i].width - width) > 0.0001)
		assertFalse(Math.abs(layout[i].height - height) > 0.0001)

		assertFalse(Math.abs(layout[i].x - x) > 0.0001)
		assertFalse(Math.abs(layout[i].y - y) > 0.0001)
	}
}