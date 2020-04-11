package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.KeyboardEvent;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.objects.components.buttons.Button;
import net.merayen.elastic.ui.objects.top.easymotion.Branch;
import net.merayen.elastic.ui.objects.top.easymotion.EasyMotionBranch;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.MutablePoint;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ParameterSlider extends UIObject implements EasyMotionBranch {
	float width = 80f;
	public float scale = 1f;
	public String label;

	private Button left_button, right_button;
	private MouseHandler mousehandler;
	private double value = 0f;
	private double drag_value;
	private Handler handler;

	@NotNull
	@Override
	public Branch getEasyMotionBranch() {
		return new Branch(this, this) {
			{  // This looks like shit in Java
				getControls().put(new HashSet<>() {{
					add(KeyboardEvent.Keys.PLUS);
				}}, new Control((something) -> {
					up();
					return null;
				}));

				getControls().put(new HashSet<>() {{
					add(KeyboardEvent.Keys.MINUS);
				}}, new Control((something) -> {
					down();
					return null;
				}));

				getControls().put(new HashSet<>() {{
					add(KeyboardEvent.Keys.Q);
				}}, new Control((something) -> Control.Companion.getSTEP_BACK()));
			}
		};
	}

	public interface Handler {
		void onChange(double value, boolean programatic);

		void onButton(int offset);

		String onLabelUpdate(double value);
	}

	public void onInit() {
		left_button = new Button();
		left_button.setLabel("-");
		left_button.setLayoutWidth(11f);
		add(left_button);

		left_button.setHandler(this::down);

		right_button = new Button();
		right_button.getTranslation().x = width - 11f;
		right_button.getTranslation().y = 0f;
		right_button.setLabel("+");
		right_button.setLayoutWidth(11f);
		add(right_button);

		right_button.setHandler(this::up);

		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(MutablePoint start_point, MutablePoint offset) {
				setValue(drag_value + (offset.getX() / width) * scale);
				if (handler != null) {
					handler.onChange(value, false);
					label = handler.onLabelUpdate(value);
				}
			}

			@Override
			public void onMouseDown(MutablePoint position) {
				drag_value = value;
			}
		});
	}

	@Override
	public void onDraw(Draw draw) {
		draw.setColor(50, 50, 50);
		draw.fillRect(10, 0, width - 20f, 15f);

		draw.setColor(150, 150, 150);
		draw.fillRect(11f, 1f, width - 21f, 13f);

		double v = Math.max(Math.min(value, 1), 0);
		draw.setColor(180, 180, 180);
		draw.fillRect(11f, 1f, (width - 21f) * (float) v, 13f);

		if (label != null) {
			draw.setFont("Verdana", 10f);
			draw.setColor(50, 50, 50); // Shadow
			float text_width = draw.getTextWidth(label);
			draw.text(label, width / 2 - text_width / 2 + 0.5f, 10.5f);

			draw.setColor(200, 200, 200);
			draw.text(label, width / 2 - text_width / 2, 10f);
		}

		super.onDraw(draw);
	}

	public void onEvent(UIEvent event) {
		mousehandler.handle(event);
	}

	public void setValue(double v, boolean no_event) {
		value = Math.max(Math.min(v, 1), 0);
		if (handler != null)
			label = handler.onLabelUpdate(value);
	}

	public void setValue(double v) {
		setValue(v, false);
	}

	public double getValue() {
		return value;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	private void up() {
		if (handler != null) {
			handler.onButton(1);
			handler.onChange(value, false);
			label = handler.onLabelUpdate(value);
		}
	}

	private void down() {
		if (handler != null) {
			handler.onButton(-1);
			handler.onChange(value, false);
			label = handler.onLabelUpdate(value);
		}
	}
}
