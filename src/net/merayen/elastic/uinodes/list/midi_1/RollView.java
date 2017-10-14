package net.merayen.elastic.uinodes.list.midi_1;

import java.util.HashMap;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.Scroll;
import net.merayen.elastic.ui.objects.components.midiroll.MidiRoll;
import net.merayen.elastic.ui.objects.node.UINode;

class RollView extends UIObject {
	float width, height;

	private UINode node;
	private MidiRoll midi_roll;
	private Scroll scroll;

	RollView(UINode node) {
		this.node = node;
	}

	@Override
	protected void onInit() {
		midi_roll = new MidiRoll(new MidiRoll.Handler() {
			@SuppressWarnings("serial")
			@Override
			public void onUp(int tangent_no) {
				node.sendData(new HashMap<String, Object>() {{
					put("tangent_up", tangent_no);
				}});
			}

			@SuppressWarnings("serial")
			@Override
			public void onDown(int tangent_no) {
				node.sendData(new HashMap<String, Object>() {{
					put("tangent_down", tangent_no);
				}});
			}
		});

		scroll = new Scroll(midi_roll);
		scroll.translation.x = 10;
		scroll.translation.y = 20;
		add(scroll);
	}

	@Override
	protected void onUpdate() {
		scroll.width = width;
		scroll.height = height;
		midi_roll.width = width;
	}
}
