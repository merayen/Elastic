package net.merayen.merasynth.client.graph;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import net.merayen.merasynth.ui.objects.components.Label;
import net.merayen.merasynth.ui.objects.components.graph.Graph;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;

public class UI extends UINode {
	private UIPort output_port;
	private Graph graph;

	// Information
	private int u = 0;

	public void onInit() {
		super.onInit();
		UI self = this;

		width = 24f;
		height = 24f;

		titlebar.title = "Graph";

		graph = new Graph();
		add(graph);
		graph.translation.x = 2f;
		graph.translation.y = 2f;
		graph.width = 20f;
		graph.height = 20f;
	}

	@Override
	protected void onDraw() {
		super.onDraw();
		/*u++;

		draw.setColor(50, 50, 100);
		draw.fillRect(0.5f, 1.5f, width - 1f, height - 2.5f);

		Path2D.Float f = new Path2D.Float();

		Point2D m = this.getAbsolutePixelPoint(2, 2);
		f.moveTo(m.getX(), m.getY());

		float g = (100 % 1000) / 1000f; 

		for(float i = 2; i < 18; i += g + 0.01) {
			Point2D p1 = this.getAbsolutePixelPoint(i, (float)(10 + Math.sin(i*5)*5));
			Point2D p2 = this.getAbsolutePixelPoint(i + (float)Math.sin(i/10f), (float)(10 + Math.sin(i*5)*5));
			Point2D p3 = this.getAbsolutePixelPoint(i, (float)(10 + Math.sin(i*5)*5));
			f.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
		}

		draw.setColor(150, 150, 255);
		draw.g2d.draw(f);*/
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
