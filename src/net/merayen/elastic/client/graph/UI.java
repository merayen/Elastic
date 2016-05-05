package net.merayen.elastic.client.graph;

import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.objects.components.graph.Graph;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort output_port;
	private UIClip clip;
	private Graph graph;

	public void onInit() {
		super.onInit();

		width = 240f;
		height = 240f;

		titlebar.title = "Graph";

		clip = new UIClip();
		clip.translation.x = 20f;
		clip.translation.y = 20f;
		clip.width = 200f;
		clip.height = 200f;
		add(clip);

		graph = new Graph();
		graph.translation.x = 0f;
		graph.translation.y = 0f;
		graph.width = 200f;
		graph.height = 200f;
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
			output_port.translation.y = 20f;
			addPort(output_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
