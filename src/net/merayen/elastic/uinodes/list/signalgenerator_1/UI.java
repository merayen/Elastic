package net.merayen.elastic.uinodes.list.signalgenerator_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.node.components.PortParameterSlider;
import net.merayen.elastic.uinodes.list.test_100.SignalBezierCurveBox;
import net.merayen.elastic.util.pack.FloatArray;

public class UI extends UINode {
	private PortParameterSlider frequency_slider;
	private float frequency = 440;

	public UI() {
		super();
		width = 200f;
		height = 200f;

		titlebar.title = "Signalgenerator";

		createFrequencySlider();
		createBezierWave();
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
	}

	@Override
	protected void onMessage(NodeParameterMessage message) {
		//System.out.printf("Signal generator onMessage() %s: %s\n", this, message);
		if(message.key.equals("data.frequency")) {
			frequency = (float)message.value;
			frequency_slider.setValue(frequency / 8000);
		}
	}

	@Override
	public void onCreatePort(UIPort port) {
		if(port.name.equals("frequency")) {
			frequency_slider.setPort(port);
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
		frequency_slider.translation.y = 20f;
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

	private void createBezierWave() {
		SignalBezierCurveBox bwb = new SignalBezierCurveBox();
		bwb.translation.x = 20;
		bwb.translation.y = 40;
		bwb.width = 200;
		bwb.height = 200;
		add(bwb);
		UI self = this;

		bwb.setHandler(new SignalBezierCurveBox.Handler() {
			int i;
			@Override
			public void onChange() {
				float[] points_flat = bwb.getFloats();
				self.sendParameter("data.curve", new FloatArray(bwb.getFloats()));
			}

			@Override
			public void onMove() {
				if(i++ % 10 == 0)
					self.sendParameter("data.curve", new FloatArray(bwb.getFloats()));
			}
		});

		bwb.insertPoint(1);
	}

	@Override
	protected void onData(NodeDataMessage message) {
		System.console();
	}
}
