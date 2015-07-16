package net.merayen.merasynth.ui.objects.node.components;

import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.components.ParameterSlider;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;

public class PortParameterSlider extends UIGroup {
	/*
	 * Convenient class that creates a port and a slider.
	 * Shows the slider when the port isn't connected.  
	 * Only a node can directly contain this class.
	 */
	public UIPort port;
	public ParameterSlider parameter_slider;
	public boolean auto_position = true;
	public final String name;
	private double value;
	private float step = 1f;

	private IHandler handler;

	public interface IHandler {
		public void onChange(double value);
		public void onButton(int offset);
	}

	public PortParameterSlider(String name) {
		super();
		this.name = name;
	}
	protected void onInit() {
		assert parent instanceof UINode;

		port = new UIPort(name, false);
		UINode node = (UINode)this.parent;
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
