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

	public TranslationData getAbsolute() {
		/*
		 * Calculates current translation data.
		 * The returned results are the absolute translations.
		 */
		TranslationData r = new TranslationData();

		for(TranslationData td : stack)
			r.translate(td);

		return r;
	}
}
