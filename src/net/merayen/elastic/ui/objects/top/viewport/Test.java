package net.merayen.elastic.ui.objects.top.viewport;

import java.util.List;

public class Test {
	private static void nope() {
		throw new RuntimeException("Nope");
	}

	private static void validate(List<Layout.CalculatedPosition> layout, int i, float x, float y, float width, float height, Object obj) {
		if(layout.get(i).obj != obj)
			nope();

		if(Math.abs(layout.get(i).width - width) > 0.0001 || Math.abs(layout.get(i).height - height) > 0.0001) // c
			nope();

		if(Math.abs(layout.get(i).x - x) > 0.0001 || Math.abs(layout.get(i).y - y) > 0.0001) // c
			nope();
	}

	public static void test() {
		String a = "a", b = "b", c = "c", d = "d", e = "e";

		Layout l = new Layout(a);

		List<Layout.CalculatedPosition> layout = l.getLayout();

		if(layout.size() != 1)
			nope();

		validate(layout, 0, 0, 0, 1, 1, a);

		l.splitVertical(a, b);

		layout = l.getLayout();

		if(layout.size() != 2)
			nope();

		validate(layout, 0, 0, 0, 1, 1, a);
		validate(layout, 1, 1, 0, 0, 1, b);

		l.resizeWidth(b, 0.5f); // This operation should be ignored, as resizing left-most object is not allowed

		layout = l.getLayout();

		validate(layout, 0, 0, 0, 1, 1, a);
		validate(layout, 1, 1, 0, 0, 1, b);

		l.splitVertical(b, c);
		l.resizeWidth(a, 0.25f);
		l.resizeWidth(b, 0.10f);

		layout = l.getLayout();

		if(layout.size() != 3)
			nope();

		validate(layout, 0, 0.00f, 0, 0.25f, 1, a);
		validate(layout, 1, 0.25f, 0, 0.10f, 1, b);
		validate(layout, 2, 0.35f, 0, 0.65f, 1, c);

		// Now create a vertical out of "b", with "d" below, inside the horizontal ruler
		l.splitHorizontal(b, d);
		l.resizeHeight(b, 0.75f);

		layout = l.getLayout();

		validate(layout, 0, 0.00f, 0, 0.25f, 1, a);
		validate(layout, 1, 0.25f, 0, 0.10f, 0.75f, b);
		validate(layout, 2, 0.35f, 0, 0.65f, 1, c);
		validate(layout, 3, 0.25f, 0.75f, 0.10f, 0.25f, d);

		// Now try to resize "d"'s width, which should resize its whole parent ruler, as it is a vertical ruler
		l.resizeWidth(d, 0.01f);

		layout = l.getLayout();

		validate(layout, 0, 0.00f, 0, 0.25f, 1, a); // Should not have been changed
		validate(layout, 1, 0.25f, 0, 0.01f, 0.75f, b);
		validate(layout, 2, 0.26f, 0, 0.74f, 1, c);
		validate(layout, 3, 0.25f, 0.75f, 0.01f, 0.25f, d);

		// Now try to resize a to take all the space, which is not possible, as a can only resize itself over its right item, and no more
		l.resizeWidth(a, 1);
		layout = l.getLayout();

		validate(layout, 0, 0.00f, 0, 0.26f, 1, a); // Should only have taken 0.01f more width due do right item (vertical ruler containing *b* and *d*) has a width of only 0.01f
		validate(layout, 1, 0.26f, 0, 0f, 0.75f, b); // Should now have zero with, as *a* has span itself over
		validate(layout, 2, 0.26f, 0, 0.74f, 1, c); // Should not have changed
		validate(layout, 3, 0.26f, 0.75f, 0f, 0.25f, d); // Should now have zero with, as *a* has span itself over

		// Now resize *a* to 0 width. *b* should take over the whole width
		l.resizeWidth(a, 0);
		layout = l.getLayout();

		validate(layout, 0, 0.00f, 0.00f, 0.00f, 1.00f, a); // Should only have taken 0.01f more width due do right item (vertical ruler containing *b* and *d*) has a width of only 0.01f
		validate(layout, 1, 0.00f, 0.00f, 0.26f, 0.75f, b); // Should now have zero with, as *a* has span itself over
		validate(layout, 2, 0.26f, 0.00f, 0.74f, 1.00f, c); // Should not have changed
		validate(layout, 3, 0.00f, 0.75f, 0.26f, 0.25f, d); // Should now have zero with, as *a* has span itself over
	}
}
