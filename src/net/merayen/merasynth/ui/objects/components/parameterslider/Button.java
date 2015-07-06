package net.merayen.merasynth.ui.objects.components.parameterslider;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Button extends UIObject {
	
	public interface IHandler {
		public void onClick();
	}

	public String label;

	public float width = 5f;
	public float height = 1.5f;

	private IHandler handler; 
	private MouseHandler mousehandler;
	private boolean mouse_down;
	private boolean mouse_over;

	protected void onInit() {
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.IMouseHandler() {
			
			@Override
			public void onMouseUp(Point position) {
				if(mouse_down && handler != null)
					handler.onClick();

				mouse_down = false;
			}
			
			@Override
			public void onMouseOver() {
				mouse_over = true;
			}
			
			@Override
			public void onMouseOut() {
				mouse_over = false;
			}
			
			@Override
			public void onMouseMove(Point position) {}
			
			@Override
			public void onMouseDrop(Point start_point, Point offset) {}
			
			@Override
			public void onMouseDrag(Point start_point, Point offset) {}
			
			@Override
			public void onMouseDown(Point position) {
				mouse_down = true;
			}
			
			@Override
			public void onGlobalMouseMove(Point global_position) {}
			
			@Override
			public void onGlobalMouseUp(Point global_position) {
				mouse_down = false;
			}
		});
	}

	protected void onDraw() {
		draw.setColor(50, 50, 50);
		draw.fillRect(0, 0, width, height);

		if(mouse_down && mouse_over)
			draw.setColor(80, 80, 80);
		else
			draw.setColor(120, 120, 120);
		draw.fillRect(0.1f, 0.1f, width - 0.2f, height - 0.2f);

		draw.setColor(200, 200, 200);
		float text_width = draw.getTextWidth(label);
		draw.text(label, (float)(width/2 - text_width/2), 1f);

		super.onDraw();
	}

	protected void onEvent(IEvent event) {
		mousehandler.handle(event);
	}

	public void setHandler(IHandler handler) {
		this.handler = handler;
	}
}
