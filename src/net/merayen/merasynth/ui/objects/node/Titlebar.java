package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.util.Moveable;

public class Titlebar extends Group {
	public float width;
	public final float height = 1f;
	private boolean over = false;
	
	private Moveable moveable = null;
	
	protected void onInit() {
		moveable = new Moveable(this.parent, this);
		moveable.setHandler(new Moveable.IMoveable() {

			@Override
			public void onGrab() {}
			
			@Override
			public void onMove() {}
			
			@Override
			public void onDrop() {}
		});
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		if(over)
			g.setPaint(new java.awt.Color(100,100,200));
		else
			g.setPaint(new java.awt.Color(200,200,255));
		
		draw.fillRect(0.2f, 0.2f, width - 0.4f, height);
		
		super.onDraw(g);
	}
	
	protected void onEvent(net.merayen.merasynth.ui.event.IEvent event) {
		moveable.handle(event);
	}
}
