package net.merayen.elastic.uinodes.list.signalgenerator_1;

import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.ParameterSlider;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.node.components.PortParameterSlider;

public class UI extends UINode {
	private WaveSelect wave_select;
	private PortParameterSlider frequency_slider;
	private PortParameterSlider amp_slider;
	private ParameterSlider offset_slider;
	private float frequency = 440;
	private float amplitude = 0.1f;
	private float offset = 0;

	public UI() {
		super();
		width = 100f;
		height = 120f;

		titlebar.title = "Signalgenerator";

		wave_select = new WaveSelect();
		wave_select.translation.x = 35;
		wave_select.translation.y = 20;
		add(wave_select);

		createFrequencySlider();
		createAmplitudeSlider();
		createOffsetSlider();
	}

	public static String getNodeName() {
		return "Signalgenerator";
	}

	public static String getNodeDescription() {
		return "Generates audio waves.";
	}

	boolean shown;
	public void onDraw() {
		super.onDraw();
		if(!shown)
			System.out.printf("Signal generator draw() %s: t=%s\n", this, translation);
		shown = true;
		if(getPort("output") != null)
			getPort("output").translation.x = width;

		if(getPort("frequency") != null)
			frequency_slider.showSlider(!getUINet().isConnected(getPort("frequency"))); // TODO Only do this when connections are actually changed

		if(getPort("amplitude") != null)
			amp_slider.showSlider(!getUINet().isConnected(getPort("amplitude")));
	}

	@Override
	protected void onMessage(NodeParameterMessage message) {
		System.out.printf("Signal generator onMessage() %s: %s\n", this, message);
		if(message.key.equals("data.frequency")) {
			frequency = (float)message.value;
			frequency_slider.setValue(frequency / 8000);
		}

		else if(message.key.equals("data.amplitude")) {
			amplitude = (float)message.value;
			amp_slider.setValue(amplitude / 10000);
		}

		else if(message.key.equals("data.offset")) {
			offset = (float)message.value;
			offset_slider.setValue(offset / 10000.0);
		}
	}

	@Override
	public void onCreatePort(UIPort port) {
		if(port.name.equals("frequency")) {
			frequency_slider.setPort(port);
		}

		if(port.name.equals("amplitude")) {
			amp_slider.setPort(port);
		}

		if(port.name.equals("output")) {
			port.translation.y = 20f;
			port.color = UIPort.AUDIO_PORT;
		}
	}

	@Override
	public void onRemovePort(UIPort port) {
		// Never removes any port anyway, so not implemented
	}

	private void createFrequencySlider() {
		UI self = this;
		frequency_slider = new PortParameterSlider("frequency");
		frequency_slider.translation.y = 40f;
		frequency_slider.color = UIPort.AUX_PORT;
		add(frequency_slider);

		frequency_slider.setHandler(new PortParameterSlider.IHandler() {
			@Override
			public void onChange(double value, boolean programatic) {
				frequency = Math.round(value * 8000.0 * 10) / 10 + 0.1f;

				if(!programatic) // If not set by setValue, but by users himself
					sendParameter("data.frequency", frequency);
			}

			@Override
			public void onButton(int offset) {
				frequency += offset*1;
				frequency_slider.setValue(frequency / 8000);
			}

			@Override
			public String onLabelUpdate(double value) {
				return String.format("%.2f Hz", frequency);
			}
		});

		frequency_slider.setValue(frequency / 8000);
		frequency_slider.setScale(0.1f);
	}

	private void createAmplitudeSlider() {
		UI self = this;
		amp_slider = new PortParameterSlider("amplitude");
		amp_slider.translation.y = 70f;
		amp_slider.color = UIPort.AUDIO_PORT;
		add(amp_slider);

		amp_slider.setHandler(new PortParameterSlider.IHandler() {
			@Override
			public void onChange(double value, boolean programatic) {
				amplitude = (float)(value * 10000f);

				if(!programatic) // If not set by setValue, but by users himself
					sendParameter("data.amplitude", amplitude);
			}

			@Override
			public void onButton(int offset) {
				amplitude += offset * 0.1f;
				amp_slider.setValue(amplitude / 10000f);
			}

			@Override
			public String onLabelUpdate(double value) {
				return String.format("%.2f", amplitude);
			}
		});

		amp_slider.setValue(0.001f);
		amp_slider.setScale(0.02f);
	}

	private void createOffsetSlider() {
		UI self = this;
		offset_slider = new ParameterSlider();
		offset_slider.translation.x = 10f;
		offset_slider.translation.y = 95f;
		add(offset_slider);

		offset_slider.setHandler(new ParameterSlider.IHandler() {
			@Override
			public void onChange(double value, boolean programatic) {
				offset = (float)(value * 10000f);

				if(!programatic)
					sendParameter("data.offset", offset);
			}

			@Override
			public void onButton(int offset) {
				self.offset += offset;
				offset_slider.setValue(offset / 10000f);
			}

			@Override
			public String onLabelUpdate(double value) {
				return String.format("%.1f", offset);
			}
		});

		offset_slider.setValue(0);
		offset_slider.scale = 0.02f;
	}
}
