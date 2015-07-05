package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.node.Titlebar;
import java.util.ArrayList;

public class Node extends Group {
	
	public float width = 50f;
	public float height = 50f;
	
	protected Titlebar titlebar;
	protected ArrayList<Port> ports = new ArrayList<Port>();
	
	protected void onInit() {
		titlebar = new Titlebar();
		add(titlebar);
	}
	
	@Override
	protected void onDraw() {
		draw.setColor(80, 80, 80);
		draw.fillRect(-0.1f, -0.1f, width + 0.2f, height + 0.2f);
		
		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
		
		draw.setColor(180, 180, 180);
		draw.fillRect(0.1f, 0.1f, width - 0.2f, height - 0.2f);

		draw.setColor(100, 100, 100);
		draw.fillRect(0.2f, 0.2f, width - 0.4f, height - 0.4f);

		titlebar.width = width;
		
		super.onDraw();
	}
	
	protected void addPort(Port port) {
		add(port);
		ports.add(port);
	}
}
