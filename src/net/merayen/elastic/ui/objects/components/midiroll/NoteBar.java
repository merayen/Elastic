package net.merayen.elastic.ui.objects.components.midiroll;

import net.merayen.elastic.ui.UIObject;

public class NoteBar extends UIObject {
	float width = 10;

	@Override
	protected void onDraw() {
		draw.fillRect(0, 0, width, 10);
	}
}
