package net.merayen.merasynth.client.output;

import java.util.HashMap;

import net.merayen.merasynth.ui.objects.components.Button;
import net.merayen.merasynth.ui.objects.components.Label;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort input_port;

	private Label avg_buffer_size_label;
	private Label sample_rate_label;
	private Label channels_label;

	// Information
	private int avg_buffer_size;
	private int sample_rate;
	private int channels;

	public void onInit() {
		super.onInit();
		UI self = this;

		width = 150f;
		height = 70f;

		titlebar.title = "Output";

		// Statistics
		avg_buffer_size_label = new Label();
		avg_buffer_size_label.translation.x = 10f;
		avg_buffer_size_label.translation.y = 20f;
		add(avg_buffer_size_label);

		sample_rate_label = new Label();
		sample_rate_label.translation.x = 10f;
		sample_rate_label.translation.y = 30f;
		add(sample_rate_label);

		channels_label = new Label();
		channels_label.translation.x = 10f;
		channels_label.translation.y = 40f;
		add(channels_label);
	}

	@Override
	protected void onDraw() {
		HashMap<String,Number> stats = ((Glue)getGlueNode()).getStatistics(); // TODO Call less often
		if(stats != null) {
			avg_buffer_size_label.label = String.format("Buffer lag: %d", stats.get("current_buffer_size"));
			sample_rate_label.label = String.format("Sample rate: %d", stats.get("sample_rate"));
			channels_label.label = String.format("Channels: %d", stats.get("channels"));
		}

		super.onDraw();
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 20f;
			input_port.color = UIPort.AUDIO_PORT;
			addPort(input_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
