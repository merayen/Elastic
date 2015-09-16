package net.merayen.merasynth.ui;

import java.util.ArrayList;

import net.merayen.merasynth.ui.util.ClipStack;

public class TranslationDataStack {
	private ArrayList<TranslationData> stack = new ArrayList<TranslationData>();

	public void push(TranslationData td) {
		stack.add(td);
	}

	public void pop() {
		stack.remove(stack.size() - 1);
	}

	public TranslationData getCurrentTranslationData() {
		/*
		 * Calculates current translation data.
		 * The returned results are the absolute translations.
		 */
		TranslationData r = new TranslationData();

		for(TranslationData td : stack) {
			r.x += td.x;
			r.y += td.y;
			r.scale_x *= td.scale_x;
			r.scale_y *= td.scale_y;
			r.visible = td.visible;

			if(td.clip != null) {
				if(r.clip == null) // TODO apply scaling below?
					r.clip = new Rect(
						r.x + td.clip.x1,
						r.y + td.clip.y1,
						r.x + td.clip.x2,
						r.y + td.clip.y2
					);
				else
					r.clip.clip(
						r.x + td.clip.x1,
						r.y + td.clip.y1,
						r.x + td.clip.x2,
						r.y + td.clip.y2
					);
			}
		}

		return r;
	}
}
