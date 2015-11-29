package net.merayen.merasynth.ui.objects.components;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.components.Button.IHandler;
import net.merayen.merasynth.ui.util.MouseHandler;

public class ParameterSlider extends UIGroup {
	float width = 80f;
	public float scale = 1f;
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
		public void onChange(double value, boolean programatic);
		public void onButton(int offset);
	}

	protected void onInit() {
		left_button = new Button();
		left_button.label = "-";
		left_button.width = 11f;
		add(left_button);

		left_button.setHandler(new Button.IHandler() {
			@Override
			public void onClick() {
				if(handler != null) {
					handler.onButton(-1);
					handler.onChange(value, false);
				}
			}
		});

		right_button = new Button();
		right_button.translation.x = width - 11f;
		right_button.translation.y = 0f;
		right_button.label = "+";
		right_button.width = 11f;
		add(right_button);

		right_button.setHandler(new Button.IHandler() {
			@Override
			public void onClick() {
				if(handler != null) {
					handler.onButton(1);
					handler.onChange(value, false);
				}
			}
		});

		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				setValue(drag_value + (offset.x / width) * scale);
				if(handler != null)
					handler.onChange(value, false);
			}

			@Override
			public void onMouseDown(Point position) {
				drag_value = value;
			}
		});
	}

	protected void onDraw() {
		draw.setColor(50, 50, 50);
		draw.fillRect(10, 0, width - 20f, 15f);

		draw.setColor(150, 150, 150);
		draw.fillRect(11f, 1f, width - 21f, 13f);

		double v = Math.max(Math.min(value, 1),  0);
		draw.setColor(180, 180, 180);
		draw.fillRect(11f, 1f, (width - 21f) * (float)v, 13f);

		if(label != null) {
			draw.setFont("Verdana", 10f);
			draw.setColor(50, 50, 50); // Shadow
			float text_width = draw.getTextWidth(label);
			draw.text(label, width/2 - text_width/2 + 0.5f, 10.5f);

			draw.setColor(200, 200, 200);
			draw.text(label, width/2 - text_width/2, 10f);
		}

		super.onDraw();
	}

	protected void onEvent(IEvent event) {
		mousehandler.handle(event);
	}

	public void setValue(double v, boolean no_event) {
		if(slider)
			value = Math.max(Math.min(v, 1), 0);
		else
			value = v;
		if(!inited) {
			handler.onChange(value, true);
			inited = true;
		}
	}

	public void setValue(double v) {
		setValue(v, false);
	}

	public double getValue() {
		return value;
	}

	public void setHandler(IHandler handler) {
		this.handler = handler;
	}
}
