package net.merayen.merasynth.ui.objects.node;

import java.awt.Color;
import java.awt.Graphics2D;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.util.Moveable;

public class PortDrag extends Group {
	/*
	 * This port is created when user starts to drag a line from a port.
	 */
	
	@Override
	protected void onDraw(Graphics2D g) {
		g.setColor(new Color(200,50,50));
		draw.fillOval(-0.004f, -0.004f, 0.008f, 0.008f);
		super.onDraw(g);
	}

}
