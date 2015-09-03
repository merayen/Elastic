package net.merayen.merasynth.ui.util;

import java.util.ArrayList;

import net.merayen.merasynth.ui.Rect;

public class ClipStack extends ArrayList<Rect> {
	/*
	 * Returns the rectangle where we can draw after all the clip rectangles has been applied.
	 * ...or null if there is no clipping active.
	 */
	public Rect getClip() {
		if(size() == 0)
			return null;

		Rect r = null;
		for(Rect m : this)
			if(r == null)
				r = new Rect(m);
			else
				r.clip(m);

		return r;
	}
}
