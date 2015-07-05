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

	protected void onInit() {
		assert this.parent instanceof Node;

		port = new Port();
		Node node = (Node)this.parent;
		node.addPort(port);

		parameter_slider = new ParameterSlider();
		add(parameter_slider);
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
}
