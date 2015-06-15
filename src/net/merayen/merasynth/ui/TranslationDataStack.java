package net.merayen.merasynth.ui;

import java.util.ArrayList;

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
		 */
		TranslationData r = new TranslationData();
		
		for(TranslationData td : stack) {
			r.x += td.x;
			r.y += td.y;
			r.scroll_x += td.scroll_x;
			r.scroll_y += td.scroll_y;
			r.scale_x *= td.scale_x;
			r.scale_y *= td.scale_y;
			r.rot_x += td.rot_x;
			r.rot_y += td.rot_y;
			// TODO calculate width and height, containing all the elements, and do also scale them
			//r.width = ;
			//r.height = ;
		}

		return r;
	}
}
