package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.FlexibleDimension;
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

public class ParameterSlider extends UIObject implements EasyMotionBranch, FlexibleDimension {
	private float layoutWidth = 80f;
	private float layoutHeight = 15f;

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

	@Override
	public float getLayoutWidth() {
		return layoutWidth;
	}

	@Override
	public void setLayoutWidth(float layoutWidth) {
		this.layoutWidth = layoutWidth;
	}

	@Override
	public float getLayoutHeight() {
		return layoutHeight;
	}

	@Override
	public void setLayoutHeight(float layoutHeight) {
		this.layoutHeight = layoutHeight;
	}

	public interface Handler {
		void onChange(double value, boolean programatic);

		void onButton(int offset);

		String onLabelUpdate(double value);
	}

	public void onInit() {
		left_button = new Button();
		left_button.setAutoDimension(false);
		left_button.setLabel("-");
		left_button.setLayoutWidth(15f);
		left_button.setLayoutHeight(layoutHeight);
		add(left_button);

		left_button.setHandler(this::down);

		right_button = new Button();
		right_button.setAutoDimension(false);
		right_button.getTranslation().x = layoutWidth - 15f;
		right_button.getTranslation().y = 0f;
		right_button.setLabel("+");
		right_button.setLayoutWidth(15f);
		right_button.setLayoutHeight(layoutHeight);
		add(right_button);

		right_button.setHandler(this::up);

		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(MutablePoint start_point, MutablePoint offset) {
				setValue(drag_value + (offset.getX() / layoutWidth) * scale);
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
		draw.setStroke(1f);
		draw.setColor(50, 50, 50);
		draw.line(15f, 0.5f, layoutWidth - 15, 0.5f);
		draw.line(15f, layoutHeight - 0.5f, layoutWidth - 15, layoutHeight - 0.5f);

		draw.setColor(150, 150, 150);
		draw.fillRect(16f, 1f, layoutWidth - 32f, layoutHeight - 2);

		double v = Math.max(Math.min(value, 1), 0);
		draw.setColor(180, 180, 180);
		draw.fillRect(15f, 1f, (layoutWidth - 30f) * (float) v, layoutHeight - 2);

		if (label != null) {
			draw.setFont("Verdana", 10f);
			draw.setColor(50, 50, 50); // Shadow
			float text_width = draw.getTextWidth(label);
			draw.text(label, layoutWidth / 2 - text_width / 2 + 0.5f, layoutHeight / 2f + 10f / 2 + 0.5f);

			draw.setColor(200, 200, 200);
			draw.text(label, layoutWidth / 2 - text_width / 2, layoutHeight / 2f + 10f / 2);
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
