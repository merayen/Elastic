package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

public class Button extends UIObject {
	public interface IHandler {
		public void onClick();
	}

	public String label;

	public float width = 50f;
	public float height = 15f;
	public boolean auto_dimension = true;
	public float font_size = 10;

	private IHandler handler; 
	private MouseHandler mousehandler;
	private boolean mouse_down;
	private boolean mouse_over;

	protected void onInit() {
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {

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
			public void onMouseDown(Point position) {
				mouse_down = true;
			}

			@Override
			public void onGlobalMouseUp(Point global_position) {
				mouse_down = false;
			}
		});
	}

	protected void onDraw() {
		draw.setFont("Geneva", font_size);

		float text_width = draw.getTextWidth(label);
		if(auto_dimension) {
			width = text_width + 10;
			height = font_size * 1.5f;
		}

		draw.setColor(50, 50, 50);
		draw.fillRect(0, 0, width, height);

		if(mouse_down && mouse_over)
			draw.setColor(80, 80, 80);
		else
			draw.setColor(120, 120, 120);
		draw.fillRect(1f, 1f, width - 2f, height - 2f);

		draw.setColor(200, 200, 200);
		draw.text(label, (float)(width/2 - text_width/2), font_size);

		super.onDraw();
	}

	protected void onEvent(UIEvent event) {
		mousehandler.handle(event);
	}

	public void setHandler(IHandler handler) {
		this.handler = handler;
	}
}
