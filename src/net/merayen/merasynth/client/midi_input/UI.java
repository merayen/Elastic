package net.merayen.merasynth.client.midi_input;

import net.merayen.merasynth.ui.objects.components.Label;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort port;

	private Label midi_device;
	private Label sample_rate_label;
	private Label channels_label;

	public void onInit() {
		super.onInit();
		UI self = this;

		width = 10f;
		height = 5f;

		titlebar.title = "MIDI Input";

		// Statistics
		midi_device = new Label();
		midi_device.translation.x = 1f;
		midi_device.translation.y = 2f;
		add(midi_device);

		sample_rate_label = new Label();
		sample_rate_label.translation.x = 1f;
		sample_rate_label.translation.y = 3f;
		add(sample_rate_label);

		channels_label = new Label();
		channels_label.translation.x = 1f;
		channels_label.translation.y = 4f;
		add(channels_label);
	}

	@Override
	protected void onDraw() {
		midi_device.label = "Device: ";
		super.onDraw();
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("output")) {
			port = new UIPort("output", false);
			port.translation.x = width;
			port.translation.y = 2f;
			addPort(port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
