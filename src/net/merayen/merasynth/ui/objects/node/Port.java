package net.merayen.merasynth.ui.objects.node;

import java.util.ArrayList;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.Net;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.util.MouseHandler;
import net.merayen.merasynth.ui.util.Search;

public class Port extends net.merayen.merasynth.ui.objects.Group {
	/*
	 * Connectable port
	 */
	
	private MouseHandler port_drag;
	public String title = "";
	
	private Port temp_port; // Used when dragging a line from this port
	
	protected void onCreate() {
		/*port_drag = new PortDrag();
		add(port_drag);*/
		port_drag = new MouseHandler(this);
		port_drag.setHandler(new MouseHandler.IMouseHandler() {
			
			@Override
			public void onMouseUp(Point position) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMouseOver() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMouseOut() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMouseMove(Point position) {
				
			}
			
			@Override
			public void onMouseDrop(Point start_point, Point offset) {
				removeTempPort();
			}
			
			@Override
			public void onMouseDrag(Point position, Point offset) {
				moveTempPort(position);
			}
			
			@Override
			public void onMouseDown(Point position) {
				// Create a new port and notifies the net
				createTempPort();
				System.out.println("Hei");
			}
			
			@Override
			public void onGlobalMouseMove(Point global_position) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		g.setPaint(new java.awt.Color(50,200,50));
		draw.fillOval(-0.005f, -0.005f, 0.01f, 0.01f);
		
		g.setColor(new java.awt.Color(0,0,0));
		draw.setFont("SansSerif", 0.015f);
		draw.text(title, 0.01f, 0.005f);
		
		super.onDraw(g);
	}
	
	protected void onEvent(net.merayen.merasynth.ui.event.IEvent event) {
		port_drag.handle(event);
	}
	
	private Net getNetObject() {
		Search s = new Search(search.getTopmost(), 1);
		ArrayList<UIObject> m = s.searchByType(net.merayen.merasynth.ui.objects.Net.class);
		assert m.size() == 1 : "Need exactly 1 net uiobject!";
		
		return (Net)m.get(0);
		// ....
	}
	
	private void createTempPort() {
		temp_port = new Port();
		add(temp_port);
		getNetObject().addLine(this, temp_port);
		
	}
	
	private void moveTempPort(Point position) { // Relative coordinates
		temp_port.translation.x = position.x;
		temp_port.translation.y = position.y;
	}
	
	private void removeTempPort() {
		removeChild(temp_port);
		getNetObject().removeLine(this, temp_port);
		temp_port = null;
	}
}
