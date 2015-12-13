package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.ui.Color;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;
import net.merayen.merasynth.ui.objects.node.components.PortParameterSlider;

public class UI extends UINode {
	private PortParameterSlider frequency_slider;
	private PortParameterSlider amp_slider;
	private UIPort output_port;
	private float frequency = 440;
	private float amplitude = 0.1f;

	public static String getNodeName() {
		return "Signalgenerator";
	}

	public static String getNodeDescription() {
		return "Generates audio waves.";
	}

	public void onInit() {
		super.onInit();

		width = 100f;
		height = 80f;

		titlebar.title = "Signalgenerator";

		createFrequencyPort();
		createAmplitudePort();
	}

	public void onDraw() {
		// TODO Only do this when connections are actually changed
		frequency_slider.showSlider(!this.getTopObject().getUINet().isConnected(this.getPort("frequency")));
		amp_slider.showSlider(!this.getTopObject().getUINet().isConnected(this.getPort("amplitude")));
		super.onDraw();
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
		frequency_slider.setValue(frequency / 8000.0);
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("frequency")) {
			// We create this port in onInit() as it is always available anyway
		}

		if(name.equals("amplitude")) {
			// We create this port in onInit() as it is always available anyway
		}

		if(name.equals("output")) {
			output_port = new UIPort("output", true);
			output_port.translation.x = width;
			output_port.translation.y = 20f;
			output_port.color = UIPort.AUDIO_PORT;
			addPort(output_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}

	private void createFrequencyPort() {
		UI self = this;
		frequency_slider = new PortParameterSlider("frequency");
		frequency_slider.translation.y = 20f;
		frequency_slider.color = UIPort.AUX_PORT;
		add(frequency_slider);

		frequency_slider.setHandler(new PortParameterSlider.IHandler() {
			@Override
			public void onChange(double value, boolean programatic) {
				frequency = Math.round(value * 8000.0 * 10) / 10 + 0.1f;
				frequency_slider.setLabel(String.format("%.2f Hz", frequency));

				if(!programatic) // If not set by setValue, but by users himself
					((Glue)self.getGlueNode()).changeFrequency(frequency);
			}

			@Override
			public void onButton(int offset) {
				frequency += offset*1;
				frequency_slider.setValue(frequency / 8000.0);
			}
		});

		frequency_slider.setValue(0);
		frequency_slider.setScale(0.1f);
	}

	private void createAmplitudePort() {
		UI self = this;
		amp_slider = new PortParameterSlider("amplitude");
		amp_slider.translation.y = 50f;
		amp_slider.color = UIPort.AUDIO_PORT;
		add(amp_slider);

		amp_slider.setHandler(new PortParameterSlider.IHandler() {
			@Override
			public void onChange(double value, boolean programatic) {
				amplitude = (float)(value * 10f);
				amp_slider.setLabel(String.format("%.2f", amplitude));

				if(!programatic) // If not set by setValue, but by users himself
					((Glue)self.getGlueNode()).changeAmplitude(amplitude);
			}

			@Override
			public void onButton(int offset) {
				amplitude += offset * 0.1f;
				amp_slider.setValue(amplitude / 10f);
			}
		});

		amp_slider.setValue(0.01f);
		amp_slider.setScale(1f);
	}
}
