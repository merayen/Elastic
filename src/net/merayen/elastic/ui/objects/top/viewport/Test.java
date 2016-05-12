package net.merayen.elastic.ui.objects.top.viewport;

import java.util.List;

public class Test {
	private static void nope() {
		throw new RuntimeException("Nope");
	}

	public static void test() {
		String a = "a", b = "b", c = "c", d = "d", e = "e";

		Layout l = new Layout(a);

		List<Layout.CalculatedPosition> layout = l.getLayout();

		if(layout.size() != 1)
			nope();

		if(layout.get(0).height != 1f || layout.get(0).width != 1f || layout.get(0).obj != a)
			nope();

		l.splitHorizontal(a, b);

		layout = l.getLayout();

		if(layout.size() != 2)
			nope();

		if(layout.get(0).height != 1f || layout.get(0).width != 1f || layout.get(0).obj != a) // a
			nope();

		if(layout.get(1).height != 1f || layout.get(1).width != 0f || layout.get(1).obj != b) // b
			nope();

		l.resizeWidth(b, 0.5f); // This operation is ignored, as resizing left-most object is not allowed

		layout = l.getLayout();

		if(layout.get(0).height != 1f || layout.get(0).width != 1f || layout.get(0).obj != a) // a
			nope();

		if(layout.get(1).height != 1f || layout.get(1).width != 0f || layout.get(1).obj != b) // b
			nope();

		l.splitHorizontal(b, c);
		l.resizeWidth(a, 0.25f);
		l.resizeWidth(b, 0.10f);

		layout = l.getLayout();

		if(layout.size() != 3)
			nope();

		if(layout.get(0).height != 1f || layout.get(0).width != 0.25f || layout.get(0).obj != a) // a
			nope();

		if(layout.get(1).height != 1f || Math.abs(layout.get(1).width - 0.10f) > 0.000001 || layout.get(1).obj != b) // b
			nope();

		if(layout.get(2).height != 1f || layout.get(2).width != 0.65f || layout.get(2).obj != c) // c
			nope();

		// Now create a vertical out of "b"
		l.splitVertical(b, d);
		//l.resizeHeight();
		layout = l.getLayout();
		
	}
}
