package net.merayen.merasynth.ui.objects.components;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.components.Button.IHandler;
import net.merayen.merasynth.ui.util.MouseHandler;

public class ParameterSlider extends Group {
	float width = 8f;
	public float step = 1f;
	/*public float max_value = 1f;
	public float min_value = 0f;*/
	public String label;
	public boolean slider = true; // When true, value can only be between 0 and 1

	private Button left_button, right_button;
	private MouseHandler mousehandler;
	private double value = 0f;
	private double drag_value;
	private boolean inited;
	private IHandler handler;

	public interface IHandler {
		public void onChange(double value);
		public void onButton(int offset);
	}

	protected void onInit() {
		left_button = new Button();
		left_button.translation.x = 0f;
		left_button.translation.y = 0f;
		left_button.label = "-";
		left_button.width = 1.1f;
		add(left_button);

		left_button.setHandler(new Button.IHandler() {
			@Override
			public void onClick() {
				if(handler != null) {
					handler.onButton(-1);
					handler.onChange(value);
				}
			}
		});

		right_button = new Button();
		right_button.translation.x = width - 1.1f;
		right_button.translation.y = 0f;
		right_button.label = "+";
		right_button.width = 1.1f;
		add(right_button);

		right_button.setHandler(new Button.IHandler() {
			@Override
			public void onClick() {
				if(handler != null) {
					handler.onButton(1);
					handler.onChange(value);
				}
			}
		});

		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				setValue(drag_value + (offset.x / width) * step);
				if(handler != null)
					handler.onChange(value);
			}

			@Override
			public void onMouseDown(Point position) {
				drag_value = value;
			}
		});
	}

	protected void onDraw() {
		draw.setColor(50, 50, 50);
		draw.fillRect(1, 0, width - 2f, 1.5f);

		draw.setColor(150, 150, 150);
		draw.fillRect(1.1f, 0.1f, width - 2.1f, 1.3f);

		double v = Math.max(Math.min(value, 1),  0);
		draw.setColor(180, 180, 180);
		draw.fillRect(1.1f, 0.1f, (width - 2.1f) * (float)v, 1.3f);

		if(label != null) {
			draw.setFont("Verdana", 1f);
			draw.setColor(50, 50, 50); // Shadow
			float text_width = draw.getTextWidth(label);
			draw.text(label, width/2 - text_width/2 + 0.05f, 1.05f);

			draw.setColor(200, 200, 200);
			draw.text(label, width/2 - text_width/2, 1f);
		}

		super.onDraw();
	}

	protected void onEvent(IEvent event) {
		mousehandler.handle(event);
	}

	public void setValue(double v) {
		if(slider)
			value = Math.max(Math.min(v, 1), 0);
		else
			value = v;
		if(!inited) {
			handler.onChange(value);
			inited = true;
		}
	}

	public double getValue() {
		return value;
	}

	public void setHandler(IHandler handler) {
		this.handler = handler;
	}
}
