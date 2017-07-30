package net.merayen.elastic.uinodes.list.adsr_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.objects.components.CircularSlider;
import net.merayen.elastic.ui.objects.components.Label;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIClip clip;
	private ADSRGraph adsrgraph;

	private CircularSlider attack_slider, decay_slider, sustain_slider, release_slider;

	public UI() {
		titlebar.title = "ADSR";

		clip = new UIClip();
		clip.translation.x = 20f;
		clip.translation.y = 20f;
		clip.width = 200f;
		clip.height = 150f;
		add(clip);

		adsrgraph = new ADSRGraph();
		adsrgraph.width = 160;
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
		attack_slider.setHandler((v) -> {
			sendParameter("attack", (float)Math.pow(v, 2) * 10);
			adsrgraph.attack_time = (float)Math.pow(attack_slider.getValue(), 2) * 10;
		});
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
		decay_slider.setHandler((v) -> {
			sendParameter("decay", (float)Math.pow(v, 2) * 10);
			adsrgraph.decay_time = (float)Math.pow(decay_slider.getValue(), 2) * 10;
		});
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
		sustain_slider.setHandler((v) -> {
			sendParameter("sustain", v);
			adsrgraph.sustain_value = sustain_slider.getValue();
		});
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
		release_slider.setHandler((v) -> {
			sendParameter("release", (float)Math.pow(v, 2) * 10);
			adsrgraph.release_time = (float)Math.pow(release_slider.getValue(), 2) * 10;
		});
		add(release_slider);

		attack_slider.setValue(0.1f);
		decay_slider.setValue(0.1f);
		sustain_slider.setValue(1f);
		release_slider.setValue(0.1f);
	}

	public void onInit() {
		super.onInit();
		width = 200f;
		height = 240f;
	}

	@Override
	public void onCreatePort(UIPort port) {
		if(port.name.equals("input")) {
			port.translation.x = 0f;
			port.translation.y = 20f;
			port.color = UIPort.MIDI_PORT;
		}

		if(port.name.equals("output")) {
			port.translation.x = 200;
			port.translation.y = 20f;
			port.color = UIPort.MIDI_PORT;
		}

		/*if(port.name.equals("fac")) {
			port.translation.x = width;
			port.translation.y = height - 20;
			port.color = UIPort.AUX_PORT;
		}*/
	}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {
		if(message.key.equals("attack")) {
			attack_slider.setValue((float)Math.pow(((Number)message.value).floatValue() / 10, 1/2f));
			adsrgraph.attack_time = ((Number)message.value).floatValue();
		} else if(message.key.equals("decay")) {
			decay_slider.setValue((float)Math.pow(((Number)message.value).floatValue() / 10, 1/2f));
			adsrgraph.decay_time = ((Number)message.value).floatValue();
		} else if(message.key.equals("sustain")) {
			sustain_slider.setValue(((Number)message.value).floatValue());
			adsrgraph.sustain_value = ((Number)message.value).floatValue();
		} else if(message.key.equals("release")) {
			release_slider.setValue((float)Math.pow(((Number)message.value).floatValue() / 10, 1/2f));
			adsrgraph.release_time = ((Number)message.value).floatValue();
		}
	}

	@Override
	protected void onData(NodeDataMessage message) {}

	@Override
	protected void onParameter(String key, Object value) {}
}
