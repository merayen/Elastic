package net.merayen.elastic.ui.objects.node.components;

import net.merayen.elastic.ui.Color;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.ParameterSlider;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class PortParameterSlider extends UIObject {
	/*
	 * Convenient class that creates a port and a slider.
	 * Shows the slider when the port isn't connected.  
	 * Only a node can directly contain this class.
	 */
	private UIPort port;
	public ParameterSlider parameter_slider;
	public boolean auto_position = true;
	public final String name;
	private double value;
	private float step = 1f;
	public Color color;

	private IHandler handler;

	public interface IHandler {
		public void onChange(double value, boolean programatic);
		public void onButton(int offset);
		public String onLabelUpdate(double value);
	}

	public PortParameterSlider(String name) {
		super();
		this.name = name;
	}

	protected void onInit() {
		if(!(getParent() instanceof UINode))
			throw new RuntimeException("Must be a direct parent of UINode");

		port = new UIPort(name, false);
		UINode node = (UINode)getParent();
		node.addPort(port);

		parameter_slider = new ParameterSlider();
		add(parameter_slider);

		parameter_slider.setHandler(new ParameterSlider.IHandler() {
			@Override
			public void onChange(double value, boolean programatic) {
				if(handler != null)
					handler.onChange(value, programatic);
			}

			@Override
			public void onButton(int offset) {
				if(handler != null)
					handler.onButton(offset);
			}

			@Override
			public String onLabelUpdate(double value) {
				if(handler != null)
					return handler.onLabelUpdate(value);

				return "";
			}
		});

		parameter_slider.setValue(value);
		parameter_slider.scale = step;
	}

	protected void onDraw() {
		if(auto_position) {
			port.translation.x = 0f;
			port.translation.y = translation.y + 7.5f;
			if(color != null)
				port.color = color;
			parameter_slider.translation.x = 10f;
			parameter_slider.translation.y = 0f;
		}

		super.onDraw();
	}

	public void setHandler(IHandler handler) {
		this.handler = handler;
	}

	public void setValue(double v) {
		value = v;
		if(parameter_slider != null)
			parameter_slider.setValue(v);
	}

	public double getValue(float v) {
		return parameter_slider.getValue();
	}

	public void setLabel(String t) {
		parameter_slider.label = t;
	}

	public void setScale(float scale) {
		this.step = scale;
		if(parameter_slider != null)
			parameter_slider.scale = scale;
	}

	public void showSlider(boolean show) {
		if(parameter_slider == null)
			return;

		if(show) {
			if(parameter_slider.getParent() == null) {
				add(parameter_slider);
			}
		} else {
			if(parameter_slider.getParent() != null) {
				remove(parameter_slider);
			}
		}
	}
}
