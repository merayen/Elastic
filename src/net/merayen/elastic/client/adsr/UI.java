package net.merayen.elastic.client.adsr;

import net.merayen.elastic.client.adsr.ui.ADSRGraph;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.objects.components.CircularSlider;
import net.merayen.elastic.ui.objects.components.Label;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort input_port, output_port, fac_port;
	private UIClip clip;
	private ADSRGraph adsrgraph;

	private CircularSlider attack_slider, decay_slider, sustain_slider, release_slider;

	public void onInit() {
		super.onInit();

		width = 240f;
		height = 240f;

		titlebar.title = "Graph";

		clip = new UIClip();
		clip.translation.x = 20f;
		clip.translation.y = 20f;
		clip.width = 200f;
		clip.height = 150f;
		add(clip);

		adsrgraph = new ADSRGraph();
		adsrgraph.width = 200;
		adsrgraph.height = 150;
		clip.add(adsrgraph);

		Label label = new Label();
		label.label = "Attack";
		label.translation.x = 35;
		label.translation.y = 175;
		label.align = Label.Align.CENTER;
		add(label);

		attack_slider = new CircularSlider();
		attack_slider.translation.x = 20;
		attack_slider.translation.y = 190;
		add(attack_slider);

		label = new Label();
		label.label = "Decay";
		label.translation.x = 75;
		label.translation.y = 175;
		label.align = Label.Align.CENTER;
		add(label);

		decay_slider = new CircularSlider();
		decay_slider.translation.x = 60;
		decay_slider.translation.y = 190;
		add(decay_slider);

		label = new Label();
		label.label = "Sustain";
		label.translation.x = 115;
		label.translation.y = 175;
		label.align = Label.Align.CENTER;
		add(label);

		sustain_slider = new CircularSlider();
		sustain_slider.translation.x = 100;
		sustain_slider.translation.y = 190;
		add(sustain_slider);

		label = new Label();
		label.label = "Release";
		label.translation.x = 155;
		label.translation.y = 175;
		label.align = Label.Align.CENTER;
		add(label);

		release_slider = new CircularSlider();
		release_slider.translation.x = 140;
		release_slider.translation.y = 190;
		add(release_slider);

		attack_slider.setValue(0.1f);
		decay_slider.setValue(0.1f);
		sustain_slider.setValue(1f);
		release_slider.setValue(0.1f);
	}

	@Override
	protected void onDraw() {
		adsrgraph.attack_time = attack_slider.getValue() * 10;
		adsrgraph.decay_time = decay_slider.getValue() * 10;
		adsrgraph.sustain_value = 1 - sustain_slider.getValue();
		adsrgraph.release_time = release_slider.getValue() * 10;
		super.onDraw();
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 20f;
			input_port.color = UIPort.MIDI_PORT;
			addPort(input_port);
		}

		if(name.equals("output")) {
			output_port = new UIPort("output", true);
			output_port.translation.x = width;
			output_port.translation.y = 20f;
			output_port.color = UIPort.MIDI_PORT;
			addPort(output_port);
		}

		if(name.equals("fac")) {
			fac_port = new UIPort("fac", true);
			fac_port.translation.x = width;
			fac_port.translation.y = height - 20;
			fac_port.color = UIPort.AUX_PORT;
			addPort(fac_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
