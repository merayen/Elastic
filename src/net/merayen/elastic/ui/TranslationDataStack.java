package net.merayen.elastic.ui;

import java.util.ArrayList;

public class TranslationDataStack {
	private ArrayList<TranslationData> stack = new ArrayList<TranslationData>();

	public void push(TranslationData td) {
		stack.add(td);
	}

	public void pop() {
		stack.remove(stack.size() - 1);
	}

	/**
	 * Calculates current translation data.
	 * The returned results are the absolute translations.
	 */
	public TranslationData getAbsolute() {
		TranslationData r = new TranslationData();

		for(TranslationData td : stack)
			r.translate(td);

		return r;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		for(TranslationData x : stack)
			s.append(String.format("\t%s\n", x));

		return String.format(
			"TranslationDataStack([\n%s])",
			s
		);
	}
}
