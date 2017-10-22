package net.merayen.elastic.uinodes.list.signalgenerator_1;

import java.util.List;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.InputSignalParameters;
import net.merayen.elastic.ui.objects.components.PopupParameter1D;
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBox;
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBoxControlFrame;
import net.merayen.elastic.ui.objects.components.framework.PortParameter;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private PortParameter frequency_port_parameter;
	private SignalBezierCurveBoxControlFrame curve;

	public UI() {
		super();
		width = 200f;
		height = 150f;

		titlebar.title = "Signalgenerator";

		createBezierWave();
	}

	public void onDraw() {
		super.onDraw();

		if(getPort("output") != null)
			getPort("output").translation.x = width;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onMessage(NodeParameterMessage message) {
		if(message.key.equals("data.frequency")) {
			((PopupParameter1D)frequency_port_parameter.not_connected).setValue((float)(Math.pow(((Number)message.value).floatValue(), 1/4.301029995663981) / 10.0));
			updateFrequencyText();
		}
		else if(message.key.equals("data.curve")) {
			curve.bezier.setPoints((List<Number>)message.value);
		} else if(message.key.equals("data.InputSignalParameters:frequency")) {
			((InputSignalParameters)frequency_port_parameter.connected).handleMessage(message);
		}
	}

	@Override
	public void onCreatePort(UIPort port) {
		if(port.name.equals("frequency")) {
			frequency_port_parameter = new PortParameter(this, getPort("frequency"), new PopupParameter1D(), new InputSignalParameters(this, "frequency"));
			frequency_port_parameter.translation.x = 20;
			frequency_port_parameter.translation.y = 20;
			add(frequency_port_parameter);

			((PopupParameter1D)frequency_port_parameter.not_connected).setHandler(new PopupParameter1D.Handler() {
				@Override
				public void onMove(float value) {
					updateFrequencyText();
					sendParameter("data.frequency", getFrequency());
				}

				@Override
				public void onChange(float value) {}
			});

			((PopupParameter1D)frequency_port_parameter.not_connected).drag_scale = 0.5f;

			port.translation.y = 20;
		}

		if(port.name.equals("output")) {
			port.translation.y = 20f;
			port.color = UIPort.AUDIO_PORT;
		}
	}

	private void createBezierWave() {
		SignalBezierCurveBoxControlFrame bwb = new SignalBezierCurveBoxControlFrame();
		bwb.translation.x = 20;
		bwb.translation.y = 40;
		bwb.width = 160;
		bwb.height = 100;
		add(bwb);
		curve = bwb;

		bwb.bezier.setHandler(new SignalBezierCurveBox.Handler() {
			int i;
			@Override
			public void onChange() {
				sendParameter("data.curve", bwb.bezier.getFloats());
			}

			@Override
			public void onMove() {
				if(i++ % 10 == 0)
					sendParameter("data.curve", bwb.bezier.getFloats());
			}
		});

		//bwb.insertPoint(1);
		//bwb.insertPoint(2);
		//bwb.insertPoint(1);
	}

	@Override
	protected void onData(NodeDataMessage message) {}

	@Override
	protected void onRemovePort(UIPort port) {}

	private float getFrequency() {
		//return (float)Math.pow(((PopupParameter1D)frequency_port_parameter.not_connected).getValue() * 2, 14);
		return (float)Math.pow(((PopupParameter1D)frequency_port_parameter.not_connected).getValue() * 10, 4.301029995663981);
	}

	private void updateFrequencyText() {
		((PopupParameter1D)frequency_port_parameter.not_connected).label.text = String.format("Frequency: %.3f", getFrequency());
	}

	@Override
	protected void onParameter(String key, Object value) {}
}
