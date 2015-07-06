package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.components.parameterslider.ParameterSlider;

public class PortParameterSlider extends Group {
	/*
	 * Convenient class that creates a port and a slider.
	 * Shows the slider when the port isn't connected.  
	 * Only a node can directly contain this class.
	 */
	public Port port;
	public ParameterSlider parameter_slider;
	public boolean auto_position = true;
	private double value;
	private float step = 1f;
	private float button_step = 0.01f;

	private IHandler handler;

	public interface IHandler {
		public void onChange(double value);
		public void onButton(int offset);
	}

	protected void onInit() {
		assert this.parent instanceof Node;

		port = new Port();
		Node node = (Node)this.parent;
		node.addPort(port);

		parameter_slider = new ParameterSlider();
		add(parameter_slider);

		parameter_slider.setHandler(new ParameterSlider.IHandler() {
			@Override
			public void onChange(double value) {
				if(handler != null)
					handler.onChange(value);
			}

			@Override
			public void onButton(int offset) {
				if(handler != null)
					handler.onButton(offset);
			}
		});

		parameter_slider.setValue(value);
		parameter_slider.step = step;
	}

	protected void onDraw() {
		if(auto_position) {
			port.translation.x = 0f;
			port.translation.y = translation.y + 0.75f;
			parameter_slider.translation.x = 1f;
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

	public void setStep(float step) {
		this.step = step;
		if(parameter_slider != null)
			parameter_slider.step = step;
	}
}
