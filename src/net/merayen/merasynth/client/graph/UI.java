package net.merayen.merasynth.client.graph;

import net.merayen.merasynth.ui.objects.UIClip;
import net.merayen.merasynth.ui.objects.components.graph.Graph;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort output_port;
	private UIClip clip;
	private Graph graph;

	public void onInit() {
		super.onInit();

		width = 24f;
		height = 24f;

		titlebar.title = "Graph";

		clip = new UIClip();
		clip.translation.x = 2f;
		clip.translation.y = 2f;
		clip.width = 20f;
		clip.height = 20f;
		add(clip);

		graph = new Graph();
		graph.translation.x = 0f;
		graph.translation.y = 0f;
		graph.width = 20f;
		graph.height = 20f;
		clip.add(graph);
	}

	@Override
	protected void onDraw() {
		super.onDraw();
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("output")) {
			output_port = new UIPort("input", false);
			output_port.translation.x = 0f;
			output_port.translation.y = 2f;
			addPort(output_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
