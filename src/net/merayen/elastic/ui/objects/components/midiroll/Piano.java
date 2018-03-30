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
	float octave_width = 5 * 7;
	float spacing = 0.2f;

	private final int octave_count;

	private List<Tangent> tangents = new ArrayList<>();
	private final int[] WHITE_POSITIONS = new int[]{0,2,4,5,7,9,11};

	private final Handler handler;

	Piano(int octave_count, Handler handler) {
		this.octave_count = octave_count;
		this.handler = handler;
	}

	@Override
	public void onInit() {
		float y = 0;

		for(int i = 0; i < octave_count * 7; i++) {
			Tangent t = new Tangent(false, getTangentHandler(WHITE_POSITIONS[i % 7] + 12 * (i / 7)));
			t.getTranslation().x = spacing;
			t.getTranslation().y = (octave_width * octave_count) - (y + spacing + octave_width / 7);
			t.width = width - spacing * 2;
			t.height = octave_width / 7 - spacing * 2;
			tangents.add(t);
			add(t);

			y += octave_width / 7;
		}

		y = 0;
		int pos = 0;
		for(int i = 0; i < octave_count * 7; i++) {
			if(pos != 2 && pos != 6) {
				Tangent t = new Tangent(true, getTangentHandler((WHITE_POSITIONS[i % 7] + 1) + 12 * (i / 7)));
				t.getTranslation().x = spacing;
				t.getTranslation().y = (octave_width * octave_count) - (y + spacing + octave_width / 7) - octave_width / (7*3);
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
				for(Tangent t : tangents)
					t.goStandby();

				handler.onDown(tangent_no + 12*2);
			}

			@Override
			public void onUp() {
				handler.onUp(tangent_no + 12*2);
			}
		};
	}
}
