package net.merayen.merasynth.ui.objects.node;

import java.awt.Graphics2D;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.util.Moveable;

public class PortDrag extends Group { // ...delete?
	/*
	 * Lives over the port, trigging when the user starts to drag.
	 * We then draw a line and a new object where the line goes between.
	 */

	private Moveable moveable;
	private boolean moving = false;
	
	protected void onCreate() {
		moveable = new Moveable(this, this);
		moveable.setHandler(new Moveable.IMoveable() {
			
			@Override
			public void onGrab() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMove() {
				// TODO Auto-generated method stub
				moving = true;
			}
			
			@Override
			public void onDrop() {
				// TODO Auto-generated method stub
				moving = false;
			}
		});
	}
	
	@Override
	protected void onDraw(Graphics2D g) {
		if(moving) {
			g.setPaint(new java.awt.Color(150,0,150));
			draw.line(0, 0, 0.1f, 0.1f);
		}
		
		super.onDraw(g);
	}

}
