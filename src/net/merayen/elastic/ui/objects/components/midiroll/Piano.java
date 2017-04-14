package net.merayen.elastic.ui.objects.components.midiroll;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.UIObject;

class Piano extends UIObject {
	public interface Handler {
		public void onDown(int tangent_no);
		public void onUp(int tangent_no);
	}

	float width = 20;
	float height = 100;
	float octave_width = 5 * 7;
	float spacing = 0.2f;

	private final int OCTAVE_COUNT = 4;

	private List<Tangent> tangents = new ArrayList<>();
	private final int[] WHITE_POSITIONS = new int[]{0,2,4,5,7,9,11};

	private final Handler handler;

	Piano(Handler handler) {
		this.handler = handler;
	}

	@Override
	protected void onInit() {
		float y = 0;

		for(int i = 0; i < OCTAVE_COUNT * 7; i++) {
			Tangent t = new Tangent(false, getTangentHandler(WHITE_POSITIONS[i % 7] + 12 * (i / 7)));
			t.translation.x = spacing;
			t.translation.y = (octave_width * OCTAVE_COUNT) - (y + spacing + octave_width / 7);
			t.width = width - spacing * 2;
			t.height = octave_width / 7 - spacing * 2;
			tangents.add(t);
			add(t);

			y += octave_width / 7;
		}

		y = 0;
		int pos = 0;
		for(int i = 0; i < OCTAVE_COUNT * 7; i++) {
			if(pos != 2 && pos != 6) {
				Tangent t = new Tangent(true, getTangentHandler((WHITE_POSITIONS[i % 7] + 1) + 12 * (i / 7)));
				t.translation.x = spacing;
				t.translation.y = (octave_width * OCTAVE_COUNT) - (y + spacing + octave_width / 7) - octave_width / (7*3);
				t.width = width / 2;
				t.height = (octave_width / 7) / 1.5f - spacing * 2;
				tangents.add(t);
				add(t);
			}

			y += octave_width / 7;
			pos++;
			pos %= 7;
		}
	}

	private Tangent.Handler getTangentHandler(int tangent_no) {
		return new Tangent.Handler() {
			@Override
			public void onDown() {
				System.out.println(tangent_no + " DOWN");
				for(Tangent t : tangents)
					t.goStandby();
			}

			@Override
			public void onUp() {
				System.out.println(tangent_no + " UP");
			}
		};
	}
}
