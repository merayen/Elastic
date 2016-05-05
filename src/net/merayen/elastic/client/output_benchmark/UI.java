package net.merayen.elastic.client.output_benchmark;

import net.merayen.elastic.ui.objects.components.Label;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort input_port;

	private Label samples_requested;
	private Label playback_speed;

	private Stats stats;

	public void onInit() {
		super.onInit();

		width = 150f;
		height = 70f;

		titlebar.title = "Benchmark";

		// Statistics
		samples_requested = new Label();
		samples_requested.translation.x = 20f;
		samples_requested.translation.y = 20f;
		add(samples_requested);

		playback_speed = new Label();
		playback_speed.translation.x = 20f;
		playback_speed.translation.y = 40f;
		add(playback_speed);
	}

	@Override
	protected void onDraw() {
		if(stats == null)
			stats = ((Glue)getGlueNode()).getStatistics();

		samples_requested.label = String.format("Samples: %dk/s", stats.avg_samples_received / 1000);
		playback_speed.label = String.format("Speed: %.1f", stats.avg_playback_speed);

		super.onDraw();
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 20f;
			input_port.color = UIPort.AUX_PORT;
			addPort(input_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
