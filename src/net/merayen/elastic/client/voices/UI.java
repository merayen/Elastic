package net.merayen.elastic.client.voices;

import net.merayen.elastic.ui.objects.components.Label;
import net.merayen.elastic.ui.objects.components.ParameterSlider;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.node.components.PortParameterSlider;

public class UI extends UINode {
	static final int MAX_VOICES = 32;
	UIPort input_port;
	UIPort output_port;
	ParameterSlider voices_slider;
	Label current_voice_count;

	PortParameterSlider mix_slider;

	public void onInit() {
		super.onInit();
		UI self = this;

		width = 100f;
		height = 60f;

		titlebar.title = "Voices";

		voices_slider = new ParameterSlider();
		voices_slider.translation.x = 10f;
		voices_slider.translation.y = 20f;
		add(voices_slider);

		current_voice_count = new Label();
		current_voice_count.translation.x = 10f;
		current_voice_count.translation.y = 40f;
		current_voice_count.label = "0 voices";
		add(current_voice_count);

		voices_slider.setHandler(new ParameterSlider.IHandler() {

			@Override
			public void onChange(double value, boolean programatic) {
				int v = (int)Math.round(value * (MAX_VOICES - 1));

				if(!programatic) // If not set by setValue, but by users himself
					((Glue)self.getGlueNode()).changeVoiceCount(v + 1);

				voices_slider.label = String.format("%d", v + 1);
			}

			@Override
			public void onButton(int offset) {
				voices_slider.setValue(voices_slider.getValue() + offset / (MAX_VOICES - 1f));
			}
		});

		voices_slider.setValue(0);
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 25f;
			input_port.color = UIPort.MIDI_PORT;
			addPort(input_port);
		} else if(name.equals("output")) {
			output_port = new UIPort("output", false);
			output_port.translation.x = width;
			output_port.translation.y = 25f;
			output_port.color = UIPort.MIDI_PORT;
			addPort(output_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}

	public void setVoiceCount(int value) {
		// TODO
	}
}
