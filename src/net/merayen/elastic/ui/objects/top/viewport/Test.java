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

		l.splitHorizontal(a, b);

		layout = l.getLayout();

		if(layout.size() != 2)
			nope();

		validate(layout, 0, 0, 0, 1, 1, a);
		validate(layout, 1, 1, 0, 0, 1, b);

		l.resize(b, 0.5f); // This operation should be ignored, as resizing left-most object is not allowed

		layout = l.getLayout();

		validate(layout, 0, 0, 0, 1, 1, a);
		validate(layout, 1, 1, 0, 0, 1, b);

		l.splitHorizontal(b, c);
		l.resize(a, 0.25f);
		l.resize(b, 0.10f);

		layout = l.getLayout();

		if(layout.size() != 3)
			nope();

		validate(layout, 0, 0.00f, 0, 0.25f, 1, a);
		validate(layout, 1, 0.25f, 0, 0.10f, 1, b);
		validate(layout, 2, 0.35f, 0, 0.65f, 1, c);

		// Now create a vertical out of "b", with "d" delow
		l.splitVertical(b, d);
		l.resize(b, 0.75f);

		layout = l.getLayout();

		validate(layout, 0, 0.00f, 0, 0.25f, 1, a);
		validate(layout, 1, 0.25f, 0, 0.10f, 0.75f, b);
		validate(layout, 2, 0.25f, 0.75f, 0.10f, 0.25f, d);
		validate(layout, 3, 0.35f, 0, 0.65f, 1, c);

		// Now try to resize "d"'s width, which should resize its parent ruler
	}
}
